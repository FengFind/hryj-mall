package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.RequestVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 李道云
 * @className: UserFeignClient
 * @description: 用户feign接口
 * @create 2018/7/11 14:31
 **/
@FeignClient(name = "user-server")
public interface UserFeignClient {

    /**
     * @author 李道云
     * @methodName: flushUserLoginVO
     * @methodDesc: 刷新用户登录缓存信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-11 14:31
     **/
    @RequestMapping(value = "/userInner/flushUserLoginVO", method = RequestMethod.POST)
    Result flushUserLoginVO(@RequestBody RequestVO requestVO);

}
