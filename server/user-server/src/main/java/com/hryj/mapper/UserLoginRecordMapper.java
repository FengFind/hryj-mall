package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.user.UserLoginRecord;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: UserLoginRecordMapper
 * @description: 用户登录记录mapper
 * @create 2018/6/26 10:46
 **/
@Component
public interface UserLoginRecordMapper extends BaseMapper<UserLoginRecord> {
}
