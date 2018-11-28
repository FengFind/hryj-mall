package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 李道云
 * @className: OrderListInfoVO
 * @description: 订单列表信息VO
 * @create 2018/6/29 22:11
 **/
@Data
@ApiModel(value = "订单列表信息VO")
public class OrderListInfoVO {

    @ApiModelProperty(value = "订单id", required = true)
    private Long order_id;

    @ApiModelProperty(value = "订单类型", required = true)
    private String order_type;

    @ApiModelProperty(value = "订单类型名称", required = true)
    private String order_type_name;

    @ApiModelProperty(value = "订单编号", required = true)
    private String order_num;

    @ApiModelProperty(value = "订单状态", notes = "01-待支付,02-待发货,03-待自提,04-已发货,05-退货申请中,06-退货成功,07-已取消,08-已完成", required = true)
    private String order_status;

    @ApiModelProperty(value = "用户id", required = true)
    private Long user_id;

    @ApiModelProperty(value = "用户名", required = true)
    private String user_name;

    @ApiModelProperty(value = "用户电话", required = true)
    private String user_phone;

    @ApiModelProperty(value = "用户地址", required = true)
    private String user_address;

    @ApiModelProperty(value = "支付方式：01-微信,02-支付宝,03-银联", required = true)
    private String pay_method;

    @ApiModelProperty(value = "配送方式：01-自提,02-送货上门,03-快递", required = true)
    private String delivery_type;

    @ApiModelProperty(value = "门店仓库名称", required = true)
    private String  party_name;

    @ApiModelProperty(value = "门店仓库类型：01-门店,02-仓库", required = true)
    private String party_type;

    @ApiModelProperty(value = "商品总数", required = true)
    private Integer total_num;

    @ApiModelProperty(value = "总金额", required = true)
    private BigDecimal total_amount;

    @ApiModelProperty(value = "申请退货次数", required = true)
    private Integer return_num;

    @ApiModelProperty(value = "商品列表", required = true)
    private List<OrderPorductVO> orderProductList;
}
