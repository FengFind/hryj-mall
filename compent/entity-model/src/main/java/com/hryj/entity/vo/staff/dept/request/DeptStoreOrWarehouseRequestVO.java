package com.hryj.entity.vo.staff.dept.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptStoreOrWarehouseResponseVO
 * @description:
 * @create 2018/7/14 0014-14:23
 **/
@ApiModel(description = "根据用户id查询门店或者仓库列表Vo")
@Data
public class DeptStoreOrWarehouseRequestVO extends RequestVO {

    @ApiModelProperty(value = "员工id")
    private Long staff_id;

    @ApiModelProperty(value = "部门id", hidden=true)
    private Long dept_id;

    @ApiModelProperty(value = "部门类型:01-门店,02-仓库,默认查所有")
    private String dept_type;
}
