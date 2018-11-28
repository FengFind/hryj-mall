package com.hryj.feign;

import com.hryj.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * @author 代廷波
 * @className: 用户模块
 * @description: 
 * @create 2018/08/20 10:01
 **/
@FeignClient(name = "user-server")
public interface UserFeignClient {
    /**
     * @author 代廷波
     * @methodName: updateUserOftenPartyInfo
     * @methodDesc: 更新用户常用门店或仓库的基本信息
     * @description:
     * @param: [party_id, party_name, party_address]
     * @return com.hryj.common.Result
     * @create 2018-08-17 17:37
     **/
    @RequestMapping(value = "/userInner/updateUserOftenPartyInfo", method = RequestMethod.POST)
    Result updateUserOftenPartyInfo(@RequestParam("party_id") Long party_id,
                                    @RequestParam(value = "party_name",required = false) String party_name,
                                    @RequestParam(value = "party_address",required = false) String party_address);
}
