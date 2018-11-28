package com.hryj.entity.vo.product.prodtype;

import com.hryj.permission.Permission;
import com.hryj.permission.PermissionManageHandler;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductTypeRoleAndProductType
 * @description:
 * @create 2018/9/10 0010 11:52
 **/
@Data
public class ProductTypeRoleAndProductType {

    private Long id;

    private Long staff_id;

    private Long role_id;

    private String product_type_id;

    private String description;

    public Permission convertToPermission(Long parent) {
        Permission item = new Permission();
        item.setPermission_type(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE.getPermission_id());
        item.setPermission_id(PermissionManageHandler.PermissionSupport.getMappingValue(this.product_type_id));
        item.setPermission_type_id(this.product_type_id);
        item.setDescription(this.description);
        item.setParent_permission_id(parent);
        return item;
    }
}
