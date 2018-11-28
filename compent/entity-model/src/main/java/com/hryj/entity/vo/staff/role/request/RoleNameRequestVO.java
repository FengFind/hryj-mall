package com.hryj.entity.vo.staff.role.request;

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
@ApiModel(description = "角色名列表查询vo")
public class RoleNameRequestVO {

    @ApiModelProperty(value = "角色名", required = false)
    private String role_name;
}
