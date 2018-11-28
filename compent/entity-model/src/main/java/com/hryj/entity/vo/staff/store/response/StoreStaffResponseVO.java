package com.hryj.entity.vo.staff.store.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 员工用户信息表
 *
 * @author daitingbo
 * @since 2018-06-25
 */
@Data
@ApiModel(value = "门店员工列表信息VO")
public class StoreStaffResponseVO {


    @ApiModelProperty(value = "员工id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long staff_id;

    @ApiModelProperty(value = "员员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "员工岗位")
    private String staff_job;

    @ApiModelProperty(value = "门店id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long store_id;


}
