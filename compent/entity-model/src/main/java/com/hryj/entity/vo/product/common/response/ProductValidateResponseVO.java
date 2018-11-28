package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: ProductValidateResponseVO
 * @description: 商品验证响应VO
 * @create 2018/7/3 0003 21:38
 **/
@ApiModel(value = "商品验证响应VO", description = "商品验证响应VO")
@Data
public class ProductValidateResponseVO {

    @ApiModelProperty(value = "商品不存在，true表示商品不存在, false表示商品存在")
    private Boolean prod_not_exists = false;

    @ApiModelProperty(value = "商品上下架状态，true上架, false下架")
    private Boolean up_down_status;

    @ApiModelProperty(value = "商品库存数量")
    private Integer inventory_quantity;

    @ApiModelProperty(value = "商品当前时刻销售价格，如果没有促销活动则为正常售价，如果有活动并且活动未过期为活动价格，活动过期为正常售价")
    private BigDecimal this_moment_sale_price;

    @ApiModelProperty(value = "商品在当事组织中的正常销售价")
    private BigDecimal normal_price;

}
