package com.hryj.entity.vo.staff.role.request;

import com.hryj.entity.vo.RequestVO;
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
@ApiModel(description = "添加角色vo")
public class RoleAddRequestVO extends RequestVO {

    @ApiModelProperty(value = "角色名", required = true)
    private String role_name;

    @ApiModelProperty(value = "角色id")
    private Long role_id;

}
