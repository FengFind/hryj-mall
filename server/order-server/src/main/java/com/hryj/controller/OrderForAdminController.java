package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.order.AdminOrderInfoVO;
import com.hryj.entity.vo.order.request.AdminOrderListRequestVO;
import com.hryj.entity.vo.order.request.DeliveryForDistributionVO;
import com.hryj.entity.vo.order.request.OrderExpressVO;
import com.hryj.entity.vo.order.request.OrderIdVO;
import com.hryj.entity.vo.order.response.AdminOrderDetailResponseVO;
import com.hryj.service.OrderAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: OrderForAdminController
 * @description: 运营后台订单服务
 * @create 2018-06-29 22:32
 **/
@Slf4j
@RestController
@RequestMapping(value = "/adminOrder")
public class OrderForAdminController {


    @Autowired
    private OrderAdminService orderAdminService;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.AdminOrderInfoVO>
     * @author 李道云
     * @methodName: searchOrderList
     * @methodDesc: 分页查询订单列表
     * @description:
     * @param: [adminOrderListRequestVO]
     * @create 2018-06-30 16:41
     **/
    @PostMapping("/searchOrderList")
    public Result<PageResponseVO<AdminOrderInfoVO>> searchOrderList(@RequestBody AdminOrderListRequestVO adminOrderListRequestVO) {
        return orderAdminService.getAdminOrderInfoVOList(adminOrderListRequestVO);

    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: deliveryForExpress
     * @methodDesc: 发货录入快递信息
     * @description:
     * @param: [order_id, express_agency, express_code]
     * @create 2018-06-30 16:46
     **/
    @PostMapping("/deliveryForExpress")
    public Result deliveryForExpress(@RequestBody OrderExpressVO orderExpressVO) {
        return orderAdminService.insertDeliveryForExpress(orderExpressVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: deliveryForDistribution
     * @methodDesc: 发货分派配送员，调度（后台管理）
     * @description:
     * @param: [order_id, staff_id]
     * @create 2018-06-30 16:50
     **/
    @PostMapping("/deliveryForDistribution")
    public Result deliveryForDistribution(@RequestBody DeliveryForDistributionVO deliveryForDistributionVO) {
        return orderAdminService.deliveryForDistribution(deliveryForDistributionVO);
    }


    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 退货单分配处理人（后台管理）
     * @param: [deliveryForDistributionVO]
     * @create 2018-07-10 16:56
     **/
    @PostMapping("/returnForDistribution")
    public Result returnForDistribution(@RequestBody DeliveryForDistributionVO deliveryForDistributionVO) {
        return orderAdminService.returnForDistribution(deliveryForDistributionVO);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.AdminOrderDetailResponseVO>
     * @author 李道云
     * @methodName: getOrderDetail
     * @methodDesc: 查询订单详情
     * @description:
     * @param: [order_id]
     * @create 2018-06-30 17:16
     **/
    @PostMapping("/getOrderDetail")
    public Result<AdminOrderDetailResponseVO> getOrderDetail(@RequestBody OrderIdVO orderIdVO) {
        return orderAdminService.getAdminOrderDetailResponse(orderIdVO);
    }

}
