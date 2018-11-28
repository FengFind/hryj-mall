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
 * @className: BackupDeptShareConfig
 * @description: 备份部门分润配置表
 * @create 2018/7/11 20:49
 **/
@Data
@TableName("backup_dept_share_config")
public class BackupDeptShareConfig extends Model<BackupDeptShareConfig> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 备份月份
     */
    private String backup_month;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 分润比例
     */
    private BigDecimal share_ratio;
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
