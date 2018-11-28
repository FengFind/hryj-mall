package com.hryj.entity.vo.staff.team.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: AppTeamDataResponseVO
 * @description:
 * @create 2018/7/9 0009-22:24
 **/
@Data
@ApiModel(value = "门店员工数据VO")
public class AppTeamDataClerkResponseVO {

    @ApiModelProperty(value = "员工id")
    private Long staff_id;

    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "员工岗位:01-店长,02-店员")
    private String staff_job;

}
