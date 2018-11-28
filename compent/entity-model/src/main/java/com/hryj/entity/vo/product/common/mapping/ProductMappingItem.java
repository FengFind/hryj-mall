package com.hryj.entity.vo.product.common.mapping;

import com.hryj.entity.vo.product.crossborder.response.CrossBorderProductValidateResponseItem;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: ProductMappingItem
 * @description:
 * @create 2018/9/18 0018 12:24
 **/
@Data
public class ProductMappingItem {

    private Long product_id;

    private String product_type_id;

    private Integer forbid_sale_flag;

    private String product_name;

    private Long prod_cate_id;

    private String prod_cate_path;

    private String specification;

    private Long brand;

    private String list_image_url;

    private BigDecimal cost_price;

    private Long tax_rate_id;

    private BigDecimal unit_1;

    private String unit_2;

    private String hs_code;

    private BigDecimal consume_tax;

    private BigDecimal increment_tax;

    private String third_sku_id;

    private Integer inventory_quantity;

    private String channel;

    public CrossBorderProductValidateResponseItem getCrossBorderProductValidateResponseItem() {
        CrossBorderProductValidateResponseItem item = new CrossBorderProductValidateResponseItem();
        item.setConsume_tax(this.consume_tax);
        item.setHs_code(this.hs_code);
        item.setIncrement_tax(this.increment_tax);
        item.setTax_id(this.tax_rate_id);
        item.setThird_sku_id(this.third_sku_id);
        item.setUnit_1(this.unit_1);
        item.setUnit_2(this.unit_2);
        return item;
    }
}
