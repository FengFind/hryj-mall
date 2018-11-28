package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: OrderLogisticsVO
 * @description: 订单物流信息VO
 * @create 2018/6/30 9:48
 **/
@Data
@ApiModel(value = "订单物流信息VO")
public class OrderLogisticsVO {

    @ApiModelProperty(value = "自提码")
    private String take_self_code;

    @ApiModelProperty(value = "自提地址")
    private String self_pick_address;

    @ApiModelProperty(value = "自提联系人")
    private String self_pick_contact;

    @ApiModelProperty(value = "自提联系电话")
    private String self_pick_phone;

    @ApiModelProperty(value = "配送员姓名")
    private String distribution_staff_name;

    @ApiModelProperty(value = "配送员电话")
    private String distribution_staff_phone;

    @ApiModelProperty(value = "期望送达开始时间")
    private String hope_delivery_start_time;

    @ApiModelProperty(value = "期望送达截止时间")
    private String hope_delivery_end_time;

    @ApiModelProperty(value = "快递公司")
    private String express_name;

    @ApiModelProperty(value = "快递单号")
    private String express_code;

    @ApiModelProperty(value = "配送状态:01-待配送,02-配送中,03-已配送,04-取消配送", required = true)
    private String distribution_status;

    @ApiModelProperty(value = "收货人姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "收货人电话", required = true)
    private String receive_phone;

    @ApiModelProperty(value = "收货电话", required = true)
    private String user_address;
}
