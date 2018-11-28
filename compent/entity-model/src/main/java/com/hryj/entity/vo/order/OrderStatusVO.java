package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * @author 叶方宇
 * @className: OrderIdVO
 * @description:
 * @create 2018/7/5 0005 9:26
 **/
@Data
@ApiModel(value = "订单状态操作表")
public class OrderStatusVO{

    @ApiModelProperty(value = "订单编号",required = true)
    private Long order_id;

    @ApiModelProperty(value = "订单状态:01-待支付,02-待发货,03-待自提,04-已发货,05-退货申请中,06-退货成功,07-已取消,08-已完成")
    private String order_status;

    @ApiModelProperty(value="登录token",name="login_token",hidden=true)
    private String login_token;

    @ApiModelProperty(value="状态说明")
    private String status_remark;

    @ApiModelProperty(value="变更原因")
    private String change_reason;

    @ApiModelProperty(value="订单完成时间")
    private Date complete_time;

    @ApiModelProperty(value="首单标识")
    private Integer new_trade_flag;

    @ApiModelProperty(value="用户Id")
    private Long user_id;
}
