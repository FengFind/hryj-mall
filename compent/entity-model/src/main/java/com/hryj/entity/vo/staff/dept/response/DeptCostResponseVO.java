package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@ApiModel(value = "部门组织成本响应VO")
public class DeptCostResponseVO {

    @ApiModelProperty(value = "部门组织成本id", required = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_cost_id;

    @ApiModelProperty(value = "成本名称", required = true)
    private String cost_name;

    @ApiModelProperty(value = "成本金额", required = true)
    private BigDecimal cost_amt;


    @ApiModelProperty(value = "创建时间")
    private String create_time;

    @ApiModelProperty(value = "部门组织成本id,没有业务逻辑,只是方便前端删除判断")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long html_dept_cost_id;



}
