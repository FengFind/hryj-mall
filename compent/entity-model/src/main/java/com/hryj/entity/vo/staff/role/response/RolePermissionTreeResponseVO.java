package com.hryj.entity.vo.staff.role.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: RolePermissionTreeResponseVO
 * @description:
 * @create 2018/8/4 0004 14:49
 **/
@ApiModel(description = "角色权限树响应VO")
@Data
public class RolePermissionTreeResponseVO {

    @ApiModelProperty(value = "权限资源树集合")
    private List<RolePermissionTreeItem> tree_list;


    @ApiModelProperty(value = "全选中的KEY集合")
    private List<Long> checked_keys;

    @ApiModelProperty(value = "半选中的KEY集合")
    private List<Long> half_checked_keys;
}
