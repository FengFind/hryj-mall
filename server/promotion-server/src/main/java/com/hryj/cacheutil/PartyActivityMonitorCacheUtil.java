package com.hryj.cacheutil;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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

    public static void removeFrom(Long party_id, List<Date> date_list) {
        if (party_id == null || party_id <= 0L || UtilValidate.isEmpty(date_list)) {
            return;
        }
        //加锁
        boolean locked = false;
        try {
            locked = lock(party_id);

            List<String> cache_list = getList(party_id);


            if (UtilValidate.isEmpty(cache_list)) {
                setPartyActivityDateCache(party_id, null);
                return;
            }

            for (Date date : date_list) {
                String this_date = DateUtil.formatDateTime(date);
                if (cache_list.contains(this_date)) {
                    cache_list.remove(this_date);
                }
            }

            setPartyActivityDateCache(party_id, cache_list);
        } catch (Exception e) {
            log.error("从门店关联活动日期中删除活动日期数据失败:", e);
        } finally {
            if (locked) {
                unlock(party_id);
            }
        }
    }

    public static Date getFirst(Long party_id) {
        if (party_id == null || party_id <= 0L) {
            return null;
        }

        String cache_json_str = RedisCacheUtil.redisCacheUtil.getRedisService().get2(PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP, party_id.toString());
        if (UtilValidate.isEmpty(cache_json_str)) {
            return null;
        }

        List<String> list = JSONArray.parseArray(cache_json_str).toJavaList(String.class);
        if (UtilValidate.isEmpty(list)) {
            return null;
        }

        try {
            return DateUtil.parseDateTime(list.get(0));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 向缓存中追加门店关联的活动日期数据
     * @param party_id
     * @param date_list
     */
    public static void appendTo(Long party_id, List<Date> date_list) {
        if (party_id == null || party_id <= 0L || UtilValidate.isEmpty(date_list)) {
            return;
        }
        //加锁
        boolean locked = false;
        try {
            locked = lock(party_id);
            List<String> cache_list = getList(party_id);

            for (Date date : date_list) {
                cache_list.add(DateUtil.formatDateTime(date));
            }

            setPartyActivityDateCache(party_id, cache_list);
        } catch (Exception e) {
            log.error("向门店关联活动日期缓存追加数据失败:", e);
        } finally {
            if (locked) {
                unlock(party_id);
            }
        }
    }

    private static List<String> getList(Long party_id) {
        if (party_id == null || party_id <= 0L) {
            return null;
        }
        String cache_json_str = RedisCacheUtil.redisCacheUtil.getRedisService().get2(PARTY_ACTIVITY_DATE_SORT_LIST_CACHE_GROUP, party_id.toString());
        return UtilValidate.isEmpty(cache_json_str) ? new ArrayList<>() : JSONArray.parseArray(cache_json_str).toJavaList(String.class);
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
}
