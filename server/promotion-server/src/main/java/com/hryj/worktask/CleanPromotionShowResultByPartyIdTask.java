package com.hryj.worktask;

import com.alibaba.fastjson.JSONArray;
import com.hryj.cache.RedisService;
import com.hryj.service.util.PromotionRedisCacheUtil;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;

import java.util.Set;

/**
 * @author 汪豪
 * @className: CleanPromotionShowResultByPartyIdTask
 * @description:
 * @create 2018/8/29 0029 8:57
 **/
public class CleanPromotionShowResultByPartyIdTask implements Runnable {

    private Long party_id;

    public CleanPromotionShowResultByPartyIdTask(Long party_id){
        if (party_id == null || party_id <= 0L) {
            throw new NullPointerException("party_id不能是空值");
        }
        this.party_id = party_id;
    }

    @Override
    public void run() {
        RedisService redisService = RedisCacheUtil.redisCacheUtil.getRedisService();
        redisService.delete2(PromotionRedisCacheUtil.PromotionRedisCacheKey.PROMOTION_SHOW_RESULT_CACHE_KEY.get(),party_id.toString());
    }
}
