package com.hryj.feign;

import com.hryj.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李道云
 * @className: OrderFeignClient
 * @description: 订单服务feign接口
 * @create 2018/8/6 19:48
 **/
@FeignClient(name = "order-server")
public interface OrderFeignClient {

    /**
     * @author 李道云
     * @methodName: statisTradeUserNum
     * @methodDesc: 统计交易用户数量
     * @description:
     * @param: [party_id, dept_path, start_date, end_date]
     * @return com.hryj.common.Result
     * @create 2018-08-06 19:59
     */
    @RequestMapping(value = "/dataOrder/statisTradeUserNum", method = RequestMethod.POST)
    Result statisTradeUserNum(@RequestParam(value = "party_id",required = false) Long party_id, @RequestParam(value = "dept_path",required = false) String dept_path,
                              @RequestParam("start_date") String start_date, @RequestParam("end_date") String end_date);

}
