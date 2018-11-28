package com.hryj.entity.vo.staff.warehouse.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: WarehouseStaffResponseVO
 * @description:
 * @create 2018/7/3 0003-18:36
 **/
@Data
@ApiModel(description = "仓库员工信息对象")
public class WarehouseStaffRequestVO {

   /* @ApiModelProperty(value = "员工组织关系主建id", required = false)
    private Long staff_dept_relation_id;//主建*/

    @ApiModelProperty(value = "员工id", required = true)
    private Long staff_id;

    @ApiModelProperty(value = "仓库id", required = false)
    private Long warehouse_id;
}
