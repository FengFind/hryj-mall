package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: RegionBalanceVO
 * @description: 区域公司结算VO
 * @create 2018/7/9 20:23
 **/
@ApiModel(value = "区域公司结算VO")
@Data
public class RegionBalanceVO {

    @ApiModelProperty(value = "结算汇总id", required = true)
    private Long summary_id;

    @ApiModelProperty(value = "结算月份", required = true)
    private String balance_month;

    @ApiModelProperty(value = "部门id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "部门名称", required = true)
    private String dept_name;

    @ApiModelProperty(value = "区域公司工资成本", required = true)
    private BigDecimal region_salary_cost;

    @ApiModelProperty(value = "区域公司非固定成本")
    private BigDecimal region_non_fixed_cost;

    @ApiModelProperty(value = "实得分润", notes = "结算前无值")
    private BigDecimal actual_profit;

    @ApiModelProperty(value = "设置状态", required = true)
    private Integer setup_status;

    @ApiModelProperty(value = "门店设置状态", required = true)
    private Integer store_setup_status;


}
