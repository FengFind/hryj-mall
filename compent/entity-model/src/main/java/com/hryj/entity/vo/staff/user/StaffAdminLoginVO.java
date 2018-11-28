package com.hryj.entity.vo.staff.user;

import com.hryj.entity.bo.staff.dept.DeptGroup;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: StaffAdminLoginVO
 * @description: 员工后台登录VO
 * @create 2018/6/28 9:04
 **/
@Data
public class StaffAdminLoginVO implements Serializable {

    private static final long serialVersionUID = 6649688555569295014L;

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
    /**
     * 权限名称集合:","分隔
     */
    private String permNameList;
    /**
     * 权限标识集合:","分隔
     */
    private String permFlagList;
    /**
     * 权限url集合:","分隔
     */
    private String permUrlList;
    /**
     * 门店id集合:","分隔
     */
    private String storeIdList;
    /**
     * 仓库id集合:","分隔
     */
    private String whIdList;

}
