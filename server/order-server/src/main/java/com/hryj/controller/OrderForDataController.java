package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.order.UserTradeVO;
import com.hryj.entity.vo.profit.request.DataQueryRequestVO;
import com.hryj.service.OrderDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 李道云
 * @className: OrderForDataController
 * @description: 订单数据controller
 * @create 2018/7/17 11:41
 **/
@Slf4j
@RestController
@RequestMapping(value = "/dataOrder")
public class OrderForDataController {

    @Autowired
    private OrderDataService orderDataService;

    /**
     * @author 李道云
     * @methodName: statisTradeUserNum
     * @methodDesc: 统计交易用户数量
     * @description:
     * @param: [party_id, dept_path, start_date, end_date]
     * @return com.hryj.common.Result
     * @create 2018-08-07 11:48
     **/
    @PostMapping("/statisTradeUserNum")
    public Result statisTradeUserNum(@RequestParam(value = "party_id",required = false) Long party_id, @RequestParam(value = "dept_path",required = false) String dept_path,
                                     @RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date){
        return orderDataService.statisTradeUserNum(party_id,dept_path,start_date,end_date);
    }

    /**
     * @author 李道云
     * @methodName: findTradeUserList
     * @methodDesc: 查询交易用户列表
     * @description:
     * @param: [dataQueryRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.order.UserTradeVO>>
     * @create 2018-07-17 11:43
     **/
    @PostMapping("/findTradeUserList")
    public Result<ListResponseVO<UserTradeVO>> findTradeUserList(@RequestBody DataQueryRequestVO dataQueryRequestVO){
       return orderDataService.findTradeUserList(dataQueryRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findNewTradeUserList
     * @methodDesc: 查询新增交易用户列表
     * @description:
     * @param: [dataQueryRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.order.UserTradeVO>>
     * @create 2018-07-17 11:48
     **/
    @PostMapping("/findNewTradeUserList")
    public Result<ListResponseVO<UserTradeVO>> findNewTradeUserList(@RequestBody DataQueryRequestVO dataQueryRequestVO){
        return orderDataService.findNewTradeUserList(dataQueryRequestVO);
    }
}
