package com.hryj.feign;

import com.hryj.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李道云
 * @className: TaskFeignClient
 * @description: 定时任务feigin接口
 * @create 2018/7/19 22:59
 **/
@FeignClient(name = "task-server")
public interface TaskFeignClient {

    /**
     * @author 李道云
     * @methodName: calculateProfit
     * @methodDesc: 计算分润
     * @description:
     * @param: [summary_id,operator_id]
     * @return com.hryj.common.Result
     * @create 2018-07-19 23:01
     **/
    @RequestMapping(value = "/profit/calculateProfit", method = RequestMethod.POST)
    Result calculateProfit(@RequestParam("summary_id")Long summary_id, @RequestParam("operator_id") Long operator_id);

}
