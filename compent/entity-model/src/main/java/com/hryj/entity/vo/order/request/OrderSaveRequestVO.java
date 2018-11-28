package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.order.OrderSaveItemVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 李道云
 * @className: OrderSaveRequestVO
 * @description: 订单保存请求VO
 * @create 2018/6/29 20:22
 **/
@Data
@ApiModel(value = "订单保存请求VO-v1.2")
public class OrderSaveRequestVO extends RequestVO {


    @ApiModelProperty(value = "用户id", notes = "代下单功能时有值，需要知道是谁的订单")
    private Long user_id;

    @ApiModelProperty(value = "收货地址", required = true)
    private String address;

    @ApiModelProperty(value = "订单条目列表", required = true)
    private List<OrderSaveItemVO> orderItemList;

    @ApiModelProperty(value = "支付总金额", required = true)
    private BigDecimal pay_price;

    @ApiModelProperty(value = "购买人姓名")
    private String buyer_name;

    @ApiModelProperty(value = "购买人身份证号")
    private String buyer_id_card;

}
