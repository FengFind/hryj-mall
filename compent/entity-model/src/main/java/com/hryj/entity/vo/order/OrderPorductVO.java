package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: OrderPorductVO
 * @description: 订单商品VO
 * @create 2018/6/30 9:21
 **/
@Data
@ApiModel(value = "订单商品VO")
public class OrderPorductVO {

    @ApiModelProperty(value = "订单商品ID", required = true)
    private Long order_product_id;

    @ApiModelProperty(value = "订单ID", required = true)
    private Long order_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品名称", required = true)
    private String product_name;

    @ApiModelProperty(value = "商品类型", required = true)
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称", required = true)
    private String product_type_name;

    @ApiModelProperty(value = "商品title标记")
    private String title_mark;

    @ApiModelProperty(value = "商品图片", required = true)
    private String list_image_url;

    @ApiModelProperty(value = "原售价", required = true)
    private String org_price;

    @ApiModelProperty(value = "实际价格", required = true)
    private String actual_price;

    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "商品状态:1-有效,0-无效", required = true)
    private Integer product_status;

    @ApiModelProperty(value = "活动id")
    private Long activity_id;

    @ApiModelProperty(value = "门店编号")
    private Long party_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动类型", notes = "01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动角标图")
    private String activity_mark_image;

    @ApiModelProperty(value = "活动价格")
    private String activity_price;


}
