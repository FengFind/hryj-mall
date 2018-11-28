package com.hryj.entity.vo.staff.dept.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: DeptCostRequestVO
 * @description: 部门组织成本请求VO
 * @create 2018-06-26 9:08
 **/
@Data
@ApiModel(value = "部门组织成本请求VO")
public class DeptCostRequestVO{

    @ApiModelProperty(value = "部门组织成本id", required = false)
    private Long dept_cost_id;

    @ApiModelProperty(value = "成本名称", required = true)
    private String cost_name;

    @ApiModelProperty(value = "成本金额", required = true)
    private BigDecimal cost_amt;




}
