package com.hryj.permission;

import com.alibaba.fastjson.JSON;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * @author 王光银
 * @className: PermissionManageHandler
 * @description:
 * @create 2018/9/10 0010 9:42
 **/
@Slf4j
public class PermissionManageHandler {

    /**
     * 加载权限资源池
     * @return
     */
    public static List<Permission> loadPermissionPool() {
        List<GenericPermissionSupport> list = PermissionSupport.getMappingBeans();
        if (UtilValidate.isEmpty(list)) {
            return null;
        }

        List<Permission> permissionList = new ArrayList<>(list.size());

        for (GenericPermissionSupport bean : list) {
            try {
                Result<ListResponseVO<Permission>> result = bean.loadPermissionPool();
                if (result == null) {
                    continue;
                }
                if (result.isSuccess() && result.getData() != null && UtilValidate.isNotEmpty(result.getData().getRecords())) {
                    permissionList.addAll(result.getData().getRecords());
                } else {
                    log.error("加载定制数据权限池数据失败:" + result.getCode() + " : " + result.getMsg());
                }
            } catch (Exception e) {
                log.warn("加载定制数据权限失败, bean=" + bean.getClass().getName(), e);
            }
        }
        return permissionList;
    }

    /**
     * 加载角色的权限
     * @param role_id
     * @return
     */
    public static List<Permission> findRolePermission(Long role_id) {
        if (role_id == null || role_id <= 0L) {
            return null;
        }
        List<GenericPermissionSupport> list = PermissionSupport.getMappingBeans();
        if (UtilValidate.isEmpty(list)) {
            return null;
        }
        List<Permission> permissionList = new ArrayList<>(list.size());

        for (GenericPermissionSupport bean : list) {
            try {
                Result<ListResponseVO<Permission>> result = bean.findRolePermission(role_id);
                if (result == null) {
                    continue;
                }
                if (result.isSuccess() && result.getData() != null && UtilValidate.isNotEmpty(result.getData().getRecords())) {
                    permissionList.addAll(result.getData().getRecords());
                } else {
                    log.error("查找角色定制数据权限失败:" + result.getCode() + " : " + result.getMsg());
                }
            } catch (Exception e) {
                log.warn("查找角色定制数据权限失败, bean=" + bean.getClass().getName(), e);
            }
        }
        return permissionList;
    }

    /**
     * 加载员工的权限
     * @param staff_id
     * @return
     */
    public static List<Permission> findStaffPermission(Long staff_id) {
        if (staff_id == null || staff_id <= 0L) {
            return null;
        }
        List<GenericPermissionSupport> list = PermissionSupport.getMappingBeans();
        if (UtilValidate.isEmpty(list)) {
            return null;
        }
        List<Permission> permissionList = new ArrayList<>(list.size());

        for (GenericPermissionSupport bean : list) {
            try {
                Result<ListResponseVO<Permission>> result = bean.findStaffPermission(staff_id);
                if (result == null) {
                    continue;
                }
                if (result.isSuccess() && result.getData() != null && UtilValidate.isNotEmpty(result.getData().getRecords())) {
                    permissionList.addAll(result.getData().getRecords());
                } else {
                    log.error("查找员工定制数据权限失败:" + result.getCode() + " : " + result.getMsg());
                }
            } catch (Exception e) {
                log.warn("查找员工定制数据权限失败, bean=" + bean.getClass().getName(), e);
            }
        }

        return permissionList;
    }

    public static void saveRolePermission(Long role_id, List<Permission> permission_list) {
        if (role_id == null || role_id <= 0L) {
            throw new NullPointerException("参数:[role_id]不能是空值");
        }

        savePermission(role_id, permission_list, 2);
    }

    public static void saveStaffPermission(Long staff_id, List<Permission> permission_list) {
        if (staff_id == null || staff_id <= 0L) {
            throw new NullPointerException("参数:[staff_id]不能是空值");
        }
        savePermission(staff_id, permission_list, 1);
    }

    /**
     * 保存数据权限
     * @param target_id 目标对象ID， 员工 或 角色的ID
     * @param permission_list 数据权限集合
     * @param target_type 保存对象类型， 1 员工， 2 角色
     */
    public static void savePermission(Long target_id, List<Permission> permission_list, int target_type) {
        String target_type_desc = (1 == target_type ? "员工" : "角色");
        List<String> permission_type_list = PermissionSupport.getPermissionTypes();
        if (UtilValidate.isEmpty(permission_type_list)) {
            return;
        }
        Map<String, List<Permission>> group_by_map = groupByPermissionType(permission_list);
        for (String type : permission_type_list) {
            List<Permission> item_list = (group_by_map == null ? null : group_by_map.get(type));
            if (item_list == null) {
                item_list = new LinkedList<>();
            }
            GenericPermissionSupport support = PermissionSupport.getBean(type);
            try {
                if (support == null) {
                    log.error("没有找到定制数据权限:[" + type + "]的处理实现");
                    continue;
                }
                Result result = null;
                switch (target_type) {
                    case 1:
                        result = support.saveStaffPermission(target_id, item_list);
                        break;
                    case 2:
                        result = support.saveRolePermission(target_id, item_list);
                        break;
                }
                if (result == null) {
                    log.error("保存" + target_type_desc + "定制数据权限失败: result=null");
                    continue;
                }
                if (result.isFailed()) {
                    log.error("保存" + target_type_desc + "定制数据权限失败: result=" + JSON.toJSONString(result));
                }
            } catch (Exception e) {
                log.warn("保存" + target_type_desc + "定制数据权限失败, bean=" + support.getClass().getName(), e);
            }
        }
    }

    private static Map<String, List<Permission>> groupByPermissionType(List<Permission> permission_list) {
        if (UtilValidate.isEmpty(permission_list)) {
            return null;
        }
        Map<String, List<Permission>> map = new LinkedHashMap<>();
        for (Permission item : permission_list) {
            String permission_type = item.getPermission_type();
            if (map.containsKey(permission_type)) {
                map.get(permission_type).add(item);
            } else {
                map.put(permission_type, UtilMisc.toList(item));
            }
        }
        return map;
    }

    public enum PermissionSupport {


        ORDER_TYPE(510L, "orderType", "orderTypeRoleSupport", null, "订单类型数据权限实现bean"),
        PRODUCT_TYPE(520L, "productType", "productTypeRoleSupport", null, "商品类型数据权限实现bean"),

        PRODUCT_TYPE_BONDED(10L, "bonded", null, "cross_border_bonded_order", "商品类型 - 跨境保税商品"),
        PRODUCT_TYPE_CROSS_BORDER(20L, "cross_border", null, null, "商品类型 - 跨境商品"),
        PRODUCT_TYPE_DRUG(30L, "drug", null, "drug_order", "商品类型 - 医药商品"),
        PRODUCT_TYPE_GENERAL(40L, "general", null, null, "商品类型 - 一般商品"),
        PRODUCT_TYPE_NEW_RETAIL(50L, "new_retail", null, "normal_order", "商品类型 - 新零售商品"),


        ORDER_TYPE_CROSS_BORDER_BONDED_ORDER(60L, "cross_border_bonded_order", null, null, "订单类型 - 跨境保税订单"),
        ORDER_TYPE_CROSS_BORDER_DIRECT_ORDER(70L, "cross_border_direct_order", null, null, "订单类型 - 跨境直邮订单"),
        ORDER_TYPE_CROSS_BORDER_ORDER(80L, "cross_border_order", null, null, "订单类型 - 跨境订单"),
        ORDER_TYPE_DRUG_ORDER(90L, "drug_order", null, null, "订单类型 - 医药订单"),
        ORDER_TYPE_HEALTH_ORDER(100L, "health_order", null, null, "订单类型 - 保健品订单"),
        ORDER_TYPE_NEW_RETAIL_ORDER(110L, "new_retail_order", null, null, "订单类型 - 新零售订单"),
        ORDER_TYPE_NORMAL_ORDER(120L, "normal_order", null, null, "订单类型 - 新零售一般订单"),
        ORDER_TYPE_PRE_SALE_CYCLE(130L, "pre_sale_cycle", null, null, "订单类型 - 新零售预售周期订单"),
        ORDER_TYPE_PRE_SALE_ORDER(140L, "pre_sale_order", null, null, "订单类型 - 新零售预售订单"),
        ORDER_TYPE_PURCHASE_ORDER(150L, "purchase_order", null, null, "订单类型 - 采购订单"),
        ORDER_TYPE_SALES_ORDER(160L, "sales_order", null, null, "订单类型 - 销售订单");

        private Long mapping_value;

        private String permission_id;

        private String mapping_bean_name;

        private String mapping_order_type;

        private String desc;

        PermissionSupport(Long mapping_value, String permission_id, String mapping_bean_name, String mapping_order_type, String desc) {
            this.permission_id = permission_id;
            this.mapping_bean_name = mapping_bean_name;
            this.desc = desc;
            this.mapping_value = mapping_value;
            this.mapping_order_type = mapping_order_type;
        }

        public static boolean containsProductType(String prod_type_id) {
            if (UtilValidate.isEmpty(prod_type_id)) {
                return false;
            }
            for (PermissionSupport item : MAPPING_BEAN_NAME_ARR) {
                if (item.mapping_value < 10L || item.mapping_value > 50L) {
                    continue;
                }
                if (item.permission_id.equals(prod_type_id)) {
                    return true;
                }
            }
            return false;
        }

        public Long getMapping_value() {
            return this.mapping_value;
        }

        public String getPermission_id() {
            return this.permission_id;
        }

        public String getMappingOrderType() {
            return this.mapping_order_type;
        }

        private static final PermissionSupport[] MAPPING_BEAN_NAME_ARR = PermissionSupport.values();

        public static String getMappingOrderType(String product_type_id) {
            if (UtilValidate.isEmpty(product_type_id)) {
                return null;
            }
            for (PermissionSupport item : MAPPING_BEAN_NAME_ARR) {
                if (item.permission_id.equals(product_type_id)) {
                    return item.mapping_order_type;
                }
            }
            return null;
        }

        public static Long getMappingValue(String permission_id) {
            if (UtilValidate.isEmpty(permission_id)) {
                return null;
            }
            for (PermissionSupport item : MAPPING_BEAN_NAME_ARR) {
                if (item.permission_id.equals(permission_id)) {
                    return item.mapping_value;
                }
            }
            return null;
        }

        public static String getPermissionId(Long mapping_value) {
            if (mapping_value == null || mapping_value <= 0L) {
                return null;
            }
            for (PermissionSupport item : MAPPING_BEAN_NAME_ARR) {
                if (item.mapping_value.equals(mapping_value)) {
                    return item.permission_id;
                }
            }
            return null;
        }

        public static List<String> getMappingBeanNames() {
            List<String> list = new ArrayList<>(MAPPING_BEAN_NAME_ARR.length);
            for (PermissionSupport permissionSupport : MAPPING_BEAN_NAME_ARR) {
                if (UtilValidate.isEmpty(permissionSupport.mapping_bean_name)) {
                    continue;
                }
                list.add(permissionSupport.mapping_bean_name);
            }
            return list;
        }

        public static List<String> getPermissionTypes() {
            List<String> list = new ArrayList<>(MAPPING_BEAN_NAME_ARR.length);
            for (PermissionSupport permissionSupport : MAPPING_BEAN_NAME_ARR) {
                if (UtilValidate.isEmpty(permissionSupport.mapping_bean_name)) {
                    continue;
                }
                list.add(permissionSupport.permission_id);
            }
            return list;
        }

        public static List<GenericPermissionSupport> getMappingBeans() {
            List<GenericPermissionSupport> list = new ArrayList<>(MAPPING_BEAN_NAME_ARR.length);
            for (PermissionSupport permissionSupport : MAPPING_BEAN_NAME_ARR) {
                if (UtilValidate.isEmpty(permissionSupport.mapping_bean_name)) {
                    continue;
                }
                GenericPermissionSupport support = SpringContextUtil.getBean(permissionSupport.mapping_bean_name, GenericPermissionSupport.class);
                if (support == null) {
                    continue;
                }
                list.add(support);
            }
            return list;
        }

        public static String getBeanName(String type) {
            if (UtilValidate.isEmpty(type)) {
                return null;
            }
            for (PermissionSupport permissionSupport : MAPPING_BEAN_NAME_ARR) {
                if (permissionSupport.permission_id.equals(type)) {
                    return permissionSupport.mapping_bean_name;
                }
            }
            return null;
        }

        public static GenericPermissionSupport getBean(String type) {
            String beanName = getBeanName(type);
            if (UtilValidate.isEmpty(beanName)) {
                return null;
            }
            return SpringContextUtil.getBean(beanName, GenericPermissionSupport.class);
        }

    }
}
