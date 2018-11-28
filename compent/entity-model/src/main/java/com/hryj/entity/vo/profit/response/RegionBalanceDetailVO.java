package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: RegionBalanceDetailVO
 * @description: 区域公司结算明细VO
 * @create 2018/7/9 20:24
 **/
@ApiModel(value = "区域公司结算明细VO")
@Data
public class RegionBalanceDetailVO {

    @ApiModelProperty(value = "区域公司结算明细id", required = true)
    private Long region_balance_detail_id;

    @ApiModelProperty(value = "部门id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "部门名称", required = true)
    private String dept_name;

    @ApiModelProperty(value = "员工姓名", required = true)
    private String staff_name;

    @ApiModelProperty(value = "工资成本", required = true)
    private BigDecimal salary_amt;

    @ApiModelProperty(value = "实得分润", notes = "结算前无值")
    private BigDecimal actual_profit;
}
