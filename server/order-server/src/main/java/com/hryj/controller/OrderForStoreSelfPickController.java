package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.order.request.ConfirmSelfPickRequestVO;
import com.hryj.entity.vo.order.request.SelfPickRequestVO;
import com.hryj.entity.vo.order.response.SelfPickResponseVO;
import com.hryj.service.OrderForStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 罗秋涵
 * @className: OrderForStoreSelfPickController
 * @description: 门店端自提管理
 * @create 2018/7/6 0006 9:27
 **/

@Slf4j
@RestController
@RequestMapping(value = "/storeOrderSelfPick")
public class OrderForStoreSelfPickController {

    @Autowired
    private OrderForStoreService orderForStoreService;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.SelfPickResponseVO>
     * @author 李道云
     * @methodName: findOrderSelfPick
     * @methodDesc: 根据自提码查询自提单信息
     * @description:
     * @param: [self_pick_code, phone_num]
     * @create 2018-06-30 15:31
     **/
    @PostMapping("/findOrderSelfPick")
    public Result<SelfPickResponseVO> findOrderSelfPick(@RequestBody SelfPickRequestVO selfPickRequestVO) {

        return orderForStoreService.findOrderSelfPick(selfPickRequestVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderSelfPickListResponseVO>
     * @author 罗秋涵
     * @description: 根据用户电话查询自提信息列表
     * @param: [phone_num]
     * @create 2018-07-03 17:22
     **/
    @PostMapping("/findOrderSelfPickListByPhoneNum")
    public Result<ListResponseVO<SelfPickResponseVO>> findOrderSelfPickListByPhoneNum(@RequestBody SelfPickRequestVO selfPickRequestVO) {

        return orderForStoreService.findOrderSelfPickListByPhoneNum(selfPickRequestVO);
    }


    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: confirmSelfPick
     * @methodDesc: 确认自提处理
     * @description:
     * @param: [self_pick_id]
     * @create 2018-06-30 15:25
     **/
    @PostMapping("/confirmSelfPick")
    public Result confirmSelfPick(@RequestBody ConfirmSelfPickRequestVO confirmSelfPickRequestVO) {

        return orderForStoreService.confirmSelfPick(confirmSelfPickRequestVO);
    }


}
