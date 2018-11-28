package com.hryj.feign;

import com.hryj.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李道云
 * @className: PofitFeignClient
 * @description: 分润模块feign接口
 * @create 2018/8/29 10:03
 **/
@FeignClient(name = "profit-server")
public interface PofitFeignClient {

    /**
     * @author 李道云
     * @methodName: updateReferralStatisData
     * @methodDesc: 更新推荐用户的统计数据
     * @description:
     * @param: [statis_date, staff_id, store_id]
     * @return com.hryj.common.Result
     * @create 2018-08-29 10:03
     **/
    @RequestMapping(value = "/profitInner/updateReferralStatisData", method = RequestMethod.POST)
    Result updateReferralStatisData(@RequestParam(name = "statis_date") String statis_date,
                                    @RequestParam(name = "staff_id") Long staff_id,
                                    @RequestParam(name = "store_id") Long store_id);
}
