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
 * @className: OrderTypeRoleSupport
 * @description:
 * @create 2018/9/10 0010 10:18
 **/
@Component("orderTypeRoleSupport")
public class OrderTypeRoleSupport implements GenericPermissionSupport {

    @Autowired
    private OrderTypeRoleFeignClient orderTypeRoleFeignClient;

    @Override
    public Result<ListResponseVO<Permission>> loadPermissionPool() {
        return orderTypeRoleFeignClient.loadPermissionPool();
    }

    @Override
    public Result<ListResponseVO<Permission>> findRolePermission(Long role_id) {
        return orderTypeRoleFeignClient.findRolePermission(role_id);
    }

    @Override
    public Result<ListResponseVO<Permission>> findStaffPermission(Long staff_id) {
        return orderTypeRoleFeignClient.findStaffPermission(staff_id);
    }

    @Override
    public Result saveRolePermission(Long role_id, List<Permission> permission_list) {
        return orderTypeRoleFeignClient.saveRolePermission(role_id, permission_list);
    }

    @Override
    public Result saveStaffPermission(Long role_id, List<Permission> permission_list) {
        return orderTypeRoleFeignClient.saveStaffPermission(role_id, permission_list);
    }
}
