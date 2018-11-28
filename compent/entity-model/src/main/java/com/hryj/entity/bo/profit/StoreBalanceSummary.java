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
 * @className: StoreBalanceSummary
 * @description: 门店结算汇总
 * @create 2018/7/11 15:44
 **/
@Data
@TableName("pr_store_balance_summary")
public class StoreBalanceSummary extends Model<StoreBalanceSummary> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 结算月份
     */
    private String balance_month;
    /**
     * 所属区域公司
     */
    private Long region_id;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 门店名称
     */
    private String store_name;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 区域公司工资成本
     */
    private BigDecimal region_salary_cost;
    /**
     * 区域公司非固定成本
     */
    private BigDecimal region_non_fixed_cost;
    /**
     * 门店固定成本
     */
    private BigDecimal store_fixed_cost;
    /**
     * 门店非固定成本
     */
    private BigDecimal store_non_fixed_cost;
    /**
     * 门店工资成本
     */
    private BigDecimal store_salary_cost;
    /**
     * 门店配送成本
     */
    private BigDecimal store_distribution_cost;
    /**
     * 非固定成本设置状态
     */
    private Boolean setup_status;
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
