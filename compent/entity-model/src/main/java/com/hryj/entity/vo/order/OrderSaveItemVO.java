package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: OrderSaveItemVO
 * @description: 订单保存条目VO
 * @create 2018/6/29 20:40
 **/
@Data
@ApiModel(value = "订单保存条目VO")
public class OrderSaveItemVO {

    @ApiModelProperty(value = "购物车记录id", required = true)
    private Long cart_record_id;

    @ApiModelProperty(value = "配送方式", notes = "01-自提,02-送货上门,03-快递", required = true)
    private String delivery_type;

    @ApiModelProperty(value = "期望送达开始时间", notes = "时间戳", required = true)
    private Long hope_delivery_start_time;

    @ApiModelProperty(value = "期望送达截止时间", notes = "时间戳", required = true)
    private Long hope_delivery_end_time;

    @ApiModelProperty(value = "门店ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "活动ID", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "商品数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "分享者ID")
    private Long share_user_id;

    @ApiModelProperty(value = "分享来源")
    private String share_source;
}
