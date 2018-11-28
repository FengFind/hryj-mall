package com.hryj.entity.vo.staff.dept.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptRequestVO
 * @description:
 * @create 2018/6/27 0027-10:57
 **/
@Data
@ApiModel(value = "部门集合详情VO")
public class DeptGroupBasicInfoRequestVO {

    @ApiModelProperty(value = "部门名称")
    private String dept_name;

    @ApiModelProperty(value = "部门id", required = true)
    private Long[] dept_ids;

}
