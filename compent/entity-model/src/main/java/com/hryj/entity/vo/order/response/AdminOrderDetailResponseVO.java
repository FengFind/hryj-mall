package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: AdminOrderDetailResponseVO
 * @description: 后台订单详情响应VO
 * @create 2018/6/30 17:00
 **/
@Data
@ApiModel(value = "后台订单详情响应VO")
public class AdminOrderDetailResponseVO {

    @ApiModelProperty(value = "订单基本信息", required = true)
    private OrderInfoVO orderInfoVO;

    @ApiModelProperty(value = "用户基本信息", required = true)
    private OrderUserVO orderUserVO;

    @ApiModelProperty(value = "订单物流信息", required = true)
    private OrderLogisticsVO orderLogisticsVO;

    @ApiModelProperty(value = "订单商品列表", required = true)
    private List<OrderPorductVO> orderPorductList;

    @ApiModelProperty(value = "订单状态列表", required = true)
    private List<OrderStatusRecordVO> orderStatusList;

    @ApiModelProperty(value = "退货信息")
    private List<AdminOrderReturnResponseVO> orderReturnList;

    @ApiModelProperty(value = "优惠金额")
    private String discount_amt;

    @ApiModelProperty(value = "实付金额")
    private String pay_amt;

    @ApiModelProperty(value = "商品总数")
    private String countQuantity;
}
