package com.hryj.entity.vo.staff.role.response;

import com.hryj.constant.StaffConstants;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: RolePermissionTreeItem
 * @description:
 * @create 2018/8/4 0004 14:51
 **/
@Data
public class RolePermissionTreeItem {

    public static final Integer NO_CHECKED = new Integer(1);
    public static final Integer CHECKED = new Integer(2);
    public static final Integer HALF_CHECKED = new Integer(3);

    @ApiModelProperty(value = "权限资源key")
    private Long key;

    @ApiModelProperty(value = "权限资源名称")
    private String title;

    @ApiModelProperty(value = "节点选中状态，1未选中，2选中，3半选中")
    private Integer checked_status;

    @ApiModelProperty(value = "选中状态, true选中， false未选中")
    private Boolean checked;

    @ApiModelProperty(value = "父级权限KEY")
    private Long parent_key;

    @ApiModelProperty(value = "权限类型,generalType:普通权限,generalType以外为其它权限,默认为generalType")
    private String permission_type = StaffConstants.GENERAL_TYPE;

    @ApiModelProperty(value = "下节权限资源集合")
    private List<RolePermissionTreeItem> children;
}
