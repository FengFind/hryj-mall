package com.hryj.entity.vo.staff.role.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: RoleNameRequestVO
 * @description:
 * @create 2018/7/5 0005-10:40
 **/
@Data
@ApiModel(description = "角色VO")
public class RoleResponseVO {

    @ApiModelProperty(value = "角色名")
    private String role_name;

    @ApiModelProperty(value = "角色id")
    private String role_id;
}
