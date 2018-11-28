package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 李道云
 * @className: ReturnVO
 * @description: 退货单信息
 * @create 2018/6/30 15:49
 **/
@Data
@ApiModel(value = "退货单信息")
public class ReturnVO {

    @ApiModelProperty(value = "退货单编号", required = true)
    private Long return_id;

    @ApiModelProperty(value = "对应订单编号", required = true)
    private Long order_id;

    @ApiModelProperty(value = "退货单类别", notes = "01-客户自主退货,02-店员代退货", required = true)
    private String return_type;

    @ApiModelProperty(value = "退货单状态", notes = "01-申请中,02-同意退货,03-取消退货,04-拒绝退货", required = true)
    private String return_status;

    @ApiModelProperty(value = "退货申请人姓名", required = true)
    private String return_apply_name;

    @ApiModelProperty(value = "退货申请时间", required = true)
    private Date return_apply_time;

    @ApiModelProperty(value = "退货申请原因", required = true)
    private String return_reason;

    @ApiModelProperty(value = "用户编号", required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "退货单处理人Id", required = true)
    private Long return_handel_id;

    @ApiModelProperty(value = "退货处理人姓名", required = true)
    private String return_handel_name;

    @ApiModelProperty(value = "取货完成时间", required = true)
    private Date take_time;



}
