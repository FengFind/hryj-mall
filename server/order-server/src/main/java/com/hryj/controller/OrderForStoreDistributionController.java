package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.order.DistributionInfoVO;
import com.hryj.entity.vo.order.request.DistributionConfirmRequestVo;
import com.hryj.entity.vo.order.request.DistributionDetailRequestVO;
import com.hryj.entity.vo.order.request.DistributionRequestVO;
import com.hryj.entity.vo.order.response.DistributionDetailResponseVO;
import com.hryj.entity.vo.order.response.DistributionListReponseVO;
import com.hryj.entity.vo.order.response.WaitHandelOrderCountResponseVO;
import com.hryj.service.OrderForStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 罗秋涵
 * @className: OrderForStoreDistributionController
 * @description: 门店端配送/取货（店员）
 * @create 2018/7/6 0006 9:18
 **/
@Slf4j
@RestController
@RequestMapping(value = "/storeOrderDistribution")
public class OrderForStoreDistributionController {

    @Autowired
    private OrderForStoreService orderForStoreService;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.WaitHandelOrderCountResponseVO>
     * @author 李道云
     * @methodName: getWaitHandelOrderCount
     * @methodDesc: 获取待处理订单数量
     * @description:
     * @param: [requestVO]
     * @create 2018-06-30 10:36
     **/
    @PostMapping("/getWaitHandelOrderCount")
    public Result<WaitHandelOrderCountResponseVO> getWaitHandelOrderCount(@RequestBody RequestVO requestVO) {
        return orderForStoreService.distributionProcessingCount(requestVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionListReponseVO>
     * @author 李道云
     * @methodName: findDistributionList
     * @methodDesc: 查询配送单列表（待配送）
     * @description: distribution_status：配送状态:01-待配送,02-配送中,03-已配送
     * @param: [requestVO, distribution_status, start_date, end_date]
     * @create 2018-06-30 11:21
     **/
    @PostMapping("/findDistributionList")
    public Result<DistributionListReponseVO> findDistributionList(@RequestBody DistributionRequestVO distributionRequestVO) {
        return orderForStoreService.findDistributionList(distributionRequestVO);
    }

    /**
     * @author 罗秋涵
     * @description: 查询配送单列表（已配送，已超时，待取货，已取货）
     * @param: [distributionRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionResponseVO>
     * @create 2018-07-17 21:50
     **/
    @PostMapping("/findDistributionForStaff")
    public Result<PageResponseVO<DistributionInfoVO>> findDistributionForStaff(@RequestBody DistributionRequestVO distributionRequestVO){

        return orderForStoreService.findDistributionForStaff(distributionRequestVO);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionDetailResponseVO>
     * @author 李道云
     * @methodName: findDistributionDetail
     * @methodDesc: 查询配送单详情
     * @description:
     * @param: [distribution_id]
     * @create 2018-06-30 14:04
     **/
    @PostMapping("/findDistributionDetail")
    public Result<DistributionDetailResponseVO> findDistributionDetail(@RequestBody DistributionDetailRequestVO distributionDetailRequestVO) {
        return orderForStoreService.findDistributionDetail(distributionDetailRequestVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: confirmDistributionDetail
     * @methodDesc: 确认配送完成/取货完成
     * @param: [distribution_id]
     * @create 2018-06-30 14:06
     **/
    @PostMapping("/confirmDistributionDetail")
    public Result confirmDistributionDetail(@RequestBody DistributionConfirmRequestVo requestVO) {
        return orderForStoreService.confirmDistributionDetail(requestVO);
    }


}
