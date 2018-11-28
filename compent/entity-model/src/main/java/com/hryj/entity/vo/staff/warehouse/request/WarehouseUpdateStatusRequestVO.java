package com.hryj.entity.vo.staff.warehouse.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: RoleUpdateStatusRequestVO
 * @description:
 * @create 2018/7/5 0005-10:26
 **/
@Data
@ApiModel(description = "修改仓库状态VO")
public class WarehouseUpdateStatusRequestVO extends RequestVO {

    @ApiModelProperty(value = "仓库d", required = true)
    private Long warehouse_id;

    @ApiModelProperty(value = "仓库状态:1-正常,0-停用", required = true)
    private Integer warehouse_status;

    @Override
    public String toString() {
        return "WarehouseUpdateStatusRequestVO{" +
                "warehouse_id=" + warehouse_id +
                ", warehouse_status=" + warehouse_status +
                '}';
    }
}
