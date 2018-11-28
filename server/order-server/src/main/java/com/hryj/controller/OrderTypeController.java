package com.hryj.controller;

import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.service.OrderTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 叶方宇
 * @className: OrderTypeController
 * @description:
 * @create 2018/9/19 0019 14:39
 **/
@Slf4j
@RestController
@RequestMapping(value = "/orderType")
public class OrderTypeController {

    @Autowired
    private OrderTypeService orderTypeService;

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 请求订单类型列表
     * @param:
     * @return
     * @create 2018-09-19 14:42
     **/
    @PostMapping("/getOrderTypeList")
    public Result getOrderTypeList(){
        return new Result(CodeEnum.SUCCESS,orderTypeService.getOrderTypeList());
    }
}
