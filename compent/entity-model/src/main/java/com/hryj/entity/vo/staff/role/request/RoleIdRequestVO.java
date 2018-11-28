package com.hryj.entity.vo.staff.role.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: RoleAddRequestVO
 * @description:
 * @create 2018/7/5 0005-10:21
 **/
@Data
@ApiModel(description = "角色Id vo")
public class RoleIdRequestVO {

    @ApiModelProperty(value = "角色id", required = true)
    private Long role_id;

}
