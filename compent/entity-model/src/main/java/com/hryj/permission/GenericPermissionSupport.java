package com.hryj.permission;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;

import java.util.List;

/**
 * @author 王光银
 * @className: GenericPermissionSupport
 * @description: 定制数据权限接口
 * @create 2018/9/10 0010 9:12
 **/
public interface GenericPermissionSupport {

    /**
     * 返回所有的权限资源
     * @return
     */
    Result<ListResponseVO<Permission>> loadPermissionPool();

    /**
     * 加载角色的权限资源
     * @param role_id
     * @return
     */
    Result<ListResponseVO<Permission>> findRolePermission(Long role_id);

    /**
     * 加载员工的权限资源
     * @param staff_id
     * @return
     */
    Result<ListResponseVO<Permission>> findStaffPermission(Long staff_id);

    /**
     * 保存角色的权限资源
     * @param role_id
     * @param permission_list
     */
    Result saveRolePermission(Long role_id, List<Permission> permission_list);

    /**
     * 保存员工的权限资源
     * @param staff_id
     * @param permission_list
     */
    Result saveStaffPermission(Long staff_id, List<Permission> permission_list);
}
