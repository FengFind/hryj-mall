package com.hryj.service.inventory.task;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.service.inventory.cache.ProductInventorySummary;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 王光银
 * @className: ProductInventoryAdjustmentItem
 * @description: 商品库存处理队列数据对象
 * @create 2018/10/8 0008 16:52
 **/
@Data
@Slf4j
public class ProductInventoryAdjustmentItem {

    private Long product_id;

    private Long party_id;

    private String product_type_id;

    private Integer adjustment_quantity;

    public void store() {
        try {
            if (this.adjustment_quantity == null || this.adjustment_quantity.intValue() == 0) {
                log.warn("ProductInventoryAdjustmentItem store: adjustment_quantity is null-value, skip and return");
                return;
            }

            if (UtilValidate.isEmpty(this.product_type_id)) {
                throw new NullPointerException("ProductInventoryAdjustmentItem store: product_type_id is null");
            }

            ProductInventorySummary summary = new ProductInventorySummary(this.product_id, this.party_id, this.product_type_id, null);
            summary.getProduct_type_id();
            Integer current_inventory_quantity = summary.getInventory_quantity();
            current_inventory_quantity += this.adjustment_quantity;

            if (current_inventory_quantity.intValue() < 0) {
                current_inventory_quantity = 0;
            }

            if (ProductTypeCacheHandler.isCrossBorder(this.product_type_id)) {
                //跨境商品的库存处理
                CrossBorderProduct crossBorderProduct = new CrossBorderProduct();
                crossBorderProduct.setId(this.product_id);
                crossBorderProduct.setInventory_quantity(current_inventory_quantity);
                crossBorderProduct.updateById();
                return;
            }

            //非跨境商品库存处理
            PartyProduct partyProduct = new PartyProduct();
            partyProduct.setInventory_quantity(current_inventory_quantity);
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("product_id", this.product_id);
            wrapper.eq("party_id", this.party_id);
            partyProduct.update(wrapper);
        } catch (Exception e) {
            log.error("异步更新库存失败:", e);
        }
    }

}
