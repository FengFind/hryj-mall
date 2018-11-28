package com.hryj.entity.bo.profit;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李道云
 * @className: BackupDeptGroup
 * @description: 备份部门组织表
 * @create 2018/7/11 20:26
 **/
@Data
@TableName("backup_dept_group")
public class BackupDeptGroup extends Model<BackupDeptGroup> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 备份日期
     */
    private String backup_date;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 部门名称
     */
    private String dept_name;
    /**
     * 部门上级id
     */
    private Long dept_pid;
    /**
     * 部门级别
     */
    private Integer dept_level;
    /**
     * 部门类别
     */
    private String dept_type;
    /**
     * 是否为区域公司
     */
    private Integer company_flag;
    /**
     * 是否为末级节点
     */
    private Integer end_flag;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 部门状态
     */
    private Integer dept_status;
    /**
     * 服务提成规则
     */
    private String service_rule;
    /**
     * 分摊成本标识
     */
    private Integer share_cost_flag;
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
