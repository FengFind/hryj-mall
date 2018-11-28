package com.hryj.feign.permission;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.permission.Permission;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderTypeRoleFeignClient
 * @description:
 * @create 2018/9/10 0010 10:09
 **/
@FeignClient(name = "order-server")
public interface OrderTypeRoleFeignClient {
    /**
     * @author 叶方宇
     * @methodName: loadPermissionPool
     * @methodDesc: 加载可以分配的定制数据权限池
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @RequestMapping(value = "/order/orderType/loadPermissionPool", method = RequestMethod.POST)
    Result<ListResponseVO<Permission>> loadPermissionPool();

    /**
     * @author 叶方宇
     * @methodName: findRolePermission
     * @methodDesc: 加载角色的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @RequestMapping(value = "/order/orderType/findRolePermission", method = RequestMethod.POST)
    Result<ListResponseVO<Permission>> findRolePermission(@RequestParam("role_id") Long role_id);

    /**
     * @author 叶方宇
     * @methodName: findStaffPermission
     * @methodDesc: 加载员工的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @RequestMapping(value = "/order/orderType/findStaffPermission", method = RequestMethod.POST)
    Result<ListResponseVO<Permission>> findStaffPermission(@RequestParam("staff_id") Long staff_id);

    /**
     * @author 叶方宇
     * @methodName: saveRolePermission
     * @methodDesc: 保存角色的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @RequestMapping(value = "/order/orderType/saveRolePermission", method = RequestMethod.POST)
    Result saveRolePermission(@RequestParam("role_id") Long role_id, @RequestBody List<Permission> permission_list);

    /**
     * @author 叶方宇
     * @methodName: saveStaffPermission
     * @methodDesc: 保存员工的权限
     * @description:
     * @param:
     * @return
     * @create 2018-09-10 11:18
     **/
    @RequestMapping(value = "/order/orderType/saveStaffPermission", method = RequestMethod.POST)
    Result saveStaffPermission(@RequestParam("staff_id") Long staff_id, @RequestBody List<Permission> permission_list);
}
