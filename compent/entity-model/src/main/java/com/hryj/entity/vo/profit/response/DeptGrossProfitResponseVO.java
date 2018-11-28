package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: DeptGrossProfitResponseVO
 * @description: 部门毛利分润响应VO
 * @create 2018/8/16 11:18
 **/
@ApiModel(value = "部门毛利分润响应VO")
@Data
public class DeptGrossProfitResponseVO  implements Serializable {

    @ApiModelProperty(value = "结算时间", notes = "时间戳格式" ,required = true)
    private Long balance_time;

    @ApiModelProperty(value = "结算月份", required = true)
    private String balance_month;

    @ApiModelProperty(value = "所分毛利金额", required = true)
    private BigDecimal gross_profit_amt;
}
