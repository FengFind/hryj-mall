package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.profit.request.DataQueryRequestVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.entity.vo.user.request.UserPhoneRequestVO;
import com.hryj.entity.vo.user.response.UserSearchResponseVO;
import com.hryj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: UserStoreController
 * @description: 用户模块门店端接口
 * @create 2018/8/29 10:39
 **/
@Slf4j
@RestController
@RequestMapping(value = "/userStore")
public class UserStoreController {

    @Autowired
    private UserService userService;

    /**
     * @author 李道云
     * @methodName: searchUserInfo
     * @methodDesc: 搜索用户信息
     * @description:
     * @param: [userPhoneRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserSearchResponseVO>
     * @create 2018-07-05 10:35
     **/
    @PostMapping("/searchUserInfo")
    public Result<UserSearchResponseVO> searchUserInfo(@RequestBody UserPhoneRequestVO userPhoneRequestVO){
        return userService.searchUserInfo(userPhoneRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findReferralRegisterUserList
     * @methodDesc: 查询推荐注册用户列表
     * @description:
     * @param: [dataQueryRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.user.UserInfoVO>>
     * @create 2018-07-07 17:32
     **/
    @PostMapping("/findReferralRegisterUserList")
    public Result<ListResponseVO<UserInfoVO>> findReferralRegisterUserList(@RequestBody DataQueryRequestVO dataQueryRequestVO){
        return userService.findReferralRegisterUserList(dataQueryRequestVO);
    }
}
