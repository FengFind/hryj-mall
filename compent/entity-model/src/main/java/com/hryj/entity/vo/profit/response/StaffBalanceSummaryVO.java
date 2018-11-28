package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: StaffBalanceSummaryVO
 * @description: 店员结算汇总VO
 * @create 2018/7/9 21:21
 **/
@ApiModel(value = "店员结算汇总VO")
@Data
public class StaffBalanceSummaryVO {


    @ApiModelProperty(value = "店员结算汇总id", required = true)
    private Long staff_balance_summary_id;

    @ApiModelProperty(value = "结算月份", required = true)
    private String balance_month;

    @ApiModelProperty(value = "店员姓名", required = true)
    private String staff_name;

    @ApiModelProperty(value = "岗位名称", required = true)
    private String staff_job_name;

    @ApiModelProperty(value = "承担总成本", required = true)
    private BigDecimal total_cost;

    @ApiModelProperty(value = "服务分润", required = true)
    private BigDecimal service_profit;

    @ApiModelProperty(value = "代下单分润", required = true)
    private BigDecimal help_order_profit;

    @ApiModelProperty(value = "实得分润", required = true)
    private BigDecimal actual_profit;
}
