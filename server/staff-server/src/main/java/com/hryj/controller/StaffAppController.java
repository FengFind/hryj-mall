package com.hryj.controller;

import com.hryj.cache.LoginCache;
import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.staff.user.StaffUserVO;
import com.hryj.entity.vo.staff.user.request.StaffLoginRequestVO;
import com.hryj.entity.vo.staff.user.request.StaffModifyPwdRequestVO;
import com.hryj.entity.vo.staff.user.response.AppStaffLoginResponseVO;
import com.hryj.entity.vo.staff.user.response.StaffUserInfoVO;
import com.hryj.exception.GlobalException;
import com.hryj.service.StaffAppService;
import com.hryj.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 代廷波
 * @className: StaffAppController
 * @description:app 门店端服务
 * @create 2018/6/27 0027-22:12
 **/
@RestController
@RequestMapping("/staffApp")
public class StaffAppController {

    @Autowired
    private StaffAppService staffAppService;

    @Autowired
    private StaffService staffService;

    /**
     * @author 李道云
     * @description: 员工登陆
     * @param: staffLoginRequestVO
     * @return com.hryj.common.Result
     * @create 2018/06/27 22:20
     **/
    @PostMapping("/loginByPwd")
    public Result<AppStaffLoginResponseVO> loginByPwd(@RequestBody StaffLoginRequestVO staffLoginRequestVO) throws GlobalException {
        return staffAppService.loginByPwd(staffLoginRequestVO);
    }

    /**
     * @author 李道云
     * @description: 修改密码
     * @param: staffModifyPwdRequestVO
     * @return com.hryj.common.Result
     * @create 2018/06/27 22:21
     **/
    @PostMapping("/updateStaffLoginPwd")
    public Result<AppStaffLoginResponseVO> updateStaffLoginPwd(@RequestBody StaffModifyPwdRequestVO staffModifyPwdRequestVO) throws GlobalException {
        return staffAppService.updateStaffLoginPwd(staffModifyPwdRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findStoreStaffList
     * @methodDesc: 查询门店的员工列表
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.user.StaffUserVO>>
     * @create 2018-07-05 14:20
     **/
    @PostMapping("/findStoreStaffList")
    public Result<ListResponseVO<StaffUserVO>> findStoreStaffList(@RequestBody RequestVO requestVO){
        String login_token = requestVO.getLogin_token();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(login_token);
        DeptGroup deptGroup = staffAppLoginVO.getDeptGroup();
        return staffService.findStaffListByDeptId(deptGroup.getId());
    }

    /**
     * @author 李道云
     * @methodName: findStaffUserInfoVO
     * @methodDesc: 查询员工基本信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.StaffUserInfoVO>
     * @create 2018-07-16 16:30
     **/
    @PostMapping("/findStaffUserInfoVO")
    public Result<StaffUserInfoVO> findStaffUserInfoVO(@RequestBody RequestVO requestVO){
        return staffAppService.findStaffUserInfoVO(requestVO);
    }

    /**
     * @author 李道云
     * @methodName: flushStaffApploginVO
     * @methodDesc: 刷新员工app登录缓存
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-21 18:52
     **/
    @PostMapping("/flushStaffApploginVO")
    public Result flushStaffAppLoginVO(@RequestBody RequestVO requestVO){
        return staffAppService.flushStaffAppLoginVO(requestVO);
    }

}
