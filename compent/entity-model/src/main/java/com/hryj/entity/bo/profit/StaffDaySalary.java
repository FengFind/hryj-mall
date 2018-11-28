package com.hryj.entity.bo.profit;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李道云
 * @className: StaffDaySalary
 * @description: 员工日工资记录
 * @create 2018/7/11 16:14
 **/
@Data
@TableName("pr_staff_day_salary")
public class StaffDaySalary extends Model<StaffDaySalary> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 工资日期
     */
    private String salary_date;
    /**
     * 所属区域公司
     */
    private Long region_id;
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
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工类别
     */
    private String staff_type;
    /**
     * 员工岗位
     */
    private String staff_job;
    /**
     * 员工姓名
     */
    private String staff_name;
    /**
     * 日工资
     */
    private BigDecimal day_salary;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
