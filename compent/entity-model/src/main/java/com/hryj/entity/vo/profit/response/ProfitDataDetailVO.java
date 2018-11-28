package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: ProfitDataDetailVO
 * @description: 分润数据明细VO
 * @create 2018/7/9 16:48
 **/
@ApiModel(value = "分润数据明细VO")
@Data
public class ProfitDataDetailVO implements Serializable {

    @ApiModelProperty(value = "结算月份", required = true)
    private String balance_month;

    @ApiModelProperty(value = "结算日期", required = true)
    private String balance_date;

    @ApiModelProperty(value = "可结算分润", notes = "结算前无值")
    private BigDecimal actual_profit;

    @ApiModelProperty(value = "当月成本", required = true)
    private BigDecimal this_month_cost;

    @ApiModelProperty(value = "上月遗留成本", required = true)
    private BigDecimal last_month_cost;

    @ApiModelProperty(value = "毛利润总额", required = true)
    private BigDecimal gross_profit;

    @ApiModelProperty(value = "服务分润", required = true)
    private BigDecimal service_profit;

    @ApiModelProperty(value = "代下单分润", required = true)
    private BigDecimal help_order_profit;

    @ApiModelProperty(value = "配送配送分润", required = true)
    private BigDecimal distribution_profit;

    @ApiModelProperty(value = "配送订单数量", required = true)
    private Integer distribution_num;

}
