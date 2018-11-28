package com.hryj.entity.vo.staff.warehouse.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 代廷波
 * @className: DeptShareConfigReqestVO
 * @description:仓库组织节点分润对象
 * @create 2018/6/27 0027-11:24
 **/
@Data
@ApiModel(value = "仓库组织节点分润对象")
public class WarehouseConfigReqestVO {

    @ApiModelProperty(value = "部门id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "分润的员工id", required = true)
    private Long staff_id;

    @ApiModelProperty(value = "分润比例", required = true)
    private BigDecimal share_ratio;

    @Override
    public String toString() {
        return "DeptShareConfigReqestVO{" +
                ", dept_id=" + dept_id +
                ", staff_id=" + staff_id +
                ", share_ratio=" + share_ratio +
                '}';
    }
}