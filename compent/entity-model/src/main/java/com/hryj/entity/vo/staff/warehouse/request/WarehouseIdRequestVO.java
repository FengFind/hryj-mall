package com.hryj.entity.vo.staff.warehouse.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 仓库信息表
 *
 * @author daitingbo
 * @since 2018-06-27
 */
@Data
@ApiModel(description = "仓库id vo")
public class WarehouseIdRequestVO {

    @ApiModelProperty(value = "仓库id", required = true)
    private Long warehouse_id;


}
