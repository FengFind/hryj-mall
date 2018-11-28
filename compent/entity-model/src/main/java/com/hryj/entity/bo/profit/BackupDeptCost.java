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
 * @className: BackupDeptCost
 * @description: 备份部门成本表
 * @create 2018/7/11 20:13
 **/
@Data
@TableName("backup_dept_cost")
public class BackupDeptCost extends Model<BackupDeptCost> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 备份月份
     */
    private String backup_month;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 成本名称
     */
    private String cost_name;
    /**
     * 成本金额
     */
    private BigDecimal cost_amt;
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
