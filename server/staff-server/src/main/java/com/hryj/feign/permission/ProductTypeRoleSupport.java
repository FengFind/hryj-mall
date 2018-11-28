package com.hryj.feign.permission;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.permission.GenericPermissionSupport;
import com.hryj.permission.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductTypeRoleSupport
 * @description:
 * @create 2018/9/10 0010 10:19
 **/
@Component("productTypeRoleSupport")
public class ProductTypeRoleSupport implements GenericPermissionSupport {

    @Autowired
    private ProductTypeRoleFeignClient productTypeRoleFeignClient;

    @Override
    public Result<ListResponseVO<Permission>> loadPermissionPool() {
        return productTypeRoleFeignClient.loadPermissionPool();
    }

    @Override
    public Result<ListResponseVO<Permission>> findRolePermission(Long role_id) {
        return productTypeRoleFeignClient.findRolePermission(role_id);
    }

    @Override
    public Result<ListResponseVO<Permission>> findStaffPermission(Long staff_id) {
        return productTypeRoleFeignClient.findStaffPermission(staff_id);
    }

    @Override
    public Result saveRolePermission(Long role_id, List<Permission> permission_list) {
        return productTypeRoleFeignClient.saveRolePermission(role_id, permission_list);
    }

    @Override
    public Result saveStaffPermission(Long staff_id, List<Permission> permission_list) {
        return productTypeRoleFeignClient.saveStaffPermission(staff_id, permission_list);
    }
}
