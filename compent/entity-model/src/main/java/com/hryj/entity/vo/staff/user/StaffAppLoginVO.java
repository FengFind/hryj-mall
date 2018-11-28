package com.hryj.entity.vo.staff.user;

import com.hryj.entity.bo.staff.dept.DeptGroup;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: StaffAppLoginVO
 * @description: 员工APP登录VO
 * @create 2018/6/28 9:04
 **/
@Data
public class StaffAppLoginVO implements Serializable {

    private static final long serialVersionUID = 6012151249166755578L;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工账号
     */
    private String staff_account;
    /**
     * 员工类别:01-普通员工,02-内置员工
     */
    private String staff_type;
    /**
     * 员工岗位
     */
    private String staff_job;
    /**
     * 员工岗位名称
     */
    private String staff_job_name;
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
     * 员工照片
     */
    private String staff_pic;
    /**
     * 员工部门信息
     */
    private DeptGroup deptGroup;
}
