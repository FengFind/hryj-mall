package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.StaffConstants;
import com.hryj.entity.bo.staff.role.PermResource;
import com.hryj.entity.bo.staff.role.RolePermRelation;
import com.hryj.entity.bo.staff.role.StaffRole;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.role.request.*;
import com.hryj.entity.vo.staff.role.response.*;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.BizException;
import com.hryj.exception.GlobalException;
import com.hryj.mapper.PermResourceMapper;
import com.hryj.mapper.RoleMapper;
import com.hryj.permission.Permission;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

/**
 * @author 代廷波
 * @className: StaffRoleService
 * @description: 员工角色service
 * @create 2018-06-26 8:35
 **/
@Slf4j
@Service
public class RoleService extends ServiceImpl<RoleMapper, StaffRole> {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private PermResourceMapper permResourceMapper;

    @Autowired
    private RolePermRelationService rolePermRelationService;


    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 保存角色名
     * @param: role_name
     * @create 2018/06/28 10:41
     **/
    @Transactional
    public Result saveRole(RoleAddRequestVO vo) throws GlobalException {
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        log.info("操作人信息={},添加角色,saveRole======{}", JSON.toJSONString(staffAdminLoginVO), JSON.toJSONString(vo));
        if (validatoRoleName(vo.getRole_name(), vo.getRole_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "角色名重复");
        }
        StaffRole staffRole = new StaffRole();
        staffRole.setRole_name(vo.getRole_name());
        staffRole.setRole_status(1);
        roleMapper.insert(staffRole);

        return new Result();
    }

    /**
     * @return boolean
     * @author 代廷波
     * @description: 角色名验证
     * @param: roleName 角色名
     * @param: role_id 角色id
     * @create 2018/07/23 19:51
     **/
    public boolean validatoRoleName(String roleName, Long role_id) {

        EntityWrapper<StaffRole> wrapper = new EntityWrapper<>();
        wrapper.eq("role_name", roleName);
        StaffRole staffRole = super.selectOne(wrapper);
        if (staffRole != null && !staffRole.getId().equals(role_id)) {
            return true;
        }
        return false;
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改角色名
     * @param: vo
     * @create 2018/07/23 19:45
     **/
    @Transactional
    public Result updateRole(RoleAddRequestVO vo) throws GlobalException {
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        log.info("操作人信息={},修改角色名,updateRole======{}", JSON.toJSONString(staffAdminLoginVO), JSON.toJSONString(vo));

        if (null == vo.getRole_id()) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "角色id为空");
        }
        if (StrUtil.isEmpty(vo.getRole_name())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "角色名为空");
        }
        if (validatoRoleName(vo.getRole_name(), vo.getRole_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "角色名重复");
        }
        StaffRole staffRole = new StaffRole();
        staffRole.setRole_name(vo.getRole_name());
        staffRole.setId(vo.getRole_id());
        roleMapper.updateById(staffRole);

        return new Result();
    }

    /**
     * @return com.hryj.common.PageResult<com.hryj.entity.vo.staff.role.request.RoleListParamResponseVO>
     * @author 代廷波
     * @description: 角色列表查询
     * @param: staffRoleDto
     * @create 2018/06/28 10:40
     **/

    public Result<PageResponseVO<RoleListParamResponseVO>> getRoleList(RoleListParamRequestVO staffRoleDto) throws GlobalException {

        PageResponseVO<RoleListParamResponseVO> msg = new PageResponseVO<RoleListParamResponseVO>();
        Page page = new Page(staffRoleDto.getPage_num(), staffRoleDto.getPage_size());
        List<RoleListParamResponseVO> list = roleMapper.getRoleList(staffRoleDto, page);
        msg.setTotal_count(page.getTotal());
        msg.setTotal_page(page.getPages());
        msg.setRecords(list);
        return new Result(CodeEnum.SUCCESS, msg);
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 修改角名状态
     * @param: role_id 角色id
     * @param: status_value 角色状态:1-正常,0-停用
     * @create 2018/06/23 14:06
     **/
    @Transactional
    public Result updateRoleStatus(RoleUpdateStatusRequestVO vo) throws GlobalException {
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        log.info("操作人信息={},修改角名状态,updateRoleStatus======{}", JSON.toJSONString(staffAdminLoginVO), JSON.toJSONString(vo));

        StaffRole staffRole = new StaffRole();
        staffRole.setId(vo.getRole_id());
        staffRole.setRole_status(vo.getRole_status());
        staffRole.setUpdate_time(new Date());
        roleMapper.updateById(staffRole);

        return new Result();
    }

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 保存角色资源
     * @param: role_id 角色
     * @param: resource_ids 资源集合
     * @create 2018/06/28 10:03
     **/
    @Transactional
    public Result saveRoleResource(RoleAddResourceRequestVO vo) {
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        /*if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }*/
        //log.info("操作人信息={},保存角色资源,saveRoleResource======{}",JSON.toJSONString(staffAdminLoginVO),JSON.toJSONString(vo));

        EntityWrapper<RolePermRelation> ew = new EntityWrapper<RolePermRelation>();
        ew.where("role_id={0}", vo.getRole_id());
        rolePermRelationService.delete(ew);

        if (CollectionUtil.isNotEmpty(vo.getResourceList())) {
            List<RolePermRelation> list = new ArrayList<>();
            RolePermRelation obj = null;
            List<Permission> permissionList = new ArrayList<>();
            Permission permission = null;
            for (ResourceRequestVO resource : vo.getResourceList()) {
                //权限类型,0:普通权限,1:商品数据权限,2:单数据权限
                if (StaffConstants.GENERAL_TYPE.equals(resource.getPermission_type())) {
                    obj = new RolePermRelation();
                    obj.setPerm_id(resource.getId());
                    obj.setRole_id(vo.getRole_id());
                    list.add(obj);
                } else {
                    permission = new Permission();
                    permission.setPermission_id(resource.getId());
                    permission.setPermission_type(resource.getPermission_type());
                    permissionList.add(permission);
                }


            }
            if (list.size() > 0) {
                rolePermRelationService.insertBatch(list);
            }

            //数据权限
            PermissionManageHandler.saveRolePermission(vo.getRole_id(), permissionList);
        }
        return new Result();
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.ResourceTreeResponseVO>
     * @author 代廷波
     * @description: 获取资源树
     * @param:
     * @create 2018/06/28 20:46
     **/
    public Result<ListResponseVO<ResourceTreeResponseVO>> getResourceTree() throws GlobalException {
        List<ResourceTreeResponseVO> db_list = roleMapper.getResourceTree();

        // 最后的结果
        List<ResourceTreeResponseVO> package_List = new ArrayList<>();

        // 先找到所有的一级对象
        for (ResourceTreeResponseVO vo : db_list) {
            if (vo.getPerm_pid().equals(1L)) {
                package_List.add(vo);
            }
        }
       /* for (int i = 0; i < db_list.size(); i++) {
            // parentId=0
            if (db_list.get(i).getPerm_pid() == 1) {

            }
        }*/
        // 为一级对象设置子对象，getChild是递归调用的
        for (ResourceTreeResponseVO obj : package_List) {
            //obj.setChildren(getChild(obj.getKey(), db_list));
            obj.setChildren(setTreeMtd(obj.getKey(), db_list));
        }

        try {
            //加载所有数据权限资源
            List<Permission> permissionList = PermissionManageHandler.loadPermissionPool();
            if (null != permissionList) {
                ResourceTreeResponseVO resource = null;
                ResourceTreeResponseVO childrenResource = null;
                if (null != permissionList) {
                    for (Permission permission : permissionList) {
                        resource = new ResourceTreeResponseVO();
                        resource.setKey(permission.getPermission_id());
                        resource.setPermission_type(permission.getPermission_type());
                        resource.setTitle(permission.getDescription());
                        if (permission.getChildren().size() > 0) {
                            List<ResourceTreeResponseVO> data_List = new ArrayList<ResourceTreeResponseVO>();
                            for (Permission permission1 : permission.getChildren()) {
                                childrenResource = new ResourceTreeResponseVO();
                                childrenResource.setKey(permission1.getPermission_id());
                                childrenResource.setPermission_type(permission1.getPermission_type());
                                childrenResource.setTitle(permission1.getDescription());
                                data_List.add(childrenResource);
                            }
                            resource.setChildren(data_List);
                        }
                        package_List.add(resource);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("获取资源树getResourceTree,加载数据权限失败");
        }


        return new Result(CodeEnum.SUCCESS, new ListResponseVO<ResourceTreeResponseVO>(package_List));
    }

    /**
     * @return java.util.List<com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>
     * @author 代廷波
     * @description:递归查找子对象
     * @param: id 当前id
     * @param: db_list 要查找的列表
     * @create 2018/06/27 21:17
     **/
    private List<ResourceTreeResponseVO> getChild(Long id, List<ResourceTreeResponseVO> db_list) throws GlobalException {

        // 子对象
        List<ResourceTreeResponseVO> childList = new ArrayList<>();
        for (ResourceTreeResponseVO menu : db_list) {
            // 遍历所有节点，将父对象id与传过来的id比较
            if (menu.getPerm_pid() != 1) {
                if (menu.getPerm_pid().equals(id)) {
                    childList.add(menu);
                }
            }
        }
        // 把子对象的子对象再循环一遍
       /*for (ResourceTreeResponseVO menu : childList) {//是否末级节点:1-是,0-否
            if (menu.getEnd_lag() == 0) {
                // 递归
                menu.setChildren(getChild(menu.getKey(), db_list));
            }
        }*/
        // 递归退出条件*/
        if (childList.size() == 0) {
            return null;
        }
        return childList;
    }

    private List<ResourceTreeResponseVO> setTreeMtd(Long pid, List<ResourceTreeResponseVO> list) {

        List<ResourceTreeResponseVO> children = new ArrayList<>();
        //根据pid装备子对象
        for (ResourceTreeResponseVO tree : list) {
            if (pid.longValue() == tree.getPerm_pid().longValue()) {
                children.add(tree);
            }

        }
        //递归设置子对象
        for (ResourceTreeResponseVO child : children) {
            for (ResourceTreeResponseVO tree : list) {
                if (child.getKey().longValue() == tree.getPerm_pid().longValue()) {
                    child.setChildren(setTreeMtd(child.getKey(), list));
                }
            }
        }
        // 递归退出条件*/
        if (children.size() == 0) {
            return null;
        }
        return children;
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.RoleNameListResponseVO>
     * @author 代廷波
     * @description: 角色名列表
     * @param: role_name 角色名
     * @create 2018/06/28 20:57
     **/
    public Result<ListResponseVO<RoleNameListResponseVO>> getRoleNameList(RoleNameRequestVO vo) throws GlobalException {
        List<RoleNameListResponseVO> list = roleMapper.getRoleNameList(vo);

        return new Result(CodeEnum.SUCCESS, new ListResponseVO<RoleNameListResponseVO>(list));
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.RoleNameListResponseVO>
     * @author 代廷波
     * @description: 根据角色查询对应的资源
     * @param: role_id 角色id
     * @create 2018/06/29 21:08
     **/
    public Result<ListResponseVO<ResourceTreeResponseVO>> getRoleIdByResource(@RequestBody RoleIdRequestVO vo) throws GlobalException {
        List<ResourceTreeResponseVO> db_list = roleMapper.getRoleIdByResource(vo);
        return new Result(CodeEnum.SUCCESS, new ListResponseVO<ResourceTreeResponseVO>(db_list));
    }


    public Result<RolePermissionTreeResponseVO> getRolePermissionTree(RoleIdRequestVO vo) throws BizException {
        if (UtilValidate.isEmpty(vo.getRole_id())) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "角色ID不能是空值");
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", vo.getRole_id());
        if (roleMapper.selectCount(wrapper) < 1) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "不存在ID为:[" + vo.getRole_id() + "]的角色");
        }
        RolePermissionTreeResponseVO responseVO = new RolePermissionTreeResponseVO();

        //加载所有权限资源
        List<PermResource> all_permission_list = permResourceMapper.findAll();
        if (UtilValidate.isEmpty(all_permission_list)) {
            all_permission_list = new ArrayList<>();
        }

        //加载所有定制数据权限池数据
        List<Permission> permission_list = PermissionManageHandler.loadPermissionPool();
        if (UtilValidate.isNotEmpty(permission_list)) {
            for (Permission item : permission_list) {
                all_permission_list.addAll(item.convertToPermResList());
            }
        }

        if (UtilValidate.isEmpty(all_permission_list)) {
            return new Result<>(CodeEnum.SUCCESS, responseVO);
        }

        //加载角色的权限资源
        List<PermResource> role_permission_list = permResourceMapper.findRolePermissions(vo.getRole_id());

        Set<Long> role_permission_set = new HashSet<>(UtilValidate.isEmpty(role_permission_list) ? 0 : role_permission_list.size());;
        if (UtilValidate.isNotEmpty(role_permission_list)) {
            for (PermResource resource : role_permission_list) {
                role_permission_set.add(resource.getId());
            }
        }

        //加载角色数据权限
        permission_list = PermissionManageHandler.findRolePermission(vo.getRole_id());
        if (UtilValidate.isNotEmpty(permission_list)) {
            for (Permission permission : permission_list) {
                role_permission_set.addAll(permission.convertToPermissionSet());
            }
        }

        //构造权限资源树形结构
        List<RolePermissionTreeItem> tree_list = constructTree(all_permission_list, role_permission_set);
        //权限树前端暂时不需要，暂不返回，如果以后需要，放开注释即可
        //responseVO.setTree_list(tree_list);

        //计算出树结构数据的选中状态
        calculateSetCheckStatus(tree_list);

        //计算出全选，半选的数据集合
        List<Long> checked_keys = new ArrayList<>(all_permission_list.size());
        List<Long> half_checked_keys = new ArrayList<>(all_permission_list.size());

        responseVO.setChecked_keys(checked_keys);
        responseVO.setHalf_checked_keys(half_checked_keys);

        for (RolePermissionTreeItem treeItem : tree_list) {
            calculateSetCheckStatusData(treeItem, checked_keys, half_checked_keys);
        }

        return new Result<>(CodeEnum.SUCCESS, responseVO);
    }

    public Result<RolePermissionTreeResponseVO> getStaffPermissionTree(List<RolePermissionTreeItem> list, Long staff_id) throws BizException {
        if (UtilValidate.isEmpty(list)) {
            list = new ArrayList<>();
        }

        RolePermissionTreeResponseVO responseVO = new RolePermissionTreeResponseVO();

        Set<Long> role_permission_set = new HashSet<>(list.size());
        for (RolePermissionTreeItem treeItem : list) {
            role_permission_set.add(treeItem.getKey());
        }

        //加载所有权限资源
        List<PermResource> all_permission_list = permResourceMapper.findAll();
        if (UtilValidate.isEmpty(all_permission_list)) {
            all_permission_list = new LinkedList<>();
        }

        //加载所有定制数据权限池数据
        List<Permission> permission_list = PermissionManageHandler.loadPermissionPool();
        if (UtilValidate.isNotEmpty(permission_list)) {
            for (Permission item : permission_list) {
                all_permission_list.addAll(item.convertToPermResList());
            }
        }

        //加载员工数据权限
        permission_list = PermissionManageHandler.findStaffPermission(staff_id);
        if (UtilValidate.isNotEmpty(permission_list)) {
            for (Permission permission : permission_list) {
                role_permission_set.addAll(permission.convertToPermissionSet());
            }
        }

        //构造权限资源树形结构
        List<RolePermissionTreeItem> tree_list = constructTree(all_permission_list, role_permission_set);

        //权限树前端暂时不需要，暂不返回，如果以后需要，放开注释即可
        //responseVO.setTree_list(tree_list);

        //计算出树结构数据的选中状态
        calculateSetCheckStatus(tree_list);

        //计算出全选，半选的数据集合
        List<Long> checked_keys = new ArrayList<>(all_permission_list.size());
        List<Long> half_checked_keys = new ArrayList<>(all_permission_list.size());

        responseVO.setChecked_keys(checked_keys);

        responseVO.setHalf_checked_keys(half_checked_keys);

        for (RolePermissionTreeItem treeItem : tree_list) {
            calculateSetCheckStatusData(treeItem, checked_keys, half_checked_keys);
        }

        return new Result<>(CodeEnum.SUCCESS, responseVO);
    }

    private void setChildren(RolePermissionTreeItem treeItem, Map<Long, List<RolePermissionTreeItem>> sub_map) {
        if (sub_map.containsKey(treeItem.getKey())) {
            treeItem.setChildren(sub_map.get(treeItem.getKey()));
        }
        if (UtilValidate.isNotEmpty(treeItem.getChildren())) {
            for (RolePermissionTreeItem item : treeItem.getChildren()) {
                setChildren(item, sub_map);
            }
        }
    }

    private void calculateSetCheckStatusData(RolePermissionTreeItem treeItem, List<Long> checked_key, List<Long> half_checked_keys) {
        if (RolePermissionTreeItem.CHECKED.equals(treeItem.getChecked_status())) {
            checked_key.add(treeItem.getKey());
        } else if (RolePermissionTreeItem.HALF_CHECKED.equals(treeItem.getChecked_status())) {
            half_checked_keys.add(treeItem.getKey());
        }
        if (UtilValidate.isNotEmpty(treeItem.getChildren())) {
            for (RolePermissionTreeItem rolePermissionTreeItem : treeItem.getChildren()) {
                calculateSetCheckStatusData(rolePermissionTreeItem, checked_key, half_checked_keys);
            }
        }
    }


    private void calculateSetCheckStatus(List<RolePermissionTreeItem> tree_list) {
        for (RolePermissionTreeItem treeItem : tree_list) {
            treeItem.setChecked_status(getCheckStatus(treeItem));
            if (UtilValidate.isNotEmpty(treeItem.getChildren())) {
                calculateSetCheckStatus(treeItem.getChildren());
            }
        }
    }

    private Integer getCheckStatus(RolePermissionTreeItem treeItem) {
        if (UtilValidate.isEmpty(treeItem.getChildren())) {
            return treeItem.getChecked() ? RolePermissionTreeItem.CHECKED : RolePermissionTreeItem.NO_CHECKED;
        }
        Map<Integer, Integer> map = new HashMap<>(treeItem.getChildren().size());
        for (RolePermissionTreeItem childTreeItem : treeItem.getChildren()) {
            Integer res = getCheckStatus(childTreeItem);
            if (map.containsKey(res)) {
                map.put(res, map.get(res) + 1);
            } else {
                map.put(res, 1);
            }
        }
        if (map.containsKey(RolePermissionTreeItem.CHECKED) && map.get(RolePermissionTreeItem.CHECKED).equals(treeItem.getChildren().size())) {
            return RolePermissionTreeItem.CHECKED;
        }
        if (map.containsKey(RolePermissionTreeItem.CHECKED) || map.containsKey(RolePermissionTreeItem.HALF_CHECKED)) {
            return RolePermissionTreeItem.HALF_CHECKED;
        }
        return RolePermissionTreeItem.NO_CHECKED;
    }

    private List<RolePermissionTreeItem> constructTree(List<PermResource> all_permission_list, Set<Long> role_permission_set) {
        if (UtilValidate.isEmpty(all_permission_list)) {
            return null;
        }

        Map<Long, List<RolePermissionTreeItem>> sub_map = new HashMap<>(all_permission_list.size());
        List<RolePermissionTreeItem> tree_list = new ArrayList<>(all_permission_list.size());
        Long ingore_pid = 1L;
        for (PermResource permResource : all_permission_list) {
            RolePermissionTreeItem treeItem = new RolePermissionTreeItem();
            treeItem.setKey(permResource.getId());
            treeItem.setTitle(permResource.getPerm_name());

            if (UtilValidate.isNotEmpty(role_permission_set) && role_permission_set.contains(permResource.getId())) {
                treeItem.setChecked(true);
            } else {
                treeItem.setChecked(false);
            }

            if (permResource.getPerm_pid() == null || permResource.getPerm_pid() <= 0L) {
                tree_list.add(treeItem);
                continue;
            }

            if (permResource.getPerm_pid().equals(ingore_pid)) {
                tree_list.add(treeItem);
                continue;
            }

            if (sub_map.containsKey(permResource.getPerm_pid())) {
                sub_map.get(permResource.getPerm_pid()).add(treeItem);
            } else {
                sub_map.put(permResource.getPerm_pid(), UtilMisc.toList(treeItem));
            }
        }

        for (RolePermissionTreeItem treeItem : tree_list) {
            setChildren(treeItem, sub_map);
        }

        return tree_list;
    }
}
