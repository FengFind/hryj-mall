package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.cart.request.CartOperationRequestVO;
import com.hryj.entity.vo.cart.request.ProductBuyRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartRequestVO;
import com.hryj.entity.vo.order.OrderListInfoVO;
import com.hryj.entity.vo.order.request.*;
import com.hryj.entity.vo.order.response.*;
import com.hryj.feign.StaffFeignClient;
import com.hryj.service.OrderForUserAppService;
import com.hryj.service.OrderService;
import com.hryj.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: OrderForUserAppController
 * @description: 用户订单服务
 * @create 2018-06-29 19:42
 **/
@Slf4j
@RestController
@RequestMapping(value = "/userOrder")
public class OrderForUserAppController {

    @Autowired
    private OrderForUserAppService orderForUserAppService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private StaffFeignClient staffFeignClient;

    /**
     * @author 李道云
     * @methodName: immediateBuy
     * @methodDesc: 立即购买（订单确认界面）
     * @description:
     * @param: [shoppingCartRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderSettlementResponseVO>
     * @create 2018-06-29 21:43
     **/
    @PostMapping("/immediateBuy")
    public Result<OrderSettlementResponseVO> immediateBuy(@RequestBody ShoppingCartRequestVO shoppingCartRequestVO){

        return orderService.immediateBuy(shoppingCartRequestVO);
    }

    /**
     * @author 白飞
     * @methodName: confirmInfo
     * @methodDesc: 订单确认
     * @description:
     * @param: [cartRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderConfirmInfoResponseVO>
     * @create 2018-06-29 21:43
     **/
    @PostMapping("/confirmInfo")
    public Result<OrderConfirmInfoResponseVO> confirmInfo(@RequestBody ProductBuyRequestVO productBuyRequestVO){
        return orderService.confirmInfo(productBuyRequestVO);
    }

    /**
     * @author 白飞
     * @methodName: create
     * @methodDesc: 订单创建-去支付
     * @description:
     * @param: [orderCreateRequest]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderToPayResponseVO>
     * @create 2018-08-22 15:27
     **/
    @PostMapping("/create")
    public Result<OrderPaymentResponseVO> create(@RequestBody OrderCreateRequestVO orderCreateRequest){
        return orderService.create(orderCreateRequest);
    }

    /**
     * @author 李道云
     * @methodName: settlementOrder
     * @methodDesc: 去结算
     * @description: 购物车去结算到订单确认页面
     * @param: [cartItemIds]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderSettlementResponseVO>
     * @create 2018-06-29 20:12
     **/
    @PostMapping("/settlementOrder")
    public Result<OrderSettlementResponseVO> settlementOrder(@RequestBody CartOperationRequestVO cartoPerationRequestVO) {

        return orderForUserAppService.settlementOrder(cartoPerationRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: saveOrderForPay
     * @methodDesc: 去支付(立即购买)
     * @description: 订单确认页去支付保存订单
     * @param: [orderSaveRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderToPayResponseVO>
     * @create 2018-06-29 20:51
     **/
    @PostMapping("/saveOrderForPay")
    public Result<OrderToPayResponseVO> saveOrderForPay(@RequestBody OrderSaveNowRequestVO orderSaveNowRequestVO){
        //return orderService.createOrderInfoNow(orderSaveNowRequestVO);
        return null;
    }


    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: confirmPayOrder
     * @methodDesc: 确认支付订单
     * @description: 多个订单编号逗号分隔，返回支付签名，由客户端发起支付
     * @param: [orderNumStr, pay_method]
     * @create 2018-06-29 21:34
     **/
    @PostMapping("/confirmPayOrder")
    public Result confirmPayOrder(@RequestBody ConfirmPayOrderRequestVO confirmPayOrderRequestVO) {

        return paymentService.confirmPayOrder(confirmPayOrderRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findWaitPayOrderList
     * @methodDesc: 查询待支付的订单列表
     * @description:
     * @param: [orderListRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderListResponseVO>
     * @create 2018-06-29 22:14
     **/
    @PostMapping("/findWaitPayOrderList")
    public Result<ListResponseVO<OrderListInfoVO>> findWaitPayOrderList(@RequestBody OrderListRequestVO orderListRequestVO){

        return orderForUserAppService.findWaitPayOrderList(orderListRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: selectOrderForPay
     * @methodDesc: 选择多个订单进行支付
     * @description: 多个订单id逗号分隔
     * @param: [orderNumStr]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderToPayResponseVO>
     * @create 2018-06-29 21:53
     **/
    @PostMapping("/selectOrderForPay")
    public Result<OrderToPayResponseVO> selectOrderForPay(@RequestBody OrderForPayRequestVO orderForPayRequestVO){

        return orderForUserAppService.selectOrderForPay(orderForPayRequestVO);
    }


    /**
     * @author 李道云
     * @methodName: findOrderListByOrderStatus
     * @methodDesc: 根据订单状态查询订单列表
     * @description:
     * @param: [userOrderListRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.order.OrderListInfoVO>>
     * @create 2018-06-29 22:16
     **/
    @PostMapping("/findOrderListByOrderStatus")
    public Result<PageResponseVO<OrderListInfoVO>> findOrderListByOrderStatus(@RequestBody OrderListRequestVO orderListRequestVO){

        return orderForUserAppService.findOrderListByOrderStatus(orderListRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findOrderDetail
     * @methodDesc: 查询订单详情
     * @description:
     * @param: [order_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderDetailResponseVO>
     * @create 2018-06-30 9:52
     **/
    @PostMapping("/findOrderDetail")
    public Result<OrderDetailResponseVO> findOrderDetail(@RequestBody OrderIdVO orderIdVO){

        return orderForUserAppService.findOrderDetail(orderIdVO);
    }

    /**
     * @author 李道云
     * @methodName: cancelOrder
     * @methodDesc: 取消订单
     * @description:
     * @param: [order_id]
     * @return com.hryj.common.Result
     * @create 2018-06-30 10:08
     **/
    @PostMapping("/cancelOrder")
    public Result cancelOrder(@RequestBody OrderIdVO orderIdVO){

        return orderService.cancelOrder(orderIdVO);
    }

    /**
     * @author 罗秋涵
     * @description: 获取退货原因列表
     * @param: []
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.ReturnReasonResponseVO>
     * @create 2018-07-03 16:41
     **/
    @PostMapping("/getReturnReason")
    public Result<ReturnReasonResponseVO> getReturnReason(){

        return orderService.getReturnReason();
    }


    /**
     * @author 李道云
     * @methodName: returnOrder
     * @methodDesc: 退货申请
     * @description:
     * @param: [order_id]
     * @return com.hryj.common.Result
     * @create 2018-06-30 10:08
     **/
    @PostMapping("/returnOrder")
    public Result returnOrder(@RequestBody  ReturnOrderRequestVO returnOrderRequestVO){

        return orderService.returnOrder(returnOrderRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: confirmReceive
     * @methodDesc: 确认收货
     * @description:
     * @param: [order_id]
     * @return com.hryj.common.Result
     * @create 2018-06-30 10:11
     **/
    @PostMapping("/confirmReceive")
    public Result confirmReceive(@RequestBody OrderIdVO orderIdVO){

        return orderForUserAppService.confirmReceive(orderIdVO);
    }

    /**
     * @author 罗秋涵
     * @description: 查询用户各状态订单数量
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderDifferentStateNumResponseVO>
     * @create 2018-07-03 16:55
     **/
    @PostMapping("/getOrderDifferentStateNum")
    public Result<OrderDifferentStateNumResponseVO> getOrderDifferentStateNum(@RequestBody  RequestVO requestVO){
        return orderForUserAppService.getOrderDifferentStateNum(requestVO);
    }

}
