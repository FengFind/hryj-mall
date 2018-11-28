package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.staff.user.StaffLoginRecord;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: StaffLoginRecordMapper
 * @description: 员工登录记录
 * @create 2018/7/28 11:55
 **/
@Component
public interface StaffLoginRecordMapper extends BaseMapper<StaffLoginRecord> {
}
