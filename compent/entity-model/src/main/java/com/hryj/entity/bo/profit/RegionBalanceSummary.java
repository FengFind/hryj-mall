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
 * @className: RegionBalanceSummary
 * @description: 区域公司结算汇总
 * @create 2018/7/11 15:00
 **/
@Data
@TableName("pr_region_balance_summary")
public class RegionBalanceSummary extends Model<RegionBalanceSummary> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 结算月份
     */
    private String balance_month;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 部门名称
     */
    private String dept_name;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 区域工资成本
     */
    private BigDecimal region_salary_cost;
    /**
     * 区域非固定成本
     */
    private BigDecimal region_non_fixed_cost;
    /**
     * 实得分润
     */
    private BigDecimal actual_profit;
    /**
     * 非固定成本设置状态
     */
    private Boolean setup_status;
    /**
     * 门店非固定成本设置状态
     */
    private Boolean store_setup_status;
    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
