package com.hryj.entity.vo.staff.role.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: RoleUpdateStatusRequestVO
 * @description:
 * @create 2018/7/5 0005-10:26
 **/
@Data
@ApiModel(description = "修改角色状态VO")
public class RoleUpdateStatusRequestVO extends RequestVO {

    @ApiModelProperty(value = "角色id", required = true)
    private Long role_id;

    @ApiModelProperty(value = "角色状态:1-正常,0-停用", required = true)
    private Integer role_status;

    @Override
    public String toString() {
        return "RoleUpdateStatusRequestVO{" +
                "role_id=" + role_id +
                ", role_status=" + role_status +
                '}';
    }
}
