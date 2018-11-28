package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.entity.vo.user.request.DefaultPartyRequestVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 王光银
 * @className: PartyFeignClient
 * @description: 当事组织服务调用客户端
 * @create 2018/7/9 0009 22:40
 **/
@FeignClient(name = "user-server")
public interface UserFeignClient {

    /**
     * @author 王光银
     * @methodName: getUserServiceRange
     * @methodDesc: 根据用户ID取服务于用户的门店和仓库
     * @description:
     * @param: [user_id]
     * @return
     * @create 2018-07-17 18:51
     **/
    @RequestMapping(value = "/userInner/getUserServiceRangeByUserId", method = RequestMethod.POST)
    Result<UserServiceRangeVO> getUserServiceRangeByUserId(@RequestParam("user_id") Long user_id);

    /**
     * @author 王光银
     * @methodName: setDefaultParty
     * @methodDesc: 设置用户默认门店
     * @description:
     * @param:
     * @return
     * @create 2018-08-28 15:45
     **/
    @RequestMapping(value = "/userInner/setDefaultParty", method = RequestMethod.POST)
    Result setDefaultParty(@RequestBody DefaultPartyRequestVO defaultPartyRequestVO);
}
