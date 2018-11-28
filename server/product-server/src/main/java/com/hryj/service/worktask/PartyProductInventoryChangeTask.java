package com.hryj.service.worktask;

import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.service.inventory.ProductInventoryService;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;

import java.util.List;

/**
 * @author 王光银
 * @className: PartyProductInventoryChangeTask
 * @description: 商品库存改变对缓存数据的影响（这个任务只能处理新零售商品缓存，不能处理跨境商品）
 * @create 2018/8/29 0029 15:04
 **/
public class PartyProductInventoryChangeTask implements Runnable {

    private List<ProductInventoryService.InventoryUniqueItem> change_list;

    public PartyProductInventoryChangeTask(List<ProductInventoryService.InventoryUniqueItem> change_list) {
        this.change_list = change_list;
    }

    @Override
    public void run() {

        if (UtilValidate.isNotEmpty(this.change_list)) {
            for (ProductInventoryService.InventoryUniqueItem item : this.change_list) {
                if (ProductTypeCacheHandler.isCrossBorder(item.getProduct_type_id())) {
                    ThreadPoolUtil.submitTask(new CrossBorderProductInventoryChangeTask(item.getProduct_id(), UtilMisc.toList(item.getParty_id())));
                    continue;
                }

                //清除门店商品统计缓存
                RedisCacheUtil.PartyProductStatisticsCacheUtil.cleanByPartyId(item.getParty_id());

                //清除门店商品搜索缓存
                RedisCacheUtil.ProductSearchCacheUtil.cleanByPartyId(item.getParty_id());

                //清除门店推荐位商品缓存
                RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(item.getParty_id());
            }
        }

    }
}
