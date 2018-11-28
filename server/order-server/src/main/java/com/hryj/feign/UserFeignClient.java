package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.bo.user.UserAddress;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.user.UserAddressVO;
import com.hryj.entity.vo.user.UserIdentityCardVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author 罗秋涵
 * @className: UserFeignClient
 * @description:
 * @create 2018/7/6 0006 17:50
 **/
@FeignClient(name = "user-server")
public interface UserFeignClient {

    /**
     * @author 李道云
     * @methodName: findUserInfoVOByUserId
     * @methodDesc: 查询用户基本信息
     * @description:
     * @param: [user_id, phone_num]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserInfoVO>
     * @create 2018-07-10 20:44
     **/
    @RequestMapping(value = "/userInner/findUserInfoVOByUserId",method = RequestMethod.POST)
    Result<UserInfoVO> findUserInfoVOByUserId(@RequestParam(name="user_id",required = false) Long user_id,
                                              @RequestParam(name="phone_num",required = false) String phone_num);

    /**
     * @author 李道云
     * @methodName: getUserServiceRangeByUserId
     * @methodDesc: 根据用户ID取服务于用户的门店和仓库
     * @description:
     * @param: [user_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserServiceRangeVO>
     * @create 2018-08-08 10:50
     **/
    @RequestMapping(value = "/userInner/getUserServiceRangeByUserId", method = RequestMethod.POST)
    Result<UserServiceRangeVO> getUserServiceRangeByUserId(@RequestParam("user_id") Long user_id);

    /**
     * @author BF
     * @methodName: findUserAddressListByUserId
     * @methodDesc: 根据用户ID取服务于用户地址
     * @description:
     * @param: [user_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserAddressVO>
     * @create 2018-08-21 14:24
     **/
    @RequestMapping(value = "/userInner/findUserAddressListByUserId", method = RequestMethod.POST)
    Result<ListResponseVO<UserAddressVO>> findUserAddressListByUserId(@RequestParam(name="user_id") Long user_id);

    /**
     * @author BF
     * @methodName: findByAddressId
     * @methodDesc: 根据收货地址ID查询
     * @description:
     * @param: [address_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserAddress>
     * @create 2018-08-23 09:02
     **/
    @RequestMapping(value = "/userInner/findByAddressId", method = RequestMethod.POST)
    Result<UserAddress> findByAddressId(@RequestParam(name="address_id") Long address_id);

    /**
     * @author BF
     * @description: 保存用户身份证信息 v1.2 新增
     * @param: [userIdentityCardVO]
     * @return com.hryj.common.Result
     * @create 2018-09-10 11:36
     **/
    @PostMapping("/userInner/saveUserIdentityCard")
    Result saveUserIdentityCard(@RequestBody UserIdentityCardVO userIdentityCardVO);



    /**
     * @author BF
     * @description: 获取用户默认身份证信息 v1.2 新增
     * @param: [userIdentityCardVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserIdentityCardVO>
     * @create 2018-09-10 10:51
     **/
    @PostMapping("/userInner/getUserDefaultIdentityCard")
    Result<UserIdentityCardVO> getUserDefaultIdentityCard(@RequestParam("user_id") Long user_id);
}
