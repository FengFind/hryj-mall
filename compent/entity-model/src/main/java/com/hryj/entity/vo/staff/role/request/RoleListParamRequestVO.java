package com.hryj.entity.vo.staff.role.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StaffRoleDto
 * @description: 角色查询条件列表
 * @create 2018/6/23 0023-14:12
 **/
@Data
@ApiModel(description = "角色查询条件")
public class RoleListParamRequestVO {

    @ApiModelProperty(value = "角色名", required = false)
    private String role_name;

    @ApiModelProperty(value = "色状态:1-正常,0-停用", required = false)
    private Integer role_status;

    @ApiModelProperty(value = "页码，默认为1", required = false)
    private int page_num=1;//页码，默认为1

    @ApiModelProperty(value = "每页大小，默认为10", required = false)
    private int page_size=10;// #每页大小，默认为10
}
