package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.order.DistributionInfoVO;
import com.hryj.entity.vo.order.ReturnVO;
import com.hryj.entity.vo.order.request.DistributionForStoreRequestVO;
import com.hryj.entity.vo.order.request.ReturnRequestVO;
import com.hryj.entity.vo.order.request.SendOrdersRequestVO;
import com.hryj.entity.vo.order.response.DistributionListReponseVO;
import com.hryj.service.OrderForStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 罗秋涵
 * @className: OrderForStoreShipmentsController
 * @description: 门店端发货管理（店长）
 * @create 2018/7/6 0006 9:24
 **/
@Slf4j
@RestController
@RequestMapping(value = "/storeOrderShipments")
public class OrderForStoreShipmentsController {

    @Autowired
    private OrderForStoreService orderForStoreService;


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionListReponseVO>
     * @author 李道云
     * @methodName: findDistributionListForStore
     * @methodDesc: 查询待分配配送单列表(店长)
     * @description: distribution_type：配送类别:01-送货,02-取货；distribution_status：配送状态:01-待配送,02-配送中,03-已配送,04-取消配送
     * @param: [requestVO, distribution_type, distribution_status]
     * @create 2018-06-30 14:18
     **/
    @PostMapping("/findDistributionListForStore")
    public Result<DistributionListReponseVO> findDistributionListForStore(@RequestBody DistributionForStoreRequestVO distributionForStoreRequestVO) {

       return orderForStoreService.findDistributionListForStore(distributionForStoreRequestVO);
    }

    /**
     * @author 罗秋涵
     * @description: 查询已分配配送单列表(店长)
     * @param: [distributionForStoreRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionResponseVO>
     * @create 2018-07-18 16:38
     **/
    @PostMapping("/findAssignedDistributionList")
    public Result<PageResponseVO<DistributionInfoVO>> findAssignedDistributionList(@RequestBody DistributionForStoreRequestVO distributionForStoreRequestVO){

        return  orderForStoreService.findAssignedDistributionList(distributionForStoreRequestVO);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionListReponseVO>
     * @author 李道云
     * @methodName: findReturnListForStore
     * @methodDesc: 查询待分配退货单列表(店长)
     * @param: [requestVO, distribution_type, distribution_status]
     * @create 2018-06-30 14:18
     **/
    @PostMapping("/findReturnListForStore")
    public Result<PageResponseVO<ReturnVO>> findReturnListForStore(@RequestBody ReturnRequestVO returnRequestVO) {

       return orderForStoreService.findReturnListForStore(returnRequestVO);
    }



    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: assignDistribution
     * @methodDesc: 分派配送单（门店端）
     * @description:
     * @param: [requestVO, distribution_id, staff_id]
     * @create 2018-06-30 14:23
     **/
    @PostMapping("/assignDistribution")
    public Result assignDistribution(@RequestBody SendOrdersRequestVO sendOrdersRequestVO) {

        return orderForStoreService.assignDistribution(sendOrdersRequestVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: assignDistribution
     * @methodDesc: 分派退货处理人（门店端）
     * @description:
     * @param: [requestVO, distribution_id, staff_id]
     * @create 2018-06-30 14:23
     **/
    @PostMapping("/returnDistribution")
    public Result returnDistribution(@RequestBody SendOrdersRequestVO sendOrdersRequestVO) {

        return orderForStoreService.returnDistribution(sendOrdersRequestVO);
    }


}
