package com.hryj.service.worktask;

import com.alibaba.fastjson.JSONArray;
import com.hryj.cache.RedisService;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;

import java.util.Set;

/**
 * @author 王光银
 * @className: RefreshProductSearchResultExpireTimeTask
 * @description:
 * @create 2018/8/29 0029 9:47
 **/
public class RefreshProductSearchResultExpireTimeTask implements Runnable {

    private Long party_id;

    private Long expire_time_in_second;

    public RefreshProductSearchResultExpireTimeTask(Long party_id, Long expire_time_in_second) {
        if (party_id == null || party_id <= 0L) {
            throw new NullPointerException("party_id 参数不能是空值");
        }
        if (expire_time_in_second == null || expire_time_in_second <= 0L) {
            throw new NullPointerException("expire_time_in_second 参数不能是空值");
        }
        this.party_id = party_id;
        this.expire_time_in_second = expire_time_in_second;
    }

    @Override
    public void run() {
        /**
         * 1、得到当前门店下的搜索搜索条件KEY
         * 2、依次刷新这些KEY对应的结果
         */
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
        for (String cache_key : cache_key_set) {
            redisService.expire2(group_name, cache_key, this.expire_time_in_second.intValue());
        }

    }
}
