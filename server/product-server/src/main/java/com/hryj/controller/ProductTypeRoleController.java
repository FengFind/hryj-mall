package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.permission.Permission;
import com.hryj.service.prodtype.ProductTypeRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @author 王光银
 * @className: ProductTypeRoleController
 * @description:
 * @create 2018/9/10 0010 11:20
 **/
@RestController
@RequestMapping("/product/productType")
public class ProductTypeRoleController {

    @Autowired
    private ProductTypeRoleService productTypeRoleService;

    /**
     * @author 王光银
     * @methodName: loadPermissionPool
     * @methodDesc: 加载可以分配的定制数据权限池
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @PostMapping("/loadPermissionPool")
    public Result<ListResponseVO<Permission>> loadPermissionPool() {
        return productTypeRoleService.loadPermissionPool();
    }

    /**
     * @author 王光银
     * @methodName: findRolePermission
     * @methodDesc: 加载角色的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @PostMapping("/findRolePermission")
    public Result<ListResponseVO<Permission>> findRolePermission(@RequestParam("role_id") Long role_id) {
        return productTypeRoleService.findRolePermission(role_id);
    }

    /**
     * @author 王光银
     * @methodName: findStaffPermission
     * @methodDesc: 加载员工的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @PostMapping("/findStaffPermission")
    public Result<ListResponseVO<Permission>> findStaffPermission(@RequestParam("staff_id") Long staff_id) {
        return productTypeRoleService.findStaffPermission(staff_id);
    }

    /**
     * @author 王光银
     * @methodName: saveRolePermission
     * @methodDesc: 保存角色的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @PostMapping("/saveRolePermission")
    public Result saveRolePermission(@RequestParam("role_id") Long role_id, @RequestBody List<Permission> permission_list) {
        return productTypeRoleService.saveRolePermission(role_id, permission_list);
    }

    /**
     * @author 王光银
     * @methodName: saveStaffPermission
     * @methodDesc: 保存员工的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @PostMapping("/saveStaffPermission")
    public Result saveStaffPermission(@RequestParam("staff_id") Long staff_id, @RequestBody List<Permission> permission_list) {
        return productTypeRoleService.saveStaffPermission(staff_id, permission_list);
    }
}
