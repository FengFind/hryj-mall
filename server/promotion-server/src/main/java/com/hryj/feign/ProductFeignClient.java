package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.userparty.request.UserPartyRequestVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author 汪豪
 * @className: ProductFeignClient
 * @description:
 * @create 2018/8/16 0016 16:20
 **/
@FeignClient(name = "product-server")
public interface ProductFeignClient {

    /**
     * @author 汪豪
     * @methodName: getUserUniqueParty
     * @methodDesc: 返回用户关联的唯一门店ID
     * @description:
     * @param: [userPartyRequestVO]
     * @return com.hryj.common.Result<java.lang.Long>
     * @create 2018-08-16 15:30
     **/
    @RequestMapping(value = "/userParty/getUserUniqueParty",method = RequestMethod.POST)
    Result<Long> getUserUniqueParty(@RequestBody UserPartyRequestVO userPartyRequestVO);
}
