package com.hryj.service.worktask;

import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.UtilValidate;

import java.util.Set;

/**
 * @author 王光银
 * @className: PartyProductUpdateTask
 * @description: 门店、仓库商品修改对缓存的影响，主要是价格与库存
 * @create 2018/8/29 0029 17:00
 **/
public class PartyProductUpdateTask implements Runnable {

    private Set<Long> party_id_set;

    public PartyProductUpdateTask(Set<Long> party_id_set) {
        this.party_id_set = party_id_set;
    }

    @Override
    public void run() {
        if (UtilValidate.isNotEmpty(party_id_set)) {
            for (Long party_id : this.party_id_set) {
                RedisCacheUtil.PartyProductStatisticsCacheUtil.cleanByPartyId(party_id);
                RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(party_id);
                RedisCacheUtil.ProductSearchCacheUtil.cleanByPartyId(party_id);
            }
        }
    }
}
