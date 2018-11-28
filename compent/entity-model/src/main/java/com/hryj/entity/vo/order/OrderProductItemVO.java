package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: OrderProductItemVO
 * @description: 订单商品明细VO.v2
 * @create 2018/8/22 9:59
 **/
@Data
@ApiModel(value = "订单商品明细VO.v1.2")
public class OrderProductItemVO {

    @ApiModelProperty(value = "购物车ID-默认为0", required = true)
    private Long cart_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品类型", required = true)
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称", required = true)
    private String product_type_name;

    @ApiModelProperty(value = "商品图片")
    private String list_image_url;

    @ApiModelProperty(value = "销售成交价", required = true)
    private BigDecimal shop_price;

    @ApiModelProperty(value = "销售价格")
    private BigDecimal sale_price;

    @ApiModelProperty(value = "商品购买数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "商品库存数量")
    private Integer inventory_quantity;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "门店编号")
    private Long party_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动类型：01-爆款,02-团购", notes = "01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动角标图")
    private String activity_mark_image;

    @ApiModelProperty(value = "活动价格")
    private BigDecimal activity_price;

    @ApiModelProperty(value = "优惠金额")
    private BigDecimal discount;

    @ApiModelProperty(value = "商品title标记")
    private String title_mark;

}
