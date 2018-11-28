package com.hryj.entity.vo.staff.role.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StaffRoleDto
 * @description: 角色查询列表
 * @create 2018/6/23 0023-14:12
 **/
@Data
@ApiModel(description = "角色查询列表")
public class RoleListParamResponseVO {

    @ApiModelProperty(value = "角色id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long role_id;

    @ApiModelProperty(value = "角色名")
    private String role_name;

    @ApiModelProperty(value = "色状态:1-正常,0-停用")
    private Integer role_status;

}
