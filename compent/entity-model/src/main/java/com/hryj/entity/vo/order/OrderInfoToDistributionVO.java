package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 叶方宇
 * @className: OrderInfoToDistribution
 * @description: 订单配送商品信息表
 * @create 2018/7/9 0009 10:07
 **/

@Data
@ApiModel(value = "配送单产品信息记录需要数据")
public class OrderInfoToDistributionVO {

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "门店ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "数量", required = true)
    private Integer quantity;

    @ApiModelProperty(value = "期望送达开始时间", notes = "时间格式: yyyy-MM-dd HH:mm:SS", required = true)
    private Date hope_delivery_start_time;

    @ApiModelProperty(value = "期望送达截止时间", notes = "时间格式: yyyy-MM-dd HH:mm:SS", required = true)
    private Date hope_delivery_end_time;



}
