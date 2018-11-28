package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.RequestVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 李道云
 * @className: StaffFeignClient
 * @description: 员工feign接口
 * @create 2018/7/21 18:50
 **/
@FeignClient(name = "staff-server")
public interface StaffFeignClient {

    /**
     * @author 李道云
     * @methodName: flushStaffApploginVO
     * @methodDesc: 刷新员工APP登录缓存信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-21 20:52
     **/
    @RequestMapping(value = "/staffApp/flushStaffApploginVO", method = RequestMethod.POST)
    Result flushStaffApploginVO(@RequestBody RequestVO requestVO);

}
