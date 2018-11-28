package com.hryj.cache;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.permission.ProductTypeRole;
import com.hryj.entity.bo.product.prodtype.ProductType;
import com.hryj.entity.bo.product.prodtype.ProductTypeAttr;
import com.hryj.mapper.prodtype.ProductTypeAttrMapper;
import com.hryj.mapper.prodtype.ProductTypeMapper;
import com.hryj.mapper.prodtype.ProductTypeRoleMapper;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.utils.SpringContextUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author 王光银
 * @className: ProductTypeCacheHandler
 * @description:
 * @create 2018/9/12 0012 10:15
 **/
@Slf4j
@Component
@Data
public class ProductTypeCacheHandler {

    private static final String PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME = "product_type_role_cache_group_name";

    public static ProductTypeCacheHandler productTypeCacheHandler;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedisLock redisLock;

    @PostConstruct
    public void init() {
        productTypeCacheHandler = this;
        productTypeCacheHandler.redisService = this.redisService;
        productTypeCacheHandler.redisLock = this.redisLock;
    }

    private static final Map<String, ProdTypeRelationship> PROD_TYPE_CACHE_MAP = new HashMap<>(20);

    /**
     * 将商品类型基础数据加载到缓存中
     */
    public static void cacheInit() {

        /**
         * 商品类型基础数据加载到应用缓存
         */
        ProductTypeMapper productTypeMapper = SpringContextUtil.getBean("productTypeMapper", ProductTypeMapper.class);
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.orderBy("product_type_id", true);
        List<ProductType> prod_type_list = productTypeMapper.selectList(wrapper);
        if (UtilValidate.isEmpty(prod_type_list)) {
            return;
        }

        for (ProductType item : prod_type_list) {
            ProdTypeRelationship this_item = new ProdTypeRelationship(item);
            PROD_TYPE_CACHE_MAP.put(this_item.getSelf().getProduct_type_id(), this_item);
        }

        for (ProductType item : prod_type_list) {
            if (UtilValidate.isEmpty(item.getParent_type_id())) {
                continue;
            }

            ProdTypeRelationship this_item = PROD_TYPE_CACHE_MAP.get(item.getProduct_type_id());
            ProdTypeRelationship parent_item = PROD_TYPE_CACHE_MAP.get(item.getParent_type_id());

            this_item.setParent(parent_item);
            parent_item.addSon(this_item);
        }

        //加载商品类型属性
        ProductTypeAttrMapper productTypeAttrMapper = SpringContextUtil.getBean("productTypeAttrMapper", ProductTypeAttrMapper.class);
        wrapper = new EntityWrapper();
        wrapper.orderBy("product_type_id", true);
        List<ProductTypeAttr> attr_list = productTypeAttrMapper.selectList(wrapper);
        if (UtilValidate.isNotEmpty(attr_list)) {
            for (ProductTypeAttr attr : attr_list) {
                ProdTypeRelationship target = getTarget(attr.getProduct_type_id());
                if (target == null) {
                    continue;
                }
                target.addAttr(attr);
            }
        }

        /**
         * 加载商品类型权限，数据权限缓存必须放在redis上，权限会发生变更
         * 数据加载需要加锁
         */
        loadProductTypeRole();

        log.info("------- 商品类型数据缓存任务执行完成...");
    }

    /**
     * 刷新角色或员工的商品类型权限缓存数据
     * @param target_id
     * @param permission_list
     */
    public static void resetProductTypePermission(Long target_id, List<String> permission_list) {
        if (target_id == null || target_id <= 0L) {
            return;
        }

        String key = target_id.toString();

        //加锁
        boolean lock_result;
        while (true) {
            lock_result = productTypeCacheHandler.redisLock.lock(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, key, 3);
            if (lock_result) {
                break;
            }
            try {
                Thread.sleep(50);
            } catch (Exception e) {
                log.error("Thread.sleep(50):", e);
            }
        }

        try {
            //先删除原有缓存数据
            productTypeCacheHandler.redisService.delete2(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, key);
            if (UtilValidate.isEmpty(permission_list)) {
                return;
            }
            productTypeCacheHandler.redisService.put2(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, target_id.toString(), JSON.toJSONString(permission_list), null);
        } catch (Exception e) {
            log.error("商品类型数据权限缓存数据反序列化失败", e);
            return;
        } finally {
            //释放锁
            productTypeCacheHandler.redisLock.unLock(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, target_id.toString());
        }
    }

    /**
     * 返回员工或角色的商品类型权限
     * @param target_id
     * @return
     */
    public static List<String> getProductTypePermission(Long target_id) {
        if (target_id == null || target_id <= 0L) {
            return null;
        }
        String cache_value = productTypeCacheHandler.redisService.get2(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, target_id.toString());
        if (UtilValidate.isEmpty(cache_value)) {
            //缓存中没有数据权限时，从数据库加载一次
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("staff_id", target_id);
            wrapper.or().eq("role_id", target_id);
            ProductTypeRoleMapper productTypeRoleMapper = SpringContextUtil.getBean("productTypeRoleMapper", ProductTypeRoleMapper.class);
            List<ProductTypeRole> list = productTypeRoleMapper.selectList(wrapper);
            if (UtilValidate.isNotEmpty(list)) {
                Map<Long, List<String>> map = new LinkedHashMap<>();
                for (ProductTypeRole role : list) {
                    if (UtilValidate.isEmpty(role.getProduct_type_id())) {
                        continue;
                    }
                    Long id = null;
                    if (role.getRole_id() != null && role.getRole_id() > 0L) {
                        id = role.getRole_id();
                    }
                    if (role.getStaff_id() != null && role.getStaff_id() > 0L) {
                        id = role.getStaff_id();
                    }
                    if (id == null) {
                        continue;
                    }
                    if (map.containsKey(id)) {
                        map.get(id).add(role.getProduct_type_id());
                    } else {
                        map.put(id, UtilMisc.toList(role.getProduct_type_id()));
                    }
                }
                if (UtilValidate.isNotEmpty(map)) {
                    for (Long id : map.keySet()) {
                        resetProductTypePermission(id, map.get(id));
                    }
                }
                return getProductTypePermission(target_id);
            }
            return null;
        }

        try {
            JSONArray cache_arr = JSONArray.parseArray(cache_value);
            if (UtilValidate.isEmpty(cache_arr)) {
                return null;
            }
            return cache_arr.toJavaList(String.class);
        } catch (Exception e) {
            log.error("商品类型数据权限缓存数据反序列化失败", e);
            return null;
        }
    }

    public static boolean hasProductTypePermission(Long target_id, String product_type_id) {
        List<String> permission_list = getProductTypePermission(target_id);
        if (UtilValidate.isEmpty(permission_list)) {
            return false;
        }
        return permission_list.contains(product_type_id);
    }

    private static void loadProductTypeRole() {
        boolean lock_result = productTypeCacheHandler.redisLock.lock(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, 3);
        if (!lock_result) {
            return;
        }

        try {
            ProductTypeRoleMapper productTypeRoleMapper = SpringContextUtil.getBean("productTypeRoleMapper", ProductTypeRoleMapper.class);
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.orderBy("product_type_id", true);
            List<ProductTypeRole> prod_type_role_list = productTypeRoleMapper.selectList(wrapper);
            if (UtilValidate.isEmpty(prod_type_role_list)) {
                cleanProductTypeRole();
                return;
            }

            Map<String, Set<String>> staff_include = new LinkedHashMap<>();
            Map<String, Set<String>> role_include = new LinkedHashMap<>();

            for (ProductTypeRole item : prod_type_role_list) {
                if (UtilValidate.isEmpty(item.getProduct_type_id())) {
                    continue;
                }
                boolean role_id_null = item.getRole_id() == null || item.getRole_id() <= 0L;
                boolean staff_id_null = item.getStaff_id() == null || item.getStaff_id() <= 0L;
                if (role_id_null && staff_id_null) {
                    continue;
                }

                if (item.getStaff_id() != null && item.getStaff_id() > 0L) {
                    if (staff_include.containsKey(item.getStaff_id().toString())) {
                        staff_include.get(item.getStaff_id().toString()).add(item.getProduct_type_id());
                    } else {
                        staff_include.put(item.getStaff_id().toString(), UtilMisc.toSet(item.getProduct_type_id()));
                    }
                }

                if (item.getRole_id() != null && item.getRole_id() > 0L) {
                    if (role_include.containsKey(item.getRole_id().toString())) {
                        role_include.get(item.getRole_id().toString()).add(item.getProduct_type_id());
                    } else {
                        role_include.put(item.getRole_id().toString(), UtilMisc.toSet(item.getProduct_type_id()));
                    }
                }
            }

            if (UtilValidate.isNotEmpty(staff_include)) {
                for (String staff_id : staff_include.keySet()) {
                    productTypeCacheHandler.redisService.put2(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, staff_id, JSON.toJSONString(staff_include.get(staff_id)), null);
                }

            }

            if (UtilValidate.isNotEmpty(role_include)) {
                for (String role_id : role_include.keySet()) {
                    productTypeCacheHandler.redisService.put2(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, role_id, JSON.toJSONString(role_include.get(role_id)), null);
                }
            }
        } catch (Exception e) {
            log.error("加载商品类型数据权限数据失败:", e);
        } finally {
            productTypeCacheHandler.redisLock.unLock(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME);
        }


    }

    private static void cleanProductTypeRole() {
        Set<String> keys = productTypeCacheHandler.redisService.getKeysByGroupName2(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME);
        if (UtilValidate.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            productTypeCacheHandler.redisService.delete2(PRODUCT_TYPE_ROLE_CACHE_GROUP_NAME, key);
        }
    }


    /**
     * 获取商品类型的 title-mark 属性值
     * @param product_type_id
     * @return
     */
    public static List<String> getProductTypeTitleMark(String product_type_id) {
        //商品类型的title_mark属性
        String attr_name = "title_mark";
        String attr_value = getProductTypeAttrValue(product_type_id, attr_name, false);
        if (UtilValidate.isEmpty(attr_value)) {
            return null;
        }
        return CollUtil.toList(attr_value.split(","));
    }

    /**
     * 返回指定商品类型的指定属性值
     * @param product_type_id 商品类型ID
     * @param attr_name 属性名称
     * @param search_parent 当指定商品类型没有查找到指定属性时，是否到父级类型中搜索属性,  true搜索父级，false 不搜索
     * @return
     */
    public static String getProductTypeAttrValue(String product_type_id, String attr_name, boolean search_parent) {
        ProductTypeAttr attr = searchProdTypeAttr(product_type_id, attr_name, search_parent);
        return attr == null ? getDoesNotExistsStr(attr_name) : attr.getAttr_value();
    }

    /**
     * 查找商品类型属性
     * @param product_type_id
     * @param attr_name
     * @param search_parent
     * @return
     */
    private static ProductTypeAttr searchProdTypeAttr(String product_type_id, String attr_name, boolean search_parent) {
        if (UtilValidate.isEmpty(product_type_id) || UtilValidate.isEmpty(attr_name)) {
            return null;
        }

        ProdTypeRelationship target = getTarget(product_type_id);
        if (target == null) {
            return null;
        }

        boolean exists_attr = target.containsAttr(attr_name);

        if (exists_attr) {
            return target.getAttr(attr_name);
        }

        if (!search_parent) {
            return null;
        }

        if (target.getParent() == null) {
            return null;
        }
        return searchProdTypeAttr(target.getParent().getSelf().getProduct_type_id(), attr_name, search_parent);
    }

    /**
     * 返回对应的商品类型ID的描述
     * @param product_type_id
     * @return
     */
    public static String getProductTypeDescription(String product_type_id) {
        if (UtilValidate.isEmpty(product_type_id)) {
            return CommonConstantPool.NULL_DEFAULT;
        }
        ProdTypeRelationship target = getTarget(product_type_id);
        return target == null ? getDoesNotExistsStr(product_type_id) : target.getSelf().getDescription();
    }

    private static String getDoesNotExistsStr(String product_type_id) {
        //return "doesn't exists:[" + product_type_id + "]";
        return null;
    }

    private static ProdTypeRelationship getTarget(String product_type_id) {
        if (UtilValidate.isEmpty(product_type_id)) {
            return null;
        }
        if (!PROD_TYPE_CACHE_MAP.containsKey(product_type_id)) {
            return null;
        }
        return PROD_TYPE_CACHE_MAP.get(product_type_id);
    }


    @Data
    public static class ProdTypeRelationship {
        public ProdTypeRelationship(ProductType self) {
            this.self = self;
        }

        private ProductType self;

        private ProdTypeRelationship parent;

        private List<ProdTypeRelationship> son_list;

        private Map<String, ProdTypeRelationship> son_map;

        private Map<String, ProductTypeAttr> attr_map;

        public void addSon(ProdTypeRelationship son) {
            if (UtilValidate.isEmpty(this.son_list)) {
                this.son_list = UtilMisc.toList(son);
                this.son_map = UtilMisc.toMap(son.self.getProduct_type_id(), son);
                return;
            }
            this.son_list.add(son);
            this.son_map.put(son.self.getProduct_type_id(), son);
        }

        public void addAttr(ProductTypeAttr attr) {
            if (UtilValidate.isEmpty(this.attr_map)) {
                this.attr_map = UtilMisc.toMap(attr.getAttr_name(), attr);
                return;
            }
            this.attr_map.put(attr.getAttr_name(), attr);
        }

        public boolean containsAttr(String attr_name) {
            if (UtilValidate.isEmpty(this.attr_map)) {
                if (this.parent == null) {
                    return false;
                }
                return this.parent.containsAttr(attr_name);
            }
            return this.attr_map.containsKey(attr_name);
        }

        public ProductTypeAttr getAttr(String attr_name) {
            if (UtilValidate.isEmpty(this.attr_map)) {
                if (this.parent == null) {
                    return null;
                }
                return this.parent.getAttr(attr_name);
            }
            return this.attr_map.containsKey(attr_name) ? this.attr_map.get(attr_name) : null;
        }
    }

    /**
     * 返回是否是跨境商品， true是，false不是
     * @param prod_type_id
     * @return
     */
    public static boolean isCrossBorder(String prod_type_id) {
        if (UtilValidate.isEmpty(prod_type_id)) {
            return false;
        }

        ProdTypeRelationship target = getTop(prod_type_id);
        if (PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_CROSS_BORDER.getPermission_id().equals(target.self.getProduct_type_id())) {
            return true;
        }
        return false;
    }

    private static ProdTypeRelationship getTop(String prod_type_id) {
        ProdTypeRelationship target = getTarget(prod_type_id);
        if (target.parent == null) {
            return target;
        }
        return getTop(target.parent.self.getProduct_type_id());
    }

}
