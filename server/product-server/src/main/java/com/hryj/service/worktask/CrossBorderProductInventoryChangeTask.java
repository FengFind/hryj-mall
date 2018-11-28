package com.hryj.service.worktask;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * @author 王光银
 * @className: CrossBorderProductInventoryChangeTask
 * @description: 跨境商品库存变更(只针对库存从无到有的情况)对商品搜索缓存的处理任务
 * @create 2018/10/11 0011 14:38
 **/
@Slf4j
public class CrossBorderProductInventoryChangeTask implements Runnable {

    private Long product_id;

    private List<Long> exclude_party_ids;

    public CrossBorderProductInventoryChangeTask(Long product_id, List<Long> exclude_party_ids) {
        this.product_id = product_id;
        this.exclude_party_ids = exclude_party_ids;
    }

    @Override
    public void run() {
        if (this.product_id == null || this.product_id <= 0L) {
            log.warn("跨境商品库存变更对商品搜索缓存的调整任务: 商品ID为空，任务未成功执行...");
            return;
        }

        //加载 所有正在销售该跨境界商品的门店
        Date curr = new Date();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("product_id", this.product_id);
        wrapper.le("introduction_date", curr);
        wrapper.ge("sales_end_date", curr);
        wrapper.eq("up_down_status", CommonConstantPool.NUMBER_ONE);
        wrapper.groupBy("party_id");
        wrapper.setSqlSelect("party_id");
        PartyProductMapper partyProductMapper = SpringContextUtil.getBean("partyProductMapper", PartyProductMapper.class);
        List<PartyProduct> partyProductList = partyProductMapper.selectList(wrapper);
        if (UtilValidate.isEmpty(partyProductList)) {
            return;
        }
        for (PartyProduct partyProduct : partyProductList) {

            if (UtilValidate.isNotEmpty(this.exclude_party_ids) && this.exclude_party_ids.contains(partyProduct.getParty_id())) {
                continue;
            }

            //清除门店商品统计缓存
            RedisCacheUtil.PartyProductStatisticsCacheUtil.cleanByPartyId(partyProduct.getParty_id());

            //清除门店商品搜索缓存
            RedisCacheUtil.ProductSearchCacheUtil.cleanByPartyId(partyProduct.getParty_id());

            //清除门店推荐位商品缓存
            RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(partyProduct.getParty_id());
        }
    }
}
