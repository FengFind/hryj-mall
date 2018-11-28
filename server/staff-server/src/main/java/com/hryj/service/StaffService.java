package com.hryj.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.cache.RedisService;
import com.hryj.cache.SmsCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.StaffConstants;
import com.hryj.constant.StaffEduEnmu;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.staff.role.RolePermRelation;
import com.hryj.entity.bo.staff.role.StaffPermRelation;
import com.hryj.entity.bo.staff.role.StaffRole;
import com.hryj.entity.bo.staff.role.StaffRoleRelation;
import com.hryj.entity.bo.staff.user.StaffDeptRelation;
import com.hryj.entity.bo.staff.user.StaffUserInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.role.request.ResourceRequestVO;
import com.hryj.entity.vo.staff.role.response.RolePermissionTreeItem;
import com.hryj.entity.vo.staff.role.response.RolePermissionTreeResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import com.hryj.entity.vo.staff.user.StaffUserVO;
import com.hryj.entity.vo.staff.user.request.*;
import com.hryj.entity.vo.staff.user.response.*;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.exception.GlobalException;
import com.hryj.mapper.DeptGroupMapper;
import com.hryj.mapper.PermResourceMapper;
import com.hryj.mapper.StaffUserInfoMapper;
import com.hryj.permission.Permission;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author 代廷波
 * @className: StaffService
 * @description: 员工service
 * @create 2018/6/26 0026-9:14
 **/
@Slf4j
@Service
public class StaffService extends ServiceImpl<StaffUserInfoMapper, StaffUserInfo> {

    @Autowired
    private StaffPermRelationService staffPermRelationService;

    @Autowired
    private StaffDeptRelationService staffDeptRelationService;

    @Autowired
    private DeptGroupMapper deptGroupMapper;

    @Autowired
    private PermResourceMapper permResourceMapper;

    @Autowired
    private StaffRoleRelationService staffRoleRelationService;

    @Autowired
    private StaffLoginTokenService staffLoginTokenService;

    @Autowired
    private StaffLoginRecordService staffLoginRecordService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private Environment env;

    @Autowired
    private RedisService redisService;

    @Autowired
    private StaffUserInfoMapper staffUserInfoMapper;

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 员工信息验证
     * @param: vo
     * @create 2018/06/26 15:36
     **/
    private String validatorStaff(StaffUserInfoRequestVO vo) {

        if (StrUtil.isEmpty(vo.getPhone_num())) {
            return "手机号码为空";
        } else {
            Boolean b = ReUtil.isMatch(StaffConstants.PHONE_REGEX, vo.getPhone_num());
            if (!b) {
                return "手机号格式不对";
            }

        }
        if (StrUtil.isEmpty(vo.getStaff_name())) {
            return "员工姓名为空";
        }
        if (StrUtil.isEmpty(vo.getId_card())) {
            return "身份证号码为空";
        } else {
            boolean valid = IdcardUtil.isValidCard(vo.getId_card());
            if (!valid) {
                return "身份证号码错误";
            }
        }
        if (StrUtil.isEmpty(vo.getHome_address())) {
            return "家庭地址为空";
        }
        if (StrUtil.isEmpty(vo.getContact_name())) {
            return "紧急联系人为空";
        }
        if (StrUtil.isEmpty(vo.getContact_tel())) {
            return "紧急联系电话为空";
        }
        if (StrUtil.isEmpty(vo.getStaff_type())) {
            return "员工类型为空";
        }
        return null;
    }

    /**
     * @return
     * @author 代廷波
     * @description: 验证手机号码是否已存在
     * @param: phoneNum 手机号码
     * @param: staff_id 员工id
     * @create 2018/07/16 16:02
     **/
    public boolean validatoPhoneNum(String phoneNum, Long staff_id) {
        EntityWrapper<StaffUserInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("phone_num", phoneNum);
        StaffUserInfo staffUserInfo = super.selectOne(wrapper);
        if (staffUserInfo != null && !staffUserInfo.getId().equals(staff_id)) {
            return true;
        }
        return false;
    }

    /**
     * @return boolean
     * @author 代廷波
     * @description: 验证身证号码是已存在
     * @param: Id_card 身证号码
     * @param: staff_id 员工id
     * @create 2018/07/23 9:51
     **/
    public boolean validatoIdCard(String id_card, Long staff_id) {
        EntityWrapper<StaffUserInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("id_card", id_card);
        StaffUserInfo staffUserInfo = super.selectOne(wrapper);
        if (staffUserInfo != null && !staffUserInfo.getId().equals(staff_id)) {
            return true;
        }
        return false;
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.StaffAccountResponseVO>
     * @author 代廷波
     * @methodName: saveStaff
     * @methodDesc: 保存员工信息
     * @description:
     * @param: [staffUserInfoRequestVO]
     * @create 2018-07-21 20:42
     **/
    @Transactional
    public Result<StaffAccountResponseVO> saveStaff(StaffUserInfoRequestVO staffUserInfoRequestVO) throws GlobalException {
        log.info("保存员工信息：staffUserInfoRequestVO=" + JSON.toJSONString(staffUserInfoRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(staffUserInfoRequestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = staffAdminLoginVO.getStaff_id();
        String validator = validatorStaff(staffUserInfoRequestVO);
        if (StrUtil.isNotEmpty(validator)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, validator);
        }
        //校验---手机号码是否为唯一
        if (validatoPhoneNum(staffUserInfoRequestVO.getPhone_num(), null)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "手机号码已存在");
        }
        //校验---身份证号码是否为唯一
        if (validatoIdCard(staffUserInfoRequestVO.getId_card(), null)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "身份证号码已存在");
        }

        StaffUserInfo staffUserInfo = new StaffUserInfo();
        BeanUtil.copyProperties(staffUserInfoRequestVO, staffUserInfo);//对象复制
        staffUserInfo.setOperator_id(operator_id);//操作人

        //生成员工账号
        String staff_account = this.createStaffAccount(staffUserInfoRequestVO.getId_card());
        if (StringUtils.isEmpty(staff_account)) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "员工账号生成失败");
        }

        staffUserInfo.setStaff_account(staff_account);

        //生成密码 =md5（员工账号 + 随机6位数字） 登陆密码校验方法要一样
        String login_pwd = RandomUtil.randomNumbers(6);
        //加密密码：md5(账号+密码)
        staffUserInfo.setLogin_pwd(SecureUtil.md5(staff_account + login_pwd));
        //生成推荐码
        int i = 0;
        String referral_code = this.createReferralCode(i);
        if (StringUtils.isEmpty(referral_code)) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "推荐码生成失败");
        }
        staffUserInfo.setReferral_code(referral_code);
        baseMapper.insert(staffUserInfo);
       /* if (null != staffUserInfoRequestVO.getResource_ids() && staffUserInfoRequestVO.getResource_ids().length > 0) {
            //保存员工对应的权限资源
            this.saveStaffPermRelation(staffUserInfo.getId(), staffUserInfoRequestVO.getResource_ids());
        }*/

        if (CollectionUtil.isNotEmpty(staffUserInfoRequestVO.getResourceList())) {
            //保存员工对应的权限资源
            this.saveStaffPermRelation(staffUserInfo.getId(), staffUserInfoRequestVO.getResourceList());
        }

        //保存员工角色
        if (null != staffUserInfoRequestVO.getRole_ids() && staffUserInfoRequestVO.getRole_ids().length > 0) {
            this.savaStaffRole(staffUserInfo.getId(), staffUserInfoRequestVO.getRole_ids());
        }
        StaffAccountResponseVO staffAccountResponseVO = new StaffAccountResponseVO();
        staffAccountResponseVO.setLogin_pwd(login_pwd);
        staffAccountResponseVO.setStaff_account(staff_account);
        log.info("保存员工信息：staffAccountResponseVO=" + JSON.toJSONString(staffAccountResponseVO));
        return new Result<>(CodeEnum.SUCCESS, staffAccountResponseVO);
    }


    /**
     * @return void
     * @author 代廷波
     * @description: 保存员工对应的资料权限
     * @param: staff_id 员工id
     * @param: add_resource_ids 资料权限id集合
     * @create 2018/06/26 14:14
     **/
    public void saveStaffPermRelation(Long staff_id, Long[] add_resource_ids) {
        List<StaffPermRelation> list = new ArrayList<>();
        StaffPermRelation spr = null;
        for (Long id : add_resource_ids) {
            spr = new StaffPermRelation();
            spr.setStaff_id(staff_id);
            spr.setPerm_id(id);
            list.add(spr);
        }
        staffPermRelationService.insertBatch(list);
    }

    public void saveStaffPermRelation(Long staff_id, List<ResourceRequestVO> resourcelist) {
        List<StaffPermRelation> list = new ArrayList<>();
        StaffPermRelation spr = null;

        List<Permission> permissionList = new ArrayList<>();
        Permission permission = null;
        if (null != resourcelist && resourcelist.size() > 0) {
            for (ResourceRequestVO resource : resourcelist) {
                //
                if (StaffConstants.GENERAL_TYPE.equals(resource.getPermission_type())) {
                    spr = new StaffPermRelation();
                    spr.setStaff_id(staff_id);
                    spr.setPerm_id(resource.getId());
                    list.add(spr);
                } else {
                    permission = new Permission();
                    permission.setPermission_id(resource.getId());
                    permission.setPermission_type(resource.getPermission_type());
                    permissionList.add(permission);
                }
            }
        }
        //数据权限
        PermissionManageHandler.saveStaffPermission(staff_id, permissionList);
        // PermissionManageHandler.saveRolePermission(role_id,permissionList);
        if (list.size() > 0) {
            staffPermRelationService.insertBatch(list);
        }

    }

    /**
     * @return void
     * @author 代廷波
     * @description:
     * @param: staff_id 员工id
     * @param: role_ids 角色集合
     * @create 2018/07/09 9:25
     **/
    public void savaStaffRole(Long staff_id, Long[] role_ids) {
        List<StaffRoleRelation> list = new ArrayList<>();
        StaffRoleRelation obj = null;
        for (Long id : role_ids) {
            obj = new StaffRoleRelation();
            obj.setStaff_id(staff_id);
            obj.setRole_id(id);
            list.add(obj);
        }
        staffRoleRelationService.insertBatch(list);

    }


    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改员工信息
     * @param: vo
     * @create 2018/06/26 15:35
     **/
    @Transactional
    public Result updateStaff(StaffUserInfoRequestVO staffUserInfoRequestVO) throws GlobalException {
        log.info("保存员工信息：staffUserInfoRequestVO=" + JSON.toJSONString(staffUserInfoRequestVO));
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(staffUserInfoRequestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long operator_id = staffAdminLoginVO.getStaff_id();
        //校验---基本信息
        String validator = validatorStaff(staffUserInfoRequestVO);
        if (StrUtil.isNotEmpty(validator)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, validator);
        }
        if (staffUserInfoRequestVO.getStaff_id() == null || "0".equals(staffUserInfoRequestVO.getStaff_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "员工id不能为空");
        }
        Long staff_id = staffUserInfoRequestVO.getStaff_id();
        //校验---手机号码是否为唯一
        if (validatoPhoneNum(staffUserInfoRequestVO.getPhone_num(), staff_id)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "手机号码已存在");
        }
        //校验---身份证号码是否为唯一
        if (validatoIdCard(staffUserInfoRequestVO.getId_card(), staff_id)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "身份证号码已存在");
        }
        StaffUserInfo staffUserInfo = new StaffUserInfo();
        BeanUtil.copyProperties(staffUserInfoRequestVO, staffUserInfo);//对象复制
        staffUserInfo.setId(staff_id);
        staffUserInfo.setOperator_id(operator_id);//操作人
        baseMapper.updateById(staffUserInfo);


        //删除员工对应的资源权限
        EntityWrapper<StaffPermRelation> permRelation = new EntityWrapper<StaffPermRelation>();
        permRelation.eq("staff_id", staff_id);
        staffPermRelationService.delete(permRelation);

        //删除员工对应的角色
        EntityWrapper<StaffRoleRelation> roleRelation = new EntityWrapper<StaffRoleRelation>();
        roleRelation.eq("staff_id", staff_id);
        staffRoleRelationService.delete(roleRelation);

        //保存员工角色
        /*if (null != staffUserInfoRequestVO.getResource_ids() && staffUserInfoRequestVO.getResource_ids().length > 0) {
            //保存员工对应的资源权限
            this.saveStaffPermRelation(staff_id, staffUserInfoRequestVO.getResource_ids());
        }*/
        //保存员工对应的权限资源
        this.saveStaffPermRelation(staffUserInfo.getId(), staffUserInfoRequestVO.getResourceList());

        if (null != staffUserInfoRequestVO.getRole_ids() && staffUserInfoRequestVO.getRole_ids().length > 0) {
            this.savaStaffRole(staff_id, staffUserInfoRequestVO.getRole_ids());
        }
        //清空token
        List<Long> staff_ids = new ArrayList<>();
        staff_ids.add(staff_id);
        staffLoginTokenService.updateClearStaffLoginTokenBatch(staff_ids);
        return new Result<>(CodeEnum.SUCCESS);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.StaffRepostVo>
     * @author 代廷波
     * @description: 根据id查询员工信息
     * @param: staff_id 员工id
     * @create 2018/06/26 15:35
     **/
    public Result<StaffResponseVO> getStaffByIdDet(StaffIdRequestVO staffIdRequestVO) {
        log.info("根据id查询员工信息：staffIdRequestVO=" + JSON.toJSONString(staffIdRequestVO));
        StaffResponseVO staffResponseVO = baseMapper.getStaffByIdDet(staffIdRequestVO);
        log.info("根据id查询员工信息：staffResponseVO=" + JSON.toJSONString(staffResponseVO));

        List<RolePermissionTreeItem> treeItems = new ArrayList<>();
        if (UtilValidate.isNotEmpty(staffResponseVO.getResourceRepostVoList())) {
            RolePermissionTreeItem rolePermissionTreeItem;
            for (StaffResourceResponseVO item : staffResponseVO.getResourceRepostVoList()) {
                rolePermissionTreeItem = new RolePermissionTreeItem();
                rolePermissionTreeItem.setKey(item.getResource_id());
                rolePermissionTreeItem.setTitle(item.getPerm_name());
                treeItems.add(rolePermissionTreeItem);
            }

        }
        Result<RolePermissionTreeResponseVO> result = roleService.getStaffPermissionTree(treeItems, staffIdRequestVO.getStaff_id());
        if (result.isSuccess()) {
            staffResponseVO.setRolePermissionTreeResponseVO(result.getData());
        }

        return new Result(CodeEnum.SUCCESS, staffResponseVO);
    }


    /**
     * @return com.hryj.common.PageResult<com.hryj.entity.vo.staff.user.response.StaffListRepostVo>
     * @author 代廷波
     * @description: 分页查询员工列表
     * @param: staffListParamRequestVO
     * @create 2018/06/26 15:36
     **/
    public Result<PageResponseVO<StaffListResponseVO>> getStaffList(StaffListParamRequestVO staffListParamRequestVO) {
        log.info("分页查询员工列表：staffListParamRequestVO=" + JSON.toJSONString(staffListParamRequestVO));
        PageResponseVO<StaffListResponseVO> pageResponseVO = new PageResponseVO<>();
        Page page = new Page(staffListParamRequestVO.getPage_num(), staffListParamRequestVO.getPage_size());
        List<StaffListResponseVO> staffListResponseVOList = baseMapper.getStaffList(staffListParamRequestVO, page);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        pageResponseVO.setRecords(staffListResponseVOList);
        log.info("分页查询员工列表：pageResponseVO=" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO                               <                               com.hryj.entity.vo.staff.user.StaffUserVO>>
     * @author 李道云
     * @methodName: findStaffListByDeptId
     * @methodDesc: 根据部门id查询员工列表
     * @description:
     * @param: [dept_id]
     * @create 2018-07-21 20:27
     **/
    public Result<ListResponseVO<StaffUserVO>> findStaffListByDeptId(Long dept_id) {
        log.info("根据部门id查询员工列表：dept_id=" + dept_id);
        List<StaffUserVO> staffUserList = baseMapper.findStaffListByDeptId(dept_id);
        log.info("根据部门id查询员工列表：staffUserList=" + JSON.toJSONString(staffUserList));
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(staffUserList));
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.StaffUserVO>
     * @author 李道云
     * @methodName: findStaffUserVOByStaffId
     * @methodDesc: 根据员工id查询员工基本信息
     * @description:
     * @param: [staff_id]
     * @create 2018-07-10 20:56
     **/
    public Result<StaffUserVO> findStaffUserVOByStaffId(Long staff_id) {
        log.info("根据员工id查询员工基本信息：staff_id=" + staff_id);
        StaffUserVO staffUserVO = baseMapper.findStaffUserVOByStaffId(staff_id);
        log.info("根据员工id查询员工基本信息：staffUserVO=" + JSON.toJSONString(staffUserVO));
        return new Result<>(CodeEnum.SUCCESS, staffUserVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: sendCodeForForgetLoginPwd
     * @methodDesc: 发送验证码(忘记密码)
     * @description:
     * @param: [staffAccountRequestVO]
     * @create 2018-07-05 16:19
     **/
    public Result sendCodeForForgetLoginPwd(StaffAccountRequestVO staffAccountRequestVO) {
        log.info("发送验证码(忘记密码)：staffAccountRequestVO=" + JSON.toJSONString(staffAccountRequestVO));
        String staff_account = staffAccountRequestVO.getStaff_account();
        if (StrUtil.isEmpty(staff_account)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "员工账号不能为空");
        }
        StaffUserInfo staffUserInfo = this.getStaffUserInfoByAccount(staff_account);
        if (staffUserInfo == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "发送失败,账号不存在");
        }
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staffUserInfo.getId());
        if (staffDeptRelation == null || staffDeptRelation.getDept_id() == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "发送失败,账号还没有分配部门");
        }
        if (!staffDeptRelation.getStaff_status()) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "发送失败,账号处于离职状态");
        }
        String phone_num = staffUserInfo.getPhone_num();
        Result result = SmsCache.sendVerifyCode(phone_num);
        return result;
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: updateStaffLoginPwdBySms
     * @methodDesc: 短信验证修改登录密码
     * @description:
     * @param: [staffSmsModifyPwdRequestVO]
     * @create 2018-07-05 16:36
     **/
    @Transactional
    public Result updateStaffLoginPwdBySms(StaffSmsModifyPwdRequestVO staffSmsModifyPwdRequestVO) throws GlobalException {
        log.info("短信验证修改登录密码：staffSmsModifyPwdRequestVO=" + JSON.toJSONString(staffSmsModifyPwdRequestVO));
        String staff_account = staffSmsModifyPwdRequestVO.getStaff_account();
        String verify_code = staffSmsModifyPwdRequestVO.getVerify_code();
        String new_login_pwd = staffSmsModifyPwdRequestVO.getNew_login_pwd();
        if (StrUtil.isEmpty(staff_account)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "登录账号不能为空");
        }
        if (StrUtil.isEmpty(verify_code)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "验证码不能为空");
        }
        if (StrUtil.isEmpty(new_login_pwd)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "新密码不能为空");
        }
        //1、验证短信
        StaffUserInfo staffUserInfo = this.getStaffUserInfoByAccount(staff_account);
        if (staffUserInfo == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "账号不存在");
        }
        String phone_num = staffUserInfo.getPhone_num();
        String envStr = env.getProperty("spring.profiles.active");
        if ("prod".equals(envStr)) {
            String noVerifyPhoneNum = CodeCache.getValueByKey("NoVerifyPhoneNum", "S01");//免验证的手机号码
            if (!(noVerifyPhoneNum.contains(phone_num) && "258369".equals(verify_code))) {
                boolean flag = SmsCache.verifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        } else {
            if (!"258369".equals(verify_code)) {
                boolean flag = SmsCache.verifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        }
        //2、更新登录密码
        if (StrUtil.isEmpty(new_login_pwd)) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "新的登录密码不能为空");
        }
        staffUserInfo.setLogin_pwd(SecureUtil.md5(staff_account + new_login_pwd));
        staffUserInfo.setLogin_fail_num(0);
        baseMapper.updateById(staffUserInfo);
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.AdminStaffLoginResponseVO>
     * @author 李道云
     * @methodName: loginByPwd
     * @methodDesc: 员工登录(后台)
     * @description:
     * @param: [staffLoginRequestVO]
     * @create 2018-07-05 17:01
     **/
    @Transactional
    public Result<AdminStaffLoginResponseVO> loginByPwd(StaffLoginRequestVO staffLoginRequestVO) {
        log.info("员工登录(后台)：staffLoginRequestVO=" + JSON.toJSONString(staffLoginRequestVO));
        String staff_account = staffLoginRequestVO.getStaff_account();
        String login_pwd = staffLoginRequestVO.getLogin_pwd();
        if (StrUtil.isEmpty(staffLoginRequestVO.getStaff_account())) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "登录账号不能为空");
        }
        if (StrUtil.isEmpty(staffLoginRequestVO.getLogin_pwd())) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "登录密码不能为空");
        }
        //1、根据账号查询员工
        StaffUserInfo staffUserInfo = this.getStaffUserInfoByAccount(staff_account);
        if (staffUserInfo == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "登录失败,账号不存在");
        }
        Long staff_id = staffUserInfo.getId();
        //2、判断登录密码是否正确
        StaffUserInfo updateStaffUser = new StaffUserInfo();
        if (!staffUserInfo.getLogin_pwd().equals(SecureUtil.md5(staffUserInfo.getStaff_account() + login_pwd))) {
            updateStaffUser.setId(staffUserInfo.getId());
            updateStaffUser.setLogin_fail_num(staffUserInfo.getLogin_fail_num() + 1);
            baseMapper.updateById(updateStaffUser);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "登录失败,密码错误");
        } else {
            updateStaffUser.setId(staffUserInfo.getId());
            updateStaffUser.setLogin_fail_num(0);
            baseMapper.updateById(updateStaffUser);
        }
        //3、判断员工部门组织关系，已离职的员工不允许再登录
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staff_id);
        if (staffDeptRelation == null || staffDeptRelation.getDept_id() == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "登录失败,账号还没有分配部门");
        }
        if (!staffDeptRelation.getStaff_status()) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "登录失败,账号处于离职状态");
        }
        String staff_type = staffUserInfo.getStaff_type();
        Long dept_id = staffDeptRelation.getDept_id();
        String dept_type = null;
        String dept_name = null;
        String staff_job = null;
        String staff_job_name = null;
        DeptGroup deptGroup = deptGroupMapper.selectById(dept_id);
        if (deptGroup != null) {
            dept_type = deptGroup.getDept_type();
            dept_name = deptGroup.getDept_name();
            staff_job = staffDeptRelation.getStaff_job();
            if (StrUtil.isNotEmpty(staff_job)) {
                staff_job_name = CodeCache.getNameByValue("StaffJob", staff_job);
            }
        }
        //4、获取当前登录员工的权限资源
        Map<String, Object> perm_map = null;
        List<Permission> permissionList = null;//数据权限
        if (staff_type.equals(CodeCache.getValueByKey("StaffType", "S03"))) {//03-超级管理员
            perm_map = permResourceMapper.findAllPermResource();
            permissionList = PermissionManageHandler.loadPermissionPool();
        } else {
            perm_map = baseMapper.findStaffPermResource(staff_id);
            permissionList = PermissionManageHandler.findStaffPermission(staff_id);
        }

        String permNameList = perm_map == null ? "" : (String) perm_map.get("permNameList");
        String permFlagList = perm_map == null ? "" : (String) perm_map.get("permFlagList");
        String permUrlList = perm_map == null ? "" : (String) perm_map.get("permUrlList");

        //加载所有数据权限资源

        if (CollectionUtil.isNotEmpty(permissionList)) {
            Set<String> dataFlagList = new HashSet<>();
            Set<String> dataNameList = new HashSet<>();
            for (Permission permission : permissionList) {
                if (UtilValidate.isEmpty(permission.getChildren())) {
                    continue;
                }
                for (Permission item : permission.getChildren()) {
                    dataFlagList.add(item.getPermission_type_id());
                    dataNameList.add(item.getDescription());
                }
            }

            permNameList += ("," + CollUtil.join(dataNameList.iterator(), ","));
            permFlagList += ("," + CollUtil.join(dataFlagList.iterator(), ","));
        }


        //5、获取当前登录员工的门店或仓库id集合
        String storeIdList = "";
        String whIdList = "";
        Map<String, Object> storeId_map = null;
        Map<String, Object> whId_map = null;
        String dept_type_store = CodeCache.getValueByKey("DeptType", "S01");//门店
        String dept_type_wh = CodeCache.getValueByKey("DeptType", "S02");//仓库
        if (staff_type.equals(CodeCache.getValueByKey("StaffType", "S03"))) {//03-超级管理员
            storeId_map = deptGroupMapper.findAllPartyIdList(dept_type_store);
            whId_map = deptGroupMapper.findAllPartyIdList(dept_type_wh);
        } else {
            storeId_map = deptGroupMapper.findChildPartyIdList(dept_type_store, deptGroup.getDept_path());
            whId_map = deptGroupMapper.findChildPartyIdList(dept_type_wh, deptGroup.getDept_path());
        }
        if (storeId_map != null) {
            storeIdList = (String) storeId_map.get("deptIdList");
        }
        if (whId_map != null) {
            whIdList = (String) whId_map.get("deptIdList");
        }
        //6、创建登录token,并将登录信息缓存
        String login_token = SecureUtil.sha1(staff_account + login_pwd + System.currentTimeMillis());
        StaffAdminLoginVO staffAdminLoginVO = new StaffAdminLoginVO();
        staffAdminLoginVO.setStaff_id(staff_id);
        staffAdminLoginVO.setStaff_account(staff_account);
        staffAdminLoginVO.setStaff_type(staff_type);
        staffAdminLoginVO.setStaff_job(staff_job);
        staffAdminLoginVO.setStaff_job_name(staff_job_name);
        staffAdminLoginVO.setPhone_num(staffUserInfo.getPhone_num());
        staffAdminLoginVO.setStaff_name(staffUserInfo.getStaff_name());
        staffAdminLoginVO.setReferral_code(staffUserInfo.getReferral_code());
        staffAdminLoginVO.setStaff_pic(staffUserInfo.getStaff_pic());
        staffAdminLoginVO.setDeptGroup(deptGroup);
        staffAdminLoginVO.setPermNameList(permNameList);
        staffAdminLoginVO.setPermFlagList(permFlagList);
        staffAdminLoginVO.setPermUrlList(permUrlList);
        staffAdminLoginVO.setStoreIdList(storeIdList);
        staffAdminLoginVO.setWhIdList(whIdList);
        log.info("员工登录(后台)：staffAdminLoginVO=" + JSON.toJSONString(staffAdminLoginVO));
        LoginCache.setStaffAdminLoginVO(login_token, staffAdminLoginVO);
        //7、更新登录token
        staffLoginTokenService.updateStaffAdminLoginToken(staffUserInfo.getId(), login_token);
        //8、保存员工登录记录
        staffLoginRecordService.saveStaffLoginRecord(staff_id, staffLoginRequestVO);
        //9、返回登录响应信息
        AdminStaffLoginResponseVO adminStaffLoginResponseVO = new AdminStaffLoginResponseVO();
        adminStaffLoginResponseVO.setLogin_token(login_token);
        adminStaffLoginResponseVO.setStaff_account(staffUserInfo.getStaff_account());
        adminStaffLoginResponseVO.setStaff_type(staff_type);
        adminStaffLoginResponseVO.setStaff_job(staff_job);
        adminStaffLoginResponseVO.setStaff_job_name(staff_job_name);
        adminStaffLoginResponseVO.setPhone_num(staffUserInfo.getPhone_num());
        adminStaffLoginResponseVO.setStaff_name(staffUserInfo.getStaff_name());
        adminStaffLoginResponseVO.setStaff_pic(staffUserInfo.getStaff_pic());
        adminStaffLoginResponseVO.setDept_id(dept_id);
        adminStaffLoginResponseVO.setDept_type(dept_type);
        adminStaffLoginResponseVO.setDept_name(dept_name);
        adminStaffLoginResponseVO.setPermNameList(permNameList);
        adminStaffLoginResponseVO.setPermFlagList(permFlagList);
        adminStaffLoginResponseVO.setPermUrlList(permUrlList);
        log.info("员工登录(后台)：adminStaffLoginResponseVO=" + JSON.toJSONString(adminStaffLoginResponseVO));
        return new Result(CodeEnum.SUCCESS, adminStaffLoginResponseVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: updateStaffLoginPwd
     * @methodDesc: 修改登录密码
     * @description:
     * @param: com.hryj.common.Result<com.hryj.entity.vo.staff.user.response.AdminStaffLoginResponseVO>
     * @create 2018-07-05 16:36
     **/
    @Transactional
    public Result<AdminStaffLoginResponseVO> updateStaffLoginPwd(StaffModifyPwdRequestVO staffModifyPwdRequestVO) throws GlobalException {
        log.info("修改登录密码：staffModifyPwdRequestVO=" + JSON.toJSONString(staffModifyPwdRequestVO));
        String login_token = staffModifyPwdRequestVO.getLogin_token();
        //1、根据登录信息获取到员工对象
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(login_token);
        if (staffAdminLoginVO == null) {
            return new Result<>(CodeEnum.FAIL_TOKEN_INVALID, "登录已失效,请重新登录");
        }
        //2、请求参数校验
        String old_login_pwd = staffModifyPwdRequestVO.getOld_login_pwd();
        String new_login_pwd = staffModifyPwdRequestVO.getNew_login_pwd();
        if (StrUtil.isEmpty(login_token)) {
            return new Result<>(CodeEnum.FAIL_UNAUTHORIZED, "你没有登录,无权限访问");
        }
        if (StrUtil.isEmpty(old_login_pwd)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "旧密码不能为空");
        }
        if (StrUtil.isEmpty(new_login_pwd)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "新密码不能为空");
        }
        if (old_login_pwd.equals(new_login_pwd)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "新密码与旧密码不能相同");
        }
        Long staff_id = staffAdminLoginVO.getStaff_id();
        StaffUserInfo staffUserInfo = baseMapper.selectById(staff_id);
        //3、判断旧登录密码是否正确
        String old_login_pwd_md5 = SecureUtil.md5(staffUserInfo.getStaff_account() + old_login_pwd);
        if (!old_login_pwd_md5.equals(staffUserInfo.getLogin_pwd())) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "原登录密码错误");
        }
        //4、更新登录密码
        staffUserInfo.setLogin_pwd(SecureUtil.md5(staffUserInfo.getStaff_account() + new_login_pwd));
        staffUserInfo.setLogin_fail_num(0);
        baseMapper.updateById(staffUserInfo);
        //5、创建新的登录token,并将登录信息缓存
        String new_login_token = SecureUtil.sha1(staffUserInfo.getStaff_account() + new_login_pwd + System.currentTimeMillis());
        log.info("修改登录密码：staffAdminLoginVO=" + JSON.toJSONString(staffAdminLoginVO));
        LoginCache.setStaffAdminLoginVO(new_login_token, staffAdminLoginVO);
        //6、更新登录token
        staffLoginTokenService.updateStaffAdminLoginToken(staffUserInfo.getId(), new_login_token);
        //7、返回登录响应信息
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staffUserInfo.getId());
        DeptGroup deptGroup = null;
        String staff_type = staffUserInfo.getStaff_type();
        String staff_job = null;
        String staff_job_name = null;
        Long dept_id = null;
        String dept_type = null;
        String dept_name = null;
        if (staffDeptRelation != null) {
            staff_job = staffDeptRelation.getStaff_job();
            if (StrUtil.isNotEmpty(staff_job)) {
                staff_job_name = CodeCache.getNameByValue("StaffJob", staff_job);
            }
            dept_id = staffDeptRelation.getDept_id();
            if (dept_id != null) {
                deptGroup = deptGroupMapper.selectById(dept_id);
                dept_type = deptGroup.getDept_type();
                dept_name = deptGroup.getDept_name();
            }
        }
        AdminStaffLoginResponseVO adminStaffLoginResponseVO = new AdminStaffLoginResponseVO();
        adminStaffLoginResponseVO.setLogin_token(new_login_token);
        adminStaffLoginResponseVO.setStaff_account(staffUserInfo.getStaff_account());
        adminStaffLoginResponseVO.setStaff_type(staff_type);
        adminStaffLoginResponseVO.setStaff_job(staff_job);
        adminStaffLoginResponseVO.setStaff_job_name(staff_job_name);
        adminStaffLoginResponseVO.setPhone_num(staffUserInfo.getPhone_num());
        adminStaffLoginResponseVO.setStaff_name(staffUserInfo.getStaff_name());
        adminStaffLoginResponseVO.setStaff_pic(staffUserInfo.getStaff_pic());
        adminStaffLoginResponseVO.setDept_id(dept_id);
        adminStaffLoginResponseVO.setDept_type(dept_type);
        adminStaffLoginResponseVO.setDept_name(dept_name);
        adminStaffLoginResponseVO.setPermNameList(staffAdminLoginVO.getPermNameList());
        adminStaffLoginResponseVO.setPermFlagList(staffAdminLoginVO.getPermFlagList());
        adminStaffLoginResponseVO.setPermUrlList(staffAdminLoginVO.getPermUrlList());
        log.info("修改登录密码：adminStaffLoginResponseVO=" + JSON.toJSONString(adminStaffLoginResponseVO));
        return new Result(CodeEnum.SUCCESS, adminStaffLoginResponseVO);
    }

    /**
     * @return com.hryj.entity.bo.staff.user.StaffUserExcelVO
     * @author 李道云
     * @methodName: getStaffUserInfoByAccount
     * @methodDesc: 根据员工账号获取员工信息
     * @description:
     * @param: [staff_account]
     * @create 2018-07-05 16:11
     **/
    public StaffUserInfo getStaffUserInfoByAccount(String staff_account) {
        EntityWrapper<StaffUserInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("staff_account", staff_account);
        return super.selectOne(wrapper);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.StaffDeptVO>
     * @author 李道云
     * @methodName: findStaffDeptVO
     * @methodDesc: 查询员工部门基本信息
     * @description:
     * @param: [referral_code, phone_num, staff_id]
     * @create 2018-07-06 12:30
     **/
    public Result<StaffDeptVO> findStaffDeptVO(String referral_code, String phone_num, Long staff_id) {
        if (StrUtil.isEmpty(referral_code) && StrUtil.isEmpty(phone_num) && staff_id == null) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "查询参数不能都为空");
        }
        StaffDeptVO staffDeptVO = baseMapper.findStaffDeptVO(referral_code, phone_num, staff_id);
        log.info("查询员工部门基本信息：staffUserVO=" + JSON.toJSONString(staffDeptVO));
        return new Result<>(CodeEnum.SUCCESS, staffDeptVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserServiceRangeVO>
     * @author 李道云
     * @methodName: getUserServiceRange
     * @methodDesc: 根据位置获取服务于用户的门店和仓库
     * @description:
     * @param: [poi_id, city_code]
     * @create 2018-07-06 14:39
     **/
    public Result<UserServiceRangeVO> getUserServiceRange(String poi_id, String city_code) {
        log.info("根据位置获取服务于用户的门店和仓库：poi_id={}, city_code={}", poi_id, city_code);
        List<UserPartyVO> storeList = baseMapper.findStoreListByPoiId(poi_id);
        UserPartyVO warehouse = baseMapper.findWarehouseByCityCode(city_code);
        UserServiceRangeVO userServiceRangeVO = new UserServiceRangeVO();
        userServiceRangeVO.setStoreList(storeList);
        userServiceRangeVO.setWarehouse(warehouse);
        log.info("根据位置获取服务于用户的门店和仓库：userServiceRangeVO=" + JSON.toJSONString(userServiceRangeVO));
        return new Result<>(CodeEnum.SUCCESS, userServiceRangeVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.StaffStoreWhVO>
     * @author 李道云
     * @methodName: findStaffStoreWhVO
     * @methodDesc: 查询员工部门下所有门店和仓库
     * @description:
     * @param: [staff_id]
     * @create 2018-07-25 9:27
     **/
    public Result<StaffStoreWhVO> findStaffStoreWhVO(Long staff_id) {
        if (staff_id == null) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "员工id不能为空");
        }
        StaffUserInfo staffUserInfo = baseMapper.selectById(staff_id);
        if (staffUserInfo == null) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "员工信息不存在");
        }
        StaffDeptRelation staffDeptRelation = staffDeptRelationService.getStaffDeptRelation(staff_id);
        String storeIdList = "";
        String whIdList = "";
        if (staffDeptRelation != null) {
            Long dept_id = staffDeptRelation.getDept_id();
            if (dept_id != null) {
                DeptGroup deptGroup = deptGroupMapper.selectById(dept_id);
                Map<String, Object> storeId_map = null;
                Map<String, Object> whId_map = null;
                String dept_type_store = CodeCache.getValueByKey("DeptType", "S01");//门店
                String dept_type_wh = CodeCache.getValueByKey("DeptType", "S02");//仓库
                if (staffUserInfo.getId().equals(CodeCache.getValueByKey("StaffType", "S03"))) {//03-超级管理员
                    storeId_map = deptGroupMapper.findAllPartyIdList(dept_type_store);
                    whId_map = deptGroupMapper.findAllPartyIdList(dept_type_wh);
                } else {
                    storeId_map = deptGroupMapper.findChildPartyIdList(dept_type_store, deptGroup.getDept_path());
                    whId_map = deptGroupMapper.findChildPartyIdList(dept_type_wh, deptGroup.getDept_path());
                }
                if (storeId_map != null) {
                    storeIdList = (String) storeId_map.get("deptIdList");
                }
                if (whId_map != null) {
                    whIdList = (String) whId_map.get("deptIdList");
                }
            }
        }
        StaffStoreWhVO staffStoreWhVO = new StaffStoreWhVO();
        staffStoreWhVO.setStoreIdList(storeIdList);
        staffStoreWhVO.setWhIdList(whIdList);
        log.info("查询员工部门下所有门店和仓库:staffStoreWhVO=" + JSON.toJSONString(staffStoreWhVO));
        return new Result<>(CodeEnum.SUCCESS, staffStoreWhVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.StaffStoreWhVO>
     * @author 李道云
     * @methodName: findStoreWhVOByDeptId
     * @methodDesc: 查询部门下所有门店和仓库
     * @description:
     * @param: [dept_id]
     * @create 2018-07-25 10:27
     **/
    public Result<StaffStoreWhVO> findStoreWhVOByDeptId(Long dept_id) {
        if (dept_id == null) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "部门id不能为空");
        }
        String storeIdList = "";
        String whIdList = "";
        DeptGroup deptGroup = deptGroupMapper.selectById(dept_id);
        String dept_type_store = CodeCache.getValueByKey("DeptType", "S01");//门店
        String dept_type_wh = CodeCache.getValueByKey("DeptType", "S02");//仓库
        Map<String, Object> storeId_map = deptGroupMapper.findChildPartyIdList(dept_type_store, deptGroup.getDept_path());
        Map<String, Object> whId_map = deptGroupMapper.findChildPartyIdList(dept_type_wh, deptGroup.getDept_path());
        if (storeId_map != null) {
            storeIdList = (String) storeId_map.get("deptIdList");
        }
        if (whId_map != null) {
            whIdList = (String) whId_map.get("deptIdList");
        }
        StaffStoreWhVO staffStoreWhVO = new StaffStoreWhVO();
        staffStoreWhVO.setStoreIdList(storeIdList);
        staffStoreWhVO.setWhIdList(whIdList);
        log.info("查询部门下所有门店和仓库:staffStoreWhVO=" + JSON.toJSONString(staffStoreWhVO));
        return new Result<>(CodeEnum.SUCCESS, staffStoreWhVO);
    }

    /**
     * @return java.lang.String
     * @author 代廷波
     * @description: 生成员工帐号
     * @param: ymd 年月日
     * @param: i
     * @create 2018/06/30 14:19
     **/
    private String createStaffAccount(String id_card) {
        EntityWrapper<StaffUserInfo> shareConfig_ew = new EntityWrapper<StaffUserInfo>();
        shareConfig_ew.orderBy(true, "id", false).last("LIMIT 1");
        StaffUserInfo staffUserInfo = super.selectOne(shareConfig_ew);
        Integer id = 0;
        if (staffUserInfo.getStaff_account().equals("admin")) {
            id = 10000;
        } else {
            id = Convert.toInt(staffUserInfo.getStaff_account().substring(0, 5)) + 1;
        }
        String account = id + id_card.substring(id_card.length() - 6, id_card.length());

        return account;
    }

    /**
     * @return
     * @author 代廷波
     * @description: 生成推荐码
     * @param: null
     * @create 2018/06/30 15:29
     **/
    private String createReferralCode(int i) {
        String code = RandomUtil.randomNumbers(6);
        int count = baseMapper.getStaffReferralCodeCount(code);
        if (count == 0) {//账号可以用
            Long state = redisService.setnx1(StaffConstants.STAFF_REFERRAL_CODE, code, String.valueOf(StaffConstants.STAFF_REFERRAL_CODE_TIME));
            if (1 == state) {
                return code;
            } else {
                ++i;
                createReferralCode(i);//递归
                if (i == 9999) {//防止死循环
                    return null;
                }

            }
        } else {
            ++i;
            createReferralCode(i);//递归
            if (i == 9999) {//防止死循环
                return null;
            }
        }
        return code;
    }
    @Transactional
    public Result<StaffUserUploadResponseVO> uploadExcel(String login_token, MultipartFile file) throws IOException {

        StaffUserUploadResponseVO uploadResponseVO = new StaffUserUploadResponseVO();
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(login_token);

        if (null == staffAdminLoginVO) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "登录过期或该账号已在其它地方登录");
        }
        log.info("excel导入员工文件数据staff_id:{},文件名:{},文件大小:{}" ,staffAdminLoginVO.getStaff_id(),file.getOriginalFilename(),file.getSize());
        if (file.isEmpty()){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "上传文件为空");
        }
        if (file.getSize()==0){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "上传文件为空");
        }
        String [] name = file.getOriginalFilename().split("\\.");
        if (name.length>0){
            Boolean b = ReUtil.isMatch(StaffConstants.EXCEL_TYPE, name[name.length-1]);
            if (!b){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK, "文件类型不对");
            }
        } else {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "文件类型不对");
        }

        Long operator_id = staffAdminLoginVO.getStaff_id();
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());
        Sheet sheet =   reader.getWorkbook().getSheetAt(0);
        /*int totalColumns = sheet.getRow(0).getPhysicalNumberOfCells();
        if (totalColumns != 10){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "上传文档模式格式不对");
        }*/
        Row row = sheet.getRow(0);
        if (null == row){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "上传文档模板格式不对");
        }
        StringBuffer sb = new StringBuffer(10);
        for (Cell cell : row) {
            try {
                sb.append(cell.getStringCellValue());
                sb.append(",");
            } catch (Exception e) {
                log.info("导入数据表头类型错误:" + cell);
                sb.append("导入数据类型错误");
                break;
            }
        }
        if (! StaffConstants.EXCEL_HEAD_NAME.equals(sb.toString())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "上传文档模板格式不对");
        }
        List<StaffUserExcelVO> list = reader.readAll(StaffUserExcelVO.class);
        int size = list.size();
        log.info("excel导入员工反向生成数据:一共:{},数据详情{}:", size,JSON.toJSONString(list));
        if (size > 2000) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "数据大于2000条");
        }
        if (size == 0) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "数据为空");
        }
        int count = validatoStaffUserExcelList(list);
        log.info("excel导入员工验证后的数据:" + JSON.toJSONString(count));
        if (count == 0){
            List<StaffUserExcelVO> db_list = staffUserInfoMapper.getStaffIdCardAndPhonNum(list);
            if (null != db_list && db_list.size()>0){
                for (StaffUserExcelVO db : db_list) {
                    for (StaffUserExcelVO excelVO : list) {
                        if (db.getId_card().equals(excelVO.getId_card())){
                            excelVO.setId_card_only(true);
                            excelVO.setId_card_validation(true);
                            ++count;
                        }
                        if (db.getPhone_num().equals(excelVO.getPhone_num())){
                            excelVO.setPhone_num_only(true);
                            excelVO.setPhone_num_validation(true);
                            ++count;
                        }
                    }
                }
                uploadResponseVO.setErrer_count(count);
                uploadResponseVO.setErrer_list(list);
                return new Result<>(CodeEnum.SUCCESS, uploadResponseVO);
            }
        }else{
            uploadResponseVO.setErrer_count(count);
            uploadResponseVO.setErrer_list(list);
            return new Result<>(CodeEnum.SUCCESS, uploadResponseVO);
        }

        StaffUserInfo bo = null;
        Integer staffMaxAccount = getStaffMaxAccount();
        List<StaffUserInfo> boList = new ArrayList<>();
        Date date = new Date();
        String image = CodeCache.getValueByKey("StaffDefaultPicture","S01");
        String referral_code =null;
        boolean b = false;
        for (StaffUserExcelVO vo : list) {
            ++staffMaxAccount;
            bo = new StaffUserInfo();
            bo.setStaff_account(staffMaxAccount + vo.getId_card().substring(vo.getId_card().length() - 6, vo.getId_card().length()));
            referral_code = this.createReferralCode(0);
            if(null == referral_code){
                b = true;
                break;
            }
            bo.setReferral_code(referral_code);
            bo.setLogin_pwd(SecureUtil.md5(bo.getStaff_account() + "123456"));
            bo.setStaff_name(vo.getStaff_name());
            bo.setPhone_num(vo.getPhone_num());
            bo.setSex(vo.getSex_val());
            bo.setId_card(vo.getId_card());
            bo.setEducation(vo.getEducation_val());
            bo.setEmail(vo.getEmail());
            bo.setHome_address(vo.getHome_address());
            bo.setContact_name(vo.getContact_name());
            bo.setContact_tel(vo.getContact_tel());
            bo.setRole_name(vo.getRole_name());
            bo.setStaff_pic(image);
            bo.setOperator_id(operator_id);
            bo.setCreate_time(date);
            bo.setStaff_type("01");// 导入的都是普通员工
            boList.add(bo);
            org.apache.commons.lang3.StringUtils.isNotEmpty("dd");
        }
        if (b){
            log.info("excel导入员工生成推荐码失败:" + JSON.toJSONString(boList));
            return new Result<>(CodeEnum.FAIL_BUSINESS, "员工验证码生成失败");
        }
        uploadResponseVO.setSuccess_List(boList);


        if (boList.size() > 0) {
            super.insertBatch(boList);
            staffRoleAndPermRelation(boList);
            log.info("excel批量导入员工:" + JSON.toJSONString(boList));
        }
        return new Result<>(CodeEnum.SUCCESS, uploadResponseVO);
    }
    /**
     * @author 代廷波
     * @description: 校验导入的员工信息
     * @param: list
     * @return int
     * @create 2018/09/29 11:39
     **/
    private int validatoStaffUserExcelList(List<StaffUserExcelVO> list) {

        int count = 0;
        EntityWrapper<StaffRole> role = new EntityWrapper<>();
        role.where("role_name={0}", StaffConstants.STAFF_IMPORT_STORE_MANAGER).or("role_name={0}",StaffConstants.STAFF_IMPORT_STORE_ASSISTANT).and("role_status={0}", 1);
        List<StaffRole> storeRole = roleService.selectList(role);
        Boolean manager = true;
        Boolean assistant = true;
        if (null!= storeRole && storeRole.size()>0){
            for (StaffRole roleDb : storeRole) {
                if (StaffConstants.STAFF_IMPORT_STORE_MANAGER.equals(roleDb.getRole_name())){
                    manager=false;
                    continue;
                }
                if (StaffConstants.STAFF_IMPORT_STORE_ASSISTANT.equals(roleDb.getRole_name())){
                    assistant=false;
                }
            }
        }
        Map<String,Object> map = new HashMap<>();

        for (StaffUserExcelVO vo : list) {
            if (StrUtil.isEmpty(vo.getStaff_name())) {
                ++count;
                vo.setStaff_name_validation(true);
            }else {

                if (vo.getStaff_name().length()>16){
                    ++count;
                    vo.setStaff_name_validation(true);
                }
            }

            if (StrUtil.isEmpty(vo.getPhone_num())) {
                ++count;
                vo.setPhone_num_validation(true);
            } else {
                Boolean b = ReUtil.isMatch(StaffConstants.PHONE_REGEX, vo.getPhone_num());
                if (!b) {
                    ++count;
                    vo.setPhone_num_validation(true);
                }
                // 验证是否重复
                if (map.containsKey(vo.getPhone_num())){
                    ++count;
                    vo.setPhone_num_validation(true);
                    vo.setId_card_only(true);
                }else{
                    map.put(vo.getPhone_num(),vo.getPhone_num());
                }

            }

            if (StrUtil.isEmpty(vo.getSex())) {
                ++count;
                vo.setSex_validation(true);
            } else {
                Boolean b = ReUtil.isMatch(StaffConstants.STAFF_SEX_TYPE, vo.getSex());
                if (!b) {
                    ++count;
                    vo.setSex_validation(true);
                } else {
                    vo.setSex_val("男".equals(vo.getSex()) ? "0" : "1");
                }
            }

            if (StrUtil.isEmpty(vo.getId_card())) {
                ++count;
                vo.setId_card_validation(true);
            } else {
                boolean valid = IdcardUtil.isValidCard(vo.getId_card());
                if (!valid) {
                    ++count;
                    vo.setId_card_validation(true);
                }
                // 验证是否重复
                if (map.containsKey(vo.getId_card())){
                    ++count;
                    vo.setId_card_validation(true);
                    vo.setId_card_only(true);
                }else{
                    map.put(vo.getId_card(),vo.getId_card());
                }
            }

            if (StrUtil.isEmpty(vo.getEducation())) {
                ++count;
                vo.setEducation_validation(true);
            }else{
                Boolean b = ReUtil.isMatch(StaffConstants.STAFF_EDUCATION, vo.getEducation());
                if (!b) {
                    ++count;
                    vo.setEducation_validation(true);
                } else {
                    vo.setEducation_val(StaffEduEnmu.getStaffEduVale(vo.getEducation()));
                }
            }

            if (StrUtil.isEmpty(vo.getHome_address())) {
                ++count;
                vo.setHome_address_validation(true);
            }else{
                if (vo.getHome_address().length()>128){
                    ++count;
                    vo.setHome_address_validation(true);
                }
            }

            if (StrUtil.isEmpty(vo.getContact_name())) {
                ++count;
                vo.setContact_name_validation(true);
            } else{
                if (vo.getContact_name().length()>32){
                    ++count;
                    vo.setContact_name_validation(true);
                }
            }

            if (StrUtil.isEmpty(vo.getContact_tel())) {
                ++count;
                vo.setContact_tel_validation(true);
            } else{
                if (vo.getContact_tel().length()>20){
                    ++count;
                    vo.setContact_tel_validation(true);
                }
            }

            if (StrUtil.isEmpty(vo.getRole_name())) {
                ++count;
                vo.setRole_name_validation(true);
            } else {
                Boolean b = ReUtil.isMatch(StaffConstants.STAFF_ROLE_NAME_TYPE, vo.getRole_name());
                if (!b) {
                    ++count;
                    vo.setRole_name_validation(true);
                }else{
                    if (StaffConstants.STAFF_IMPORT_STORE_MANAGER.equals(vo.getRole_name())){
                        vo.setRole_name_db_flag(manager);
                        if(manager){
                            ++count;
                            vo.setRole_name_db_flag(true);
                        }
                    }
                    if (StaffConstants.STAFF_IMPORT_STORE_ASSISTANT.equals(vo.getRole_name())){
                        vo.setRole_name_db_flag(assistant);
                        if(assistant){
                            vo.setRole_name_db_flag(true);
                            ++count;

                        }
                    }

                }
            }


        }
        return count;
    }

    /**
     * @return java.lang.Integer
     * @author 代廷波
     * @description: 获取当前最大的员工账号
     * @param:
     * @create 2018/09/26 10:32
     **/
    public void staffRoleAndPermRelation(List<StaffUserInfo> boList) {

        // 店长权限
        List<RolePermRelation> storeManagerList = staffPermRelationService.getRoleNamePermRelationList(StaffConstants.STAFF_IMPORT_STORE_MANAGER);
        // 店员权限
        List<RolePermRelation> storeAssistantList = staffPermRelationService.getRoleNamePermRelationList(StaffConstants.STAFF_IMPORT_STORE_ASSISTANT);

        List<StaffPermRelation> storeManagerPermRelation = null;
        // 将角色资源权限转成员工资源权限
        if (null != storeManagerList && storeManagerList.size() > 0){
            storeManagerPermRelation = setStaffPermission(storeManagerList);
        }
        List<StaffPermRelation> storeAssistantPermRelation= null;
        if (null!=storeAssistantList && storeAssistantList.size() > 0) {
            storeAssistantPermRelation = setStaffPermission(storeAssistantList);
        }
        // 资源
        List<StaffPermRelation> comStaffPermRelation = new ArrayList<>();
        StaffPermRelation staffPermRelation = null;

        List<StaffRoleRelation> comStaffRoleRelation = new ArrayList<>();
        StaffRoleRelation staffRoleRelation = null;

        List<StaffPermRelation> list = new ArrayList<>();
        StaffPermRelation spr = null;

        for (StaffUserInfo userInfo : boList) {
            staffRoleRelation = new StaffRoleRelation();
            staffRoleRelation.setStaff_id(userInfo.getId());
            // 店长权限
            if (null != storeManagerPermRelation && StaffConstants.STAFF_IMPORT_STORE_MANAGER.equals(userInfo.getRole_name())) {
                staffRoleRelation.setRole_id(storeManagerList.get(0).getRole_id());
                for (StaffPermRelation store_manager : storeManagerPermRelation) {
                    staffPermRelation = new StaffPermRelation();
                    staffPermRelation.setStaff_id(userInfo.getId());
                    staffPermRelation.setPerm_id(store_manager.getPerm_id());
                    comStaffPermRelation.add(staffPermRelation);
                }
            }

            if (null !=storeAssistantPermRelation && StaffConstants.STAFF_IMPORT_STORE_ASSISTANT.equals(userInfo.getRole_name())) {
                staffRoleRelation.setRole_id(storeAssistantList.get(0).getRole_id());
                for (StaffPermRelation assistant_manager : storeAssistantPermRelation) {
                    staffPermRelation = new StaffPermRelation();
                    staffPermRelation.setPerm_id(assistant_manager.getPerm_id());
                    staffPermRelation.setStaff_id(userInfo.getId());
                    comStaffPermRelation.add(staffPermRelation);
                }
            }
            comStaffRoleRelation.add(staffRoleRelation);
        }



        if (comStaffRoleRelation.size() > 0) {
            staffRoleRelationService.insertBatch(comStaffRoleRelation);
        }
        if (comStaffPermRelation.size() > 0) {
            staffPermRelationService.insertBatch(comStaffPermRelation);

        }

    }

    /**
     * @return java.lang.Integer
     * @author 代廷波
     * @description: 获取员工最大帐号
     * @param:
     * @create 2018/09/26 14:33
     **/
    private Integer getStaffMaxAccount() {

        EntityWrapper<StaffUserInfo> shareConfig_ew = new EntityWrapper<StaffUserInfo>();
        shareConfig_ew.orderBy(true, "id", false).last("LIMIT 1");
        StaffUserInfo staffUserInfo = super.selectOne(shareConfig_ew);
        Integer id = 0;
        if (staffUserInfo.getStaff_account().equals("admin")) {
            id = 100000;
        } else {
            id = Convert.toInt(staffUserInfo.getStaff_account().substring(0, 5));
        }
        return id;
    }

    /**
     * @return java.util.List<com.hryj.entity.bo.staff.role.StaffPermRelation>
     * @author 代廷波
     * @description: 将角色资源权限转换成员工资源权限
     * @param: rolePermRelationList 将角色资源权限
     * @create 2018/09/26 14:10
     **/
    private List<StaffPermRelation> setStaffPermission(List<RolePermRelation> rolePermRelationList) {

        List<StaffPermRelation> list = new ArrayList<>();
        StaffPermRelation spr = null;
        for (RolePermRelation resource : rolePermRelationList) {
            spr = new StaffPermRelation();
            spr.setPerm_id(resource.getPerm_id());
            list.add(spr);
        }
        return list;
    }


}
