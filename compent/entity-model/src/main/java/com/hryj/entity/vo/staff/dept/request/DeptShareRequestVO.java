package com.hryj.entity.vo.staff.dept.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @autho 波
 * @className: DeptRequestVO
 * @description:
 * @create 2018/6/27 0027-10:57
 **/
@Data
@ApiModel(value = "门店分润请求VO")
public class DeptShareRequestVO {


    @ApiModelProperty(value = "部门id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "门店或者仓库id,店或者仓库查询必须传id")
    private Long store_id;

    @ApiModelProperty(value = "查询类型,01-组织查询,02-门店或者仓库,默认为01组织查询")
    private String search_type="01";

}
