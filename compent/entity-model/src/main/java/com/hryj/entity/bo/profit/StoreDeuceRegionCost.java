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
 * @className: StoreDeuceRegionCost
 * @description: 门店平摊区域公司的成本
 * @create 2018/7/11 18:30
 **/
@Data
@TableName("pr_store_deuce_region_cost")
public class StoreDeuceRegionCost extends Model<StoreDeuceRegionCost> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 计算日期
     */
    private String cal_date;
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
     * 区域公司id
     */
    private Long dept_id;
    /**
     * 区域公司名称
     */
    private String dept_name;
    /**
     * 平摊区域公司的工资成本
     */
    private BigDecimal dept_salary_cost;
    /**
     * 平摊区域公司的非固定成本
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
