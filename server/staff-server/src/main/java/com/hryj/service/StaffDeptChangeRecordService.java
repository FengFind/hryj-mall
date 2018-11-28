package com.hryj.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.profit.StaffDeptChangeRecord;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.bo.staff.user.StaffDeptRelation;
import com.hryj.entity.bo.staff.user.StaffUserInfo;
import com.hryj.mapper.DeptGroupMapper;
import com.hryj.mapper.StaffDeptChangeRecordMapper;
import com.hryj.mapper.StaffUserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 李道云
 * @className: StaffDeptChangeRecordService
 * @description: 员工部门变更记录
 * @create 2018/7/19 22:05
 **/
@Service
@Slf4j
public class StaffDeptChangeRecordService extends ServiceImpl<StaffDeptChangeRecordMapper,StaffDeptChangeRecord> {

    @Autowired
    private DeptGroupMapper deptGroupMapper;

    @Autowired
    private StaffDeptChangeRecordMapper staffDeptChangeRecord;
    @Autowired
    private StaffUserInfoMapper staffUserInfoMapper;
    /**
     * @author 代廷波
     * @description:
     * @param: staff_ids
     * @param: change_type
     * @param: dept_id
     * @param: dept_name
     * @return boolean
     * @create 2018/07/20 11:09
     **/
    @Transactional
    public StaffDeptChangeRecord getAfterStaffDeptChangeRecord(Long staff_id,Long after_dept_id){

        StaffDeptChangeRecord  staffDeptChangeRecord= deptGroupMapper.getAfterStaffDeptChangeRecord(staff_id,after_dept_id);

        return staffDeptChangeRecord;
    }
    /**
     * @author 代廷波
     * @description: 员工转移或者离职
     * @param: list
     * @param: staff_status 变动类型：01-员工离职，02-转移部门
     * @param: dept_id 转移前的组织id
     * @return void
     * @create 2018/07/20 14:36
     **/
    public void saveStaffDeptChangeRecord(List<StaffDeptRelation> list, String staff_status, Long dept_id){

        DeptGroup dept = deptGroupMapper.selectById(dept_id);
        List<StaffDeptChangeRecord> changeRecordList = new ArrayList<>();
        StaffDeptChangeRecord staffDeptChangeRecord=null;
        Date date = new Date();
        if(staff_status.equals("01")){
            StaffUserInfo staffUserInfo =null;
            //员工离职:部门转移和转移后 都是一样
            for (StaffDeptRelation staffDeptRelation : list) {
                staffUserInfo = staffUserInfoMapper.selectById(staffDeptRelation.getStaff_id());
                staffDeptChangeRecord = new StaffDeptChangeRecord();
                staffDeptChangeRecord.setAfter_dept_id(dept.getId());
                staffDeptChangeRecord.setAfter_dept_name(dept.getDept_name());
                staffDeptChangeRecord.setPre_dept_id(dept.getId());
                staffDeptChangeRecord.setPre_dept_name(dept.getDept_name());
                staffDeptChangeRecord.setChange_type(staff_status);// 变动类型：01-员工离职，02-转移部门
                staffDeptChangeRecord.setStaff_name(staffUserInfo.getStaff_name());
                staffDeptChangeRecord.setStaff_id(staffUserInfo.getId());
                staffDeptChangeRecord.setChange_time(date);
                changeRecordList.add(staffDeptChangeRecord);
            }
        }else{
            for (StaffDeptRelation staffDeptRelation : list) {
                staffDeptChangeRecord = deptGroupMapper.getAfterStaffDeptChangeRecord(staffDeptRelation.getStaff_id(),staffDeptRelation.getDept_id());
                staffDeptChangeRecord.setPre_dept_id(dept.getId());//转移前部门id
                staffDeptChangeRecord.setPre_dept_name(dept.getDept_name());//转移前部门名称
                staffDeptChangeRecord.setChange_type(staff_status);// 变动类型：01-员工离职，02-转移部门
                staffDeptChangeRecord.setChange_time(date);
                changeRecordList.add(staffDeptChangeRecord);
            }

        }
        super.insertBatch(changeRecordList);
        log.info("员工转移或者离职：(变动类型01-员工离职02-转移部门)staff_status={},saveStaffDeptChangeRecord======{}",staff_status,dept_id,JSON.toJSONString(changeRecordList));
    }
}
