package com.hryj.service.inventory.cache;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.SqlHelper;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.mapper.CrossBorderProductMapper;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.mapper.ProductMapper;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilValidate;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductInventorySummary
 * @description: 商品库存缓存摘要封装对象
 * @create 2018/10/8 0008 15:37
 **/
@Data
public class ProductInventorySummary {

    private Long product_id;

    private Long party_id;

    private String product_type_id;

    private Integer inventory_quantity;

    public ProductInventorySummary(Long product_id, Long party_id) {
        this.product_id = product_id;
        this.party_id = party_id;
    }

    public ProductInventorySummary(Long product_id, Long party_id, String product_type_id, Integer inventory_quantity) {
        this(product_id, party_id);
        this.product_type_id = product_type_id;
        this.inventory_quantity = inventory_quantity;
    }

    private void initInventoryQuantity() {
        if (this.inventory_quantity == null) {
            if (UtilValidate.isEmpty(this.product_type_id)) {
                initProductType();
            }
            if (ProductTypeCacheHandler.isCrossBorder(this.product_type_id)) {
                CrossBorderProductMapper crossBorderProductMapper = SpringContextUtil.getBean("crossBorderProductMapper", CrossBorderProductMapper.class);
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("id", this.product_id);
                List<CrossBorderProduct> list = crossBorderProductMapper.selectList(wrapper);
                CrossBorderProduct crossBorderProduct = SqlHelper.getObject(list);
                if (crossBorderProduct == null) {
                    throw new NullPointerException("cross-border-product doesn't exists, product-id:" + this.product_id);
                }
                this.inventory_quantity = crossBorderProduct.getInventory_quantity() == null ? 0 : crossBorderProduct.getInventory_quantity();
            } else {
                PartyProductMapper partyProductMapper = SpringContextUtil.getBean("partyProductMapper", PartyProductMapper.class);
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("party_id", this.party_id);
                wrapper.eq("product_id", this.product_id);
                wrapper.setSqlSelect("inventory_quantity");
                List<PartyProduct> list = partyProductMapper.selectList(wrapper);
                PartyProduct partyProduct = SqlHelper.getObject(list);
                if (partyProduct == null) {
                    throw new NullPointerException("party-product doesn't exists, party-id:" + this.party_id + ", product-id:" + this.product_id);
                }
                this.inventory_quantity = partyProduct.getInventory_quantity() == null ? 0 : partyProduct.getInventory_quantity();
            }
        }
    }

    private void initProductType() {
        if (UtilValidate.isEmpty(this.product_type_id)) {
            //从数据库加载
            ProductMapper productMapper = SpringContextUtil.getBean("productMapper", ProductMapper.class);
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id", this.product_id);
            wrapper.setSqlSelect("product_type_id");
            List<ProductInfo> list = productMapper.selectList(wrapper);
            ProductInfo productInfo = SqlHelper.getObject(list);
            if (productInfo == null) {
                throw new NullPointerException("product doesn't exists, product-id:" + this.product_id);
            }
            if (UtilValidate.isEmpty(productInfo.getProduct_type_id())) {
                throw new NullPointerException("product data error, product_type_id:" + productInfo.getProduct_type_id());
            }
            this.product_type_id = productInfo.getProduct_type_id();
        }
    }

    public String getProduct_type_id() {
        if (UtilValidate.isEmpty(this.product_type_id)) {
            initProductType();
        }
        return this.product_type_id;
    }

    public Integer getInventory_quantity() {
        if (this.inventory_quantity == null) {
            initInventoryQuantity();
        }
        return this.inventory_quantity;
    }

    @Override
    public int hashCode() {
        if (UtilValidate.isEmpty(this.product_type_id)) {
            throw new NullPointerException("product_type_id is null");
        }
        if (ProductTypeCacheHandler.isCrossBorder(this.product_type_id)) {
            return this.product_id == null ? -1 : this.product_id.hashCode();
        }
        int party_id_hash = this.party_id == null ? -1 : this.party_id.hashCode();
        int product_id_hash = this.party_id == null ? -1 : this.party_id.hashCode();
        return party_id_hash + product_id_hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }

        ProductInventorySummary pis = (ProductInventorySummary) obj;
        if (UtilValidate.isEmpty(this.product_type_id) || UtilValidate.isEmpty(pis.product_type_id)) {
            return false;
        }
        if (!this.product_type_id.equals(pis.product_type_id)) {
            return false;
        }
        if (ProductTypeCacheHandler.isCrossBorder(this.product_type_id)) {
            return this.product_id.equals(pis.product_id);
        }
        return this.party_id.equals(pis.party_id) && this.product_id.equals(pis.product_id);
    }
}
