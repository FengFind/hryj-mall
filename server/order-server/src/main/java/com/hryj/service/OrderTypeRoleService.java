package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.ThirdOrderStatusEnum;
import com.hryj.entity.bo.permission.OrderTypeRole;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.order.ordertype.OrderTypeRoleAndOrderTypeVO;
import com.hryj.exception.BizException;
import com.hryj.mapper.OrderTypeRoleMapper;
import com.hryj.permission.GenericPermissionSupport;
import com.hryj.permission.Permission;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderTypeRoleService
 * @description:
 * @create 2018/9/10 0010 16:38
 **/
@Slf4j
@Service
public class OrderTypeRoleService extends ServiceImpl<OrderTypeRoleMapper, OrderTypeRole> implements GenericPermissionSupport {

    @Autowired
    OrderTypeRoleMapper orderTypeRoleMapper;
    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 加载角色的权限
     * @param:
     * @return
     * @create 2018-09-10 16:45
     **/
    @Override
    public Result<ListResponseVO<Permission>> loadPermissionPool() {
        List<Permission> list = generateRootNode();

        Permission new_retail = new Permission();
        new_retail.setPermission_type(PermissionManageHandler.PermissionSupport.ORDER_TYPE.getPermission_id());
        new_retail.setPermission_id(PermissionManageHandler.PermissionSupport.ORDER_TYPE_NEW_RETAIL_ORDER.getMapping_value());
        new_retail.setDescription("新零售订单");
        new_retail.setPermission_type_id(ThirdOrderStatusEnum.NEW_RETAIL_ORDER);
        new_retail.setParent_permission_id(list.get(0).getPermission_id());
        list.get(0).getChildren().add(new_retail);

        Permission bonded = new Permission();
        bonded.setPermission_type(PermissionManageHandler.PermissionSupport.ORDER_TYPE.getPermission_id());
        bonded.setPermission_id(PermissionManageHandler.PermissionSupport.ORDER_TYPE_CROSS_BORDER_BONDED_ORDER.getMapping_value());
        bonded.setDescription("跨境订单");
        bonded.setPermission_type_id(ThirdOrderStatusEnum.CROSS_BORDER_BONDED_ORDER);
        bonded.setParent_permission_id(list.get(0).getPermission_id());
        list.get(0).getChildren().add(bonded);

        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(list));
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc: 
     * @description: 加载员工的权限
     * @param: 
     * @return 
     * @create 2018-09-10 16:45
     **/
    @Override
    public Result<ListResponseVO<Permission>> findRolePermission(Long role_id) {
        if (role_id == null || role_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[role_id]不能是空值");
        }
        List<OrderTypeRoleAndOrderTypeVO> result_list = orderTypeRoleMapper.findRolePermission(role_id);
        if (UtilValidate.isEmpty(result_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }
        List<Permission> return_list = generateRootNode();
        for (OrderTypeRoleAndOrderTypeVO item : result_list) {
            if (PermissionManageHandler.PermissionSupport.ORDER_TYPE.getPermission_id().equals(item.getOrder_type_id())) {
                continue;
            }
            return_list.get(0).getChildren().add(generateItem(item, return_list.get(0).getPermission_id()));
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(return_list));
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc: 
     * @description: 保存角色的权限
     * @param: 
     * @return 
     * @create 2018-09-10 16:45
     **/
    @Override
    public Result<ListResponseVO<Permission>> findStaffPermission(Long staff_id) {
        if (staff_id == null || staff_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[staff_id]不能是空值");
        }
        List<OrderTypeRoleAndOrderTypeVO> result_list = orderTypeRoleMapper.findStaffPermission(staff_id);
        if (UtilValidate.isEmpty(result_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }
        List<Permission> return_list = generateRootNode();
        for (OrderTypeRoleAndOrderTypeVO item : result_list) {
            return_list.get(0).getChildren().add(generateItem(item, return_list.get(0).getPermission_id()));
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(return_list));
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc: 
     * @description: 保存角色的权限
     * @param: 
     * @return 
     * @create 2018-09-10 16:45
     **/
    @Override
    public Result saveRolePermission(Long role_id, List<Permission> permission_list) {
        if (role_id == null || role_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[role_id]不能是空值");
        }
        try {
            deleteByRoleId(role_id);
            if (UtilValidate.isNotEmpty(permission_list)) {
                for (Permission permission : permission_list) {
                    if (PermissionManageHandler.PermissionSupport.ORDER_TYPE.getMapping_value().equals(permission.getPermission_id())) {
                        continue;
                    }
                    OrderTypeRole orderTypeRole = new OrderTypeRole();
                    orderTypeRole.setOrder_type_id(PermissionManageHandler.PermissionSupport.getPermissionId(permission.getPermission_id()));
                    orderTypeRole.setRole_id(role_id);
                    orderTypeRole.insert();
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            throw new BizException("保存角色订单类型数据权限失败", e);
        }
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc: 
     * @description: 保存员工的权限
     * @param: 
     * @return 
     * @create 2018-09-10 16:46
     **/
    @Override
    public Result saveStaffPermission(Long staff_id, List<Permission> permission_list) {
        if (staff_id == null || staff_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[staff_id]不能是空值");
        }
        try {
            deleteByStaffId(staff_id);
            if (UtilValidate.isNotEmpty(permission_list)) {
                for (Permission permission : permission_list) {
                    if (PermissionManageHandler.PermissionSupport.ORDER_TYPE.getMapping_value().equals(permission.getPermission_id())) {
                        continue;
                    }
                    OrderTypeRole orderTypeRole = new OrderTypeRole();
                    orderTypeRole.setOrder_type_id(PermissionManageHandler.PermissionSupport.getPermissionId(permission.getPermission_id()));
                    orderTypeRole.setStaff_id(staff_id);
                    orderTypeRole.insert();
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            throw new BizException("保存员工订单类型数据权限失败", e);
        }
    }


    private void deleteByRoleId(Long role_id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("role_id", role_id);
        super.delete(wrapper);
    }

    private void deleteByStaffId(Long staff_id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("staff_id", staff_id);
        super.delete(wrapper);
    }

    private List<Permission> generateRootNode() {
        List<Permission> list = new ArrayList<>();
        Permission permission = new Permission();
        permission.setPermission_type(PermissionManageHandler.PermissionSupport.ORDER_TYPE.getPermission_id());
        permission.setPermission_id(PermissionManageHandler.PermissionSupport.ORDER_TYPE.getMapping_value());
        permission.setDescription("订单类型数据权限");
        permission.setChildren(new ArrayList<>(2));
        list.add(permission);
        return list;
    }

    private Permission generateItem(OrderTypeRoleAndOrderTypeVO orderTypeRole, Long parent) {
        Permission item = new Permission();
        item.setPermission_type(PermissionManageHandler.PermissionSupport.ORDER_TYPE.getPermission_id());
        item.setPermission_id(PermissionManageHandler.PermissionSupport.getMappingValue(orderTypeRole.getOrder_type_id()));
        item.setPermission_type_id(orderTypeRole.getOrder_type_id());
        item.setDescription(orderTypeRole.getDescription());
        item.setParent_permission_id(parent);
        return item;
    }
}
