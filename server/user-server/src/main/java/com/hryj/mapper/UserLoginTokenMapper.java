package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.user.UserLoginToken;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: UserLoginTokenMapper
 * @description: 用户登录token
 * @create 2018/7/20 21:28
 **/
@Component
public interface UserLoginTokenMapper extends BaseMapper<UserLoginToken> {

}
