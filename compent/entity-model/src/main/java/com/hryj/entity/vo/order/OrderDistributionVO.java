package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


/**
 * @author 叶方宇
 * @className: OrderDistribution
 * @description: 订单配送信息
 * @create 2018-07-02 11:01
 **/
@Data
public class OrderDistributionVO {

    @ApiModelProperty(value = "id", required = true)
    private Long id;

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "配送类别:01-送货,02-取货", required = true)
    private String distribution_type;

    @ApiModelProperty(value = "配送状态:01-待配送,02-配送中,03-已配送,04-取消配送", required = true)
    private String distribution_status;

    @ApiModelProperty(value = "退货单编号", required = true)
    private Long return_id;

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

    @ApiModelProperty(value = "期望送达开始时间", required = true)
    private Date hope_delivery_start_time;

    @ApiModelProperty(value = "期望送达截止时间", required = true)
    private Date hope_delivery_end_time;

    @ApiModelProperty(value = "配送员id", required = true)
    private Long distribution_staff_id;

    @ApiModelProperty(value = "配送员姓名", required = true)
    private String distribution_staff_name;

    @ApiModelProperty(value = "配送员电话", required = true)
    private String distribution_staff_phone;

    @ApiModelProperty(value = "配送费用", required = true)
    private BigDecimal distribution_amt;

    @ApiModelProperty(value = "分配人id", required = true)
    private Long assign_staff_id;

    @ApiModelProperty(value = "分配人姓名", required = true)
    private String assign_staff_name;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date create_time;

    @ApiModelProperty(value = "更新时间", required = true)
    private Date update_time;

    @ApiModelProperty(value = "是否有退货申请", required = true)
    private Long isReturn;
}
