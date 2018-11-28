package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李道云
 * @className: OrderInfoVO
 * @description: 订单基本信息VO
 * @create 2018/6/30 9:46
 **/
@Data
@ApiModel(value = "订单基本信息VO")
public class OrderInfoVO {

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "光彩订单id")
    private String third_order_code;

    @ApiModelProperty(value = "订单类型", required = true)
    private String order_type;

    @ApiModelProperty(value = "订单类型名称", required = true)
    private String order_type_name;

    @ApiModelProperty(value = "报关状态:待申报-1、申报中-2、申报成功-3、申报失败-4、已出货-5、确认失败-6", hidden = true)
    private Integer third_order_status;

    @ApiModelProperty(value = "报关状态:申报中、申报成功、申报失败、已取消")
    private String third_order_status_desc;

    @ApiModelProperty(value = "报关失败原因")
    private String failed_reason;

    @ApiModelProperty(value = "订单取消次数",hidden = true)
    private Integer cancel_count;

    @ApiModelProperty(value = "订单取消原因",hidden = true)
    private String cancel_failed_reason;

    @ApiModelProperty(value = "订单税金")
    private BigDecimal order_tax;

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "代下单账号", required = true)
    private String help_staff_id;

    @ApiModelProperty(value = "用户名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "收货人电话", required = true)
    private String receive_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "订单编号", required = true)
    private String order_num;

    @ApiModelProperty(value = "门店仓库编号", required = true)
    private Long party_id;

    @ApiModelProperty(value = "门店仓库名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "发货仓库名称")
    private String delivery_warehouse_name;

    @ApiModelProperty(value = "仓库或门店联系人姓名")
    private String party_contact_name;

    @ApiModelProperty(value = "仓库或门店联系人电话")
    private String party_contact_phone;

    @ApiModelProperty(value = "仓库或门店地址")
    private String party_address;

    @ApiModelProperty(value = "部门类型（部门类别:01-门店,02-仓库,03-普通部门）", required = true)
    private String party_type;

    @ApiModelProperty(value = "订单状态", required = true)
    private String order_status;

    @ApiModelProperty(value = "订单金额", required = true)
    private String order_amt;

    @ApiModelProperty(value = "优惠金额", required = true)
    private String discount_amt;

    @ApiModelProperty(value = "实付金额", required = true)
    private String pay_amt;

    @ApiModelProperty(value = "创建时间", required = true)
    private Date create_time;

    @ApiModelProperty(value = "付款时间", required = true)
    private Date pay_time;

    @ApiModelProperty(value = "支付剩余时间",required = true)
    private String lastTime;

    @ApiModelProperty(value = "支付方式：01-微信,02-支付宝,03-银联", required = true)
    private String pay_method;

    @ApiModelProperty(value = "配送方式：01-自提,02-送货上门,03-快递", required = true)
    private String delivery_type;


}
