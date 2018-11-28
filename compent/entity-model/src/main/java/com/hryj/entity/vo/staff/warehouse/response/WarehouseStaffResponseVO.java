package com.hryj.entity.vo.staff.warehouse.response;

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
@ApiModel(description = "仓库员工响应VO")
public class WarehouseStaffResponseVO {


    @ApiModelProperty(value = "员工id")
    private Long staff_id;

    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "仓库id,")
    private Long warehouse_id;

    @ApiModelProperty(value = "仓库名称")
    private String warehouse_name;

}
