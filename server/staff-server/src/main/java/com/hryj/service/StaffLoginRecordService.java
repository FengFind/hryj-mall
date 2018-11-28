package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.staff.user.StaffLoginRecord;
import com.hryj.entity.vo.RequestVO;
import com.hryj.mapper.StaffLoginRecordMapper;
import org.springframework.stereotype.Service;

/**
 * @author 李道云
 * @className: StaffLoginRecordService
 * @description: 员工登录记录service
 * @create 2018/7/28 11:57
 **/
@Service
public class StaffLoginRecordService extends ServiceImpl<StaffLoginRecordMapper,StaffLoginRecord> {

    /**
     * @author 李道云
     * @methodName: saveStaffLoginRecord
     * @methodDesc: 保存员工登录记录
     * @description:
     * @param: [staff_id, requestVO]
     * @return void
     * @create 2018-07-28 12:19
     **/
    public void saveStaffLoginRecord(Long staff_id, RequestVO requestVO){
        StaffLoginRecord staffLoginRecord = new StaffLoginRecord();
        staffLoginRecord.setStaff_id(staff_id);
        staffLoginRecord.setCall_source(requestVO.getCall_source());
        staffLoginRecord.setApp_channel(requestVO.getApp_channel());
        staffLoginRecord.setApp_version(requestVO.getApp_version());
        staffLoginRecord.setDevice_id(requestVO.getDevice_id());
        staffLoginRecord.setDevice_model(requestVO.getDevice_model());
        staffLoginRecord.setOs_version(requestVO.getOs_version());
        staffLoginRecord.setLogin_ip(requestVO.getDevice_ip());
        baseMapper.insert(staffLoginRecord);
    }
}
