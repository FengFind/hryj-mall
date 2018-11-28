package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.OrderInfoVO;
import com.hryj.entity.vo.order.OrderPorductVO;
import com.hryj.entity.vo.order.OrderLogisticsVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: OrderDetailResponseVO
 * @description: 订单详情响应VO
 * @create 2018/6/30 9:11
 **/
@Data
@ApiModel(value = "订单详情响应VO")
public class OrderDetailResponseVO {

    @ApiModelProperty(value = "订单基本信息", required = true)
    private OrderInfoVO orderInfoVO;

    @ApiModelProperty(value = "订单物流信息", required = true)
    private OrderLogisticsVO orderLogisticsVO;

    @ApiModelProperty(value = "退货信息", required = true)
    private ReturnOrderDetailsResponseVO oderDetailsResponseVO;

    @ApiModelProperty(value = "订单商品列表", required = true)
    private List<OrderPorductVO> orderPorductList;
}
