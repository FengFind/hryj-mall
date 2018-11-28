package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 叶方宇
 * @className: OrderCreateFromCart
 * @description:
 * @create 2018/7/6 0006 10:09
 **/
@Data
@ApiModel(value = "从购物车处获取订单价格，优惠，成本VO")
public class OrderCreateFromCartVO {

    @ApiModelProperty(value = "APP_KEY", required = true)
    private String app_key;

    @ApiModelProperty(value = "门店ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "门店名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "门店类别", required = true)
    private String party_type;

    @ApiModelProperty(value = "部门路径", required = true)
    private String party_path;

    @ApiModelProperty(value = "购物车id", required = true)
    private Long cart_record_id;

    @ApiModelProperty(value = "活动id", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "活动费价格", required = true)
    private BigDecimal activity_price;

    @ApiModelProperty(value = "代下单员工id")
    private Long help_staff_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品分类id", required = true)
    private Long prod_cate_id;

    @ApiModelProperty(value = "商品图片", required = true)
    private String list_image_url;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "成本价格", required = true)
    private BigDecimal cost_price;

    @ApiModelProperty(value = "售价", required = true)
    private BigDecimal sale_price;

    @ApiModelProperty(value = "商品数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "分享者ID", required = true)
    private Long share_user_id;

    @ApiModelProperty(value = "分享来源", required = true)
    private String share_source;

}
