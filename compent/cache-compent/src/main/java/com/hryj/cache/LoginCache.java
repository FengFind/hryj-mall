package com.hryj.cache;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.user.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author 李道云
 * @className: LoginCache
 * @description: 登录缓存
 * @create 2018/7/2 11:10
 **/
@Component
public class LoginCache {

    private static LoginCache loginCache;

    @Autowired
    private RedisService redisService;

    private static final int USER_LOGIN_TOKEN_TIME = 60*60*24*30;//30天

    private static final int STAFF_ADMIN_LOGIN_TOKEN_TIME = 60*30;//半小时

    private static final int STAFF_APP_LOGIN_TOKEN_TIME = 60*60*24*10;//10天

    @PostConstruct
    public void init() {
        loginCache = this;
        loginCache.redisService = this.redisService;
    }


/*************************************************用户登录信息缓存*******************************************************/

    /**
     * @author 李道云
     * @description: 设置用户登录信息到缓存
     * @param: [login_token, userLoginVO]
     * @return void
     * @create 2018-06-27 8:59
     **/
    public static void setUserLoginVO(String login_token,UserLoginVO userLoginVO){
        loginCache.redisService.put1(CacheGroup.USER_LOGIN.getValue(),login_token, JSON.toJSONString(userLoginVO,SerializerFeature.DisableCircularReferenceDetect), USER_LOGIN_TOKEN_TIME);
    }

    /**
     * @author 李道云
     * @description: 获取用户登录信息
     * @param: [login_token]
     * @return com.hryj.entity.vo.user.UserLoginVO
     * @create 2018-06-27 8:55
     **/
    public static UserLoginVO getUserLoginVO(String login_token){
        String value = loginCache.redisService.get1(CacheGroup.USER_LOGIN.getValue(),login_token);
        if(StrUtil.isEmpty(value)){
            return null;
        }
        return JSON.parseObject(value, UserLoginVO.class);
    }

    /**
     * @author 李道云
     * @methodName: clearUserLoginVO
     * @methodDesc: 移除用户登录信息
     * @description:
     * @param: [login_token]
     * @return void
     * @create 2018-07-02 21:14
     **/
    public static void clearUserLoginVO(String login_token){
        loginCache.redisService.delete1(CacheGroup.USER_LOGIN.getValue(),login_token);
    }

    /**
     * @author 李道云
     * @description: 刷新用户登录的时间
     * @param: [login_token]
     * @return void
     * @create 2018-06-27 9:07
     **/
    public static void expireUserLoginTime(String login_token){
        loginCache.redisService.expire1(CacheGroup.USER_LOGIN.getValue(),login_token,USER_LOGIN_TOKEN_TIME);
    }

/************************************************员工后台登录信息缓存*****************************************************/

    /**
     * @author 李道云
     * @description: 设置员工后台登录信息到缓存
     * @param: [login_token, staffAdminLoginVO]
     * @return void
     * @create 2018-06-27 8:59
     **/
    public static void setStaffAdminLoginVO(String login_token,StaffAdminLoginVO staffAdminLoginVO){
        loginCache.redisService.put1(CacheGroup.STAFF_ADMIN_LOGIN.getValue(),login_token, JSON.toJSONString(staffAdminLoginVO,SerializerFeature.DisableCircularReferenceDetect), STAFF_ADMIN_LOGIN_TOKEN_TIME);
    }

    /**
     * @author 李道云
     * @description: 获取员工后台登录信息
     * @param: [login_token]
     * @return com.hryj.entity.vo.user.StaffAdminLoginVO
     * @create 2018-06-27 8:55
     **/
    public static StaffAdminLoginVO getStaffAdminLoginVO(String login_token){
        String value = loginCache.redisService.get1(CacheGroup.STAFF_ADMIN_LOGIN.getValue(),login_token);
        if(StrUtil.isEmpty(value)){
            return null;
        }
        return JSON.parseObject(value, StaffAdminLoginVO.class);
    }

    /**
     * @author 李道云
     * @methodName: clearStaffAdminLoginVO
     * @methodDesc: 移除员工后台登录信息
     * @description:
     * @param: [login_token]
     * @return void
     * @create 2018-07-02 21:14
     **/
    public static void clearStaffAdminLoginVO(String login_token){
        loginCache.redisService.delete1(CacheGroup.STAFF_ADMIN_LOGIN.getValue(),login_token);
    }

    /**
     * @author 李道云
     * @description: 刷新员工后台登录的时间
     * @param: [login_token]
     * @return void
     * @create 2018-06-27 9:07
     **/
    public static void expireStaffAdminLoginTime(String login_token){
        loginCache.redisService.expire1(CacheGroup.STAFF_ADMIN_LOGIN.getValue(),login_token,STAFF_ADMIN_LOGIN_TOKEN_TIME);
    }

/************************************************员工App登录信息缓存*****************************************************/

    /**
     * @author 李道云
     * @description: 设置员工APP登录信息到缓存
     * @param: [login_token, StaffAppLoginVO]
     * @return void
     * @create 2018-06-27 8:59
     **/
    public static void setStaffAppLoginVO(String login_token,StaffAppLoginVO staffAppLoginVO){
        loginCache.redisService.put1(CacheGroup.STAFF_APP_LOGIN.getValue(),login_token, JSON.toJSONString(staffAppLoginVO,SerializerFeature.DisableCircularReferenceDetect), STAFF_APP_LOGIN_TOKEN_TIME);
    }

    /**
     * @author 李道云
     * @description: 获取员工APP登录信息
     * @param: [login_token]
     * @return com.hryj.entity.vo.user.StaffAppLoginVO
     * @create 2018-06-27 8:55
     **/
    public static StaffAppLoginVO getStaffAppLoginVO(String login_token){
        String value = loginCache.redisService.get1(CacheGroup.STAFF_APP_LOGIN.getValue(),login_token);
        if(StrUtil.isEmpty(value)){
            return null;
        }
        return JSON.parseObject(value,StaffAppLoginVO.class);
    }

    /**
     * @author 李道云
     * @methodName: clearStaffAppLoginVO
     * @methodDesc: 移除员工APP登录信息
     * @description:
     * @param: [login_token]
     * @return void
     * @create 2018-07-02 21:14
     **/
    public static void clearStaffAppLoginVO(String login_token){
        loginCache.redisService.delete1(CacheGroup.STAFF_APP_LOGIN.getValue(),login_token);
    }

    /**
     * @author 李道云
     * @description: 刷新员工APP登录的时间
     * @param: [login_token]
     * @return void
     * @create 2018-06-27 9:07
     **/
    public static void expireStaffAppLoginTime(String login_token){
        loginCache.redisService.expire1(CacheGroup.STAFF_APP_LOGIN.getValue(),login_token,STAFF_APP_LOGIN_TOKEN_TIME);
    }
}
