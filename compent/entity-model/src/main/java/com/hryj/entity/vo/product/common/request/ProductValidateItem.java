package com.hryj.entity.vo.product.common.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductValidateItem
 * @description:
 * @create 2018/7/19 0019 21:46
 **/
@Data
public class ProductValidateItem {

    public ProductValidateItem() {}

    public ProductValidateItem(Long party_id, Long product_id, Long activity_id, Integer required_min_inventory_quantity) {
        this.party_id = party_id;
        this.product_id = product_id;
        this.activity_id = activity_id;
        this.required_min_inventory_quantity = required_min_inventory_quantity;
    }

    public ProductValidateItem(Long party_id, Long product_id, Long activity_id, Integer required_min_inventory_quantity, String follow_value) {
        this.party_id = party_id;
        this.product_id = product_id;
        this.activity_id = activity_id;
        this.required_min_inventory_quantity = required_min_inventory_quantity;
        this.follow_value = follow_value;
    }


    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "需要的最小库存量，可不传，如果不传则验证库存是否大于0")
    private Integer required_min_inventory_quantity;

    @ApiModelProperty(value = "该参数可不传，如果传了，则原样返回")
    private String follow_value;

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
