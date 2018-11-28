package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.staff.user.StaffLoginToken;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: StaffLoginTokenMapper
 * @description: 员工登录token
 * @create 2018/7/20 21:43
 **/
@Component
public interface StaffLoginTokenMapper extends BaseMapper<StaffLoginToken> {
}
