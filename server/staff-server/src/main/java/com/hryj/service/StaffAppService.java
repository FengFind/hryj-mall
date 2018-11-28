package com.hryj.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.staff.user.StaffDeptRelation;
import com.hryj.entity.bo.staff.user.StaffUserInfo;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.staff.team.response.AppTeamDataClerkResponseVO;
import com.hryj.entity.vo.staff.team.response.AppTeamDataDeptResponseVO;
import com.hryj.entity.vo.staff.team.response.AppTeamDataResponseVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.staff.user.request.StaffLoginRequestVO;
import com.hryj.entity.vo.staff.user.request.StaffModifyPwdRequestVO;
import com.hryj.entity.vo.staff.user.response.AppStaffLoginResponseVO;
import com.hryj.entity.vo.staff.user.response.StaffUserInfoVO;
import com.hryj.mapper.DeptGroupMapper;
import com.hryj.mapper.StaffUserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 代廷波
 * @className: StaffAppService
 * @description: 员工service（门店端）
 * @create 2018/6/27 0027-22:14
 **/
@Slf4j
@Service
public class StaffAppService extends ServiceImpl<StaffUserInfoMapper,StaffUserInfo> {

    @Autowired
    private DeptGroupMapper deptGroupMapper;

    @Autowired
    private StaffDeptRelationService staffDeptRelationService;

    @Autowired
    private StaffLoginTokenService staffLoginTokenService;

    @Autowired
    private StaffLoginRecordService staffLoginRecordService;

    /**
     * @author 李道云
     * @methodName: getStaffUserInfoByAccount
     * @methodDesc: 根据员工账号获取员工信息
     * @description:
     * @param: [staff_account]
     * @return com.hryj.entity.bo.staff.user.StaffUserExcelVO
     * @create 2018-07-02 22:57
     **/
    public StaffUserInfo getStaffUserInfoByAccount(String staff_account){
        EntityWrapper<StaffUserInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("staff_account",staff_account);
        return super.selectOne(wrapper);
    }

    /**
     * @author 李道云
     * @methodName: loginByPwd
     * @methodDesc: 门店端登录
     * @description:
     * @param: [appStaffLoginRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.AppStaffLoginResponseVO>
     * @create 2018-07-02 22:34
     **/
    @Transactional
    public Result<AppStaffLoginResponseVO> loginByPwd(StaffLoginRequestVO staffLoginRequestVO) {
        log.info("门店端登录：staffLoginRequestVO=" + JSON.toJSONString(staffLoginRequestVO));
        //1、根据账号查询员工
        String staff_account = staffLoginRequestVO.getStaff_account();
        String login_pwd = staffLoginRequestVO.getLogin_pwd();
        if(StrUtil.isEmpty(staffLoginRequestVO.getStaff_account())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "登录账号不能为空");
        }
        if(StrUtil.isEmpty(staffLoginRequestVO.getLogin_pwd())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "登录密码不能为空");
        }
        StaffUserInfo staffUserInfo = this.getStaffUserInfoByAccount(staff_account);
        if(staffUserInfo ==null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"登录失败,账号不存在");
        }
        Long staff_id = staffUserInfo.getId();
        //2、判断登录密码是否正确
        StaffUserInfo updateStaffUser = new StaffUserInfo();
        if(!staffUserInfo.getLogin_pwd().equals(SecureUtil.md5(staffUserInfo.getStaff_account()+login_pwd))){
            updateStaffUser.setId(staff_id);
            updateStaffUser.setLogin_fail_num(staffUserInfo.getLogin_fail_num()+1);
            super.updateById(updateStaffUser);
            return new Result<>(CodeEnum.FAIL_BUSINESS,"登录失败,密码错误");
        }else{
            updateStaffUser.setId(staff_id);
            updateStaffUser.setLogin_fail_num(0);
            super.updateById(updateStaffUser);
        }
        //3、判断员工部门组织关系，已离职的员工不允许再登录
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staff_id);
        if(staffDeptRelation ==null || staffDeptRelation.getDept_id() ==null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"登录失败,账号还没有分配部门");
        }
        if(!staffDeptRelation.getStaff_status()){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"登录失败,账号处于离职状态");
        }
        Long dept_id = staffDeptRelation.getDept_id();
        DeptGroup deptGroup = deptGroupMapper.selectById(dept_id);
        String dept_type = deptGroup.getDept_type();
        String dept_name = deptGroup.getDept_name();
        String staff_type = staffUserInfo.getStaff_type();
        String staff_job = staffDeptRelation.getStaff_job();
        String staff_job_name = null;
        if(StrUtil.isNotEmpty(staff_job)){
            staff_job_name = CodeCache.getNameByValue("StaffJob",staff_job);
        }
        //4、创建登录token,并将登录信息缓存
        String login_token = SecureUtil.sha1(staffUserInfo.getStaff_account() + login_pwd + System.currentTimeMillis());
        StaffAppLoginVO staffAppLoginVO = new StaffAppLoginVO();
        staffAppLoginVO.setStaff_id(staff_id);
        staffAppLoginVO.setStaff_account(staffUserInfo.getStaff_account());
        staffAppLoginVO.setStaff_type(staff_type);
        staffAppLoginVO.setStaff_job(staff_job);
        staffAppLoginVO.setStaff_job_name(staff_job_name);
        staffAppLoginVO.setPhone_num(staffUserInfo.getPhone_num());
        staffAppLoginVO.setStaff_name(staffUserInfo.getStaff_name());
        staffAppLoginVO.setReferral_code(staffUserInfo.getReferral_code());
        staffAppLoginVO.setStaff_pic(staffUserInfo.getStaff_pic());
        staffAppLoginVO.setDeptGroup(deptGroup);
        log.info("门店端登录：staffAppLoginVO=" + JSON.toJSONString(staffAppLoginVO));
        LoginCache.setStaffAppLoginVO(login_token,staffAppLoginVO);
        //5、更新登录token
        staffLoginTokenService.updateStaffAppLoginToken(staffUserInfo.getId(),login_token,staffLoginRequestVO.getDevice_id());
        //6、保存员工登录记录
        staffLoginRecordService.saveStaffLoginRecord(staff_id,staffLoginRequestVO);
        //7、返回登录响应信息
        AppStaffLoginResponseVO appStaffLoginResponseVO = new AppStaffLoginResponseVO();
        appStaffLoginResponseVO.setLogin_token(login_token);
        appStaffLoginResponseVO.setStaff_account(staffUserInfo.getStaff_account());
        appStaffLoginResponseVO.setStaff_type(staff_type);
        appStaffLoginResponseVO.setStaff_job(staff_job);
        appStaffLoginResponseVO.setStaff_job_name(staff_job_name);
        appStaffLoginResponseVO.setPhone_num(staffUserInfo.getPhone_num());
        appStaffLoginResponseVO.setStaff_name(staffUserInfo.getStaff_name());
        appStaffLoginResponseVO.setReferral_code(staffUserInfo.getReferral_code());
        appStaffLoginResponseVO.setStaff_pic(staffUserInfo.getStaff_pic());
        appStaffLoginResponseVO.setDept_id(dept_id);
        appStaffLoginResponseVO.setDept_type(dept_type);
        appStaffLoginResponseVO.setDept_name(dept_name);
        log.info("门店端登录：appStaffLoginResponseVO=" + JSON.toJSONString(appStaffLoginResponseVO));
        return new Result(CodeEnum.SUCCESS, appStaffLoginResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: updateStaffLoginPwd
     * @methodDesc: 门店端修改密码
     * @description:
     * @param: [appStaffModifyPwdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.AppStaffLoginResponseVO>
     * @create 2018-07-02 22:35
     **/
    public Result<AppStaffLoginResponseVO> updateStaffLoginPwd(StaffModifyPwdRequestVO staffModifyPwdRequestVO) {
        log.info("门店端修改密码：staffModifyPwdRequestVO=" + JSON.toJSONString(staffModifyPwdRequestVO));
        String login_token = staffModifyPwdRequestVO.getLogin_token();
        //1、根据登录信息获取到员工对象
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(login_token);
        if(staffAppLoginVO ==null){
            return new Result<>(CodeEnum.FAIL_TOKEN_INVALID, "登录已失效,请重新登录");
        }
        //2、请求参数校验
        String old_login_pwd = staffModifyPwdRequestVO.getOld_login_pwd();
        String new_login_pwd = staffModifyPwdRequestVO.getNew_login_pwd();
        if(StrUtil.isEmpty(login_token)){
            return new Result<>(CodeEnum.FAIL_UNAUTHORIZED, "你没有登录,无权限访问");
        }
        if(StrUtil.isEmpty(old_login_pwd)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "旧密码不能为空");
        }
        if(StrUtil.isEmpty(new_login_pwd)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "新密码不能为空");
        }
        if(old_login_pwd.equals(new_login_pwd)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "新密码与旧密码不能相同");
        }
        Long staff_id = staffAppLoginVO.getStaff_id();
        StaffUserInfo staffUserInfo = super.selectById(staff_id);
        String staff_account = staffUserInfo.getStaff_account();
        //3、判断旧登录密码是否正确
        String old_login_pwd_md5 = SecureUtil.md5(staff_account+old_login_pwd);
        if(!old_login_pwd_md5.equals(staffUserInfo.getLogin_pwd())){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"原登录密码错误");
        }
        //4、更新登录密码
        String new_loagin_pwd = staffModifyPwdRequestVO.getNew_login_pwd();
        if(StrUtil.isEmpty(new_loagin_pwd)){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"新的登录密码不能为空");
        }
        staffUserInfo.setLogin_pwd(SecureUtil.md5(staff_account+new_login_pwd));
        staffUserInfo.setLogin_fail_num(0);
        super.updateById(staffUserInfo);
        //5、创建新的登录token
        String new_login_token = SecureUtil.sha1(staff_account + new_login_pwd + System.currentTimeMillis());
        LoginCache.setStaffAppLoginVO(new_login_token,staffAppLoginVO);
        log.info("门店端修改密码：staffAppLoginVO=" + JSON.toJSONString(staffAppLoginVO));
        //6、更新登录token
        staffLoginTokenService.updateStaffAppLoginToken(staff_id,new_login_token,staffModifyPwdRequestVO.getDevice_id());
        //7、返回登录响应信息
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staff_id);
        String staff_type = staffUserInfo.getStaff_type();
        String staff_job = null;
        String staff_job_name = null;
        Long dept_id = null;
        String dept_type = null;
        String dept_name = null;
        if(staffDeptRelation !=null){
            staff_job = staffDeptRelation.getStaff_job();
            if(StrUtil.isNotEmpty(staff_job)){
                staff_job_name = CodeCache.getNameByValue("StaffJob",staff_job);
            }
            dept_id = staffDeptRelation.getDept_id();
            if(dept_id !=null){
                DeptGroup deptGroup = deptGroupMapper.selectById(dept_id);
                dept_type = deptGroup.getDept_type();
                dept_name = deptGroup.getDept_name();
            }
        }
        AppStaffLoginResponseVO appStaffLoginResponseVO = new AppStaffLoginResponseVO();
        appStaffLoginResponseVO.setLogin_token(new_login_token);
        appStaffLoginResponseVO.setStaff_account(staff_account);
        appStaffLoginResponseVO.setStaff_type(staff_type);
        appStaffLoginResponseVO.setStaff_job(staff_job);
        appStaffLoginResponseVO.setStaff_job_name(staff_job_name);
        appStaffLoginResponseVO.setPhone_num(staffUserInfo.getPhone_num());
        appStaffLoginResponseVO.setStaff_name(staffUserInfo.getStaff_name());
        appStaffLoginResponseVO.setReferral_code(staffUserInfo.getReferral_code());
        appStaffLoginResponseVO.setStaff_pic(staffUserInfo.getStaff_pic());
        appStaffLoginResponseVO.setDept_id(dept_id);
        appStaffLoginResponseVO.setDept_type(dept_type);
        appStaffLoginResponseVO.setDept_name(dept_name);
        log.info("门店端修改密码：appStaffLoginResponseVO=" + JSON.toJSONString(appStaffLoginResponseVO));
        return new Result(CodeEnum.SUCCESS, appStaffLoginResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: flushStaffAppLoginVO
     * @methodDesc: 刷新员工app登录缓存
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-21 18:41
     **/
    public Result LoginCacheflushStaffAppLoginVO(RequestVO requestVO){
        String login_token = requestVO.getLogin_token();
        if (StrUtil.isNotEmpty(login_token)) {
            StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(login_token);
            if(staffAppLoginVO !=null){
                Long staff_id = staffAppLoginVO.getStaff_id();
                StaffUserInfo staffUserInfo = baseMapper.selectById(staff_id);
                StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staff_id);
                Long dept_id = staffDeptRelation.getDept_id();
                DeptGroup deptGroup = null;
                if(dept_id !=null){
                   deptGroup = deptGroupMapper.selectById(dept_id);
                }
                String staff_job = staffDeptRelation.getStaff_job();
                String staff_job_name = null;
                if(StrUtil.isNotEmpty(staff_job)){
                    staff_job_name = CodeCache.getNameByValue("StaffJob",staff_job);
                }
                StaffAppLoginVO new_staffAppLoginVO = new StaffAppLoginVO();
                new_staffAppLoginVO.setStaff_id(staffUserInfo.getId());
                new_staffAppLoginVO.setStaff_account(staffUserInfo.getStaff_account());
                new_staffAppLoginVO.setStaff_type(staffUserInfo.getStaff_type());
                new_staffAppLoginVO.setStaff_job(staff_job);
                new_staffAppLoginVO.setStaff_job_name(staff_job_name);
                new_staffAppLoginVO.setPhone_num(staffUserInfo.getPhone_num());
                new_staffAppLoginVO.setStaff_name(staffUserInfo.getStaff_name());
                new_staffAppLoginVO.setReferral_code(staffUserInfo.getReferral_code());
                new_staffAppLoginVO.setStaff_pic(staffUserInfo.getStaff_pic());
                new_staffAppLoginVO.setDeptGroup(deptGroup);
                log.info("刷新员工app登录缓存：new_staffAppLoginVO=" + JSON.toJSONString(new_staffAppLoginVO));
                LoginCache.setStaffAppLoginVO(login_token,new_staffAppLoginVO);
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @author 李道云
     * @methodName: findStaffUserInfoVO
     * @methodDesc: 查询员工基本信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.StaffUserInfoVO>
     * @create 2018-07-16 16:26
     **/
    public Result<StaffUserInfoVO> findStaffUserInfoVO(RequestVO requestVO){
        log.info("查询员工基本信息：requestVO=" + JSON.toJSONString(requestVO));
        String login_token = requestVO.getLogin_token();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(login_token);
        Long staff_id = staffAppLoginVO.getStaff_id();
        StaffUserInfo staffUserInfo = baseMapper.selectById(staff_id);
        String staff_type = staffUserInfo.getStaff_type();
        String staff_job = null;
        String staff_job_name = null;
        Long dept_id = null;
        String dept_type = null;
        String dept_name = null;
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staffUserInfo.getId());
        if(staffDeptRelation !=null){
            DeptGroup deptGroup = deptGroupMapper.selectById(staffDeptRelation.getDept_id());
            dept_id = deptGroup.getId();
            dept_type = deptGroup.getDept_type();
            dept_name = deptGroup.getDept_name();
            staff_job = staffDeptRelation.getStaff_job();
            if(StrUtil.isNotEmpty(staff_job)){
                staff_job_name = CodeCache.getNameByValue("StaffJob",staff_job);
            }
        }
        StaffUserInfoVO staffUserInfoVO = new StaffUserInfoVO();
        staffUserInfoVO.setStaff_account(staffUserInfo.getStaff_account());
        staffUserInfoVO.setStaff_type(staff_type);
        staffUserInfoVO.setStaff_job(staff_job);
        staffUserInfoVO.setStaff_job_name(staff_job_name);
        staffUserInfoVO.setPhone_num(staffUserInfo.getPhone_num());
        staffUserInfoVO.setStaff_name(staffUserInfo.getStaff_name());
        staffUserInfoVO.setReferral_code(staffUserInfo.getReferral_code());
        staffUserInfoVO.setStaff_pic(staffUserInfo.getStaff_pic());
        staffUserInfoVO.setDept_id(dept_id);
        staffUserInfoVO.setDept_type(dept_type);
        staffUserInfoVO.setDept_name(dept_name);
        log.info("查询员工基本信息：staffUserInfoVO=" + JSON.toJSONString(staffUserInfoVO));
        return new Result<>(CodeEnum.SUCCESS,staffUserInfoVO);
    }

    /**
     * @author 李道云
     * @methodName: getTeamData
     * @methodDesc: 获取团队数据
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.response.AppTeamDataResponseVO>
     * @create 2018-07-25 9:03
     **/
    public Result<AppTeamDataResponseVO> getTeamData(RequestVO requestVO){
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(requestVO.getLogin_token());
        DeptGroup deptGroup = staffAppLoginVO.getDeptGroup();
        String dept_path = deptGroup.getDept_path();
        //店员集合
        List<AppTeamDataClerkResponseVO> clerkRList = baseMapper.findStoreStaffUserList(dept_path);
        //部门集合
        List<AppTeamDataDeptResponseVO> deptList = baseMapper.findUnderDeptList(dept_path);
        //门店集合
        List<AppTeamDataDeptResponseVO> storeList = baseMapper.findUnderStoreList(dept_path);
        //仓库集合
        List<AppTeamDataDeptResponseVO> whList = baseMapper.findUnderWHList(dept_path);

        AppTeamDataResponseVO appTeamDataResponseVO = new AppTeamDataResponseVO();
        appTeamDataResponseVO.setClerkRList(clerkRList);
        appTeamDataResponseVO.setDeptList(deptList);
        appTeamDataResponseVO.setStoreList(storeList);
        appTeamDataResponseVO.setWhList(whList);
        log.info("获取团队数据:appTeamDataResponseVO=" + JSON.toJSONString(appTeamDataResponseVO));
        return new Result(CodeEnum.SUCCESS, appTeamDataResponseVO);
    }
}
