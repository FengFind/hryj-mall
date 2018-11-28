package com.hryj.feign;

import com.hryj.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 白飞
 * @className: OrderDeclareFeignClient
 * @description:
 * @create 2018/9/26 16:17
 **/
@FeignClient(name = "declare-server")
public interface OrderDeclareFeignClient {

    @RequestMapping(value = "/order/declare/geteway", method = RequestMethod.POST)
    Result geteway(@RequestParam("data") String data);
}
