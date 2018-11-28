package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 叶方宇
 * @className: OrderReturnForUserVO
 * @description:
 * @create 2018/7/12 0012 9:55
 **/
@Data
@ApiModel(value = "根据订单id查询退货单信息和用户信息")
public class OrderReturnForUserVO {

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户姓名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "订单状态", required = true)
    private String order_status;

    @ApiModelProperty(value = "经纬度", required = true)
    private String locations;

    @ApiModelProperty(value = "配送员ID", required = true)
    private Long distribution_staff_id;

    @ApiModelProperty(value = "配送员姓名", required = true)
    private String distribution_staff_name;

    @ApiModelProperty(value = "配送员电话", required = true)
    private String distribution_staff_phone;

    @ApiModelProperty(value = "配送金额", required = true)
    private BigDecimal distribution_amt;




}
