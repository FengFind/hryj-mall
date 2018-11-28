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
 * @className: StaffCostDetail
 * @description: 员工成本明细
 * @create 2018/7/11 18:56
 **/
@Data
@TableName("pr_staff_cost_detail")
public class StaffCostDetail extends Model<StaffCostDetail> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 计算日期
     */
    private String cal_date;
    /**
     * 所属区域公司id
     */
    private Long region_id;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工名称
     */
    private String staff_name;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 门店名称
     */
    private String store_name;
    /**
     * 平摊门店的工资成本
     */
    private BigDecimal store_salary_cost;
    /**
     * 平摊门店的固定成本
     */
    private BigDecimal store_fixed_cost;
    /**
     * 平摊门店的非固定成本
     */
    private BigDecimal store_non_fixed_cost;
    /**
     * 平摊门店的配送成本
     */
    private BigDecimal store_distribution_cost;
    /**
     * 平摊门店的区域公司工资成本
     */
    private BigDecimal dept_salary_cost;
    /**
     * 平摊门店的区域公司非固定成本
     */
    private BigDecimal dept_non_fixed_cost;
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
