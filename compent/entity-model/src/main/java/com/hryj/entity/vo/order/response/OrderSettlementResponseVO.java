package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.UserReceiveAddressVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: OrderSettlementResponseVO
 * @description: 订单结算响应VO
 * @create 2018/6/29 19:46
 **/
@Data
@ApiModel(value = "订单结算响应VO-v1.2")
public class OrderSettlementResponseVO {

    @ApiModelProperty(value = "用户收货地址", required = true)
    private UserReceiveAddressVO userReceiveAddressVO;

    @ApiModelProperty(value = "订单结算商品信息", required = true)
    private List<OrderConfirmProductResponseVO> productList;

    @ApiModelProperty(value = "订单金额", required = true)
    private String order_amt;

    @ApiModelProperty(value = "是否显示订购人和身份证号码")
    private Boolean is_show_buyer;

    @ApiModelProperty(value = "购买人姓名")
    private String buyer_name;

    @ApiModelProperty(value = "购买人身份证号")
    private String buyer_id_card;
}
