package com.hryj.service.inventory;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.entity.vo.inventory.mapping.InventoryLockMappingItem;
import com.hryj.entity.vo.inventory.request.ProductInventoryLockItem;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRequestVO;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRollBackRequestVO;
import com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO;
import com.hryj.entity.vo.product.request.ProductInventoryAdjustmentRequestVO;
import com.hryj.mapper.CrossBorderProductMapper;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.service.ProductService;
import com.hryj.service.inventory.cache.InventoryCacheUtil;
import com.hryj.service.inventory.cache.ProductInventorySummary;
import com.hryj.service.inventory.task.ProductInventoryAdjustmentItem;
import com.hryj.service.inventory.task.ProductInventoryStoreTask;
import com.hryj.service.util.CommonUtil;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.service.worktask.PartyProductInventoryChangeTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 王光银
 * @className: ProductInventoryService
 * @description:
 * @create 2018/8/20 0020 11:00
 **/
@Service
@Slf4j
public class ProductInventoryService extends ServiceImpl<PartyProductMapper, PartyProduct> {

    @Autowired
    private CrossBorderProductMapper crossBorderProductMapper;

    @Autowired
    private ProductService productService;

    public Result crossBorderProductInventoryAdjustment(ProductInventoryAdjustmentRequestVO requestVO) {
        if (requestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请求参数不能是空值");
        }
        if (requestVO.getProduct_id() == null || requestVO.getProduct_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID不能是空值");
        }
        if (requestVO.getInventory_quantity() == null || requestVO.getInventory_quantity() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "库存数据不能是空值且必须大于0");
        }

        boolean lock_result = false;
        boolean sync_cache = false;
        InventoryUniqueItem item = new InventoryUniqueItem(requestVO.getProduct_id());
        try {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id", requestVO.getProduct_id());
            wrapper.setSqlSelect("product_type_id");
            ProductInfo productInfo = productService.selectOne(wrapper);

            if (productInfo == null) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "商品不存在, id=" + requestVO.getProduct_id());
            }

            if (!ProductTypeCacheHandler.hasProductTypePermission(CommonUtil.getStaffIdFromCache(requestVO.getLogin_token()), productInfo.getProduct_type_id())) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您没有商品类型:[" + ProductTypeCacheHandler.getProductTypeDescription(productInfo.getProduct_type_id()) + "]的权限");
            }


            lock_result = RedisCacheUtil.PartyProductInventoryLockCacheUtil.addLock(UtilMisc.toList(item));
            if (!lock_result) {
                return new Result(CodeEnum.FAIL_BUSINESS, "系统繁忙,请稍后再试");
            }

            wrapper = new EntityWrapper();
            wrapper.eq("id", requestVO.getProduct_id());
            wrapper.setSqlSelect("inventory_quantity");
            List<CrossBorderProduct> list = crossBorderProductMapper.selectList(wrapper);
            if (UtilValidate.isEmpty(list)) {
                return new Result(CodeEnum.FAIL_BUSINESS, "跨境商品不存在, id=" + requestVO.getProduct_id());
            }
            CrossBorderProduct prod = list.remove(0);
            prod.setId(requestVO.getProduct_id());
            prod.setInventory_quantity(requestVO.getInventory_quantity());
            prod.updateById();
            sync_cache = true;
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("跨境商品库存更新失败", e);
            //只有一个进行持久化的地方，不需要抛出异常，返回正常失败消息
            return new Result(CodeEnum.FAIL_BUSINESS, "跨境商品库存更新失败");
        } finally {
            //同步库存缓存
            if (sync_cache) {
                ProductInventorySummary pis = item.getProductInventorySummary();
                pis.setInventory_quantity(requestVO.getInventory_quantity());
                InventoryCacheUtil.addCache(pis);
            }
            //释放锁
            if (lock_result) {
                RedisCacheUtil.PartyProductInventoryLockCacheUtil.releaseLock(UtilMisc.toList(item));
            }
        }
    }


    /**
     * @author 王光银
     * @methodName: lockProductInventory
     * @methodDesc: 锁定商品库存
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO>
     * @create 2018-08-20 17:03
     **/
    public Result<ProductsInventoryLockResponseVO> lockProductInventory(ProductsInventoryLockRequestVO requestVO) {
        return partyProductInventoryAdjust(requestVO, false);
    }

    @Transactional(rollbackFor = {RuntimeException.class})
    protected Result<ProductsInventoryLockResponseVO> partyProductInventoryAdjust(ProductsInventoryLockRequestVO requestVO, final Boolean is_compensate) {
        if (!requestVO.checkLockModel()) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "不能识别的锁定模式参数值: lock_model=" + requestVO.getLock_model());
        }
        if (UtilValidate.isEmpty(requestVO.getLock_items())) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "商品库存请求条目集合参数不能是空值");
        }
        Map<InventoryUniqueItem, InventoryUniqueItem> lock_sum_map = new HashMap<>(requestVO.getLock_items().size());
        for (ProductInventoryLockItem lockItem : requestVO.getLock_items()) {
            if (lockItem.getParty_id() == null || lockItem.getParty_id() <= 0L) {
                return new Result<>(CodeEnum.FAIL_PARAMCHECK, "商品库存锁定条目[party_id]不能为空");
            }
            if (lockItem.getProduct_id() == null || lockItem.getProduct_id() <= 0L) {
                return new Result<>(CodeEnum.FAIL_PARAMCHECK, "商品库存锁定条目[product_id]不能为空");
            }
            if (lockItem.getLock_quantity() == null || lockItem.getLock_quantity() == 0L) {
                return new Result<>(CodeEnum.FAIL_PARAMCHECK, "商品库存锁定条目[lock_quantity]不能为空且不能等于0");
            }
            if (CommonConstantPool.SUB.equals(requestVO.getLock_model())) {
                lockItem.setLock_quantity(-Math.abs(lockItem.getLock_quantity()));
            } else {
                lockItem.setLock_quantity(Math.abs(lockItem.getLock_quantity()));
            }

            //加载商品类型，商品类型决定是否对商品进行合并验证，特别是跨境商品
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id", lockItem.getProduct_id());
            wrapper.setSqlSelect("product_type_id");
            ProductInfo productInfo = productService.selectOne(wrapper);
            if (productInfo == null) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "商品不存在");
            }

            InventoryUniqueItem item = new InventoryUniqueItem(lockItem, productInfo.getProduct_type_id());
            if (lock_sum_map.containsKey(item)) {
                lock_sum_map.get(item).merge(item);
            } else {
                lock_sum_map.put(item, item);
            }
        }

        if (UtilValidate.isEmpty(lock_sum_map)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "无效的商品库存请求参数");
        }

        /**
         * 开始库存的锁定操作
         */

        Collection<InventoryUniqueItem> lock_item_list = lock_sum_map.values();

        List<InventoryUniqueItem> inventory_out_list = new LinkedList<>();
        List<InventoryUniqueItem> inventory_ok_list = new LinkedList<>();

        boolean lock_result = false;
        boolean operation_result = false;
        List<InventoryUniqueItem> need_to_refresh_cache_list = new LinkedList<>();
        try {
            //加锁
            lock_result = RedisCacheUtil.PartyProductInventoryLockCacheUtil.addLock(lock_item_list);
            if (!lock_result) {
                log.error("商品库存锁定 - 由于并发导致锁定失败返回");
                return new Result<>(CodeEnum.FAIL_BUSINESS, "系统繁忙,请稍后再试");
            }

            //加载库存（V1.2版本开始：库存调整为从缓存中获取, 商品数据库中库存数量更新采取异步队列方式更新）
            List<ProductInventoryAdjustmentItem> for_sync_process = new LinkedList<>();

            for (InventoryUniqueItem item : lock_item_list) {

                if (CommonConstantPool.SUB.equals(requestVO.getLock_model())) {
                    if (!InventoryCacheUtil.check(item)) {
                        inventory_out_list.add(item);
                        continue;
                    }
                }

                inventory_ok_list.add(item);
            }

            ProductsInventoryLockResponseVO return_result = new ProductsInventoryLockResponseVO();

            //如果存在库存不足的商品，本次库存锁定请求失败，返回失败结果
            if (UtilValidate.isNotEmpty(inventory_out_list)) {
                return_result.setLock_result(CommonConstantPool.UPPER_N);
                List<ProductInventoryLockItem> return_lock_failed_list = new ArrayList<>(inventory_out_list.size());
                for (InventoryUniqueItem item : inventory_out_list) {
                    return_lock_failed_list.addAll(item.getLockItemList());
                }
                return_result.setInventory_out_item(return_lock_failed_list);
                return new Result(CodeEnum.FAIL_BUSINESS, return_result);
            }

            //库存锁定成功，更新缓存数据 并且生成异步库存处理对象
            for (InventoryUniqueItem item : lock_item_list) {
                for_sync_process.add(item.getProductInventoryAdjustmentItem());


                /**
                 * 库存锁定处理（库存变更直接影响商品的搜索结果）
                 * 库存变更对缓存的影响只需要关心两种情况，1：变更后库存为0      2：变更前库存为0
                 */

                Integer before_locked_quantity = InventoryCacheUtil.adjustment(item);
                Integer after_locked_quantity = before_locked_quantity + item.getLock_num();

                if (before_locked_quantity.intValue() <= 0 || after_locked_quantity.intValue() <= 0) {
                    need_to_refresh_cache_list.add(item);
                }

                //异步处理缓存库存更新
                //ThreadPoolUtil.submitTask(() -> {
                //    InventoryCacheUtil.adjustment(item);
                //});
            }

            //异步处理库存更新的队列添加操作
            ThreadPoolUtil.submitTask(() -> {
                for (ProductInventoryAdjustmentItem taskItem : for_sync_process) {
                    ProductInventoryStoreTask.add(taskItem);
                }
            });

            //释放锁
            RedisCacheUtil.PartyProductInventoryLockCacheUtil.releaseLock(lock_item_list);
            lock_result = false;
            return_result.setLock_result(CommonConstantPool.UPPER_Y);

            /**
             * 库存锁定成功后，将请求数据缓存，以备回滚时使用
             */
            if (!is_compensate) {
                String transaction_code = UUID.randomUUID().toString();
                return_result.setTransaction_code(transaction_code);

                ThreadPoolUtil.submitTask(() -> {
                    String json_str = null;
                    try {
                        json_str = JSON.toJSONString(requestVO);
                    } catch (Exception e) {
                        log.error("库存锁定失败 - 数据序列化失败:", e);
                    }

                    try {
                        //缓存库存锁定数据
                        RedisCacheUtil.PartyProductInventoryLockCacheUtil.setCacheData(transaction_code, json_str);
                    } catch (Exception e) {
                        log.error("库存锁定失败 - 缓存锁定数据失败:", e);
                    }
                });
            }

            operation_result = true;
            return new Result<>(CodeEnum.SUCCESS, return_result);
        } catch (Exception e) {
            throw new RuntimeException("库存锁定操作失败:" + e.getMessage(), e);
        } finally {
            if (lock_result) {
                RedisCacheUtil.PartyProductInventoryLockCacheUtil.releaseLock(lock_item_list);
            }
            if (operation_result) {

                if (UtilValidate.isNotEmpty(need_to_refresh_cache_list)) {
                    //库存操作成功后启动任务处理缓存数据
                    ThreadPoolUtil.submitTask(new PartyProductInventoryChangeTask(need_to_refresh_cache_list));
                }
            }
        }
    }


    /**
     * @author 王光银
     * @methodName: compensateProductsLock
     * @methodDesc:
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-20 17:04
     **/
    public Result compensateProductsLock(ProductsInventoryLockRollBackRequestVO requestVO) {
        if (UtilValidate.isEmpty(requestVO.getTransaction_code())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数: transaction_code不能为空");
        }
        String cache_data = RedisCacheUtil.PartyProductInventoryLockCacheUtil.getCacheData(requestVO.getTransaction_code());
        if (UtilValidate.isEmpty(cache_data)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "无效的transaction_code,或者该事务码已经执行过回滚");
        }
        ProductsInventoryLockRequestVO lockRequestVO = null;
        try {
            lockRequestVO = JSON.toJavaObject(JSON.parseObject(cache_data), ProductsInventoryLockRequestVO.class);
        } catch (Exception e) {
            log.error("商品库存锁定补偿失败 - 反序列化数据失败:", e);
            return new Result(CodeEnum.FAIL_BUSINESS, "商品库存锁定补偿失败 - 反序列化数据失败:" + e.getMessage());
        }
        if (CommonConstantPool.SUB.equals(lockRequestVO.getLock_model())) {
            lockRequestVO.setLock_model(CommonConstantPool.ADD);
        } else {
            lockRequestVO.setLock_model(CommonConstantPool.SUB);
        }
        Result<ProductsInventoryLockResponseVO> result = partyProductInventoryAdjust(lockRequestVO, true);
        if (result.isSuccess()) {
            RedisCacheUtil.PartyProductInventoryLockCacheUtil.cleanByCacheKey(requestVO.getTransaction_code());
            return new Result(CodeEnum.SUCCESS);
        }
        return new Result(CodeEnum.FAIL_BUSINESS, result.getMsg());
    }

    @Data
    public static class InventoryUniqueItem {

        public ProductInventorySummary getProductInventorySummary() {
            if (this.is_cross_border) {
                return new ProductInventorySummary(this.product_id, 1L, this.product_type_id, this.lock_num);
            } else if (this.party_id == null || this.party_id <= 0L) {
                throw new IllegalArgumentException("party_id is null-value");
            } else {
                return new ProductInventorySummary(this.product_id, this.party_id, this.product_type_id, this.lock_num);
            }
        }

        public ProductInventoryAdjustmentItem getProductInventoryAdjustmentItem() {
            ProductInventoryAdjustmentItem item = new ProductInventoryAdjustmentItem();
            item.setProduct_type_id(this.product_type_id);
            item.setProduct_id(this.product_id);
            item.setParty_id(this.party_id);
            item.setAdjustment_quantity(this.lock_num);
            return item;
        }

        public InventoryUniqueItem(ProductInventoryLockItem item, String product_type_id) {
            this.product_id = item.getProduct_id();
            this.party_id = item.getParty_id();
            this.lock_num = item.getLock_quantity();
            this.lockItemList = new ArrayList<>();
            this.lockItemList.add(item);
            this.product_type_id = product_type_id;
            this.is_cross_border = ProductTypeCacheHandler.isCrossBorder(this.product_type_id);
            if (this.is_cross_border) {
                this.party_id = null;
            }
        }

        public InventoryUniqueItem(Long product_id, Long party_id, Integer lock_num, String product_type_id) {
            this.product_id = product_id;
            this.party_id = party_id;
            this.lock_num = lock_num;
            this.product_type_id = product_type_id;
            this.is_cross_border = ProductTypeCacheHandler.isCrossBorder(this.product_type_id);
            if (this.is_cross_border) {
                this.party_id = null;
            }
        }

        /**
         * 创建一个基于跨境商品的库存对象
         * @param product_id
         */
        public InventoryUniqueItem(Long product_id) {
            this.product_id = product_id;
            this.product_type_id = PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_BONDED.getPermission_id();
            this.is_cross_border = true;
        }

        public InventoryUniqueItem(InventoryLockMappingItem item) {
            this.product_id = item.getProduct_id();
            this.party_id = item.getParty_id();
            this.product_type_id = item.getProduct_type_id();
            this.is_cross_border = ProductTypeCacheHandler.isCrossBorder(this.product_type_id);
        }

        private Long party_id;
        private Long product_id;
        private String product_type_id;
        private boolean is_cross_border;
        private Integer lock_num;
        private List<ProductInventoryLockItem> lockItemList;

        public void merge(InventoryUniqueItem item) {
            if (item == null) {
                return;
            }
            if (item.hashCode() != this.hashCode() || !item.equals(this)) {
                return;
            }
            this.lockItemList.addAll(item.lockItemList);
            if (this.lock_num == null) {
                this.lock_num = 0;
            }
            if (item.lock_num != null) {
                this.lock_num += item.lock_num;
            }
        }

        @Override
        public int hashCode() {
            int party_id_hash = party_id == null ? -1 : party_id.hashCode();
            int prod_id_hash = product_id == null ? -1 : product_id.hashCode();
            return this.is_cross_border ? prod_id_hash : party_id_hash * prod_id_hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            InventoryUniqueItem item = (InventoryUniqueItem) obj;

            if (this.is_cross_border != item.is_cross_border) {
                return false;
            }

            boolean party_id_eq = false;
            if (this.party_id != null && this.party_id.equals(item.getParty_id())) {
                party_id_eq = true;
            }
            boolean prod_id_eq = false;
            if (this.product_id != null && this.product_id.equals(item.product_id)) {
                prod_id_eq = true;
            }
            return this.is_cross_border ? prod_id_eq : party_id_eq && prod_id_eq;
        }
    }
}
