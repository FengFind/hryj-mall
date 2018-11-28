package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 李道云
 * @className: AdminOrderInfoVO
 * @description: 后台订单基本信息VO
 * @create 2018/6/30 16:32
 **/
@Data
@ApiModel(value = "后台订单基本信息VO")
public class AdminOrderInfoVO {

    @ApiModelProperty(value = "订单id", required = true)
    private String order_id;

    @ApiModelProperty(value = "订单编号", required = true)
    private String order_num;

    @ApiModelProperty(value = "订单类型", required = true)
    private String order_type;

    @ApiModelProperty(value = "订单类型名称", required = true)
    private String description;

    @ApiModelProperty(value = "门店类型", required = true)
    private String party_type;

    @ApiModelProperty(value = "门店id", required = true)
    private String party_id;

    @ApiModelProperty(value = "订单状态", notes = "01-待支付,02-待发货,03-待自提,04-已发货,05-退货申请中,06-退货成功,07-已取消,08-已完成", required = true)
    private String order_status;

    @ApiModelProperty(value = "用户手机", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "下单时间", required = true)
    private Date create_time;

    @ApiModelProperty(value = "支付方式", notes = "01-微信,02-支付宝,03-银联", required = true)
    private String pay_method;

    @ApiModelProperty(value = "代下单员工id")
    private Long help_staff_id;

    @ApiModelProperty(value = "配送方式", notes = "配送方式:01-自提,02-送货上门,03-快递", required = true)
    private String delivery_type;

    @ApiModelProperty(value = "配送单状态", notes = "01-待分配,02-待配送,03-配送完成,04-配送超时", required = true)
    private String distribution_status;

}
