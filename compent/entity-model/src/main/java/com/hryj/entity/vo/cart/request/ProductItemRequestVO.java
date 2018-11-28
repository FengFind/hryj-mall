package com.hryj.entity.vo.cart.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: ProductItemRequestVO
 * @description: 立即购买、购物车至订单确认页商品请求VO
 * @version 2.0
 * @create 2018/8/16 16:50
 **/
@ApiModel(description = "立即购买、购物车至订单确认页请求VO.v2")
@Data
public class ProductItemRequestVO {

    @ApiModelProperty(value = "购物车ID(立即购买时为空，购物车列表时不为空)")
    private Long cart_id;

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "商品销售价")
    private BigDecimal shop_price;
}
