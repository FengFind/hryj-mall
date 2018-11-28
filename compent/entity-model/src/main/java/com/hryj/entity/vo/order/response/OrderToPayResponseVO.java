package com.hryj.entity.vo.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: OrderToPayResponseVO
 * @description: 订单去支付响应VO
 * @create 2018/6/29 20:44
 **/
@Data
@ApiModel(value = "订单去支付响应VO")
public class OrderToPayResponseVO {

    @ApiModelProperty(value = "待支付金额", required = true)
    private String pay_amt;

    @ApiModelProperty(value = "订单编号集合", required = true,name = "多个订单编号用逗号分隔")
    private String orderNumStr;

    @ApiModelProperty(value = "支付方式列表", notes = "01-微信,02-支付宝,03-银联", required = true)
    private List<String> payMethodList;


}
