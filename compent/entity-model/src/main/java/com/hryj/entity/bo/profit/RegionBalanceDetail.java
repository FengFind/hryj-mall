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
 * @className: RegionBalanceDetail
 * @description: 区域公司结算明细
 * @create 2018/7/11 15:51
 **/
@Data
@TableName("pr_region_balance_detail")
public class RegionBalanceDetail extends Model<RegionBalanceDetail> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 结算汇总id
     */
    private Long summary_id;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 部门名称
     */
    private String dept_name;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工姓名
     */
    private String staff_name;
    /**
     * 岗位名称
     */
    private String staff_job;
    /**
     * 工资金额:当月的累计工资
     */
    private BigDecimal salary_amt;
    /**
     * 实得分润
     */
    private BigDecimal actual_profit;
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
