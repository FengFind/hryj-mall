package com.hryj.service.worktask;

import com.alibaba.fastjson.JSONArray;
import com.hryj.cache.RedisService;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;

import java.util.Set;

/**
 * @author 王光银
 * @className: CleanProductSearchResultByPartyIdTask
 * @description:
 * @create 2018/8/24 0024 16:32
 **/
public class CleanProductSearchResultByPartyIdTask implements Runnable {

    private Long party_id;
    public CleanProductSearchResultByPartyIdTask(Long party_id) {
        if (party_id == null || party_id <= 0L) {
            throw new NullPointerException("party_id不能是空值");
        }
        this.party_id = party_id;
    }

    @Override
    public void run() {
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
    }
}
