package com.hryj.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.cache.SmsCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.user.*;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.profit.request.DataQueryRequestVO;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import com.hryj.entity.vo.user.*;
import com.hryj.entity.vo.user.request.*;
import com.hryj.entity.vo.user.response.UserInfoResponseVO;
import com.hryj.entity.vo.user.response.UserLoginResponseVO;
import com.hryj.entity.vo.user.response.UserSearchResponseVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.feign.PofitFeignClient;
import com.hryj.feign.StaffFeignClient;
import com.hryj.mapper.UserIdentityCardMapper;
import com.hryj.mapper.UserInfoMapper;
import com.hryj.mapper.UserLoginRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李道云
 * @className: UserService
 * @description: 用户service
 * @create 2018/6/26 10:47
 **/
@Slf4j
@Service
public class UserService extends ServiceImpl<UserInfoMapper, UserInfo> {

    @Autowired
    private UserAddressService userAddressService;

    @Autowired
    private UserLoginRecordMapper userLoginRecordMapper;

    @Autowired
    private UserOftenPartyService userOftenPartyService;

    @Autowired
    private UserLoginTokenService userLoginTokenService;

    @Autowired
    private StaffFeignClient staffFeignClient;

    @Autowired
    private PofitFeignClient pofitFeignClient;

    @Autowired
    private Environment env;

    @Autowired
    private UserIdentityCardMapper userIdentityCardMapper;

    /**
     * @return com.hryj.entity.bo.user.UserInfo
     * @author 李道云
     * @description: 根据手机号码查询用户
     * @param: [phone_num]
     * @create 2018-06-26 13:19
     **/
    public UserInfo getUserInfoByPhoneNum(String phone_num) {
        EntityWrapper<UserInfo> wrapper = new EntityWrapper<>();
        wrapper.eq("phone_num", phone_num);
        return super.selectOne(wrapper);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserLoginResponseVO>
     * @author 李道云
     * @description: 用户登录
     * @param: [userLoginRequestVO]
     * @create 2018-06-26 13:41
     **/
    @Transactional
    public Result<UserLoginResponseVO> loginBySmsCode(UserLoginRequestVO userLoginRequestVO){
        log.info("用户登录:userLoginRequestVO===" + JSON.toJSONString(userLoginRequestVO));
        //1、判断验证码是否正确
        String phone_num = userLoginRequestVO.getPhone_num();
        String verify_code = userLoginRequestVO.getVerify_code();
        if (StrUtil.isEmpty(phone_num)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号不能为空");
        }
        if (phone_num.length() != 11) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号不正确");
        }
        if (StrUtil.isEmpty(verify_code)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "验证码不能为空");
        }
        if(verify_code.length()!=6 || !NumberUtil.isInteger(verify_code)){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请输入6位数字验证码");
        }
        UserInfo userInfo = this.getUserInfoByPhoneNum(phone_num);
        if (userInfo == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "登录失败,手机号没注册");
        }
        String envStr = env.getProperty("spring.profiles.active");
        if("prod".equals(envStr)){
            String noVerifyPhoneNum = CodeCache.getValueByKey("NoVerifyPhoneNum","S01");//免验证的手机号码
            if(!(noVerifyPhoneNum.contains(phone_num) && "258369".equals(verify_code))){
                boolean flag = SmsCache.verifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        }else{
            if (!"258369".equals(verify_code)) {
                boolean flag = SmsCache.verifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        }
        Long user_id = userInfo.getId();
        //2、创建用户登录token,并将登录信息缓存
        String login_token = SecureUtil.sha1(phone_num + System.currentTimeMillis());
        UserLoginVO userLoginVO = this.setUserLoginVO(userInfo);
        LoginCache.setUserLoginVO(login_token, userLoginVO);
        UserLoginResponseVO userLoginResponseVO = new UserLoginResponseVO();
        userLoginResponseVO.setPhone_num(phone_num);
        userLoginResponseVO.setLogin_token(login_token);
        userLoginResponseVO.setReferral_code(userInfo.getReferral_code());
        userLoginResponseVO.setDefaultParty(userLoginVO.getDefaultParty());
        //3、更新用户登录token，游客账号无需更新token
        if(!"000000000Aa".equals(phone_num) && !"00000000000".equals(phone_num)){
            userLoginTokenService.updateUserLoginToken(userLoginRequestVO.getApp_key(),user_id,login_token,userLoginRequestVO.getDevice_id());
            //4、保存用户登录记录
            this.saveUserLoginRecord(user_id, userLoginRequestVO);
        }
        log.info("用户登录:userLoginResponseVO===" + JSON.toJSONString(userLoginResponseVO));
        return new Result(CodeEnum.SUCCESS, userLoginResponseVO);
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
    public Result verifyRegisterUser(UserRegisterVerifyRequestVO userRegisterVerifyRequestVO){
        log.info("验证注册用户:userRegisterVerifyRequestVO===" + JSON.toJSONString(userRegisterVerifyRequestVO));
        //判断验证码是否正确
        String phone_num = userRegisterVerifyRequestVO.getPhone_num();
        String verify_code = userRegisterVerifyRequestVO.getVerify_code();
        if (StrUtil.isEmpty(phone_num)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号不能为空");
        }
        if (!Validator.isNumber(phone_num) || phone_num.length() != 11) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号不正确");
        }
        if (StrUtil.isEmpty(verify_code)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "验证码不能为空");
        }
        if(!NumberUtil.isNumber(verify_code) || verify_code.length() != 6){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请输入6位数字验证码");
        }
        if (getUserInfoByPhoneNum(phone_num) != null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "该手机号码已注册,请直接登录");
        }
        String envStr = env.getProperty("spring.profiles.active");
        if("prod".equals(envStr)){
            String noVerifyPhoneNum = CodeCache.getValueByKey("NoVerifyPhoneNum","S01");//免验证的手机号码
            if(!(noVerifyPhoneNum.contains(phone_num) && "258369".equals(verify_code))){
                boolean flag = SmsCache.onlyVerifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        }else{
            if (!"258369".equals(verify_code)) {
                boolean flag = SmsCache.onlyVerifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        }
        //验证推荐码
        String referral_code = userRegisterVerifyRequestVO.getReferral_code();
        if (StrUtil.isNotEmpty(referral_code) ) {
            //推荐码校验，1、长度必须6位 2、必须是数字
            if(referral_code.length()!=6 || !NumberUtil.isInteger(referral_code)){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请输入6位数字推荐码");
            }
            Result<StaffDeptVO> staffResult = staffFeignClient.findStaffDeptVO(referral_code, null, null);
            if(staffResult.isSuccess()){
                StaffDeptVO staffDeptVO = staffResult.getData();
                if (staffDeptVO == null) {
                    return new Result<>(CodeEnum.FAIL_PARAMCHECK, "推荐码不正确，请重新输入");
                }
            }else{
                throw new ServerException("员工服务不可用，请稍后再试");
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserLoginResponseVO>
     * @author 李道云
     * @description: 用户注册
     * @param: [userRegisterRequestVO]
     * @create 2018-06-26 12:41
     **/
    @Transactional
    public Result<UserLoginResponseVO> register(UserRegisterRequestVO userRegisterRequestVO){
        log.info("用户注册:userRegisterRequestVO===" + JSON.toJSONString(userRegisterRequestVO));
        //1、判断验证码是否正确
        String phone_num = userRegisterRequestVO.getPhone_num();
        //String verify_code = userRegisterRequestVO.getVerify_code();
        if (StrUtil.isEmpty(phone_num)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号不能为空");
        }
        if (!Validator.isNumber(phone_num) || phone_num.length() != 11) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号不正确");
        }
        /**
        if (StrUtil.isEmpty(verify_code)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "验证码不能为空");
        }
        if(!NumberUtil.isNumber(verify_code) || verify_code.length() != 6){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请输入6位数字验证码");
        }*/
        if (getUserInfoByPhoneNum(phone_num) != null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "该手机号码已注册,请直接登录");
        }
        //去掉验证码的验证 2018-09-27
        /**
        String envStr = env.getProperty("spring.profiles.active");
        if("prod".equals(envStr)){
            String noVerifyPhoneNum = CodeCache.getValueByKey("NoVerifyPhoneNum","S01");//免验证的手机号码
            if(!(noVerifyPhoneNum.contains(phone_num) && "258369".equals(verify_code))){
                boolean flag = SmsCache.verifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        }else{
            if (!"258369".equals(verify_code)) {
                boolean flag = SmsCache.verifySmsCode(phone_num, verify_code);
                if (!flag) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "验证码错误或已过期");
                }
            }
        }*/
        UserAddressVO userAddressVO = userRegisterRequestVO.getUserAddressVO();
        Result result = userAddressService.validateUserAddress(userAddressVO);
        if (result.isFailed()) {
            return result;
        }
        //2、获取推荐员工信息
        String referral_code = userRegisterRequestVO.getReferral_code();
        Long staff_id = null;
        String staff_name = null;
        String staff_phone = null;
        String staff_job_name = null;
        Long store_id = null;
        String store_name = null;
        String dept_path = null;
        if (StrUtil.isNotEmpty(referral_code)) {
            //推荐码校验，1、长度必须6位 2、必须是数字
            if(!NumberUtil.isNumber(referral_code) || referral_code.length() != 6){
                return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请输入6位数字推荐码");
            }
            Result<StaffDeptVO> staffResult = staffFeignClient.findStaffDeptVO(referral_code, null, null);
            if(staffResult.isSuccess()){
                StaffDeptVO staffDeptVO = staffResult.getData();
                if (staffDeptVO == null) {
                    return new Result<>(CodeEnum.FAIL_PARAMCHECK, "推荐码不正确，请重新输入");
                }
                staff_id = staffDeptVO.getStaff_id();
                staff_name = staffDeptVO.getStaff_name();
                staff_phone = staffDeptVO.getPhone_num();
                staff_job_name = staffDeptVO.getStaff_job_name();
                store_id = staffDeptVO.getDept_id();
                store_name = staffDeptVO.getDept_name();
                dept_path = staffDeptVO.getDept_path();
            }else{
                throw new ServerException("员工服务不可用，请稍后再试");
            }
        }
        //3、保存用户基本信息
        UserInfo userInfo = new UserInfo();
        userInfo.setPhone_num(phone_num);
        userInfo.setReferral_code(referral_code);
        userInfo.setStaff_id(staff_id);
        userInfo.setStaff_name(staff_name);
        userInfo.setStaff_phone(staff_phone);
        userInfo.setStaff_job_name(staff_job_name);
        userInfo.setStore_id(store_id);
        userInfo.setStore_name(store_name);
        userInfo.setDept_path(dept_path);
        userInfo.setApp_key(userRegisterRequestVO.getApp_key());
        userInfo.setCall_source(userRegisterRequestVO.getCall_source());
        userInfo.setApp_channel(userRegisterRequestVO.getApp_channel());
        userInfo.setApp_version(userRegisterRequestVO.getApp_version());
        userInfo.setDevice_id(userRegisterRequestVO.getDevice_id());
        userInfo.setDevice_model(userRegisterRequestVO.getDevice_model());
        userInfo.setOs_version(userRegisterRequestVO.getOs_version());
        baseMapper.insert(userInfo);
        Long user_id = userInfo.getId();
        //4、保存用户地址信息
        userAddressService.saveUserAddress(user_id, userAddressVO);
        //5、创建用户登录token,并将登录信息缓存
        String login_token = SecureUtil.sha1(phone_num + System.currentTimeMillis());
        UserLoginVO userLoginVO = this.setUserLoginVO(userInfo);
        LoginCache.setUserLoginVO(login_token, userLoginVO);
        log.info("用户注册:userLoginVO===" + JSON.toJSONString(userLoginVO));
        UserLoginResponseVO userLoginResponseVO = new UserLoginResponseVO();
        userLoginResponseVO.setPhone_num(phone_num);
        userLoginResponseVO.setLogin_token(login_token);
        userLoginResponseVO.setReferral_code(userInfo.getReferral_code());
        userLoginResponseVO.setDefaultParty(userLoginVO.getDefaultParty());
        //6、更新用户登录token
        userLoginTokenService.updateUserLoginToken(userRegisterRequestVO.getApp_key(),user_id,login_token,userRegisterRequestVO.getDevice_id());
        //7、保存用户登录记录
        this.saveUserLoginRecord(user_id, userRegisterRequestVO);
        log.info("用户注册:userRegisterRequestVO===" + JSON.toJSONString(userRegisterRequestVO));
        return new Result(CodeEnum.SUCCESS, userLoginResponseVO);
    }

    /**
     * @return void
     * @author 李道云
     * @description: 保存用户登录记录
     * @param: [user_id, requestVO]
     * @create 2018-06-26 12:57
     **/
    public void saveUserLoginRecord(Long user_id, RequestVO requestVO) {
        UserLoginRecord userLoginRecord = new UserLoginRecord();
        userLoginRecord.setUser_id(user_id);
        userLoginRecord.setApp_key(requestVO.getApp_key());
        userLoginRecord.setCall_source(requestVO.getCall_source());
        userLoginRecord.setApp_channel(requestVO.getApp_channel());
        userLoginRecord.setApp_version(requestVO.getApp_version());
        userLoginRecord.setDevice_id(requestVO.getDevice_id());
        userLoginRecord.setDevice_model(requestVO.getDevice_model());
        userLoginRecord.setOs_version(requestVO.getOs_version());
        userLoginRecord.setLogin_ip(requestVO.getDevice_ip());
        userLoginRecordMapper.insert(userLoginRecord);
    }

    /**
     * @author 李道云
     * @methodName: findUserInfo
     * @methodDesc: 查询用户基本信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserInfoResponseVO>
     * @create 2018-08-29 9:47
     **/
    public Result<UserInfoResponseVO> findUserInfo(RequestVO requestVO){
        //1、验证用户登录信息
        String login_token = requestVO.getLogin_token();
        if(StrUtil.isEmpty(login_token)){
            return new Result<>(CodeEnum.FAIL_TOKEN_INVALID);
        }
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(login_token);
        if(userLoginVO ==null){
            return new Result<>(CodeEnum.FAIL_TOKEN_INVALID);
        }
        //2、返回用户基本信息，从登录缓存获取
        UserInfoResponseVO userInfoResponseVO = new UserInfoResponseVO();
        userInfoResponseVO.setPhone_num(userLoginVO.getPhone_num());
        userInfoResponseVO.setReferral_code(userLoginVO.getReferral_code());
        userInfoResponseVO.setReceive_name(userLoginVO.getReceive_name());
        userInfoResponseVO.setReceive_phone(userLoginVO.getReceive_phone());
        userInfoResponseVO.setReceive_address(userLoginVO.getReceive_address());
        userInfoResponseVO.setLocations(userLoginVO.getLocations());
        userInfoResponseVO.setDefaultParty(userLoginVO.getDefaultParty());
        return new Result<>(CodeEnum.SUCCESS,userInfoResponseVO);
    }

    /**
     * @return
     * @author 李道云
     * @methodName: searchUserList
     * @methodDesc: 分页查询用户信息
     * @description:
     * @param: [userListRequestVO]
     * @create 2018-06-28 11:43
     **/
    public Result<PageResponseVO<UserInfoVO>> searchUserList(UserListRequestVO userListRequestVO) {
        log.info("分页查询用户信息:userListRequestVO===" + JSON.toJSONString(userListRequestVO));
        Page page = new Page(userListRequestVO.getPage_num(), userListRequestVO.getPage_size());
        List<UserInfoVO> records = baseMapper.searchUserList(userListRequestVO, page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(records);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        log.info("分页查询用户信息:pageResponseVO===" + JSON.toJSONString(pageResponseVO));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.response.UserSearchResponseVO>
     * @author 李道云
     * @methodName: searchUserInfo
     * @methodDesc: 搜索用户信息
     * @description:
     * @param: [userPhoneRequestVO]
     * @create 2018-07-05 11:14
     **/
    public Result<UserSearchResponseVO> searchUserInfo(UserPhoneRequestVO userPhoneRequestVO) {
        log.info("搜索用户信息:userPhoneRequestVO===" + JSON.toJSONString(userPhoneRequestVO));
        String phone_num = userPhoneRequestVO.getPhone_num();
        if (StrUtil.isEmpty(phone_num)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号码不能为空");
        }
        if (!Validator.isNumber(phone_num) || phone_num.length() != 11) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "手机号不正确");
        }
        UserInfo userInfo = this.getUserInfoByPhoneNum(phone_num);
        if(userInfo ==null){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"用户不存在");
        }
        UserAddress userAddress = userAddressService.getUserDefaultAddressByUserId(userInfo.getId());
        StringBuffer receive_address = new StringBuffer();
        receive_address.append(userAddress.getProvince_name());
        receive_address.append(userAddress.getCity_name());
        receive_address.append(userAddress.getArea_name());
        receive_address.append(userAddress.getLocation_address());
        receive_address.append(userAddress.getDetail_address());
        UserSearchResponseVO userSearchResponseVO = new UserSearchResponseVO();
        userSearchResponseVO.setUser_id(userInfo.getId());
        userSearchResponseVO.setPhone_num(userInfo.getPhone_num());
        userSearchResponseVO.setReceive_name(userAddress.getReceive_name());
        userSearchResponseVO.setReceive_phone(userAddress.getReceive_phone());
        userSearchResponseVO.setReceive_address(receive_address.toString());
        log.info("搜索用户信息:userSearchResponseVO===" + JSON.toJSONString(userSearchResponseVO));
        return new Result<>(CodeEnum.SUCCESS, userSearchResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO   <   com.hryj.entity.vo.user.UserInfoVO>>
     * @author 李道云
     * @methodName: findReferralRegisterUserList
     * @methodDesc: 查询推荐注册用户列表
     * @description:
     * @param: [dataQueryRequestVO]
     * @create 2018-07-07 16:59
     **/
    public Result<ListResponseVO<UserInfoVO>> findReferralRegisterUserList(DataQueryRequestVO dataQueryRequestVO) {
        log.info("查询推荐注册用户列表:dataQueryRequestVO===" + JSON.toJSONString(dataQueryRequestVO));
        String start_date = dataQueryRequestVO.getStart_date();
        String end_date = dataQueryRequestVO.getEnd_date();
        Long staff_id = dataQueryRequestVO.getStaff_id();
        Long store_id = dataQueryRequestVO.getStore_id();
        Long dept_id = dataQueryRequestVO.getDept_id();
        Long wh_id = dataQueryRequestVO.getWh_id();
        if (StrUtil.isEmpty(start_date) || StrUtil.isEmpty(end_date)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "查询日期不能为空");
        }
        //都为空的时候查询当前登录员工的推荐注册用户数据
        if (staff_id == null && store_id == null && dept_id == null && wh_id==null) {
            String login_token = dataQueryRequestVO.getLogin_token();
            staff_id = LoginCache.getStaffAppLoginVO(login_token).getStaff_id();
        }
        List<UserInfoVO> userInfoVOList = new ArrayList<>();
        //查询单个员工的推荐注册用户数据
        if (staff_id != null) {
            userInfoVOList = baseMapper.findReferralRegisterUserList(staff_id, null, null, start_date, end_date);
        }
        //查询门店的推荐注册用户数据
        if (store_id != null) {
            userInfoVOList = baseMapper.findReferralRegisterUserList(null, store_id, null, start_date, end_date);
        }
        //查询部门下的推荐注册用户数据
        if (dept_id != null) {
            Result<DeptGroup> result = staffFeignClient.getDeptGroupByDeptId(dept_id);
            DeptGroup deptGroup = result.getData();
            userInfoVOList = baseMapper.findReferralRegisterUserList(null, null, deptGroup.getDept_path(), start_date, end_date);
        }
        //查询仓库的推荐注册用户数据
        if (wh_id != null) {
            userInfoVOList = baseMapper.findReferralRegisterUserList(null, wh_id, null, start_date, end_date);
        }
        log.info("查询推荐注册用户列表:userInfoVOList===" + JSON.toJSONString(userInfoVOList));
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(userInfoVOList));
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserInfoVO>
     * @author 李道云
     * @methodName: findUserInfoVOByUserId
     * @methodDesc: 查询用户基本信息
     * @description:
     * @param: [user_id, phone_num]
     * @create 2018-07-10 20:43
     **/
    public Result<UserInfoVO> findUserInfoVOByUserId(Long user_id, String phone_num) {
        UserInfoVO userInfoVO = baseMapper.findUserInfoVOByUserId(user_id, phone_num);
        if(userInfoVO!=null){
            userInfoVO.setUserAddress(userAddressService.getUserDefaultAddressByUserId(user_id));
        }
        log.info("查询用户基本信息:userInfoVO===" + JSON.toJSONString(userInfoVO));
        return new Result<>(CodeEnum.SUCCESS, userInfoVO);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: flushUserLoginVO
     * @methodDesc: 刷新用户登录缓存信息
     * @description:
     * @param: [requestVO]
     * @create 2018-07-11 14:29
     **/
    public Result flushUserLoginVO(RequestVO requestVO) {
        String login_token = requestVO.getLogin_token();
        if (StrUtil.isNotEmpty(login_token)) {
            this.flushUserLoginVO(login_token);
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * 刷新用户登录缓存
     * @param login_token
     * @return
     */
    public Result flushUserLoginVO(String login_token){
        log.info("刷新用户登录缓存login_token：" + login_token);
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(login_token);
        if(userLoginVO !=null){
            Long user_id = userLoginVO.getUser_id();
            UserInfo userInfo = baseMapper.selectById(user_id);
            log.info("用户基本信息userInfo：" + JSON.toJSONString(userInfo));
            UserLoginVO new_userLoginVO = this.setUserLoginVO(userInfo);
            LoginCache.setUserLoginVO(login_token, new_userLoginVO);
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserServiceRangeVO>
     * @author 李道云
     * @methodName: getUserServiceRangeByUserId
     * @methodDesc: 根据用户id获取服务于用户的范围
     * @description:
     * @param: [user_id]
     * @create 2018-07-17 18:55
     **/
    public Result<UserServiceRangeVO> getUserServiceRangeByUserId(Long user_id) {
        if (user_id == null || user_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "用户ID不能是空值");
        }
        //获取用户默认地址
        UserAddress userAddress = userAddressService.getUserDefaultAddressByUserId(user_id);
        if (userAddress == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "ID为:["  + user_id +"]的用户没有默认地址");
        }
        //获取服务于用户的门店和仓库
        String city_code = userAddress.getCity_code();
        Result<UserServiceRangeVO> result = staffFeignClient.getUserServiceRange(userAddress.getPoi_id(), city_code);
        if(result.isSuccess()){
            log.info("根据用户id获取服务于用户的范围:result===" + JSON.toJSONString(result));
            List<Long> partyIdList = new ArrayList<>();
            UserServiceRangeVO userServiceRangeVO = result.getData();
            if(userServiceRangeVO !=null){
                if(CollectionUtil.isNotEmpty(userServiceRangeVO.getStoreList())){
                    for(UserPartyVO userPartyVO : userServiceRangeVO.getStoreList()){
                        partyIdList.add(userPartyVO.getParty_id());
                    }
                }
                UserPartyVO warehouse = userServiceRangeVO.getWarehouse();
                if(warehouse !=null){
                    partyIdList.add(warehouse.getParty_id());
                }
            }
            //用户默认门店或仓库
            UserOftenParty userOftenParty = userOftenPartyService.getDefaultParty(user_id);
            if(userOftenParty !=null){
                if(partyIdList.contains(userOftenParty.getParty_id())){
                    UserPartyVO defaultParty = new UserPartyVO();
                    BeanUtil.copyProperties(userOftenParty,defaultParty);
                    result.getData().setDefaultParty(defaultParty);
                }else{//如果没有覆盖了，就删除默认门店或仓库
                    userOftenPartyService.deleteById(userOftenParty.getId());
                }
            }
        }else{
            throw new ServerException("员工服务不可用，请稍后再试");
        }
        return result;
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
    @Transactional
    public Result setReferralCode(UserReferralCodeRequestVO userReferralCodeRequestVO){
        //1、验证用户登录信息
        String login_token = userReferralCodeRequestVO.getLogin_token();
        if(StrUtil.isEmpty(login_token)){
            return new Result<>(CodeEnum.FAIL_TOKEN_INVALID);
        }
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(login_token);
        if(userLoginVO ==null){
            return new Result<>(CodeEnum.FAIL_TOKEN_INVALID);
        }
        Long user_id = userLoginVO.getUser_id();
        //2、验证用户是否已存在推荐码
        UserInfo userInfo = baseMapper.selectById(user_id);
        if(StrUtil.isNotEmpty(userInfo.getReferral_code())){
            return new Result<>(CodeEnum.FAIL_BUSINESS,"用户已设置推荐码");
        }
        //3、获取推荐员工信息
        String referral_code = userReferralCodeRequestVO.getReferral_code();
        //推荐码校验，1、不能为空 2、长度必须是6位 3、必须是数字
        if(StrUtil.isEmpty(referral_code) || !NumberUtil.isNumber(referral_code) || referral_code.length() != 6){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK,"请输入6位数字推荐码");
        }
        Long staff_id = null;
        String staff_name = null;
        String staff_phone = null;
        String staff_job_name = null;
        Long store_id = null;
        String store_name = null;
        String dept_path = null;
        if (StrUtil.isNotEmpty(referral_code)) {
            Result<StaffDeptVO> staffResult = staffFeignClient.findStaffDeptVO(referral_code, null, null);
            if(staffResult.isSuccess()){
                StaffDeptVO staffDeptVO = staffResult.getData();
                if (staffDeptVO == null) {
                    return new Result<>(CodeEnum.FAIL_PARAMCHECK, "推荐码不正确，请重新输入");
                }
                staff_id = staffDeptVO.getStaff_id();
                staff_name = staffDeptVO.getStaff_name();
                staff_phone = staffDeptVO.getPhone_num();
                staff_job_name = staffDeptVO.getStaff_job_name();
                store_id = staffDeptVO.getDept_id();
                store_name = staffDeptVO.getDept_name();
                dept_path = staffDeptVO.getDept_path();
            }else{
                throw new ServerException("员工服务不可用，请稍后再试");
            }
        }
        //4、更新用户的推荐人信息
        UserInfo updateUserInfo = new UserInfo();
        updateUserInfo.setId(user_id);
        updateUserInfo.setReferral_code(referral_code);
        updateUserInfo.setStaff_id(staff_id);
        updateUserInfo.setStaff_name(staff_name);
        updateUserInfo.setStaff_phone(staff_phone);
        updateUserInfo.setStaff_job_name(staff_job_name);
        updateUserInfo.setStore_id(store_id);
        updateUserInfo.setStore_name(store_name);
        updateUserInfo.setDept_path(dept_path);
        baseMapper.updateById(updateUserInfo);
        //刷新登录缓存
        this.flushUserLoginVO(login_token);
        //5、异步更新统计数据
        String statis_date = DateUtil.format(userInfo.getCreate_time(), DatePattern.NORM_DATE_PATTERN);
        //注册时间不为当天才需要去更新统计数据，因为当天的会重新计算
        if(!statis_date.equals(DateUtil.today())){
            Long final_staff_id = staff_id;
            Long final_store_id = store_id;
            ThreadUtil.excAsync(() -> {
                pofitFeignClient.updateReferralStatisData(statis_date, final_staff_id, final_store_id);
            },false);
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return UserLoginVO
     * @author 李道云
     * @methodName: setUserLoginVO
     * @methodDesc: 设置用户登录信息
     * @description:
     * @param: [userInfo]
     * @create 2018-07-02 20:26
     **/
    private UserLoginVO setUserLoginVO(UserInfo userInfo) throws BizException {
        UserLoginVO userLoginVO = new UserLoginVO();
        userLoginVO.setUser_id(userInfo.getId());
        userLoginVO.setPhone_num(userInfo.getPhone_num());
        userLoginVO.setReferral_code(userInfo.getReferral_code());
        //用户地址信息
        UserAddress userAddress = userAddressService.getUserDefaultAddressByUserId(userInfo.getId());
        if(userAddress !=null){
            userLoginVO.setUserAddress(userAddress);
            userLoginVO.setPoi_id(userAddress.getPoi_id());
            userLoginVO.setReceive_name(userAddress.getReceive_name());
            userLoginVO.setReceive_phone(userAddress.getReceive_phone());
            userLoginVO.setLocations(userAddress.getLocations());
            String city_code = userAddress.getCity_code();
            userLoginVO.setCity_code(city_code);
            StringBuffer receive_address = new StringBuffer();
            receive_address.append(userAddress.getProvince_name());
            receive_address.append(userAddress.getCity_name());
            receive_address.append(userAddress.getArea_name());
            receive_address.append(userAddress.getLocation_address());
            receive_address.append(userAddress.getDetail_address());
            userLoginVO.setReceive_address(receive_address.toString());
            //获取服务于用户的门店和仓库
            Result<UserServiceRangeVO> result = staffFeignClient.getUserServiceRange(userAddress.getPoi_id(), city_code);
            List<Long> partyIdList = new ArrayList<>();
            if(result.isSuccess()){
                UserServiceRangeVO userServiceRangeVO = result.getData();
                if(userServiceRangeVO !=null){
                    if(CollectionUtil.isNotEmpty(userServiceRangeVO.getStoreList())){
                        userLoginVO.setStoreList(userServiceRangeVO.getStoreList());
                        for(UserPartyVO userPartyVO : userServiceRangeVO.getStoreList()){
                            partyIdList.add(userPartyVO.getParty_id());
                        }
                    }
                    UserPartyVO warehouse = userServiceRangeVO.getWarehouse();
                    if(warehouse !=null){
                        userLoginVO.setWarehouse(warehouse);
                        partyIdList.add(warehouse.getParty_id());
                    }
                }
            }
            //用户默认门店或仓库
            UserOftenParty userOftenParty = userOftenPartyService.getDefaultParty(userInfo.getId());
            if(userOftenParty !=null){
                if(partyIdList.contains(userOftenParty.getParty_id())){
                    UserPartyVO defaultParty = new UserPartyVO();
                    BeanUtil.copyProperties(userOftenParty,defaultParty);
                    userLoginVO.setDefaultParty(defaultParty);
                }else{//如果没有覆盖了，就删除默认门店或仓库
                    userOftenPartyService.deleteById(userOftenParty.getId());
                }
            }
        }
        return userLoginVO;
    }


    /**
     * @author 罗秋涵
     * @description: 保存用户身份证信息
     * @param: [userIdentityCardVO]
     * @return com.hryj.common.Result
     * @create 2018-09-12 14:27
     **/
    @Transactional
    public Result saveUserIdentityCard(UserIdentityCardVO userIdentityCardVO) {
        log.info("保存用户身份证信息参数:{}", JSON.toJSONString(userIdentityCardVO));
        //参数校验
        if (userIdentityCardVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (userIdentityCardVO.getUser_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "用户编号不能为空");
        }
        if (userIdentityCardVO.getIdentity_card() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "身份证号码不能为空");
        }
        if (userIdentityCardVO.getTrue_name() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "姓名不能为空");
        }
        if(!IdcardUtil.isValidCard(userIdentityCardVO.getIdentity_card())){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请输入有效的身份证号码");
        }
        //根据用户编号
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id", userIdentityCardVO.getUser_id());
        List<UserIdentityCard> cardList = userIdentityCardMapper.selectList(wrapper);
        //用户默认身份证信息
        UserIdentityCard defaultCard=null;
        UserIdentityCard userIdentityCardTmp=null;
        //创建新身份证信息
        UserIdentityCard newUserIdentityCard=new UserIdentityCard();
        newUserIdentityCard.setUser_id(userIdentityCardVO.getUser_id());
        newUserIdentityCard.setTrue_name(userIdentityCardVO.getTrue_name());
        newUserIdentityCard.setIdentity_card(userIdentityCardVO.getIdentity_card());
        //用户无身份证记录
        if(cardList==null||cardList.size()==0){
            log.info("用户无身份证记录直接插入数据");
            //保存
            newUserIdentityCard.setIs_default(true);
            newUserIdentityCard.insert();
        }else{
            //循环获取用户身份证信息
            for(UserIdentityCard userIdentityCard:cardList){
                //获取用户默认身份证信息
                if(userIdentityCard.getIs_default()){
                    defaultCard=userIdentityCard;
                }
                //获得本次添加身份证对应记录
                if(userIdentityCard.getIdentity_card().equals(userIdentityCardVO.getIdentity_card())){
                    userIdentityCardTmp=userIdentityCard;
                }
            }
            //如果没有本次添加的身份证记录则新增身份证记录并设置为默认
            if(defaultCard!=null&&userIdentityCardTmp==null){
                //保存新身份证信息
                newUserIdentityCard.setIs_default(true);
                newUserIdentityCard.insert();
                //取消原默认信息
                UserIdentityCard userIdentityCard=new UserIdentityCard();
                userIdentityCard.setId(defaultCard.getId());
                userIdentityCard.setIs_default(false);
                userIdentityCardMapper.updateById(userIdentityCard);
            }else if(defaultCard!=null&&userIdentityCardTmp!=null){
                //如果本次提交身份证已存且不是默认则设置本次为默认
                if(!defaultCard.equals(userIdentityCardTmp)){
                    //取消原默认
                    defaultCard.setIs_default(false);
                    userIdentityCardMapper.updateById(defaultCard);
                    //设置新默认
                    userIdentityCardTmp.setIs_default(true);
                    userIdentityCardMapper.updateById(userIdentityCardTmp);
                }

            }

        }

        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 罗秋涵
     * @description: 获取用户默认身份证信息
     * @param: [user_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.user.UserIdentityCardVO>
     * @create 2018-09-12 14:28
     **/
    public Result<UserIdentityCardVO> getUserDefaultIdentityCard(Long user_id) {
        if(user_id==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"用户编号不能为空");
        }
        //根据用户编号
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id", user_id);
        wrapper.eq("is_default", true);
        List<UserIdentityCard> cardList = userIdentityCardMapper.selectList(wrapper);
        if(cardList!=null&&cardList.size()>0){
            return new Result(CodeEnum.SUCCESS,cardList.get(0));
        }
        return new Result(CodeEnum.SUCCESS);
    }
}
