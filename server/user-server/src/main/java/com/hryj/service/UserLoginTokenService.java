package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.entity.bo.user.UserLoginToken;
import com.hryj.mapper.UserLoginTokenMapper;
import org.springframework.stereotype.Service;

/**
 * @author 李道云
 * @className: UserLoginTokenService
 * @description: 用户登录token
 * @create 2018/7/20 21:36
 **/
@Service
public class UserLoginTokenService extends ServiceImpl<UserLoginTokenMapper,UserLoginToken> {

    /**
     * @author 李道云
     * @methodName: updateUserLoginToken
     * @methodDesc: 更新用户登录token
     * @description:
     * @param: [app_key, user_id, login_token, device_id]
     * @return void
     * @create 2018-07-20 21:37
     **/
    public void updateUserLoginToken(String app_key,Long user_id,String login_token,String device_id){
        EntityWrapper<UserLoginToken> wrapper = new EntityWrapper<>();
        wrapper.eq("user_id",user_id);
        UserLoginToken userLoginToken = super.selectOne(wrapper);
        if(userLoginToken ==null){
            userLoginToken = new UserLoginToken();
        }else{
            String old_login_token = userLoginToken.getLogin_token();
            if(StrUtil.isNotEmpty(old_login_token)){
                LoginCache.clearUserLoginVO(old_login_token);
            }
        }
        userLoginToken.setApp_key(app_key);
        userLoginToken.setUser_id(user_id);
        userLoginToken.setLogin_token(login_token);
        userLoginToken.setLogin_time(DateUtil.date());
        userLoginToken.setDevice_id(device_id);
        super.insertOrUpdate(userLoginToken);
    }
}
