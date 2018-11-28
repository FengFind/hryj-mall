package com.hryj.entity.vo.staff.role.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: RoleNameListResponseVO
 * @description:
 * @create 2018/6/28 0028-20:55
 **/
@ApiModel(description = "角色名列表")
@Data
public class RoleNameListResponseVO {
    @ApiModelProperty(value = "role_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long role_id;

    @ApiModelProperty(value = "角色名")
    private String role_name;
}
