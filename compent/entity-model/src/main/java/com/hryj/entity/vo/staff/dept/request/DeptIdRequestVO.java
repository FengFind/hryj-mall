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
@ApiModel(value = "部门id请求Vo")
public class DeptIdRequestVO {


    @ApiModelProperty(value = "部门id", required = true)
    private Long dept_id;

}
