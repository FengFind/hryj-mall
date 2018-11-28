package com.hryj.entity.vo.staff.dept.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptCreateCompanyFlag
 * @description:
 * @create 2018/7/9 0009-16:26
 **/
@Data
@ApiModel(value = "区域公司响应VO")
public class DeptCreateCompanyFlag {

    @ApiModelProperty(value = "是否可以创建区域公司:1-是,0-否")
    private Integer company_flag;

    @ApiModelProperty(value = "部门名称")
    private String dept_name;
}
