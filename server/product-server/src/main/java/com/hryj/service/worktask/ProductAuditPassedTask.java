package com.hryj.service.worktask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductBackup;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.service.util.ProductUtil;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

/**
 * @author 王光银
 * @className: ProductAuditPassedTask
 * @description: 商品审核通过处理任务，处理商品对推荐位与商品搜索结果的影响
 * @create 2018/8/29 0029 13:56
 **/
@Slf4j
public class ProductAuditPassedTask implements Runnable {

    private ProductBackup backup;

    //private Long product_backup_id;

    public ProductAuditPassedTask(ProductBackup backup) {
        this.backup = backup;
    }

    @Override
    public void run() {
        /**
         * 如果是新增商品的审核，不做处理，新增的商品需要门店、仓库上架后才能销售
         */
        if (UtilValidate.isEmpty(backup.getProd_data_after()) || UtilValidate.isEmpty(backup.getProd_data_before())) {
            return;
        }

        try {
            JSONObject before_prod = JSON.parseObject(backup.getProd_data_before());
            ProductInfo before = JSON.toJavaObject(before_prod, ProductInfo.class);

            JSONObject after_prod = JSON.parseObject(backup.getProd_data_after());
            ProductInfo after = JSON.toJavaObject(after_prod, ProductInfo.class);

            boolean need_to_clean_cache = false;

            if (!before.getProduct_name().equals(after.getProduct_name())) {
                need_to_clean_cache = true;
            }

            if (!before.getList_image_url().equals(after.getList_image_url())) {
                need_to_clean_cache = true;
            }

            if (!before.getSpecification().equals(after.getSpecification())) {
                need_to_clean_cache = true;
            }

            //TODO 调整品牌数据的获取
            //if (!before.getBrand_name().equals(after.getBrand_name())) {
            //    need_to_clean_cache = true;
            //}

            if (need_to_clean_cache) {
                PartyProductMapper partyProductMapper = SpringContextUtil.getBean("partyProductMapper", PartyProductMapper.class);
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("product_id", backup.getProduct_id());
                wrapper.eq("up_down_status", ProductUtil.UP_STATUS);
                wrapper.gt("inventory_quantity", 0);
                wrapper.groupBy("party_id");
                wrapper.setSqlSelect("party_id");
                List<PartyProduct> item_list = partyProductMapper.selectList(wrapper);
                if (UtilValidate.isNotEmpty(item_list)) {
                    for (PartyProduct partyProduct : item_list) {
                        RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(partyProduct.getParty_id());
                        RedisCacheUtil.ProductSearchCacheUtil.cleanByPartyId(partyProduct.getParty_id());
                    }
                }
            }
        } catch (Exception e) {
            log.error("商品审核通过后处理 推荐位商品、商品搜索缓存失败:", e);
        }
    }
}
