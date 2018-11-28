package com.hryj.entity.vo.staff.dept.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 代廷波
 * @className: DeptStaffListResponseVO
 * @description:
 * @create 2018/6/28 0028-15:33
 **/
@ApiModel(description = "部门员工信息")
@Data
public class DeptStaffListRequestVO {

    @ApiModelProperty(value = "员工id", required = true)
    private Long staff_id;

    @ApiModelProperty(value = "部门id，如果是转移的员工需传转到的部门id")
    private Long dept_id;

    @ApiModelProperty(value = "工资金额", required = true)
    private BigDecimal salary_amt;

    @ApiModelProperty(value = "员工类型;01-普通员工,02-内置员工", required = true)
    private String staff_type;

}
