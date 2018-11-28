package com.hryj.controller;

import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.user.UserAddress;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.user.UserAddressVO;
import com.hryj.entity.vo.user.UserIdentityCardVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.entity.vo.user.request.DefaultPartyRequestVO;
import com.hryj.service.UserAddressService;
import com.hryj.service.UserOftenPartyService;
import com.hryj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 李道云saveUserIdentityCard
 * @className: UserInnerController
 * @description: 用户模块内部接口
 * @create 2018/8/29 10:28
 **/
@Slf4j
@RestController
@RequestMapping(value = "/userInner")
public class UserInnerController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserOftenPartyService userOftenPartyService;

    /**
     * @author 李道云
     * @methodName: findUserAddressListByUserId
     * @methodDesc: 根据用户ID查询用户地址列表
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserAddressVO>
     * @create 2018-08-21 14:45
     **/
    @PostMapping("/findUserAddressListByUserId")
    public Result<ListResponseVO<UserAddressVO>> findUserAddressListByUserId(@RequestParam(name="user_id") Long user_id){
        return userAddressService.findUserAddressList(user_id);
    }

    /**
     * @author 李道云
     * @methodName: findByAddressId
     * @methodDesc: 根据收货地址ID查询
     * @description:
     * @param: [address_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserAddress>
     * @create 2018-08-23 09:00
     **/
    @PostMapping("/findByAddressId")
    public Result<UserAddress> findByAddressId(@RequestParam(name="address_id") Long address_id){
        UserAddress userAddress = userAddressService.selectById(address_id);
        return new Result(CodeEnum.SUCCESS, userAddress);
    }

    /**
     * @author 李道云
     * @methodName: findUserInfoVOByUserId
     * @methodDesc: 查询用户基本信息
     * @description:
     * @param: [user_id, phone_num]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserInfoVO>
     * @create 2018-07-10 20:44
     **/
    @PostMapping("/findUserInfoVOByUserId")
    public Result<UserInfoVO> findUserInfoVOByUserId(@RequestParam(name="user_id",required = false) Long user_id,
                                                     @RequestParam(name="phone_num",required = false) String phone_num){
        return userService.findUserInfoVOByUserId(user_id,phone_num);
    }

    /**
     * @author 李道云
     * @methodName: getUserServiceRangeByUserId
     * @methodDesc: 根据用户id获取服务于用户的范围
     * @description:
     * @param: [user_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserServiceRangeVO>
     * @create 2018-07-17 19:00
     **/
    @PostMapping("/getUserServiceRangeByUserId")
    public Result<UserServiceRangeVO> getUserServiceRangeByUserId(@RequestParam(name="user_id") Long user_id){
        return userService.getUserServiceRangeByUserId(user_id);
    }

    /**
     * @author 李道云
     * @methodName: flushUserLoginVO
     * @methodDesc: 刷新用户登录缓存信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-11 14:30
     **/
    @PostMapping("/flushUserLoginVO")
    public Result flushUserLoginVO(@RequestBody RequestVO requestVO){
        return userService.flushUserLoginVO(requestVO);
    }

    /**
     * @author 李道云
     * @methodName: updateUserOftenPartyInfo
     * @methodDesc: 更新用户常用门店或仓库的基本信息
     * @description:
     * @param: [party_id, party_name, party_address]
     * @return com.hryj.common.Result
     * @create 2018-08-17 17:37
     **/
    @PostMapping("/updateUserOftenPartyInfo")
    public Result updateUserOftenPartyInfo(@RequestParam("party_id") Long party_id,
                                           @RequestParam(value = "party_name",required = false) String party_name,
                                           @RequestParam(value = "party_address",required = false) String party_address){
        return userOftenPartyService.updateUserOftenPartyInfo(party_id,party_name,party_address);
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
     * @author 罗秋涵
     * @description: 保存用户身份证信息 v1.2 新增
     * @param: [userIdentityCardVO]
     * @return com.hryj.common.Result
     * @create 2018-09-10 11:36
     **/
    @PostMapping("/saveUserIdentityCard")
    public Result saveUserIdentityCard(@RequestBody UserIdentityCardVO userIdentityCardVO){

        return userService.saveUserIdentityCard(userIdentityCardVO);
    }


    /**
     * @author 罗秋涵
     * @description: 获取用户默认身份证信息 v1.2 新增
     * @param: [userIdentityCardVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserIdentityCardVO>
     * @create 2018-09-10 10:51
     **/
    @PostMapping("/getUserDefaultIdentityCard")
    public Result<UserIdentityCardVO> getUserDefaultIdentityCard(@RequestParam("user_id") Long user_id){
        return userService.getUserDefaultIdentityCard(user_id);
    }

}
