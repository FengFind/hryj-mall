package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.order.ReturnOrderDetailsRequestVO;
import com.hryj.entity.vo.order.ReturnVO;
import com.hryj.entity.vo.order.request.DistributionOrderIdVO;
import com.hryj.entity.vo.order.request.ReturnOrderListRequestVO;
import com.hryj.entity.vo.order.response.ReturnOrderDetailsResponseVO;
import com.hryj.service.OrderAdminService;
import com.hryj.service.OrderForStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 罗秋涵
 * @className: OrderForStoreReturnController
 * @description: 门店端退货管理
 * @create 2018/7/6 0006 9:31
 **/
@Slf4j
@RestController
@RequestMapping(value = "/storeOrderReturn")
public class OrderForStoreReturnController {

    @Autowired
    private OrderForStoreService orderForStoreService;

    @Autowired
    private OrderAdminService orderAdminService;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.ReturnResponseVO>
     * @author 李道云
     * @methodName: findReturnList
     * @methodDesc: 查询退货单列表
     * @description:
     * @param: [requestVO]
     * @create 2018-06-30 15:59
     **/
    @PostMapping("/findReturnList")
    public Result<PageResponseVO<ReturnVO>> findReturnList(@RequestBody ReturnOrderListRequestVO returnOrderListRequestVO) {

        return orderForStoreService.findReturnListForStaff(returnOrderListRequestVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.ReturnResponseVO>
     * @author 罗秋涵
     * @methodName: findReturnList
     * @methodDesc: 获取退货详情（处理退货时用）
     * @description:
     * @param: [requestVO]
     * @create 2018-06-30 15:59
     **/
    @PostMapping("/getReturnOrderDetails")
    public Result<ReturnOrderDetailsResponseVO> getReturnOrderDetails(@RequestBody ReturnOrderDetailsRequestVO returnOrderDetailsRequestVO) {

        return orderForStoreService.getReturnOrderDetails(returnOrderDetailsRequestVO);
    }


    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: handelReturnOrder
     * @methodDesc: 处理退货单
     * @description:
     * @param: [requestVO, return_id, return_status, return_handel_remark]
     * @create 2018-06-30 16:01
     **/
    @PostMapping("/handelReturnOrder")
    public Result handelReturnOrder(@RequestBody DistributionOrderIdVO vo) {
        return orderAdminService.setReturnStatus(vo);
    }

}
