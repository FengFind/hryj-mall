package com.hryj.controller;

import com.alibaba.fastjson.JSON;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.cart.request.CartOperationRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartRequestVO;
import com.hryj.entity.vo.order.request.*;
import com.hryj.entity.vo.order.response.*;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.service.OrderForStoreService;
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
 * @author 罗秋涵
 * @className: OrderForStoreProxyController
 * @description: 门店端代下单管理
 * @create 2018/7/6 0006 9:29
 **/
@Slf4j
@RestController
@RequestMapping(value = "/storeOrderProxy")
public class OrderForStoreProxyController {

    @Autowired
    private OrderForStoreService orderForStoreService;

    @Autowired
    private OrderForUserAppService orderForUserAppService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

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
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderSettlementResponseVO>
     * @author 李道云
     * @methodName: settlementOrderForHelp
     * @methodDesc: 代下单去结算
     * @description: 购物车去结算到订单确认页面
     * @param: [user_id, cartItemIds]
     * @create 2018-06-30 14:52
     **/
    @PostMapping("/settlementOrder")
    public Result<OrderSettlementResponseVO> settlementOrder(@RequestBody CartOperationRequestVO cartoPerationRequestVO) {
        return orderForUserAppService.settlementOrder(cartoPerationRequestVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderToPayResponseVO>
     * @author 李道云
     * @methodName: saveHelpOrderInfo
     * @methodDesc: 购物车生成订单
     * @description: 购物车生成订单
     * @param: [orderSaveRequestVO]
     * @create 2018-06-29 20:51
     **/
    @PostMapping("/saveOrderForPay")
    public Result<OrderToPayResponseVO> saveOrderForPay(@RequestBody OrderSaveRequestVO orderSaveRequestVO) {
        log.info("购物车生成订单请求参数：" + JSON.toJSONString(orderSaveRequestVO));
        Result<OrderToPayResponseVO> result =  orderForStoreService.createOrderInfoDetails(orderSaveRequestVO);
        log.info("购物车生成订单返回值：" + JSON.toJSONString(result));
        return result;
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderListResponseVO>
     * @author 李道云
     * @methodName: findWaitPayOrderList
     * @methodDesc: 查询待支付的订单列表
     * @description:
     * @param: [orderListRequestVO]
     * @create 2018-06-29 22:14
     **/
    @PostMapping("/findWaitPayOrderList")
    public Result<ListResponseVO<HistoricalOrderListResponseVO>> findWaitPayOrderList(@RequestBody OrderListRequestVO orderListRequestVO) {

        return orderForStoreService.findWaitPayOrderList(orderListRequestVO);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderToPayResponseVO>
     * @author 李道云
     * @methodName: selectOrderForPay
     * @methodDesc: 选择多个订单进行支付
     * @description: 多个订单id逗号分隔
     * @param: [orderNumStr]
     * @create 2018-06-29 21:53
     **/
    @PostMapping("/selectOrderForPay")
    public Result<OrderToPayResponseVO> selectOrderForPay(@RequestBody OrderForPayRequestVO orderForPayRequestVO) {

        return orderForUserAppService.selectOrderForPay(orderForPayRequestVO);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO       <       com.hryj.entity.vo.order.OrderListInfoVO>>
     * @author 李道云
     * @methodName: findOrderListByOrderStatus
     * @methodDesc: 根据订单状态查询订单列表
     * @description:
     * @param: [userOrderListRequestVO]
     * @create 2018-06-29 22:16
     **/
    @PostMapping("/findOrderListByOrderStatus")
    public Result<PageResponseVO<HistoricalOrderListResponseVO>> findOrderListByOrderStatus(@RequestBody OrderListRequestVO orderListRequestVO) {

        return orderForStoreService.findOrderListByOrderStatus(orderListRequestVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderDetailResponseVO>
     * @author 李道云
     * @methodName: findOrderDetail
     * @methodDesc: 查询订单详情
     * @description:
     * @param: [order_id]
     * @create 2018-06-30 9:52
     **/
    @PostMapping("/findOrderDetail")
    public Result<OrderDetailResponseVO> findOrderDetail(@RequestBody OrderIdVO orderIdVO) {

        return orderForUserAppService.findOrderDetail(orderIdVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: cancelOrder
     * @methodDesc: 取消订单
     * @description:
     * @param: [order_id]
     * @create 2018-06-30 10:08
     **/
    @PostMapping("/cancelOrder")
    public Result cancelOrder(@RequestBody OrderIdVO orderIdVO) {

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
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: returnOrder
     * @methodDesc: 退货申请
     * @description:
     * @param: [order_id]
     * @create 2018-06-30 10:08
     **/
    @PostMapping("/returnOrder")
    public Result returnOrder(@RequestBody ReturnOrderRequestVO returnOrderRequestVO) {

        return orderService.returnOrder(returnOrderRequestVO);
    }


    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 判断员工是否有代下单访问权限
     * @param: [requestVO]
     * @create 2018-07-03 19:43
     **/
    @PostMapping("/getHelpOrderAccessRight")
    public Result getHelpOrderAccessRight(@RequestBody RequestVO requestVO) {
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(requestVO.getLogin_token());
        //部门类型:01-门店,02-仓库,03-普通部门
        if (CodeCache.getValueByKey("DeptType", "S01").equals(staffAppLoginVO.getDeptGroup().getDept_type())) {
            return new Result(CodeEnum.SUCCESS);
        }
        return new Result(CodeEnum.FAIL_UNAUTHORIZED);
    }

}
