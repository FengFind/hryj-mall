package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: StoreCostDetailVO
 * @description: 门店成本明细响应VO
 * @create 2018/7/9 22:02
 **/
@ApiModel(value = "门店成本明细响应VO")
@Data
public class StoreCostDetailVO {

    @ApiModelProperty(value = "门店成本明细id", required = true)
    private Long store_cost_detail_id;

    @ApiModelProperty(value = "成本名称", required = true)
    private String cost_name;

    @ApiModelProperty(value = "成本金额", required = true)
    private BigDecimal cost_amt;

}
