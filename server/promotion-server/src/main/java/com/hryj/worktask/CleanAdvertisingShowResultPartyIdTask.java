package com.hryj.worktask;

import com.hryj.cache.RedisService;
import com.hryj.service.util.PromotionRedisCacheUtil;
import com.hryj.service.util.RedisCacheUtil;

/**
 * @author 汪豪
 * @className: CleanAdvertisingShowResultPartyIdTask
 * @description:
 * @create 2018/8/29 0029 9:11
 **/
public class CleanAdvertisingShowResultPartyIdTask implements Runnable{

    private Long party_id;

    public CleanAdvertisingShowResultPartyIdTask(Long party_id){
        if (party_id == null || party_id <= 0L) {
            throw new NullPointerException("party_id不能是空值");
        }
        this.party_id = party_id;
    }


    @Override
    public void run() {
        RedisService redisService = RedisCacheUtil.redisCacheUtil.getRedisService();
        redisService.delete2(PromotionRedisCacheUtil.PromotionRedisCacheKey.ADVERTISING_SHOW_RESULT_CACHE_KEY.get(),party_id.toString());
    }
}
