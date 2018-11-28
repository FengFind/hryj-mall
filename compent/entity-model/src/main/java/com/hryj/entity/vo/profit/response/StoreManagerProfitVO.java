package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: StoreManagerProfitVO
 * @description: 店长分润数据VO
 * @create 2018/7/9 22:46
 **/
@ApiModel(value = "店长分润数据VO")
@Data
public class StoreManagerProfitVO {

    @ApiModelProperty(value = "店长分润数据id", required = true)
    private Long store_manager_profit_id;

    @ApiModelProperty(value = "结算月份", required = true)
    private String balance_month;

    @ApiModelProperty(value = "店长姓名", required = true)
    private String staff_name;

    @ApiModelProperty(value = "门店名称", required = true)
    private String store_name;

    @ApiModelProperty(value = "承担总成本", required = true)
    private BigDecimal total_cost;

    @ApiModelProperty(value = "服务分润", required = true)
    private BigDecimal service_profit;

    @ApiModelProperty(value = "代下单分润", required = true)
    private BigDecimal help_order_profit;

    @ApiModelProperty(value = "配送分润", required = true)
    private BigDecimal distribution_profit;

    @ApiModelProperty(value = "实得分润", notes = "结算前无值")
    private BigDecimal actual_profit;

    @ApiModelProperty(value = "结算状态", notes = "1-已结算,0-未结算")
    private Integer balance_status;
}
