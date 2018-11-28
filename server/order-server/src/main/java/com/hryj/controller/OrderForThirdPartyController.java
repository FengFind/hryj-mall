package com.hryj.controller;

import com.hryj.scheduled.OrderScheduled;
import com.hryj.service.OrderForThirdPartyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 罗秋涵
 * @className: OrderFroThirdPartyController
 * @description: 第三方订单管理
 * @create 2018/9/13 0013 15:23
 **/
@Slf4j
@RestController
@RequestMapping(value = "/orderForThirdParty")
public class OrderForThirdPartyController {

    @Autowired
    private OrderForThirdPartyService orderForThirdPartyService;

    @Autowired
    private OrderScheduled orderScheduled;

    /**
     * @author 罗秋涵
     * @description: 光彩订单同步
     * @param: []
     * @return void
     * @create 2018-09-19 14:59
     **/
    @PostMapping("/synchronizationOrder")
    public void synchronizationOrder(){
        orderForThirdPartyService.synchronizationOrder();
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 轮询光彩接口查询订单
     * @param:
     * @return
     * @create 2018-09-19 15:01
     **/
    @PostMapping("/findOrderForGCStatus")
    public void findOrderForGCStatus(){
        orderScheduled.findOrderForGCStatus();
    }



}
