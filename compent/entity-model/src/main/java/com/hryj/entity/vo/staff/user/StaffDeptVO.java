package com.hryj.entity.vo.staff.user;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: StaffDeptVO
 * @description: 员工部门信息VO
 * @create 2018/7/6 12:13
 **/
@Data
public class StaffDeptVO implements Serializable {

    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工账号
     */
    private String staff_account;
    /**
     * 手机号码
     */
    private String phone_num;
    /**
     * 员工姓名
     */
    private String staff_name;
    /**
     * 推荐码
     */
    private String referral_code;
    /**
     * 员工类别
     */
    private String staff_type;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 部门类别
     */
    private String dept_type;
    /**
     * 部门名称
     */
    private String dept_name;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 员工岗位
     */
    private String staff_job;
    /**
     * 员工岗位名称
     */
    private String staff_job_name;
    /**
     * 员工状态
     */
    private Boolean staff_status;
}
