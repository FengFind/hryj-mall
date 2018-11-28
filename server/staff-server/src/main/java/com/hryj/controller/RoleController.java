package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.role.request.*;
import com.hryj.entity.vo.staff.role.response.ResourceTreeResponseVO;
import com.hryj.entity.vo.staff.role.response.RoleListParamResponseVO;
import com.hryj.entity.vo.staff.role.response.RoleNameListResponseVO;
import com.hryj.entity.vo.staff.role.response.RolePermissionTreeResponseVO;
import com.hryj.exception.BizException;
import com.hryj.exception.GlobalException;
import com.hryj.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author daitingbo
 * @className: StaffRoleController
 * @description: 员工角色controller
 * @create 2018-06-23
 **/
@RestController
@RequestMapping(value = "/role",produces="application/json;charset=UTF-8")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * @author 代廷波
     * @description: 添加色色
     * @param: role_name 角色名
     * @return com.hryj.common.Result
     * @create 2018/06/23 14:02
     **/
    @PostMapping("/saveRole")
    public Result saveRole(@RequestBody RoleAddRequestVO vo) throws GlobalException {

       return roleService.saveRole(vo);

    }
    /**
     * @author 代廷波
     * @description: 修改角色名
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/07/23 20:00
     **/
    @PostMapping("/updateRole")
    public Result updateRole(@RequestBody RoleAddRequestVO vo) throws GlobalException {

        return roleService.updateRole(vo);

    }
    /**
     * @author 代廷波
     * @description: 修改角名状态
     * @param: role_id 角色id
     * @param: status_value 角色状态:1-正常,0-停用
     * @return com.hryj.common.Result
     * @create 2018/06/23 14:06
     **/
    @RequestMapping(value = "/updateRoleStatus",method = RequestMethod.POST)
    public Result updateRoleStatus(@RequestBody RoleUpdateStatusRequestVO vo)throws GlobalException {
        return roleService.updateRoleStatus(vo);
    }


    /**
     * @author 代廷波
     * @description: 添加角色资源
     * @param: role_id 角色id
     * @param: resource_ids 资源集合 数组
     * @param: login_token 用户 token
     * @return com.hryj.common.Result
     * @create 2018/06/27 21:23
     **/
    @PostMapping("/saveRoleResource")
    public Result saveRoleResource(@RequestBody RoleAddResourceRequestVO vo
                                  ) throws GlobalException {

        return roleService.saveRoleResource(vo);

    }

    /**
     * @author 代廷波
     * @description: 角色列表查询
     * @param: staffRoleDto
     * @return com.hryj.common.PageResult
     * @create 2018/06/23 14:31
     **/
    @PostMapping("/getRoleList")
    public Result<PageResponseVO<RoleListParamResponseVO>> getRoleList(@RequestBody RoleListParamRequestVO staffRoleDto)throws GlobalException {

        return roleService.getRoleList(staffRoleDto);
    }

    /**
     * @author 代廷波
     * @description: 获取资源树
     * @param:
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.ResourceTreeResponseVO>
     * @create 2018/06/28 20:46
     **/
    @PostMapping("/getResourceTree")
    public Result<ListResponseVO<ResourceTreeResponseVO>> getResourceTree()throws GlobalException {

        return roleService.getResourceTree();
    }
    /**
     * @author 代廷波
     * @description: 角色名列表
     * @param: role_name 角色名
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.RoleNameListResponseVO>
     * @create 2018/06/28 20:57
     **/
    @PostMapping("/getRoleNameList")

    public Result<ListResponseVO<RoleNameListResponseVO>> getRoleNameList(@RequestBody RoleNameRequestVO vo)throws GlobalException {

        return roleService.getRoleNameList(vo);
    }

    /**
     * @author 代廷波
     * @description: 根据角色查询对应的资源
     * @param: role_id 角色id
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.RoleNameListResponseVO>
     * @create 2018/06/29 21:08
     **/
    @PostMapping("/getRoleIdByResource")

    public Result<ListResponseVO<ResourceTreeResponseVO>> getRoleIdByResource(@RequestBody RoleIdRequestVO vo)throws GlobalException {


        return roleService.getRoleIdByResource(vo);
    }

    /**
     * @author 王光银
     * @methodName: getRolePermissionTree
     * @methodDesc: 加载角色权限树
     * @description:
     * @param: [vo]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.RolePermissionTreeResponseVO>
     * @create 2018-08-04 15:05
     **/
    @PostMapping("/getRolePermissionTree")
    public Result<RolePermissionTreeResponseVO> getRolePermissionTree(@RequestBody RoleIdRequestVO vo) throws BizException {
        return roleService.getRolePermissionTree(vo);
    }
}

