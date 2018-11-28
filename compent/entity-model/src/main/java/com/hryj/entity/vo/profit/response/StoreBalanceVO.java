package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: StoreBalanceVO
 * @description: 门店结算VO
 * @create 2018/7/9 20:23
 **/
@ApiModel(value = "门店结算VO")
@Data
public class StoreBalanceVO {

    @ApiModelProperty(value = "结算汇总id", required = true)
    private Long summary_id;

    @ApiModelProperty(value = "结算月份", required = true)
    private String balance_month;

    @ApiModelProperty(value = "门店id", required = true)
    private Long store_id;

    @ApiModelProperty(value = "门店名称", required = true)
    private String store_name;

    @ApiModelProperty(value = "区域公司工资成本", required = true)
    private BigDecimal region_salary_cost;

    @ApiModelProperty(value = "区域公司非固定成本")
    private BigDecimal region_non_fixed_cost;

    @ApiModelProperty(value = "门店固定成本", required = true)
    private BigDecimal store_fixed_cost;

    @ApiModelProperty(value = "门店非固定成本")
    private BigDecimal store_non_fixed_cost;

    @ApiModelProperty(value = "门店工资成本", required = true)
    private BigDecimal store_salary_cost;

    @ApiModelProperty(value = "门店配送成本", required = true)
    private BigDecimal store_distribution_cost;

    @ApiModelProperty(value = "设置状态", required = true)
    private Integer setup_status;

    @ApiModelProperty(value = "区域公司设置状态", required = true)
    private Integer region_setup_status;

}
