package com.hryj.entity.vo.inventory.mapping;

import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.utils.UtilValidate;
import lombok.Data;

/**
 * @author 王光银
 * @className: InventoryLockMappingItem
 * @description:
 * @create 2018/9/18 0018 10:52
 **/
@Data
public class InventoryLockMappingItem {

    /**
     * 门店商品ID
     */
    private Long id;

    /**
     * 门店ID
     */
    private Long party_id;


    /**
     *  商品ID
     */
    private Long product_id;


    /**
     *  商品类型
     */
    private String product_type_id;

    /**
     * 门店库存
     */
    private Integer inventory_quantity;

    /**
     * 商品中心跨境界商品库存
     */
    private Integer center_inventory_quantity;

    private Boolean is_cross_border;

    public void initProductTypeStatus(GenericConverter<Boolean> is_cross_border) {
        if (UtilValidate.isEmpty(this.product_type_id)) {
            throw new NullPointerException("数据错误: product_type_id是空值");
        }
        this.is_cross_border = is_cross_border.convert(this.product_type_id);
    }

    /**
     * 将库存持久化到数据库，根据商品类型区别处理
     */
    public void persistence() {
        if (null == this.is_cross_border) {
            throw new NullPointerException("成员未初始化: is_cross_border");
        }
        if (this.is_cross_border) {
            CrossBorderProduct crossBorderProduct = new CrossBorderProduct();
            crossBorderProduct.setId(this.product_id);
            crossBorderProduct.setInventory_quantity(this.center_inventory_quantity);
            crossBorderProduct.updateById();
            return;
        }
        PartyProduct partyProduct = new PartyProduct();
        partyProduct.setId(this.id);
        partyProduct.setInventory_quantity(this.inventory_quantity);
        partyProduct.updateById();
    }

    public PartyProduct getPersistencePartyProduct() {
        if (Boolean.TRUE.equals(this.is_cross_border)) {
            return null;
        }
        PartyProduct partyProduct = new PartyProduct();
        partyProduct.setId(this.id);
        partyProduct.setInventory_quantity(this.inventory_quantity);
        return partyProduct;
    }

    public CrossBorderProduct getPersistenceCrossBorderProduct() {
        if (!Boolean.TRUE.equals(this.is_cross_border)) {
            return null;
        }
        CrossBorderProduct crossBorderProduct = new CrossBorderProduct();
        crossBorderProduct.setId(this.product_id);
        crossBorderProduct.setInventory_quantity(this.center_inventory_quantity);
        return crossBorderProduct;
    }

}
