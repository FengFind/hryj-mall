package com.hryj.entity.vo.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 白飞
 * @className: OrderPaymentResponseVO
 * @description: 订单去支付响应VO
 * @create 2018/6/29 20:44
 **/
@Data
@ApiModel(value = "订单去支付响应VO-v1.2")
public class OrderPaymentResponseVO {

    @ApiModelProperty(value = "待支付金额", required = true)
    private BigDecimal pay_amount;

    @ApiModelProperty(value = "订单编号集合", required = true,name = "多个订单编号用逗号分隔")
    private String order_num_str;

    @ApiModelProperty(value = "支付方式列表", notes = "01-微信,02-支付宝,03-银联", required = true)
    private List<String> pay_method_list;


}
