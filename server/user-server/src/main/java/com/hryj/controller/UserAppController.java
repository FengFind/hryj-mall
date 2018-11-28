package com.hryj.controller;

import com.hryj.cache.LoginCache;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.user.UserAddressVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.request.*;
import com.hryj.entity.vo.user.response.UserInfoResponseVO;
import com.hryj.entity.vo.user.response.UserLoginResponseVO;
import com.hryj.service.UserAddressService;
import com.hryj.service.UserOftenPartyService;
import com.hryj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 李道云
 * @className: UserController
 * @description: 用户模块APP接口
 * @create 2018/6/18 09:08
 **/
@Slf4j
@RestController
@RequestMapping(value = "/userApp")
public class UserAppController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserOftenPartyService userOftenPartyService;

    /**
     * @author 李道云
     * @description: 用户登录
     * @param: [userLoginRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserLoginResponseVO>
     * @create 2018-06-26 13:45
     **/
    @PostMapping("/loginBySmsCode")
    public Result<UserLoginResponseVO> loginBySmsCode(@RequestBody UserLoginRequestVO userLoginRequestVO){
        return userService.loginBySmsCode(userLoginRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: verifyRegisterUser
     * @methodDesc: 验证注册用户
     * @description:
     * @param: [userRegisterVerifyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-16 11:42
     **/
    @PostMapping("/verifyRegisterUser")
    public Result verifyRegisterUser(@RequestBody UserRegisterVerifyRequestVO userRegisterVerifyRequestVO){
        return userService.verifyRegisterUser(userRegisterVerifyRequestVO);
    }

    /**
     * @author 李道云
     * @description: 用户注册
     * @param: [userRegisterRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserLoginResponseVO>
     * @create 2018-06-26 10:43
     **/
    @PostMapping("/register")
    public Result<UserLoginResponseVO> register(@RequestBody UserRegisterRequestVO userRegisterRequestVO){
        return userService.register(userRegisterRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findUserInfoForApp
     * @methodDesc: 查询用户基本信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserInfoResponseVO>
     * @create 2018-08-29 9:47
     **/
    @PostMapping("/findUserInfo")
    public Result<UserInfoResponseVO> findUserInfo(@RequestBody RequestVO requestVO){
        return userService.findUserInfo(requestVO);
    }

    /**
     * @author 李道云
     * @methodName: findUserAddressList
     * @methodDesc: 查询用户地址列表
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserAddressVO>
     * @create 2018-07-02 10:26
     **/
    @PostMapping("/findUserAddressList")
    public Result<ListResponseVO<UserAddressVO>> findUserAddressList(@RequestBody RequestVO requestVO){
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(requestVO.getLogin_token());
        Long user_id = userLoginVO.getUser_id();
        return userAddressService.findUserAddressList(user_id);
    }

    /**
     * @author 李道云
     * @methodName: updateUserAddress
     * @methodDesc: 更新用户地址信息
     * @description:
     * @param: [userAddressVO]
     * @return com.hryj.common.Result
     * @create 2018-07-02 10:45
     **/
    @PostMapping("/updateUserAddress")
    public Result updateUserAddress(@RequestBody UserAddressVO userAddressVO){
        Result result = userAddressService.updateUserAddress(userAddressVO);
        if(result.isSuccess()){
            //更新地址后刷新用户的登录缓存
            userService.flushUserLoginVO(userAddressVO);
        }
        return result;
    }

    /**
     * @author 李道云
     * @methodName: setDefaultParty
     * @methodDesc: 设置默认门店或仓库
     * @description:
     * @param: [defaultPartyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-15 15:47
     **/
    @PostMapping("/setDefaultParty")
    public Result setDefaultParty(@RequestBody DefaultPartyRequestVO defaultPartyRequestVO){
        return userOftenPartyService.setDefaultParty(defaultPartyRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: setReferralCode
     * @methodDesc: 设置推荐码
     * @description:
     * @param: [userReferralCodeRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-29 8:55
     **/
    @PostMapping("/setReferralCode")
    public Result setReferralCode(@RequestBody UserReferralCodeRequestVO userReferralCodeRequestVO){
        return userService.setReferralCode(userReferralCodeRequestVO);
    }
}
