package com.hryj.entity.vo.staff.dept.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptStoreOrWarehouseResponseVO
 * @description:
 * @create 2018/7/14 0014-14:23
 **/
@ApiModel(description = "门店或者仓库查询列表信息")
@Data
public class DeptStoreOrWarehouseResponseVO {
    @ApiModelProperty(value = "门店或者仓库id")
    private Long dept_id;

    @ApiModelProperty(value = "门店或者仓库名称")
    private String dept_name;

    @ApiModelProperty(value = "部门类型:01-门店,02-仓库,03-普通部门")
    private String dept_type;
}
