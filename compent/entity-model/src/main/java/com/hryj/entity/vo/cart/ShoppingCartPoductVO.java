package com.hryj.entity.vo.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ShoppingCartPoductVO
 * @description: 购物车商品信息
 * @create 2018/6/29 0029 11:12
 **/
@ApiModel(value = "购物车商品信息-v1.2")
@Data
public class ShoppingCartPoductVO {

    @ApiModelProperty(value = "购物车记录id", required = true)
    private Long cart_record_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品名称", required = true)
    private String product_name;

    @ApiModelProperty(value = "商品类型", required = true)
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称", required = true)
    private String product_type_name;

    @ApiModelProperty(value = "商品图片", required = true)
    private String list_image_url;

    @ApiModelProperty(value = "商品title标记")
    private String title_mark;

    @ApiModelProperty(value = "加入时单价", required = true)
    private String into_cart_price;

    @ApiModelProperty(value = "销售价格", required = true)
    private String sale_price;

    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "商品库存数量", required = true)
    private Integer inventory_quantity;

    @ApiModelProperty(value = "活动id")
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
    private String activity_price;

    @ApiModelProperty(value = "优惠金额")
    private String discount_amt;

}
