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
 * @className: BackupStaffDeptRelation
 * @description: 备份员工部门组织关系表
 * @create 2018/7/11 20:53
 **/
@Data
@TableName("backup_staff_dept_relation")
public class BackupStaffDeptRelation extends Model<BackupStaffDeptRelation> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 备份日期
     */
    private String backup_date;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工姓名
     */
    private String staff_name;
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
     * 员工类型
     */
    private String staff_type;
    /**
     * 岗位名称
     */
    private String staff_job;
    /**
     * 工资金额
     */
    private BigDecimal salary_amt;
    /**
     * 代下单提成比例
     */
    private BigDecimal help_order_ratio;
    /**
     * 服务提成比例
     */
    private BigDecimal service_ratio;
    /**
     * 配送单价
     */
    private BigDecimal distribution_amt;
    /**
     * 成本分摊比例
     */
    private BigDecimal share_cost_ratio;
    /**
     * 员工状态
     */
    private Integer staff_status;
    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 备份时间
     */
    private Date backup_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
