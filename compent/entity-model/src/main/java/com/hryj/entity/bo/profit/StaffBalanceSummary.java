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
 * @className: StaffBalanceSummary
 * @description: 员工结算汇总
 * @create 2018/7/11 17:43
 **/
@Data
@TableName("pr_staff_balance_summary")
public class StaffBalanceSummary extends Model<StaffBalanceSummary> {

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
     * 员工id
     */
    private Long staff_id;
    /**
     * 员工姓名
     */
    private String staff_name;
    /**
     * 岗位名称代码
     */
    private String staff_job;
    /**
     * 岗位名称
     */
    private String staff_job_name;
    /**
     * 转移成本
     */
    private BigDecimal transfer_cost;
    /**
     * 上月剩余成本
     */
    private BigDecimal last_month_cost;
    /**
     * 本月成本
     */
    private BigDecimal this_month_cost;
    /**
     * 承担总成本
     */
    private BigDecimal total_cost;
    /**
     * 服务分润
     */
    private BigDecimal service_profit;
    /**
     * 代下单分润
     */
    private BigDecimal help_order_profit;
    /**
     * 配送分润
     */
    private BigDecimal distribution_profit;
    /**
     * 配送单数量
     */
    private Integer distribution_num;
    /**
     * 总分润:没有剔除成本
     */
    private BigDecimal total_profit;
    /**
     * 实得分润
     */
    private BigDecimal actual_profit;
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
