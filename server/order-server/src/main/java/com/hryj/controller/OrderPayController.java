package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.order.SingleParamVO;
import com.hryj.entity.vo.order.request.ConfirmPayOrderRequestVO;
import com.hryj.entity.vo.payment.PaymentGroupResponseVO;
import com.hryj.service.OrderService;
import com.hryj.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 罗秋涵
 * @className: OrderPayController
 * @description: 订单支付服务
 * @create 2018/7/10 0010 14:14
 **/
@Slf4j
@RestController
@RequestMapping(value = "/orderPay")
public class OrderPayController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderService orderService;


    /**
     * @author 李道云
     * @methodName: confirmPayOrder
     * @methodDesc: 确认支付订单
     * @description: 多个订单编号逗号分隔
     * @param: [orderNumStr, pay_method]
     * @return com.hryj.common.Result
     * @create 2018-06-29 21:34
     **/
    @PostMapping("/confirmPayOrder")
    public Result<Map> confirmPayOrder(@RequestBody ConfirmPayOrderRequestVO confirmPayOrderRequestVO){
        return paymentService.confirmPayOrder(confirmPayOrderRequestVO);
    }

    /**
     * @author 罗秋涵
     * @description: 订单支付后查询订单支付结果
     * @param: [orderNumStr]
     * @return com.hryj.common.Result
     * @create 2018-07-03 16:19
     **/
    @PostMapping("/getOrderPayResult")
    public Result<PaymentGroupResponseVO> getOrderPayResult(@RequestBody SingleParamVO singleParamVO){
        return  orderService.getOrderPayResult(singleParamVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @methodName: notifyForWxPay
     * @methodDesc: 微信支付回调
     * @description:
     * @param: [param_map]
     * @create 2018-07-10 20:01
     **/
    @PostMapping("/notifyForWxPay")
    public Result notifyForWxPay(@RequestBody Map<String, Object> param_map){
        return paymentService.notifyForWxPay(param_map);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @methodName: notifyForAliPay
     * @methodDesc: 支付宝支付回调
     * @description:
     * @param: [param_map]
     * @create 2018-07-10 20:01
     **/
    @PostMapping("/notifyForAliPay")
    public Result notifyForAliPay(@RequestBody Map<String, Object> param_map){
        return paymentService.notifyForAliPay(param_map);
    }

}
