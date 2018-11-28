package com.hryj.service.prodtype;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.permission.ProductTypeRole;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.prodtype.ProductTypeRoleAndProductType;
import com.hryj.exception.BizException;
import com.hryj.mapper.prodtype.ProductTypeRoleMapper;
import com.hryj.permission.GenericPermissionSupport;
import com.hryj.permission.Permission;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.service.worktask.RefreshProductTypeRoleCacheTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author 王光银
 * @className: ProductTypeRoleService
 * @description:
 * @create 2018/9/10 0010 11:23
 **/
@Service
@Slf4j
public class ProductTypeRoleService extends ServiceImpl<ProductTypeRoleMapper, ProductTypeRole> implements GenericPermissionSupport {

    @Autowired
    private ProductTypeRoleMapper productTypeRoleMapper;

    @Override
    public Result<ListResponseVO<Permission>> loadPermissionPool() {
        List<Permission> list = generateRootNode();

        Permission new_retail = new Permission();
        new_retail.setPermission_type(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE.getPermission_id());
        new_retail.setPermission_id(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_NEW_RETAIL.getMapping_value());
        new_retail.setDescription("新零售");
        new_retail.setParent_permission_id(list.get(0).getPermission_id());
        new_retail.setPermission_type_id(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_NEW_RETAIL.getPermission_id());
        list.get(0).getChildren().add(new_retail);

        Permission bonded = new Permission();
        bonded.setPermission_type(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE.getPermission_id());
        bonded.setPermission_id(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_BONDED.getMapping_value());
        bonded.setDescription("跨境商品");
        bonded.setParent_permission_id(list.get(0).getPermission_id());
        bonded.setPermission_type_id(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_BONDED.getPermission_id());
        list.get(0).getChildren().add(bonded);

        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(list));
    }

    @Override
    public Result<ListResponseVO<Permission>> findRolePermission(Long role_id) {
        if (role_id == null || role_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[role_id]不能是空值");
        }
        List<ProductTypeRoleAndProductType> result_list = productTypeRoleMapper.findRolePermission(role_id);
        if (UtilValidate.isEmpty(result_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }
        List<Permission> return_list = generateRootNode();
        for (ProductTypeRoleAndProductType item : result_list) {
            return_list.get(0).getChildren().add(item.convertToPermission(result_list.get(0).getId()));
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(return_list));
    }

    @Override
    public Result<ListResponseVO<Permission>> findStaffPermission(Long staff_id) {
        if (staff_id == null || staff_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[staff_id]不能是空值");
        }
        List<ProductTypeRoleAndProductType> result_list = productTypeRoleMapper.findStaffPermission(staff_id);
        if (UtilValidate.isEmpty(result_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }
        List<Permission> return_list = generateRootNode();
        for (ProductTypeRoleAndProductType item : result_list) {
            return_list.get(0).getChildren().add(item.convertToPermission(result_list.get(0).getId()));
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(return_list));
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, BizException.class})
    public Result saveRolePermission(Long role_id, List<Permission> permission_list) {
        if (role_id == null || role_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[role_id]不能是空值");
        }

        List<String> product_type_role_list = new LinkedList<>();
        try {
            deleteByRoleId(role_id);

            if (UtilValidate.isNotEmpty(permission_list)) {
                for (Permission permission : permission_list) {
                    if (PermissionManageHandler.PermissionSupport.PRODUCT_TYPE.getMapping_value().equals(permission.getPermission_id())) {
                        continue;
                    }
                    ProductTypeRole productTypeRole = new ProductTypeRole();
                    productTypeRole.setProduct_type_id(PermissionManageHandler.PermissionSupport.getPermissionId(permission.getPermission_id()));
                    productTypeRole.setRole_id(role_id);
                    product_type_role_list.add(productTypeRole.getProduct_type_id());
                    productTypeRole.insert();
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            throw new BizException("保存角色商品类型数据权限失败", e);
        } finally {
            //启动任务刷新角色缓存中的商品类型权限
            ThreadPoolUtil.submitTask(new RefreshProductTypeRoleCacheTask(role_id, product_type_role_list));
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, BizException.class})
    public Result saveStaffPermission(Long staff_id, List<Permission> permission_list) {
        if (staff_id == null || staff_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "参数:[staff_id]不能是空值");
        }

        List<String> product_type_role_list = new LinkedList<>();
        try {
            deleteByStaffId(staff_id);

            if (UtilValidate.isNotEmpty(permission_list)) {
                for (Permission permission : permission_list) {
                    if (PermissionManageHandler.PermissionSupport.PRODUCT_TYPE.getMapping_value().equals(permission.getPermission_id())) {
                        continue;
                    }
                    ProductTypeRole productTypeRole = new ProductTypeRole();
                    productTypeRole.setProduct_type_id(PermissionManageHandler.PermissionSupport.getPermissionId(permission.getPermission_id()));
                    productTypeRole.setStaff_id(staff_id);
                    product_type_role_list.add(productTypeRole.getProduct_type_id());
                    productTypeRole.insert();
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            throw new BizException("保存员工商品类型数据权限失败", e);
        } finally {
            //启动任务刷新员工缓存中的商品类型权限
            ThreadPoolUtil.submitTask(new RefreshProductTypeRoleCacheTask(staff_id, product_type_role_list));
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
        permission.setPermission_type(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE.getPermission_id());
        permission.setPermission_id(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE.getMapping_value());
        permission.setDescription("商品类型数据权限");
        permission.setChildren(new ArrayList<>(2));
        list.add(permission);
        return list;
    }
}
