package com.hryj.worktask;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.hryj.cache.RedisService;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Set;

/**
 * @author 王光银
 * @className: RefreshProductSearchResultExpireTimeTask
 * @description:
 * @create 2018/8/29 0029 9:47
 **/
@Slf4j
public class RefreshProductSearchResultExpireTimeTask implements Runnable {

    private Long party_id;

    private String expire_time;

    public RefreshProductSearchResultExpireTimeTask(Long party_id, String expire_time) {
        if (party_id == null || party_id <= 0L) {
            throw new NullPointerException("party_id 参数不能是空值");
        }
        if (UtilValidate.isEmpty(expire_time)) {
            throw new NullPointerException("expire_time 参数不能是空值");
        }
        this.party_id = party_id;
        this.expire_time = expire_time;
    }

    @Override
    public void run() {
        Date target_date;
        try {
            target_date = DateUtil.parseDateTime(this.expire_time);
        } catch (Exception e) {
            log.error("刷新门店商品搜索结果缓存时效失败:", e);
            return;
        }

        if (target_date.before(new Date())) {
            //如果目标日期在当前日期之前，直接清除所有缓存
            RedisService redisService = RedisCacheUtil.redisCacheUtil.getRedisService();
            String cache_data = redisService.get2(RedisCacheUtil.RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get(), party_id.toString());
            if (UtilValidate.isEmpty(cache_data)) {
                return;
            }
            Set<String> cache_key_set = UtilMisc.toSet(JSONArray.parseArray(cache_data).toJavaList(String.class));
            if (UtilValidate.isNotEmpty(cache_key_set)) {
                String group_name = RedisCacheUtil.ProductSearchCacheUtil.generateSearchResultGroupName(party_id);
                for (String cache_key : cache_key_set) {
                    redisService.delete2(group_name, cache_key);
                }
            }
            RedisCacheUtil.ProductSearchCacheUtil.removeCacheKey(party_id, null, true);

            return;
        }


        RedisService redisService = RedisCacheUtil.redisCacheUtil.getRedisService();
        String cache_data = redisService.get2(RedisCacheUtil.RedisCacheKey.PROD_SEARCH_RESULT_CACHE_KEY.get(), party_id.toString());
        if (UtilValidate.isEmpty(cache_data)) {
            return;
        }
        Set<String> cache_key_set = UtilMisc.toSet(JSONArray.parseArray(cache_data).toJavaList(String.class));

        if (UtilValidate.isEmpty(cache_key_set)) {
            return;
        }

        String group_name = RedisCacheUtil.ProductSearchCacheUtil.generateSearchResultGroupName(party_id);
        Date curr = new Date();
        for (String cache_key : cache_key_set) {
            redisService.expire2(group_name, cache_key, Long.valueOf(DateUtil.between(curr, target_date, DateUnit.SECOND)).intValue());
        }
    }

}
