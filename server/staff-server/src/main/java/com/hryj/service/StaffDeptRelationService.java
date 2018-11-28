package com.hryj.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.bo.staff.dept.DeptShareConfig;
import com.hryj.entity.bo.staff.user.StaffDeptRelation;
import com.hryj.entity.vo.staff.dept.request.DeptStaffListRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptStaffRequestVO;
import com.hryj.exception.GlobalException;
import com.hryj.mapper.DeptShareConfigMapper;
import com.hryj.mapper.StaffDeptRelationMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 代廷波
 * @className: StaffDeptRelationService
 * @description: 员工组织关系
 * @create 2018/6/28 0028-10:07
 **/
@Service
@Slf4j
public class StaffDeptRelationService extends ServiceImpl<StaffDeptRelationMapper, StaffDeptRelation> {
    @Autowired
    private DeptShareConfigMapper deptShareConfigMapper;
    @Autowired
    private StaffDeptChangeRecordService staffDeptChangeRecordService;

    @Autowired
    private StaffLoginTokenService staffLoginTokenService;

    /**
     * @return com.hryj.entity.bo.staff.user.StaffDeptRelation
     * @author 李道云
     * @methodName: getStaffDeptRelation
     * @methodDesc: 查询员工部门组织关系
     * @description:
     * @param: [staff_id]
     * @create 2018-07-03 8:59
     **/
    public StaffDeptRelation getStaffDeptRelation(Long staff_id) {
        EntityWrapper<StaffDeptRelation> wrapper = new EntityWrapper<>();
        wrapper.eq("staff_id", staff_id);
        return super.selectOne(wrapper);
    }

    /**
     * @return void
     * @author 代廷波
     * @description: 保存新增部门员工
     * @param: deptStaffDetRequestVOList
     * @create 2018/06/30 17:34
     **/
    @Transactional
    public void saveDeptStaff(List<DeptStaffListRequestVO> deptStaffDetRequestVOList, Long dept_id, Long operator_id) throws GlobalException {
        List<StaffDeptRelation> insert_list = new ArrayList<>();
        List<StaffDeptRelation> update_list = new ArrayList<>();

        StaffDeptRelation dept_staff = null;
        Map<Long, Long> dept_staff_ids_map = new HashMap<>();


        //判断员工是否以前离过职,离职的员工只需要将状态改回来
        int size = deptStaffDetRequestVOList.size();
        Long[] staff_ids = new Long[size];
        for (int i = 0; i < size; i++) {
            staff_ids[i] = deptStaffDetRequestVOList.get(i).getStaff_id();
        }
        EntityWrapper<StaffDeptRelation> staffInsert = new EntityWrapper<StaffDeptRelation>();
        staffInsert.in("staff_id", staff_ids);
        List<StaffDeptRelation> staffOdList = super.selectList(staffInsert);
        if (null != staffOdList && staffOdList.size() > 0) {
            for (StaffDeptRelation vo : staffOdList) {
                dept_staff_ids_map.put(vo.getStaff_id(), vo.getId());//员工id,关系主键id
            }
        }
        Date date = new Date();
        for (DeptStaffListRequestVO vo : deptStaffDetRequestVOList) {
            dept_staff = new StaffDeptRelation();
            dept_staff.setDept_id(dept_id);//部门
            if(SysCodeEnmu.SAFFTYPE_01.getCodeValue().equals(vo.getStaff_type())){
                dept_staff.setSalary_amt(vo.getSalary_amt());//工资
            }
            dept_staff.setStaff_status(true);//正常
            dept_staff.setStaff_id(vo.getStaff_id());//员工id
            dept_staff.setOperator_id(operator_id);//操作人
            if (dept_staff_ids_map.containsKey(vo.getStaff_id())) {//数据库有的
                dept_staff.setId(dept_staff_ids_map.get(vo.getStaff_id()));//设置主键数据
                dept_staff.setUpdate_time(date);
                dept_staff.setDept_id(dept_id);//设置部门id
                update_list.add(dept_staff);
            } else {
                insert_list.add(dept_staff);
            }

        }
        if (insert_list.size() > 0) {
            super.insertBatch(insert_list);
        }
        if (update_list.size() > 0) {
            super.updateBatchById(update_list);
        }
    }
    /**
     * @author 代廷波
     * @description: 保存部门员工
     * @param: operator_id 操作人
     * @param: deptStaffRequestVO 员工对象
     * @return com.hryj.common.Result
     * @create 2018/07/27 17:55
     **/
    @Transactional
    public Result saveDeptStaff(Long operator_id, DeptStaffRequestVO deptStaffRequestVO) {

        //新增员工
        List<StaffDeptRelation> insert_list = new ArrayList<>();
        //修改员工
        List<StaffDeptRelation> update_list = new ArrayList<>();
        //转移员工
        List<StaffDeptRelation> move_list = new ArrayList<>();
        //删除员工
        List<StaffDeptRelation> del_list = new ArrayList<>();
        //转移员工id集合
        List<Long> move_staff_ids = new ArrayList<>();
        //删除员工id集合
        List<Long> del_staff_ids = new ArrayList<>();
        List<Long> new_stff_ids = new ArrayList<>();//前端传过来的新增员工id
        List<Long> toke_stff_ids = new ArrayList<>();//转移和删除员工集合

        StaffDeptRelation dept_staff = null;
        Map<Long, Long> db_staff_list = new HashMap<>();//当前组织数据有的员工 map.key=员工id,map.value=员工组织关系表主建id
        Map<Long, Long> update_staff_status_ids = new HashMap<>();//需要将离职员工变成在职的员工
        Date date = new Date();
        EntityWrapper<StaffDeptRelation> wrapper = null;

        //如果是修改,先查找当前门店的员工,将当前门店的员工 id 放入map中
        wrapper = new EntityWrapper<>();
        wrapper.where("dept_id={0}", deptStaffRequestVO.getDept_id()).and("staff_status={0}", 1);
        List<StaffDeptRelation> staffList = super.selectList(wrapper);//当前门店数据库中员工列表
        for (StaffDeptRelation db : staffList) {
            db_staff_list.put(db.getStaff_id(), db.getId());//员工id
        }

        //前端传过来的新增员工id
        for (DeptStaffListRequestVO storeList : deptStaffRequestVO.getDeptStaffListRequestVOList()) {
            if (!db_staff_list.containsKey(storeList.getStaff_id())) {
                new_stff_ids.add(storeList.getStaff_id());//map.key=员工id,map.value=员工组织关系表主建id
            }
        }
        if (new_stff_ids.size() > 0) {
            //需要将离职的员工变成在职的员工
            wrapper = new EntityWrapper<>();
            wrapper.in("staff_id", new_stff_ids);
            List<StaffDeptRelation> staffOdList = super.selectList(wrapper);
            if (null != staffOdList && staffOdList.size() > 0) {
                for (StaffDeptRelation vo : staffOdList) {
                    update_staff_status_ids.put(vo.getStaff_id(), vo.getId());//员工id,关系主键id
                }
            }
        }
        for (DeptStaffListRequestVO vo : deptStaffRequestVO.getDeptStaffListRequestVOList()) {
            dept_staff = new StaffDeptRelation();
            if(SysCodeEnmu.SAFFTYPE_01.getCodeValue().equals(vo.getStaff_type())){//普通员工
                dept_staff.setSalary_amt(vo.getSalary_amt());//工资
            }
            dept_staff.setStaff_id(vo.getStaff_id());//员工id
            dept_staff.setStaff_status(true);//状态正常
            dept_staff.setOperator_id(operator_id);//操作人
            dept_staff.setDept_id(deptStaffRequestVO.getDept_id());
            dept_staff.setUpdate_time(date);//修改时间
            dept_staff.setDept_id(deptStaffRequestVO.getDept_id());//组织id
            if (db_staff_list.size() > 0) {
                if (db_staff_list.containsKey(vo.getStaff_id())) {//数据库有的员工 为修改
                    dept_staff.setId(db_staff_list.get(vo.getStaff_id()));//设置主键
                    db_staff_list.remove(vo.getStaff_id());//删除 map中的员工id key
                    //转移员工
                    if (null != deptStaffRequestVO.getDept_id() && (deptStaffRequestVO.getDept_id().longValue() != vo.getDept_id().longValue())) {
                        dept_staff.setDept_id(vo.getDept_id());
                        move_list.add(dept_staff);
                        move_staff_ids.add(vo.getStaff_id());
                        toke_stff_ids.add(vo.getStaff_id());
                    } else {
                        update_list.add(dept_staff);
                    }
                } else {
                    if (update_staff_status_ids.containsKey(vo.getStaff_id())) {//数据库有的
                        dept_staff.setId(update_staff_status_ids.get(vo.getStaff_id()));//设置主键数据
                        update_list.add(dept_staff);
                    } else {
                        insert_list.add(dept_staff);
                    }
                }
                //组织没有员工全部是新增
            } else {
                if (update_staff_status_ids.containsKey(vo.getStaff_id())) {//数据库有的员工
                    dept_staff.setId(update_staff_status_ids.get(vo.getStaff_id()));//设置主键
                    update_list.add(dept_staff);
                } else {
                    insert_list.add(dept_staff);
                }
            }
        }
        //map剩下的员工表示是要删除的员工
        if (db_staff_list.size() > 0) {
            Iterator<Map.Entry<Long, Long>> iterator = db_staff_list.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, Long> entry = iterator.next();
                dept_staff = new StaffDeptRelation();
                dept_staff.setStaff_status(false);//员工状态:1-正常,0-离职
                dept_staff.setId(entry.getValue());
                dept_staff.setStaff_id(entry.getKey());
                if (entry.getKey().longValue() == 100000L) {//超级管理员
                    continue;
                }
                del_list.add(dept_staff);
                del_staff_ids.add(entry.getKey());//删除的员工集合
                toke_stff_ids.add(entry.getKey());
            }

        }
        //校验删除 员工
        if (del_staff_ids.size() > 0) {
            if (validatorShareStaff(del_staff_ids)) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "分润员工不能被删除");
            }
        }
        //校验转移员工
        if (move_staff_ids.size() > 0) {
            if (validatorShareStaff(move_staff_ids)) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "分润员工不能被转移");
            }
        }
        if (del_list.size() > 0) {
            super.updateBatchById(del_list);
            //变动类型：01-员工离职，02-转移部门
            //添加离职员工记录
            staffDeptChangeRecordService.saveStaffDeptChangeRecord(del_list, "01", deptStaffRequestVO.getDept_id());
            log.info("部门删除部门员工:dept_id={},operator_id={},del_staff_ids={}", deptStaffRequestVO.getDept_id(), operator_id, JSON.toJSON(del_list));

        }

        if (move_list.size() > 0) {
            // 添加转移员工记录
            super.updateBatchById(move_list);
            //添加转移员工记录
            staffDeptChangeRecordService.saveStaffDeptChangeRecord(move_list, "02", deptStaffRequestVO.getDept_id());

            log.info("删除部门员工:dept_id={},operator_id={},del_staff_ids={}", deptStaffRequestVO.getDept_id(), operator_id, JSON.toJSON(del_list));
        }

        if (insert_list.size() > 0) {
            super.insertBatch(insert_list);
        }
        if (update_list.size() > 0) {
            super.updateBatchById(update_list);
        }
        //删除和转移员工 清除toke
        if(toke_stff_ids.size()>0){
            staffLoginTokenService.updateClearStaffLoginTokenBatch(toke_stff_ids);
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return java.lang.Boolean
     * @author 代廷波
     * @description: 校验是不是分润员工
     * @param: ids 员工id集合
     * @create 2018/07/26 11:13
     **/
    public Boolean validatorShareStaff(List<Long> ids) {
        EntityWrapper<DeptShareConfig> shareConfig_ew = new EntityWrapper<DeptShareConfig>();
        shareConfig_ew.in("staff_id", ids);
        List<DeptShareConfig> list = deptShareConfigMapper.selectList(shareConfig_ew);
        if (list.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void updateClearStaffLoginToken(Long dept_id) {
        EntityWrapper<StaffDeptRelation> store_wrapper=null;

        //如果是修改,先查找当前门店的员工,将当前门店的员工 id 放入map中
        store_wrapper = new EntityWrapper<StaffDeptRelation>();
        store_wrapper.where("dept_id={0}", dept_id).and("staff_status={0}", 1);
        List<StaffDeptRelation>  staffList = super.selectList(store_wrapper);//当前门店数据库中员工列表
        List<Long> staff_ids=new ArrayList<>();
        if(staffList.size()>0) {
            for (StaffDeptRelation db : staffList) {
                staff_ids.add(db.getStaff_id());//员工id
            }
            staffLoginTokenService.updateClearStaffLoginTokenBatch(staff_ids);
        }
    }

}
