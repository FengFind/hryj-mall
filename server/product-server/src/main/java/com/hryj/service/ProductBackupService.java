package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.hryj.cache.*;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.constant.DataDictionaryGroup;
import com.hryj.entity.bo.product.ProductAuditRecord;
import com.hryj.entity.bo.product.ProductBackup;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.product.audit.mapping.ProductAuditPageMappingItem;
import com.hryj.entity.vo.product.audit.request.ProductBackupRequestVO;
import com.hryj.entity.vo.product.audit.request.ProductHandledResultSubmitRequestVO;
import com.hryj.entity.vo.product.audit.request.SearchProductAuditRequestVO;
import com.hryj.entity.vo.product.audit.response.ProductAuditPageItemResponseVO;
import com.hryj.entity.vo.product.audit.response.ProductAuditResponseVO;
import com.hryj.entity.vo.product.audit.response.ProductBackupResponseVO;
import com.hryj.entity.vo.product.request.ProductAttributeItemRequestVO;
import com.hryj.entity.vo.product.request.ProductAttributeRequestVO;
import com.hryj.entity.vo.product.request.ProductIdRequestVO;
import com.hryj.entity.vo.product.response.ProdAttrItemResponseVO;
import com.hryj.entity.vo.product.response.ProdAttrsResponseVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.mapper.CrossBorderProductMapper;
import com.hryj.mapper.ProductAuditRecordMapper;
import com.hryj.mapper.ProductBackupMapper;
import com.hryj.mapper.ProductMapper;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.service.inventory.cache.InventoryCacheUtil;
import com.hryj.service.inventory.cache.ProductInventorySummary;
import com.hryj.service.prodcate.ProductCategoryUtilService;
import com.hryj.service.util.CommonUtil;
import com.hryj.service.util.ProductTypeConditionUtil;
import com.hryj.service.util.ProductUtil;
import com.hryj.service.worktask.ProductAuditPassedTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 王光银
 * @className: ProductBackupService
 * @description:
 * @create 2018/7/2 0002 22:10
 **/
@Slf4j
@Service
public class ProductBackupService extends ServiceImpl<ProductBackupMapper, ProductBackup> {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductBackupMapper productBackupMapper;

    @Autowired
    private ProductAuditRecordMapper productAuditRecordMapper;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCategoryUtilService productCategoryUtilService;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private CrossBorderProductMapper crossBorderProductMapper;

    private final static String back_up_cross_border_product_key = "cross_border_product";

    /**
     * @author 王光银
     * @methodName: searchProductAuditByPage
     * @methodDesc: 分页查询商品审核数据
     * @description:
     * @param: [productAuditSearchRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.audit.response.ProductAuditPageItemResponseVO>>
     * @create 2018-07-06 9:44
     **/
    public Result<PageResponseVO<ProductAuditPageItemResponseVO>> searchProductAuditByPage(
            @RequestBody SearchProductAuditRequestVO search_condition) throws ServerException {

        Map<String, Object> params_map = new LinkedHashMap<>();

        List<String> product_type_permission = search_condition.getPermission_list((Object value) -> (ProductTypeCacheHandler.getProductTypePermission(CommonUtil.getStaffIdFromCache(value.toString()))));
        if (UtilValidate.isEmpty(product_type_permission)) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        ProductTypeConditionUtil.setProductTypeCondition(search_condition, params_map, product_type_permission);

        //参数处理
        if (UtilValidate.isEmpty(search_condition.getAudit_status())) {
            search_condition.setAudit_status(ProductUtil.HANDLE_STATUS_NOT_HANDLED);
        }

        params_map.put("audit_status", search_condition.getAudit_status());

        if (UtilValidate.isNotEmpty(search_condition.getProduct_id())) {
            params_map.put("product_id", search_condition.getProduct_id());
        }
        if (UtilValidate.isNotEmpty(search_condition.getProduct_name())) {
            params_map.put("prod_name", search_condition.getProduct_name().trim());
        }
        if (UtilValidate.isNotEmpty(search_condition.getBrand_name())) {
            params_map.put("brand_name", search_condition.getBrand_name().trim());
        }
        if (search_condition.getBrand_id() != null && search_condition.getBrand_id() > 0L) {
            params_map.put("brand_id", search_condition.getBrand_id());
        }
        if (UtilValidate.isNotEmpty(search_condition.getCategory_name())) {
            params_map.put("cate_name", search_condition.getCategory_name().trim());
        }
        if (search_condition.getCategory_id() != null && search_condition.getCategory_id() > 0L) {
            //当根据商品分类查询商品时，必须要保证商品分类为最末级分类
            Set<Long> cate_set = productCategoryUtilService.getLastProdCate(search_condition.getCategory_id(), true);
            if (UtilValidate.isNotEmpty(cate_set)) {
                if (cate_set.size() > 1) {
                    params_map.put("cate_id_set", cate_set);
                } else {
                    params_map.put("cate_id", cate_set.iterator().next());
                }
            }
        }
        if (UtilValidate.isNotEmpty(search_condition.getSubmit_by())) {
            params_map.put("submit_by", search_condition.getSubmit_by().trim());
        }
        if (UtilValidate.isNotEmpty(search_condition.getHandled_by())) {
            params_map.put("handle_by", search_condition.getHandled_by().trim());
        }
        if (ProductUtil.HANDLE_STATUS_HANDLED.equals(search_condition.getHandled_result()) || ProductUtil.HANDLE_STATUS_NOT_HANDLED.equals(search_condition.getHandled_result())) {
            params_map.put("handle_result", search_condition.getHandled_result());
        }

        if (UtilValidate.isNotEmpty(search_condition.getSubmit_time_begin())) {
            try {
                params_map.put("submit_time_begin", DateUtil.parseDateTime(search_condition.getSubmit_time_begin()));
            } catch (Exception e) {}
        }

        if (UtilValidate.isNotEmpty(search_condition.getSubmit_time_end())) {
            try {
                params_map.put("submit_time_end", DateUtil.parseDateTime(search_condition.getSubmit_time_end()));
            } catch (Exception e) {}
        }

        if (UtilValidate.isNotEmpty(search_condition.getHandled_time_begin())) {
            try {
                params_map.put("handled_time_begin", DateUtil.parseDateTime(search_condition.getHandled_time_begin()));
            } catch (Exception e) {}
        }

        if (UtilValidate.isNotEmpty(search_condition.getHandled_time_end())) {
            try {
                params_map.put("handled_time_end", DateUtil.parseDateTime(search_condition.getHandled_time_end()));
            } catch (Exception e) {}
        }

        try {
            Page<ProductAuditPageMappingItem> pageCond = new Page(search_condition.getPage_num(), search_condition.getPage_size());
            List<ProductAuditPageMappingItem> list_result = productBackupMapper.selectProdAuditPage(params_map, pageCond);
            if (UtilValidate.isEmpty(list_result)) {
                return new Result(CodeEnum.SUCCESS, new PageResponseVO(0L, 0L, new ArrayList(0)));
            } else {
                List<ProductAuditPageItemResponseVO> list = new ArrayList<>(list_result.size());
                for (ProductAuditPageMappingItem item : list_result) {
                    list.add(item.convertTo(ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_TYPE_NAME_GETTER));
                }
                return new Result(CodeEnum.SUCCESS, new PageResponseVO(pageCond.getTotal(), pageCond.getPages(), list));
            }
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("查询商品审核列表数据失败", e);
            }
            throw new ServerException("查询商品审核列表数据失败", e);
        }
    }

    public Result<ProductBackupResponseVO> getOneProductBackupData(
            @RequestBody ProductBackupRequestVO productBackupRequestVO) throws ServerException {
        if (productBackupRequestVO == null
                || productBackupRequestVO.getProduct_backup_id() == null
                || productBackupRequestVO.getProduct_backup_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品审核备份数据ID不能是空值");
        }

        try {
            ProductBackup backup = super.selectById(productBackupRequestVO.getProduct_backup_id());
            if (backup == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "当前审核信息已变更，请重新刷新查看, ID:" + productBackupRequestVO.getProduct_backup_id());
            }

            backup.getProduct_id();
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id", backup.getProduct_id());
            wrapper.setSqlSelect("product_type_id");
            ProductInfo productInfo = productService.selectOne(wrapper);
            if (productInfo == null) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "商品不存在,id=" + backup.getProduct_id());
            }

            StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(productBackupRequestVO.getLogin_token());
            if (staffAdminLoginVO == null) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "登陆过期");
            }

            if (!ProductTypeCacheHandler.hasProductTypePermission(staffAdminLoginVO.getStaff_id(), productInfo.getProduct_type_id())) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您没有权限处理:" + ProductTypeCacheHandler.getProductTypeDescription(productInfo.getProduct_type_id()));
            }

            ProductBackupResponseVO vo_backup = backup.convertTo(ProductUtil.PROD_TYPE_NAME_GETTER, ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER);

            if (productBackupRequestVO.getInclude_audit_record()) {
                wrapper = new EntityWrapper();
                wrapper.eq("product_id", backup.getProduct_id());
                wrapper.orderBy("handle_time", false);
                List<ProductAuditRecord> audit_record_list = productAuditRecordMapper.selectList(wrapper);
                if (UtilValidate.isNotEmpty(audit_record_list)) {
                    List<ProductAuditResponseVO> vo_audit_record_list = new ArrayList<>(audit_record_list.size());
                    vo_backup.setAudit_record_list(vo_audit_record_list);
                    for (ProductAuditRecord record : audit_record_list) {
                        vo_audit_record_list.add(record.convertTo());
                    }
                }
            }

            //加载商品属性, 新增的从商品属性表加载，修改的从JSON数据中获取
            if (UtilValidate.isEmpty(backup.getProd_data_after())) {
                ProductIdRequestVO productIdRequestVO = new ProductIdRequestVO();
                productIdRequestVO.setProduct_id(vo_backup.getProduct_id());
                Result<ProdAttrsResponseVO> attr_data_result = productService.getProductAttributeData(productIdRequestVO);
                if (attr_data_result.isSuccess()) {
                    if (attr_data_result.getData() != null) {
                        if (UtilValidate.isNotEmpty(attr_data_result.getData().getAttr_list())) {
                            vo_backup.setAttr_list(attr_data_result.getData().getAttr_list());
                        }
                        if (UtilValidate.isNotEmpty(attr_data_result.getData().getCategory_attr_list())) {
                            vo_backup.setCategory_attr_list(attr_data_result.getData().getCategory_attr_list());
                        }
                    }
                } else {
                    log.error("加载商品审核备份数据 - 加载商品属性数据失败:" + attr_data_result.getMsg());
                }
            } else {
                try {
                    JSONObject after_prod = JSON.parseObject(backup.getProd_data_after());
                    String arr_key = "attr_list";
                    if (after_prod.containsKey(arr_key)) {
                        JSONArray attr_array = after_prod.getJSONArray("attr_list");
                        List<ProdAttrItemResponseVO> attr_list;
                        List<ProdAttrItemResponseVO> cate_attr_list;
                        if (UtilValidate.isNotEmpty(attr_array)) {
                            attr_list = new ArrayList<>(attr_array.size());
                            cate_attr_list = new ArrayList<>(attr_array.size());
                            vo_backup.setAttr_list(attr_list);
                            vo_backup.setCategory_attr_list(cate_attr_list);
                            for (Object obj : attr_array) {
                                JSONObject thisObj = (JSONObject) obj;
                                Long product_attribute_id = thisObj.getLong("product_attribute_id");
                                String attr_type = thisObj.getString("attr_type");
                                String attr_name = thisObj.getString("attr_name");
                                String attr_value = thisObj.getString("attr_value");
                                Long prod_cate_attr_id = thisObj.getLong("prod_cate_attr_id");
                                Long prod_cate_attr_item_id = thisObj.getLong("prod_cate_attr_item_id");
                                ProdAttrItemResponseVO thisVo = new ProdAttrItemResponseVO();
                                thisVo.setProd_attr_id(IdWorker.getId());
                                thisVo.setAttr_type(attr_type);
                                thisVo.setAttr_value(attr_value);
                                thisVo.setProd_cate_attr_id(prod_cate_attr_id);
                                thisVo.setProd_cate_attr_item_id(prod_cate_attr_item_id);
                                thisVo.setAttr_name(attr_name);
                                if (ProductUtil.PROD_ATTR_TYPE_CATE.equals(thisVo.getAttr_type())) {
                                    cate_attr_list.add(thisVo);
                                } else {
                                    attr_list.add(thisVo);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("产品备份数据反序列化失败", e);
                }
            }

            return new Result<>(CodeEnum.SUCCESS, vo_backup);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("加载商品审核数据失败", e);
            }
            throw new ServerException("加载商品审核数据失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result submitProductAuditResult(
            @RequestBody ProductHandledResultSubmitRequestVO productHandledResultSubmitRequestVO) throws ServerException {
        if (productHandledResultSubmitRequestVO == null
                || productHandledResultSubmitRequestVO.getProduct_backup_id() == null
                || productHandledResultSubmitRequestVO.getProduct_backup_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "审核备份数据ID不能是空值");
        }
        if (productHandledResultSubmitRequestVO.getHandle_result() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "审核处理结果不能是空值");
        }
        if (!productHandledResultSubmitRequestVO.getHandle_result().equals(ProductUtil.HANDLE_STATUS_HANDLED)
                && !productHandledResultSubmitRequestVO.getHandle_result().equals(ProductUtil.HANDLE_STATUS_NOT_HANDLED)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不能识别的审核处理结果: handle_result=" + productHandledResultSubmitRequestVO.getHandle_result());
        }
        if (productHandledResultSubmitRequestVO.getHandle_result().equals(ProductUtil.HANDLE_STATUS_NOT_HANDLED)
                && UtilValidate.isEmpty(productHandledResultSubmitRequestVO.getAudit_remark())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "处理为驳回时,驳回原因是必须的");
        }

        StaffAdminLoginVO admin_login = LoginCache.getStaffAdminLoginVO(productHandledResultSubmitRequestVO.getLogin_token());
        if (admin_login == null) {
            log.error("从缓存中获取用户信息失败,发生在产品审核结果提交处理接口:(submitProductAuditResult),token=" + productHandledResultSubmitRequestVO.getLogin_token());
            return new Result(CodeEnum.FAIL_BUSINESS, "获取当前操作用户信息失败: token=" + productHandledResultSubmitRequestVO.getLogin_token());
        }

        /**
         * 并发控制： 审核处理操作与导致审核处理的操作的并发处理
         */
        ProductBackup backup = null;
        ProductInventorySummary pis = null;
        try {
            backup = super.selectById(productHandledResultSubmitRequestVO.getProduct_backup_id());
            if (backup == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "当前审核信息已变更，请重新刷新查看");
            }

            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id", backup.getProduct_id());
            wrapper.setSqlSelect("product_type_id");
            ProductInfo check_info = productService.selectOne(wrapper);
            if (check_info == null) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "商品不存在,id=" + backup.getProduct_id());
            }

            if (!ProductTypeCacheHandler.hasProductTypePermission(admin_login.getStaff_id(), check_info.getProduct_type_id())) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您没有权限处理:" + ProductTypeCacheHandler.getProductTypeDescription(check_info.getProduct_type_id()));
            }

            boolean lockResult = redisLock.lock(CacheGroup.PRODUCT_AUDIT_LOCK.getValue(), backup.getProduct_id().toString(), 3);
            while (!lockResult) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
                lockResult = redisLock.lock(CacheGroup.PRODUCT_AUDIT_LOCK.getValue(), backup.getProduct_id().toString(), 3);
            }

            //双重检测，确保并发安全
            backup = super.selectById(productHandledResultSubmitRequestVO.getProduct_backup_id());
            if (backup == null) {
                redisLock.unLock(CacheGroup.PRODUCT_AUDIT_LOCK.getValue(), backup.getProduct_id().toString());
                return new Result(CodeEnum.FAIL_BUSINESS, "当前审核信息已变更，请重新刷新查看");
            }

            if (ProductUtil.HANDLE_STATUS_HANDLED.equals(backup.getHandle_status())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "当前审核信息已经处理,不能重复处理, ID:" + productHandledResultSubmitRequestVO.getProduct_backup_id());
            }

            //生成审核记录
            ProductAuditRecord record = new ProductAuditRecord();
            record.setProduct_id(backup.getProduct_id());
            record.setAudit_remark(productHandledResultSubmitRequestVO.getAudit_remark());
            record.setHandle_result(productHandledResultSubmitRequestVO.getHandle_result());
            record.setProd_backup_data_id(productHandledResultSubmitRequestVO.getProduct_backup_id());
            record.setSubmit_staff_id(backup.getUpdated_by());
            record.setSubmit_staff_name(backup.getUpdated_user());
            record.setSubmit_time(backup.getUpdate_time());
            record.setSubmit_type(UtilValidate.isEmpty(backup.getProd_data_after()) ? ProductUtil.SUBMIT_TYPE_ADD : ProductUtil.SUBMIT_TYPE_MODIFY);
            record.setHandle_staff_id(admin_login.getStaff_id());
            record.setHandle_staff_name(admin_login.getStaff_name());
            record.setHandle_time(new Date());
            record.insert();

            //修改审核数据的状态
            backup.setHandle_status(ProductUtil.HANDLE_STATUS_HANDLED);
            backup.setData_status(ProductUtil.HANDLE_STATUS_HANDLED.equals(productHandledResultSubmitRequestVO.getHandle_result()) ? ProductUtil.HANDLE_STATUS_HANDLED : ProductUtil.HANDLE_STATUS_NOT_HANDLED);
            backup.updateById();



            if (ProductUtil.HANDLE_STATUS_HANDLED.equals(productHandledResultSubmitRequestVO.getHandle_result())) {
                //将备份数据覆盖到生产数据
                if (UtilValidate.isEmpty(backup.getProd_data_after())) {
                    //新增覆盖只需要修改状态
                    ProductInfo productInfo = new ProductInfo();
                    productInfo.setAudit_status(ProductUtil.HANDLE_STATUS_HANDLED);
                    productInfo.setUp_down_status(ProductUtil.HANDLE_STATUS_HANDLED);
                    productInfo.setId(backup.getProduct_id());
                    productInfo.updateById();
                } else {
                    ProductInfo backup_prod;
                    CrossBorderProduct backup_cross_border_prod = null;
                    List<ProductAttributeItemRequestVO> attr_list = null;
                    String arr_key = "attr_list";
                    try {
                        JSONObject after_prod = JSON.parseObject(backup.getProd_data_after());
                        backup_prod = JSON.toJavaObject(after_prod, ProductInfo.class);

                        if (after_prod.containsKey(back_up_cross_border_product_key)) {
                            backup_cross_border_prod = JSON.toJavaObject(after_prod.getJSONObject(back_up_cross_border_product_key), CrossBorderProduct.class);
                        }

                        if (after_prod.containsKey(arr_key)) {
                            JSONArray attr_array = after_prod.getJSONArray("attr_list");
                            if (UtilValidate.isNotEmpty(attr_array)) {
                                attr_list = new ArrayList<>(attr_array.size());
                                for (Object obj : attr_array) {
                                    JSONObject thisObj = (JSONObject) obj;
                                    attr_list.add(thisObj.toJavaObject(ProductAttributeItemRequestVO.class));
                                }
                            }
                        }

                        String detail_image_list_key = "detail_image_list";
                        int max_detail_num = 5;
                        if (after_prod.containsKey(detail_image_list_key)) {
                            JSONArray detail_image_list = after_prod.getJSONArray("detail_image_list");
                            if (UtilValidate.isNotEmpty(detail_image_list)) {
                                for (int i = 1; i <= max_detail_num; i++) {
                                    if (detail_image_list.size() >= i) {
                                        backup_prod.setDetailImage(i, detail_image_list.getString(i - 1));
                                    } else {
                                        backup_prod.setDetailImage(i, null);
                                    }
                                }
                            } else {
                                for (int i = 1; i <= max_detail_num; i++) {
                                    backup_prod.setDetailImage(i, null);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error("产品备份数据反序列化失败", e);
                        throw e;
                    }

                    //从数据库中心加载商品
                    ProductInfo db_info = productService.selectById(backup.getProduct_id());
                    if (db_info == null) {
                        throw new BizException("商品中心已不存在ID为:[" + backup.getProduct_id() + "]的商品");
                    }

                    //覆盖商品主表数据
                    backupProdToWorks(db_info, backup_prod);
                    db_info.setAudit_status(ProductUtil.HANDLE_STATUS_HANDLED);
                    db_info.setUp_down_status(ProductUtil.HANDLE_STATUS_HANDLED);
                    db_info.updateAllColumnById();

                    //覆盖跨境商品表数据
                    if (backup_cross_border_prod != null) {
                        CrossBorderProduct db_cross_border_prod = crossBorderProductMapper.selectById(db_info.getId());
                        if (db_cross_border_prod == null) {
                            throw new NullPointerException("数据库跨境商品数据丢失");
                        }
                        backup_cross_border_prod.setId(db_cross_border_prod.getId());
                        BeanUtils.copyProperties(backup_cross_border_prod, db_cross_border_prod);
                        db_cross_border_prod.updateAllColumnById();
                        pis = new ProductInventorySummary(db_info.getId(), 1L, PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_BONDED.getPermission_id(), db_cross_border_prod.getInventory_quantity());
                    }

                    ProductAttributeRequestVO productAttributeRequestVO = new ProductAttributeRequestVO();
                    productAttributeRequestVO.setProduct_id(db_info.getId());
                    productAttributeRequestVO.setAttr_list(attr_list);
                    Result result = productService.configManyProductAttribute(productAttributeRequestVO);
                    if (result.isFailed()) {
                        throw new BizException(result.getMsg());
                    }
                }
            } else {
                ProductInfo productInfo = new ProductInfo();
                productInfo.setId(backup.getProduct_id());
                productInfo.setAudit_status(ProductUtil.HANDLE_STATUS_HANDLED);
                productInfo.updateById();
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            StringBuilder error = new StringBuilder("处理审核提交失败");
            if (UtilValidate.isNotEmpty(e.getMessage())) {
                error.append(":").append(e.getMessage()) ;
            }
            if (log.isErrorEnabled()) {
                log.error(error.toString(), e);
            }
            throw new ServerException(error.toString(), e);
        } finally {
            if (backup != null) {
                redisLock.unLock(CacheGroup.PRODUCT_AUDIT_LOCK.getValue(), backup.getProduct_id().toString());
                //审核数据为修改，并且审核通过时，查找出引用了这些商品的所有门店，并清除这些门店相关的缓存数据
                ThreadPoolUtil.submitTask(new ProductAuditPassedTask(backup));
                //商品类型为跨境商品时，要刷新商品库存缓存
                if (pis != null) {
                    InventoryCacheUtil.addCache(pis);
                }
            }
        }
    }


    /**
     * @author 王光银
     * @methodName: productBackupProcess
     * @methodDesc:
     * @description:
     * @param: [before, after, data_type, staffAdminLoginVO]
     * @return void
     * @create 2018-07-03 9:00
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public void productBackupProcess(ProductBackupHandler before,
                                     ProductBackupHandler after,
                                     String data_type,
                                     Long submit_by,
                                     String submit_user) {
        if (before == null) {
            return;
        }
        if (before.product.getId() == null || before.product.getId() <= 0L) {
            throw new BizException("商品审核备份-修改前(新增)的商品ID不能是空值");
        }
        if (after != null) {
            if (after.product.getId() == null || after.product.getId() <= 0L) {
                throw new BizException("商品审核备份-修改后的商品ID不能是空值");
            }
            if (!before.product.getId().equals(after.product.getId())) {
                throw new BizException("修改前的商品ID与修改后的商品ID不一致");
            }
        }
        if (UtilValidate.isEmpty(data_type)) {
            data_type = ProductUtil.DATA_TYPE_CENTER;
        }

        /**
         * 商品审核备份逻辑：
         * 1、after对象为空时，处理为新增的备份，
         * 2、before after都有值时，处理为修改备份
         * 3、每个商品的未处理备份只能有一个
         */

        String before_json_str = JSON.toJSONString(generateProductBackupDataMap(before));
        String after_json_str = (after == null ? null : JSON.toJSONString(generateProductBackupDataMap(after)));

        //生成备份记录数据
        ProductBackup backup = new ProductBackup();
        backup.setProduct_id(before.product.getId());
        backup.setData_status(ProductUtil.DATA_STATUS_NOT_COVERED);
        backup.setData_type(data_type);
        backup.setHandle_status(ProductUtil.HANDLE_STATUS_NOT_HANDLED);
        backup.setProd_data_before(before_json_str);
        backup.setProd_data_after(after_json_str);
        backup.setUpdate_time(new Date());
        backup.setUpdated_user(submit_user);
        backup.setUpdated_by(submit_by);

        /**
         * 处理并发，确保审核处理操作与导致需要审核的操作的并发问题
         */
        try {
            boolean lockResult = redisLock.lock(CacheGroup.PRODUCT_AUDIT_LOCK.getValue(), before.product.getId().toString(), 3);
            while (!lockResult) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
                lockResult = redisLock.lock(CacheGroup.PRODUCT_AUDIT_LOCK.getValue(), before.product.getId().toString(), 3);
            }
            //插入之前先删除当前商品的未处理的备份数据
            super.deleteByMap(UtilMisc.toMap("product_id", before.product.getId(), "handle_status", ProductUtil.HANDLE_STATUS_NOT_HANDLED));
            super.insert(backup);

            //备份完成后将商品修改为为未审核状态
            ProductInfo productInfo = new ProductInfo();
            productInfo.setId(before.product.getId());
            productInfo.setAudit_status(ProductUtil.PROD_AUDIT_STATUS_UNAUDITED);
            productMapper.updateById(productInfo);
        } catch (Exception e) {
            throw e;
        } finally {
            redisLock.unLock(CacheGroup.PRODUCT_AUDIT_LOCK.getValue(), before.product.getId().toString());
        }
    }

    public TreeMap<String, Object> generateProductBackupDataMap(ProductBackupHandler prodHandler) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TreeMap<String, Object> map = new TreeMap<>((String o1, String o2) -> (o1.compareTo(o2)));
        map.put("product_type_id", prodHandler.product.getProduct_type_id());
        map.put("product_name", UtilValidate.isEmpty(prodHandler.product.getProduct_name()) ? "" : prodHandler.product.getProduct_name());
        map.put("prod_cate_id", prodHandler.product.getProd_cate_id() == null ? "" : prodHandler.product.getProd_cate_id().toString());
        map.put("prod_cate_path", UtilValidate.isEmpty(prodHandler.product.getProd_cate_path()) ? "" : prodHandler.product.getProd_cate_path());
        map.put("brand", prodHandler.product.getBrand() == null || prodHandler.product.getBrand() <= 0L ? "" : prodHandler.product.getBrand());
        map.put("made_where", prodHandler.product.getMade_where() == null || prodHandler.product.getMade_where() <= 0L ? "" : prodHandler.product.getMade_where());
        map.put("shelf_life", UtilValidate.isEmpty(prodHandler.product.getShelf_life()) ? "" : prodHandler.product.getShelf_life());
        map.put("specification", UtilValidate.isEmpty(prodHandler.product.getSpecification()) ? "" : prodHandler.product.getSpecification());
        map.put("integral", prodHandler.product.getIntegral() == null ? "" : prodHandler.product.getIntegral().toString());
        map.put("product_info", UtilValidate.isEmpty(prodHandler.product.getProduct_info()) ? "" : prodHandler.product.getProduct_info().trim());
        map.put("product_detail", UtilValidate.isEmpty(prodHandler.product.getProduct_detail()) ? "" : prodHandler.product.getProduct_detail().trim());
        map.put("list_image_url", UtilValidate.isEmpty(prodHandler.product.getList_image_url()) ? "" : prodHandler.product.getList_image_url().trim());
        map.put("cost_price", prodHandler.product.getCost_price() == null ? "" : prodHandler.product.getCost_price().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        map.put("erp_code", UtilValidate.isEmpty(prodHandler.product.getErp_code()) ? "" : prodHandler.product.getErp_code().trim());
        map.put("introduction_date", prodHandler.product.getIntroduction_date() == null ? "" : sdf.format(prodHandler.product.getIntroduction_date()));
        map.put("sales_end_date", prodHandler.product.getSales_end_date() == null ? "" : sdf.format(prodHandler.product.getSales_end_date()));
        List<String> detail_image_list = new ArrayList<>(5);
        if (UtilValidate.isNotEmpty(prodHandler.product.getDetail_image_url_one())) {
            detail_image_list.add(prodHandler.product.getDetail_image_url_one());
        }
        if (UtilValidate.isNotEmpty(prodHandler.product.getDetail_image_url_two())) {
            detail_image_list.add(prodHandler.product.getDetail_image_url_two());
        }
        if (UtilValidate.isNotEmpty(prodHandler.product.getDetail_image_url_three())) {
            detail_image_list.add(prodHandler.product.getDetail_image_url_three());
        }
        if (UtilValidate.isNotEmpty(prodHandler.product.getDetail_image_url_four())) {
            detail_image_list.add(prodHandler.product.getDetail_image_url_four());
        }
        if (UtilValidate.isNotEmpty(prodHandler.product.getDetail_image_url_five())) {
            detail_image_list.add(prodHandler.product.getDetail_image_url_five());
        }
        map.put("detail_image_list", detail_image_list);
        if (UtilValidate.isNotEmpty(prodHandler.attr_list)) {
            map.put("attr_list", prodHandler.attr_list);
        }
        if (prodHandler.crossBorderProduct != null) {
            TreeMap<String, Object> cross_border_prod = new TreeMap<>((String o1, String o2) -> (o1.compareTo(o2)));
            cross_border_prod.put("hs_code", UtilValidate.isEmpty(prodHandler.crossBorderProduct.getHs_code()) ? "" : prodHandler.crossBorderProduct.getHs_code());
            cross_border_prod.put("channel", UtilValidate.isEmpty(prodHandler.crossBorderProduct.getChannel()) ? "" : prodHandler.crossBorderProduct.getChannel());
            cross_border_prod.put("channel_name", CodeCache.getNameByKey(DataDictionaryGroup.CrossBorderProductDeliveryWarehouse, CommonConstantPool.S_ZERO_ONE));
            cross_border_prod.put("unit_1", UtilValidate.isEmpty(prodHandler.crossBorderProduct.getUnit_1()) ? "" : prodHandler.crossBorderProduct.getUnit_1());
            cross_border_prod.put("unit_2", UtilValidate.isEmpty(prodHandler.crossBorderProduct.getUnit_2()) ? "" : prodHandler.crossBorderProduct.getUnit_2());
            cross_border_prod.put("unit_2_desc", CodeCache.getNameByValue(DataDictionaryGroup.NetContentGroup, prodHandler.crossBorderProduct.getUnit_2()));
            cross_border_prod.put("third_sku_id", UtilValidate.isEmpty(prodHandler.crossBorderProduct.getThird_sku_id()) ? "" : prodHandler.crossBorderProduct.getThird_sku_id());
            cross_border_prod.put("declare_price", prodHandler.crossBorderProduct.getDeclare_price() == null ? "0" : prodHandler.crossBorderProduct.getDeclare_price());
            cross_border_prod.put("inventory_quantity", prodHandler.crossBorderProduct.getInventory_quantity() == null ? "0" : prodHandler.crossBorderProduct.getInventory_quantity());
            map.put(back_up_cross_border_product_key, cross_border_prod);
        }
        return map;
    }

    private void backupProdToWorks(ProductInfo db_info, ProductInfo back_info) {
        db_info.setProduct_name(back_info.getProduct_name());
        db_info.setProd_cate_id(back_info.getProd_cate_id());
        db_info.setProd_cate_path(back_info.getProd_cate_path());
        db_info.setBrand(back_info.getBrand());
        db_info.setMade_where(back_info.getMade_where());
        db_info.setShelf_life(back_info.getShelf_life());
        db_info.setSpecification(back_info.getSpecification());
        db_info.setIntegral(back_info.getIntegral());
        db_info.setProduct_info(back_info.getProduct_info());
        db_info.setProduct_detail(back_info.getProduct_detail());
        db_info.setList_image_url(back_info.getList_image_url());
        db_info.setCost_price(back_info.getCost_price());
        db_info.setErp_code(back_info.getErp_code());
        db_info.setIntroduction_date(back_info.getIntroduction_date());
        db_info.setSales_end_date(back_info.getSales_end_date());
        db_info.setDetail_image_url_one(back_info.getDetail_image_url_one());
        db_info.setDetail_image_url_two(back_info.getDetail_image_url_two());
        db_info.setDetail_image_url_three(back_info.getDetail_image_url_three());
        db_info.setDetail_image_url_four(back_info.getDetail_image_url_four());
        db_info.setDetail_image_url_five(back_info.getDetail_image_url_five());
    }

    @Data
    public static class ProductBackupHandler {

        private ProductInfo product;

        private CrossBorderProduct crossBorderProduct;

        private List<ProductAttributeItemRequestVO> attr_list;
    }
}
