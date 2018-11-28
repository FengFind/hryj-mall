package com.hryj.service.util.cacheutil;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hryj.entity.bo.promotion.ActivityScopeItem;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.service.worktask.CleanPartyActivityThruDateTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author 王光银
 * @className: PartyActivityMonitorCacheUtil
 * @description:
 * @create 2018/8/31 0031 9:08
 **/
@Slf4j
public class PartyActivityMonitorCacheUtil {

    private static final String PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP = "party.activity.date.sort.list.cache.group";

    private static final String NO_ACTIVITY_DEFAULT_VALUE = "default.value";

    private static List<String> getList(Long party_id) {
        if (party_id == null || party_id <= 0L) {
            return null;
        }
        String cache_json_str = RedisCacheUtil.redisCacheUtil.getRedisService().get2(PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP, party_id.toString());
        return UtilValidate.isEmpty(cache_json_str) ? new ArrayList<>() : JSONArray.parseArray(cache_json_str).toJavaList(String.class);
    }

    /**
     * 获取门店对应的最早的活动变化时间
     * @param party_id
     * @return
     */
    public static Date getEarliestAfterNow(Long party_id) {
        if (party_id == null || party_id <= 0L) {
            return null;
        }

        String cache_json_str;
        while (true) {
            cache_json_str = RedisCacheUtil.redisCacheUtil.getRedisService().get2(PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP, party_id.toString());
            if (UtilValidate.isEmpty(cache_json_str)) {
                /**
                 * 如果缓存中没有当前门店的缓存数据，则从数据中加载
                 */
                checkLoadFromDB(party_id);
                continue;
            }
            break;
        }

        List<String> list = JSONArray.parseArray(cache_json_str).toJavaList(String.class);
        if (UtilValidate.isEmpty(list)) {
            return null;
        }

        String curr = DateUtil.formatDateTime(new Date());

        boolean need_to_startup_clean_task = false;

        String earliest = null;

        for (String date_time : list) {
            if (NO_ACTIVITY_DEFAULT_VALUE.equals(date_time)) {
                continue;
            }

            if (date_time.compareTo(curr) <= 0) {
                need_to_startup_clean_task = true;
                continue;
            }
            earliest = date_time;
            break;
        }

        if (need_to_startup_clean_task) {
            /**
             * 启动任务清理缓存中的过期日期数据
             */
            ThreadPoolUtil.submitTask(new CleanPartyActivityThruDateTask(party_id));
        }

        return earliest == null ? null : DateUtil.parseDateTime(earliest);
    }

    /**
     * 加载所有门店的活动相关日期，开放给外部任务调用
     */
    public static void checkLoadFromDB() {
        checkLoadFromDB(null);
    }

    /**
     * 检查数据库，并且加载已经开始的活动和即将开始的活动
     */
    private static void checkLoadFromDB(Long target_party_id) {
        List<ActivityScopeItem> data_list = loadFromDB(target_party_id);
        if (UtilValidate.isEmpty(data_list)) {
            if (target_party_id != null && target_party_id > 0L) {
                //如果门店确实没有活动时，设置一个默认值
                setPartyActivityDateCache(target_party_id, UtilMisc.toList(NO_ACTIVITY_DEFAULT_VALUE));
            }
            return;
        }

        Map<Long, Set<String>> party_group_map = new HashMap<>(data_list.size());
        for (ActivityScopeItem scopeItem : data_list) {
            Set<String> this_list;
            if (party_group_map.containsKey(scopeItem.getParty_id())) {
                this_list = party_group_map.get(scopeItem.getParty_id());
            } else {
                this_list = new HashSet<>();
                party_group_map.put(scopeItem.getParty_id(), this_list);
            }

            try {
                if (scopeItem.getStart_date() != null) {
                    this_list.add(DateUtil.formatDateTime(scopeItem.getStart_date()));
                }
                if (scopeItem.getEnd_date() != null) {
                    this_list.add(DateUtil.formatDateTime(scopeItem.getEnd_date()));
                }
            } catch (Exception e) {
                log.error("缓存活动开始、结束日期： 日期格式错误, party_id=" + scopeItem.getParty_id() + ", start_date=" + scopeItem.getStart_date().getTime() + ", end_date=" + scopeItem.getEnd_date().getTime(), e);
            }
        }

        //缓存门店的所有相关日期
        for (Long item_party_id : party_group_map.keySet()) {
            setPartyActivityDateCache(item_party_id, new ArrayList<>(CollUtil.toCollection(party_group_map.get(item_party_id))));
        }
    }

    /**
     * 从数据库加载门店相关的活动日期数据
     * @param party_id
     * @return
     */
    private static List<ActivityScopeItem> loadFromDB(Long party_id) {
        PartyProductMapper partyProductMapper = SpringContextUtil.getBean("partyProductMapper", PartyProductMapper.class);
        List<ActivityScopeItem> list = partyProductMapper.findPartyActivityDate(party_id, new Date());
        return list;
    }

    /**
     * 将门店相关的活动日期进行缓存
     * @param party_id
     * @param list
     */
    private static List<String> setPartyActivityDateCache(Long party_id, List<String> list) {
        if (party_id == null || party_id <= 0L) {
            return null;
        }
        if (UtilValidate.isEmpty(list)) {
            list = UtilMisc.toList(NO_ACTIVITY_DEFAULT_VALUE);
        }
        sortDateList(list);
        RedisCacheUtil.redisCacheUtil.getRedisService().put2(PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP, party_id.toString(), JSON.toJSONString(list), null);
        return list;
    }

    /**
     * 将集合中的日期格式字符串进行升序排序
     * @param date_str_list
     */
    private static void sortDateList(List<String> date_str_list) {
        if (UtilValidate.isEmpty(date_str_list)) {
            return;
        }
        date_str_list.sort((String::compareTo));
        filterDateList(date_str_list);
    }

    /**
     * 过滤掉已经过期的日期
     * @param date_str_list
     */
    private static void filterDateList(List<String> date_str_list) {
        if (UtilValidate.isEmpty(date_str_list)) {
            return;
        }

        if (date_str_list.size() > 1 && date_str_list.contains(NO_ACTIVITY_DEFAULT_VALUE)) {
            date_str_list.remove(NO_ACTIVITY_DEFAULT_VALUE);
        }

        String curr = DateUtil.formatDateTime(new Date());
        Iterator<String> it = date_str_list.iterator();
        while (it.hasNext()) {
            String date_str = it.next();
            if (curr.compareTo(date_str) >= 0) {
                it.remove();
            }
        }
    }

    private static boolean lock(Long party_id) {
        int try_times = 20;
        int sleep = 50;
        try {
            int counter = 0;
            while (true) {
                boolean lock_result = RedisCacheUtil.redisCacheUtil.getRedisLock().lock(PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP, party_id.toString(), 2);
                if (lock_result) {
                    return true;
                }
                counter ++;
                if (counter > try_times) {
                    break;
                }
                Thread.sleep(sleep);
            }
            return false;
        } catch (Exception e) {
            log.error("门店活动监控-尝试加锁操作失败:", e);
            return false;
        }
    }

    private static boolean unlock(Long party_id) {
        return RedisCacheUtil.redisCacheUtil.getRedisLock().unLock(PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP, party_id.toString());
    }

    /**
     * 清除门店下过期的关联活动日期
     * @param party_id
     */
    public static void cleanThruDate(Long party_id) {
        if (party_id == null || party_id <= 0L) {
            return;
        }

        boolean locked = false;
        try {
            locked = lock(party_id);
            if (!locked) {
                return;
            }
            List<String> list = getList(party_id);
            if (UtilValidate.isNotEmpty(list)) {
                String curr = DateUtil.formatDateTime(new Date());
                Iterator<String> it = list.iterator();
                while (it.hasNext()) {
                    String date_str = it.next();
                    if (date_str.compareTo(curr) <= 0) {
                        it.remove();
                    }
                }
                setPartyActivityDateCache(party_id, list);
            }
        } catch (Exception e) {
            log.error("清除门店过期活动日期数据失败:", e);
        } finally {
            if (locked) {
                unlock(party_id);
            }
        }
    }
}
