package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author 代廷波
 * @className: DeptStaffListResponseVO
 * @description:
 * @create 2018/6/28 0028-15:33
 **/
@ApiModel(description = "部门员工查询列表信息")
@Data
public class DeptStaffListResponseVO implements Serializable {

    /* @ApiModelProperty(value = "员工组织关系表主键,新增员工不传，编辑有的需要传")
     @JsonFormat(shape = JsonFormat.Shape.STRING)
     private Long staff_dept_relation_id;
 */
    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "员工id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long staff_id;

    @ApiModelProperty(value = "部门id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;

    @ApiModelProperty(value = "工资金额")
    private BigDecimal salary_amt;

    @ApiModelProperty(value = "员工编号")
    private String staff_account;

    @ApiModelProperty(value = "手机号码")
    private String phone_num;

    @ApiModelProperty(value = "部门名称")
    private String dept_name;

    @ApiModelProperty(value = "是否是分润员工:1是,0否")
    private String is_share;

    @ApiModelProperty(value = "员工类型;01-普通员工,02-内置员工", required = true)
    private String staff_type;


}
