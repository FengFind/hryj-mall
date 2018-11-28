package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 李道云
 * @className: DistributionInfoVO
 * @description: 配送单信息VO
 * @create 2018/6/30 11:04
 **/
@Data
@ApiModel(value = "配送单信息VO")
public class DistributionInfoVO {

    @ApiModelProperty(value = "配送单id", required = true)
    private Long distribution_id;

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "订单类型", required = true)
    private String order_type;

    @ApiModelProperty(value = "订单类型名称", required = true)
    private String order_type_name;

    @ApiModelProperty(value = "第三方订单状态:待申报-1、申报中-2、申报成功-3、申报失败-4、已出货-5、确认失败-6", required = true)
    private Integer third_order_status;

    @ApiModelProperty(value = "退货单id", required = true)
    private Long return_id;

    @ApiModelProperty(value = "配送类别", notes = "01-配送,02-取货", required = true)
    private String distribution_type;

    @ApiModelProperty(value = "配送状态", notes = "01-待配送,02-配送中,03-已配送,04-取消配送", required = true)
    private String distribution_status;

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "用户地址经纬度", required = true)
    private String address_locations;

    @ApiModelProperty(value = "期望送达开始时间")
    private Date hope_delivery_start_time;

    @ApiModelProperty(value = "期望送达开始时间")
    private String start_timestamp;

    @ApiModelProperty(value = "期望送达截止时间")
    private Date hope_delivery_end_time;

    @ApiModelProperty(value = "实际送达截止时间")
    private Date actual_delivery_end_time;

    @ApiModelProperty(value = "期望送达开始时间")
    private String end_timestamp;

    @ApiModelProperty(value = "配送员ID")
    private Long distribution_staff_id;

    @ApiModelProperty(value = "配送员姓名")
    private String distribution_staff_name;

    @ApiModelProperty(value = "配送员电话")
    private String distribution_staff_phone;

    @ApiModelProperty(value = "配送完成时间")
    private Date complete_time;

    @ApiModelProperty(value = "剩余时间",required = true)
    private String lastTime;

}
