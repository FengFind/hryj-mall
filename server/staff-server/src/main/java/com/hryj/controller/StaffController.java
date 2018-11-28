package com.hryj.controller;

import com.hryj.cache.LoginCache;
import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import com.hryj.entity.vo.staff.user.StaffUserVO;
import com.hryj.entity.vo.staff.user.request.*;
import com.hryj.entity.vo.staff.user.response.*;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.exception.GlobalException;
import com.hryj.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author 代廷波
 * @className: StaffController
 * @description: 员工模块(后台)
 * @create 2018/6/26 0026-9:13
 **/
@RestController
@RequestMapping("/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 添加员工
     * @param: staffUserInfoRequestVO
     * @create 2018/06/26 9:36
     **/
    @PostMapping("/saveStaff")
    public Result<StaffAccountResponseVO> saveStaff(@RequestBody StaffUserInfoRequestVO staffUserInfoRequestVO) throws GlobalException {
        return staffService.saveStaff(staffUserInfoRequestVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改员工
     * @param: staffUserInfoRequestVO
     * @create 2018/06/26 9:36
     **/
    @PostMapping("/updateStaff")
    public Result updateStaff(@RequestBody StaffUserInfoRequestVO staffUserInfoRequestVO) throws GlobalException {
        return staffService.updateStaff(staffUserInfoRequestVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 根据员工id查询详情
     * @param: staffIdRequestVO
     * @create 2018/06/26 9:41
     **/
    @PostMapping("/getStaffByIdDet")
    public Result<StaffResponseVO> getStaffByIdDet(@RequestBody StaffIdRequestVO staffIdRequestVO) throws GlobalException {
        return staffService.getStaffByIdDet(staffIdRequestVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 查询员工列表
     * @param: staffListParamRequestVO
     * @create 2018/06/26 9:36
     **/
    @PostMapping("/getStaffList")
    public Result<PageResponseVO<StaffListResponseVO>> getStaffList(@RequestBody StaffListParamRequestVO staffListParamRequestVO) throws GlobalException {
        return staffService.getStaffList(staffListParamRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findStaffUserVOByStaffId
     * @methodDesc: 根据员工id查询员工基本信息
     * @description:
     * @param: [staff_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.StaffUserVO>
     * @create 2018-07-10 20:57
     **/
    @PostMapping("/findStaffUserVOByStaffId")
    public Result<StaffUserVO> findStaffUserVOByStaffId(@RequestParam("staff_id") Long staff_id){
        return staffService.findStaffUserVOByStaffId(staff_id);
    }

    /**
     * @author 李道云
     * @methodName: sendCodeForForgetLoginPwd
     * @methodDesc: 发送验证码(忘记密码)
     * @description:
     * @param: [staffAccountRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-05 16:19
     **/
    @PostMapping("/sendCodeForForgetLoginPwd")
    public Result sendCodeForForgetLoginPwd(@RequestBody StaffAccountRequestVO staffAccountRequestVO){
        return staffService.sendCodeForForgetLoginPwd(staffAccountRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: updateStaffLoginPwdBySms
     * @methodDesc: 短信验证修改登录密码
     * @description:
     * @param: [staffSmsModifyPwdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:01
     **/
    @PostMapping("/updateStaffLoginPwdBySms")
    public Result updateStaffLoginPwdBySms(@RequestBody StaffSmsModifyPwdRequestVO staffSmsModifyPwdRequestVO) throws GlobalException {
        return staffService.updateStaffLoginPwdBySms(staffSmsModifyPwdRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: loginByPwd
     * @methodDesc: 员工登录(后台)
     * @description:
     * @param: [staffLoginRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.AdminStaffLoginResponseVO>
     * @create 2018-07-05 16:44
     **/
    @PostMapping("/loginByPwd")
    public Result<AdminStaffLoginResponseVO> loginByPwd(@RequestBody StaffLoginRequestVO staffLoginRequestVO){
        return staffService.loginByPwd(staffLoginRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: updateStaffLoginPwd
     * @methodDesc: 修改登录密码
     * @description:
     * @param: [staffSmsModifyPwdRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 23:01
     **/
    @PostMapping("/updateStaffLoginPwd")
    public Result<AdminStaffLoginResponseVO> updateStaffLoginPwd(@RequestBody StaffModifyPwdRequestVO staffModifyPwdRequestVO) throws GlobalException {
        return staffService.updateStaffLoginPwd(staffModifyPwdRequestVO);
    }

    /**
     * @author 李道云
     * @methodName: findStaffDeptVO
     * @methodDesc: 查询员工部门信息
     * @description:
     * @param: [referral_code, phone_num, staff_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.StaffDeptVO>
     * @create 2018-07-06 14:00
     **/
    @PostMapping("/findStaffDeptVO")
    public Result<StaffDeptVO> findStaffDeptVO(@RequestParam(name="referral_code",required = false) String referral_code,
                                               @RequestParam(name="phone_num",required = false) String phone_num,
                                               @RequestParam(name="staff_id",required = false) Long staff_id){
        return staffService.findStaffDeptVO(referral_code,phone_num,staff_id);
    }

    /**
     * @author 李道云
     * @methodName: getUserServiceRange
     * @methodDesc: 根据位置获取服务于用户的门店和仓库
     * @description:
     * @param: [poi_id, city_code]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserServiceRangeVO>
     * @create 2018-07-06 14:05
     **/
    @PostMapping("/getUserServiceRange")
    public Result<UserServiceRangeVO> getUserServiceRange(@RequestParam("poi_id") String poi_id,
                                                          @RequestParam("city_code") String city_code){
        return staffService.getUserServiceRange(poi_id,city_code);
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
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(login_token);
        DeptGroup deptGroup = staffAdminLoginVO.getDeptGroup();
        return staffService.findStaffListByDeptId(deptGroup.getId());
    }

    /**
     * @author 李道云
     * @methodName: findStaffStoreWhVO
     * @methodDesc: 查询员工部门下所有门店和仓库
     * @description:
     * @param: [staff_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.StaffStoreWhVO>
     * @create 2018-07-25 9:29
     **/
    @PostMapping("/findStaffStoreWhVO")
    public Result<StaffStoreWhVO> findStaffStoreWhVO(@RequestParam("staff_id") Long staff_id){
        return staffService.findStaffStoreWhVO(staff_id);
    }

    /**
     * @author 李道云
     * @methodName: findStaffStoreWhVO
     * @methodDesc: 查询部门下所有门店和仓库
     * @description:
     * @param: [staff_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.StaffStoreWhVO>
     * @create 2018-07-25 9:29
     **/
    @PostMapping("/findStoreWhVOByDeptId")
    public Result<StaffStoreWhVO> findStoreWhVOByDeptId(@RequestParam("dept_id") Long dept_id){
        return staffService.findStoreWhVOByDeptId(dept_id);
    }

    @PostMapping(value = "/upload/excel",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<StaffUserUploadResponseVO>  uploadExcel(@RequestParam("login_token") String login_token, @RequestParam(name = "file") MultipartFile file)throws IOException {
        return staffService.uploadExcel(login_token,file);
    }




}
