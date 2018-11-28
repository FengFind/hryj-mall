package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.entity.vo.user.request.UserListRequestVO;
import com.hryj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: UserAdminController
 * @description: 用户模块运营端接口
 * @create 2018/8/29 10:29
 **/
@Slf4j
@RestController
@RequestMapping(value = "/userAdmin")
public class UserAdminController {

    @Autowired
    private UserService userService;

    /**
     * @author 李道云
     * @methodName: searchUserList
     * @methodDesc: 分页查询用户列表
     * @description:
     * @param: [userListRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserListResponseVO>
     * @create 2018-06-27 22:13
     **/
    @PostMapping("/searchUserList")
    public Result<PageResponseVO<UserInfoVO>> searchUserList(@RequestBody UserListRequestVO userListRequestVO){
        return userService.searchUserList(userListRequestVO);
    }
}
