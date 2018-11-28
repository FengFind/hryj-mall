package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.hryj.cache.CrossBorderProductTaxRateCacheHandler;
import com.hryj.cache.LoginCache;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.bo.product.*;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.product.audit.response.ProductAuditResponseVO;
import com.hryj.entity.vo.product.category.request.ProductCategoryAttrFindRequestVO;
import com.hryj.entity.vo.product.category.response.ProdCateAttrEnumItemResponseVO;
import com.hryj.entity.vo.product.category.response.ProdCateAttrResponseVO;
import com.hryj.entity.vo.product.crossborder.request.CrossBorderProductRequestVO;
import com.hryj.entity.vo.product.request.*;
import com.hryj.entity.vo.product.response.ProdAttrItemResponseVO;
import com.hryj.entity.vo.product.response.ProdAttrsResponseVO;
import com.hryj.entity.vo.product.response.ProdListItemResponseVO;
import com.hryj.entity.vo.product.response.ProductDetailResponseVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.mapper.*;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.service.prodcate.ProductCategoryService;
import com.hryj.service.prodcate.ProductCategoryUtilService;
import com.hryj.service.util.CommonUtil;
import com.hryj.service.util.ProductTypeConditionUtil;
import com.hryj.service.util.ProductUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 王光银
 * @className: ProductService
 * @description:
 * @create 2018/7/2 0002 20:18
 **/
@Slf4j
@Service
public class ProductService extends ServiceImpl<ProductMapper, ProductInfo> {

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Autowired
    private ProductCategoryAttrMapper categoryAttrMapper;

    @Autowired
    private ProductCategoryAttrItemMapper attrItemMapper;

    @Autowired
    private ProductAuditRecordMapper productAuditRecordMapper;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Autowired
    private ProductBackupService backupService;

    @Autowired
    private ProductCategoryUtilService productCategoryUtilService;

    @Autowired
    private CrossBorderProductMapper crossBorderProductMapper;

    public CrossBorderProductMapper getCrossBorderProductMapper() {
        return this.crossBorderProductMapper;
    }

    /**
     * @author 王光银
     * @methodName: searchProductByPage
     * @methodDesc: 分页查询商品中心数据
     * @description:
     * @param:
     * @return
     * @create 2018-07-19 15:24
     **/
    public Result<PageResponseVO<ProdListItemResponseVO>> searchProductByPage(SearchProductRequestVO productSearchRequestVO) {
        List<String> product_type_permission = productSearchRequestVO.getPermission_list((Object value) -> (ProductTypeCacheHandler.getProductTypePermission(CommonUtil.getStaffIdFromCache(value.toString()))));
        if (UtilValidate.isEmpty(product_type_permission)) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        //组装查询条件
        Map<String, Object> params_map = new LinkedHashMap<>();
        ProductTypeConditionUtil.setProductTypeCondition(productSearchRequestVO, params_map, product_type_permission);

        if (UtilValidate.isNotEmpty(productSearchRequestVO.getProduct_name())) {
            params_map.put("product_name", productSearchRequestVO.getProduct_name());
        }
        if (productSearchRequestVO.getAudit_status() != null) {
            params_map.put("audit_status", productSearchRequestVO.getAudit_status());
        }
        if (productSearchRequestVO.getUp_down_status() != null) {
            params_map.put("up_down_status", productSearchRequestVO.getUp_down_status());
        }
        if (UtilValidate.isNotEmpty(productSearchRequestVO.getBrand_name())) {
            params_map.put("brand_name", productSearchRequestVO.getBrand_name());
        }
        if (productSearchRequestVO.getBrand_id() != null && productSearchRequestVO.getBrand_id() > 0L) {
            params_map.put("brand_id", productSearchRequestVO.getBrand_id());
        }

        if (productSearchRequestVO.getCategory_id() != null && productSearchRequestVO.getCategory_id() > 0) {
            //当根据商品分类查询商品时，必须要保证商品分类为最末级分类
            Set<Long> cate_set = productCategoryUtilService.getLastProdCate(productSearchRequestVO.getCategory_id(), true);
            if (UtilValidate.isNotEmpty(cate_set)) {
                if (cate_set.size() == 1) {
                    params_map.put("prod_cate_id", cate_set.iterator().next());
                } else {
                    params_map.put("prod_cate_id_list", cate_set);
                }
            }
        }
        try {
            Result<PageResponseVO<ProdListItemResponseVO>> result = new Result<>(CodeEnum.SUCCESS);
            PageResponseVO pageResponseVO = new PageResponseVO();
            result.setData(pageResponseVO);

            Page page_cond = new Page<>(productSearchRequestVO.getPage_num(), productSearchRequestVO.getPage_size());
            List<ProductInfo> list_result = super.baseMapper.pageFindProduct(params_map, page_cond);
            if (UtilValidate.isNotEmpty(list_result)) {
                List<ProdListItemResponseVO> page_list = new ArrayList<>(list_result.size());
                for (ProductInfo info : list_result) {
                    page_list.add(info.convertToListItem(ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER));
                }
                pageResponseVO.setRecords(page_list);
                pageResponseVO.setTotal_page(page_cond.getPages());
                pageResponseVO.setTotal_count(page_cond.getTotal());
            }
            return result;
        } catch (Exception e) {
            log.error("分页查询商品中心数据 - 加载商品数据失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "加载商品数据失败");
        }
    }

    /**
     * @author 王光银
     * @methodName: getProduct
     * @methodDesc: 加载商品详细信息数据
     * @description:
     * @param:
     * @return
     * @create 2018-07-19 15:25
     **/
    public Result<ProductDetailResponseVO> getProduct(ProductIdRequestVO productIdRequestVO) {
        if (productIdRequestVO == null || productIdRequestVO.getProduct_id() == null || productIdRequestVO.getProduct_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID是空值");
        }
        Result<ProductDetailResponseVO> result = new Result<>(CodeEnum.SUCCESS);
        try {
            ProductInfo productInfo = super.selectById(productIdRequestVO.getProduct_id());
            if (productInfo == null) {
                return result;
            }

            if (!ProductTypeCacheHandler.hasProductTypePermission(CommonUtil.getStaffIdFromCache(productIdRequestVO.getLogin_token()), productInfo.getProduct_type_id())) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您没有商品类型:[" + ProductTypeCacheHandler.getProductTypeDescription(productInfo.getProduct_type_id()) + "]的权限");
            }

            ProductDetailResponseVO vo = productInfo.convertToDetailResponse(ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER);
            result.setData(vo);


            //判断是否是跨境商品
            if (ProductTypeCacheHandler.isCrossBorder(productInfo.getProduct_type_id())) {
                CrossBorderProduct crossBorderProduct = crossBorderProductMapper.selectById(productIdRequestVO.getProduct_id());
                if (crossBorderProduct != null) {
                    vo.setCross_border_product(crossBorderProduct.convertTo(ProductUtil.CROSS_BORDER_PROD_CHANNEL_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER));
                }
            }

            //加载属性数据
            Result<ProdAttrsResponseVO> attr_data_result = getProductAttributeData(productIdRequestVO);
            if (attr_data_result.isFailed()) {
                return new Result(CodeEnum.FAIL_BUSINESS, attr_data_result.getMsg());
            }

            if (attr_data_result.getData() != null) {
                if (UtilValidate.isNotEmpty(attr_data_result.getData().getCategory_attr_list())) {
                    vo.setCategory_attr_list(attr_data_result.getData().getCategory_attr_list());
                }
                if (UtilValidate.isNotEmpty(attr_data_result.getData().getAttr_list())) {
                    vo.setAttr_list(attr_data_result.getData().getAttr_list());
                }
            }

            //加载审核记录数据
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.orderBy("handle_time", false);
            wrapper.eq("product_id", productIdRequestVO.getProduct_id());
            wrapper.orderBy("handle_time", false);
            List<ProductAuditRecord> audit_record_list = productAuditRecordMapper.selectList(wrapper);
            if (UtilValidate.isNotEmpty(audit_record_list)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                List<ProductAuditResponseVO> audit_vo_list = new ArrayList<>(audit_record_list.size());
                vo.setAudit_record_list(audit_vo_list);
                for (ProductAuditRecord record : audit_record_list) {
                    ProductAuditResponseVO audit_vo = new ProductAuditResponseVO();
                    audit_vo_list.add(audit_vo);
                    audit_vo.setId(record.getId());
                    audit_vo.setSubmit_staff(record.getSubmit_staff_name());
                    audit_vo.setSubmit_time(record.getSubmit_time() != null ? sdf.format(record.getSubmit_time()) : "");
                    audit_vo.setSubmit_type(record.getSubmit_type());
                    audit_vo.setHandle_result(record.getHandle_result() == null || record.getHandle_result().intValue() == 0 ? false : true);
                    audit_vo.setHandle_staff(record.getHandle_staff_name());
                    audit_vo.setHandle_time(record.getHandle_time() != null ? sdf.format(record.getHandle_time()) : "");
                    audit_vo.setAudit_remark(record.getAudit_remark());
                }
            }
            return result;
        } catch (Exception e) {
            log.error("加载商品详细信息数据失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "加载商品详细信息数据失败");
        }
    }

    /**
     * @author 王光银
     * @methodName:
     * @methodDesc: 查询商品属性数据
     * @description:
     * @param:
     * @return
     * @create 2018-07-19 15:35
     **/
    public Result<ProdAttrsResponseVO> getProductAttributeData(ProductIdRequestVO productIdRequestVO) {
        if (productIdRequestVO == null || productIdRequestVO.getProduct_id() == null || productIdRequestVO.getProduct_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID是空值");
        }
        Result<ProdAttrsResponseVO> result = new Result<>(CodeEnum.SUCCESS);
        ProdAttrsResponseVO vo = new ProdAttrsResponseVO();
        result.setData(vo);
        try {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id", productIdRequestVO.getProduct_id());
            wrapper.setSqlSelect("prod_cate_id");
            ProductInfo thisInfo = super.selectOne(wrapper);
            if (thisInfo == null) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "不存在ID为:[" + productIdRequestVO.getProduct_id() + "]的商品");
            }

            if (thisInfo.getProd_cate_id() == null || thisInfo.getProd_cate_id() <= 0L) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "商品的所属分类丢失, 商品ID为:" + productIdRequestVO.getProduct_id());
            }

            wrapper = new EntityWrapper();
            wrapper.eq("product_id", productIdRequestVO.getProduct_id());
            wrapper.orderBy("attr_type", true);
            wrapper.orderBy("id", true);

            //加载商品的所有属性
            List<ProductAttribute> product_attr_list = productAttributeMapper.selectList(wrapper);
            Set<Long> category_id_set = new HashSet<>();
            if (UtilValidate.isNotEmpty(product_attr_list)) {
                for (ProductAttribute attribute : product_attr_list) {
                    if (ProductUtil.ENUM_ATTR_TYPE.equals(attribute.getAttr_type())) {
                        category_id_set.add(attribute.getProd_cate_attr_id());
                    }
                }
            }

            //加载商品分类的所有属性(包含继承属性)
            ProductCategoryAttrFindRequestVO productCategoryAttrFindRequestVO = new ProductCategoryAttrFindRequestVO();
            productCategoryAttrFindRequestVO.setReturn_parent_attr(true);
            productCategoryAttrFindRequestVO.setProduct_category_id(thisInfo.getProd_cate_id());
            Result<ListResponseVO<ProdCateAttrResponseVO>> category_attr_result = productCategoryService.findProdCategoryAttr(productCategoryAttrFindRequestVO);
            if (category_attr_result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "加载当前时刻的商品分类属性失败:" + category_attr_result.getMsg());
            }

            List<ProdCateAttrResponseVO> list = null;
            if (category_attr_result.getData() != null && UtilValidate.isNotEmpty(category_attr_result.getData().getRecords())) {
                list = category_attr_result.getData().getRecords();
                Iterator<ProdCateAttrResponseVO> it = list.iterator();
                while (it.hasNext()) {
                    ProdCateAttrResponseVO voItem = it.next();
                    if (category_id_set.contains(voItem.getProd_cate_attr_id())) {
                        it.remove();
                        continue;
                    }
                    voItem.setProd_attr_id(IdWorker.getId());
                }
            }


            //商品属性集合，一部分是商品已经维护了的分类属性，一部分可能是分类新增的
            List<ProdAttrItemResponseVO> category_attr_list = new ArrayList<>();
            List<ProdAttrItemResponseVO> attr_list = new ArrayList<>();
            vo.setCategory_attr_list(category_attr_list);
            vo.setAttr_list(attr_list);

            //所有枚举类型的分类属性ID， 需要加载枚举条目
            Set<Long> need_to_load_item_set = new HashSet<>(20);

            if (UtilValidate.isNotEmpty(list)) {
                for (ProdCateAttrResponseVO voItem : list) {
                    ProdAttrItemResponseVO attribute_vo = new ProdAttrItemResponseVO();
                    category_attr_list.add(attribute_vo);

                    attribute_vo.setProd_attr_id(voItem.getProd_attr_id());
                    attribute_vo.setAttr_type(voItem.getAttr_type());
                    attribute_vo.setAttr_name(voItem.getAttr_name());
                    attribute_vo.setProd_cate_attr_id(voItem.getProd_cate_attr_id());

                    if (ProductUtil.ENUM_ATTR_TYPE.equals(voItem.getAttr_type())) {
                        need_to_load_item_set.add(voItem.getProd_cate_attr_id());
                    }
                }
            }


            if (UtilValidate.isNotEmpty(product_attr_list)) {
                for (ProductAttribute productAttribute : product_attr_list) {
                    ProdAttrItemResponseVO attribute_vo = new ProdAttrItemResponseVO();
                    if (ProductUtil.PROD_ATTR_TYPE_CATE.equals(productAttribute.getAttr_type())) {
                        category_attr_list.add(attribute_vo);
                        //验证分类属性是否为枚举属性
                        ProductCategoryAttr categoryAttr = categoryAttrMapper.selectById(productAttribute.getProd_cate_attr_id());
                        if (ProductUtil.ENUM_ATTR_TYPE.equals(categoryAttr.getAttr_type())) {
                            need_to_load_item_set.add(categoryAttr.getId());
                        }
                    } else {
                        attr_list.add(attribute_vo);
                    }

                    attribute_vo.setProd_attr_id(productAttribute.getId());
                    attribute_vo.setProduct_id(productAttribute.getProduct_id());
                    attribute_vo.setAttr_type(productAttribute.getAttr_type());
                    attribute_vo.setAttr_name(productAttribute.getAttr_name());
                    attribute_vo.setAttr_value(productAttribute.getAttr_value());
                    attribute_vo.setProd_cate_attr_id(productAttribute.getProd_cate_attr_id());
                    attribute_vo.setProd_cate_attr_item_id(productAttribute.getProd_cate_attr_item_id());

                }
            }

            if (UtilValidate.isNotEmpty(need_to_load_item_set)) {
                wrapper = new EntityWrapper();
                wrapper.in("prod_cate_attr_id", need_to_load_item_set);
                List<ProductCategoryAttrItem> attrItemList = attrItemMapper.selectList(wrapper);
                if (UtilValidate.isNotEmpty(attrItemList)) {
                    Map<Long, List<ProdCateAttrEnumItemResponseVO>> enum_item_map = new HashMap<>(attrItemList.size());
                    for (ProductCategoryAttrItem attrItem : attrItemList) {
                        ProdCateAttrEnumItemResponseVO attrItemVO = new ProdCateAttrEnumItemResponseVO();
                        attrItemVO.setAttr_value(attrItem.getAttr_value());
                        attrItemVO.setAttr_enum_item_id(attrItem.getId());
                        if (enum_item_map.containsKey(attrItem.getProd_cate_attr_id())) {
                            enum_item_map.get(attrItem.getProd_cate_attr_id()).add(attrItemVO);
                        } else {
                            enum_item_map.put(attrItem.getProd_cate_attr_id(), UtilMisc.toList(attrItemVO));
                        }
                    }

                    for (ProdAttrItemResponseVO itemResponseVO : category_attr_list) {
                        itemResponseVO.setAttr_item_list(enum_item_map.get(itemResponseVO.getProd_cate_attr_id()));
                    }
                }
            }

            return result;
        } catch (Exception e) {
            log.error("加载商品属性数据失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "加载商品属性数据失败");
        }
    }

    /**
     * @author 王光银
     * @methodName:
     * @methodDesc: 新增商品
     * @description:
     * @param:
     * @return
     * @create 2018-07-19 15:34
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result saveCreateProduct(ProductRequestVO product) throws BizException, ServerException {
        //验证商品数据
        Result check_res = checkProductData(product);
        if (check_res.isFailed()) {
            return check_res;
        }

        if (!ProductTypeCacheHandler.hasProductTypePermission(CommonUtil.getStaffIdFromCache(product.getLogin_token()), product.getProduct_type_id())) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "您没有商品类型:[" + ProductTypeCacheHandler.getProductTypeDescription(product.getProduct_type_id()) + "]的权限");
        }

        //获取当前操作用户信息
        StaffAdminLoginVO admin_login = LoginCache.getStaffAdminLoginVO(product.getLogin_token());
        if (admin_login == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取当前请求用户信息失败, token=" + product.getLogin_token());
        }

        ProductInfo productInfo;
        try {
            productInfo = product.convertTo();
        } catch (Exception e) {
            log.error("商品推介日期或销售终止日期格式错误, 要求格式为: yyyy-MM-dd HH:mm:ss", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "商品推介日期或销售终止日期格式错误, 要求格式为: yyyy-MM-dd HH:mm:ss");
        }

        //完善商品其他信息
        productInfo.setOperator_id(admin_login.getStaff_id());
        productInfo.setOperator_name(admin_login.getStaff_name());
        productInfo.setCreate_time(new Date());
        productInfo.setUpdate_time(new Date());
        //获取商品分类名称
        String category_tree_desc = productCategoryService.generateCategoryTreeDesc(product.getProd_cate_id());
        productInfo.setProd_cate_path(category_tree_desc);
        productInfo.setUp_down_status(ProductUtil.DOWN_STATUS);

        //保存商品
        try {
            super.insert(productInfo);
        } catch (Exception e) {
            log.error("新增商品保存失败", e);
            throw new BizException("新增商品保存失败", e);
        }

        //如果是跨境商品生成跨境数据
        CrossBorderProduct crossBorderProduct = null;
        if (ProductTypeCacheHandler.isCrossBorder(product.getProduct_type_id())) {
            crossBorderProduct = product.getCross_border_product().convertTo();
            crossBorderProduct.setId(productInfo.getId());
            try {
                crossBorderProduct.insert();
            } catch (Exception e) {
                log.error("新增商品-保存跨境商品数据失败", e);
                throw new BizException("新增商品-保存跨境商品数据失败", e);
            }
        }

        //处理商品属性数据
        if (UtilValidate.isNotEmpty(product.getAttr_list())) {
            ProductAttributeRequestVO productAttributeRequestVO = new ProductAttributeRequestVO();
            productAttributeRequestVO.setProduct_id(productInfo.getId());
            productAttributeRequestVO.setAttr_list(product.getAttr_list());
            try {
                Result result = configManyProductAttribute(productAttributeRequestVO);
                if (result.isFailed()) {
                    throw new BizException(result.getMsg());
                }
            } catch (BizException e) {
                log.error("处理商品属性数据保存失败:" + e.getMessage(), e);
                throw e;
            } catch (Exception e) {
                log.error("处理商品属性数据保存失败", e);
                throw new BizException("处理商品属性数据保存失败", e);
            }
        }

        //新增商品数据备份以备审核处理
        try {
            ProductBackupService.ProductBackupHandler before = new ProductBackupService.ProductBackupHandler();
            before.setProduct(productInfo);
            before.setCrossBorderProduct(crossBorderProduct);
            before.setAttr_list(product.getAttr_list());

            backupService.productBackupProcess(before, null, ProductUtil.DATA_TYPE_CENTER, admin_login.getStaff_id(), admin_login.getStaff_name());
        } catch (Exception e) {
            log.error("商品审核备份失败", e);
            throw new BizException("商品审核备份失败", e);
        }

        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 王光银
     * @methodName:
     * @methodDesc: 保存修改商品（完整数据处理）
     * @description:
     * @param:
     * @return
     * @create 2018-07-19 15:36
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateProduct(ProductRequestVO product) throws BizException, ServerException {
        if (product == null || product.getProduct_id() == null || product.getProduct_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID不能是空值");
        }
        //验证商品数据
        Result check_res = checkProductData(product);
        if (check_res.isFailed()) {
            return check_res;
        }

        if (!ProductTypeCacheHandler.hasProductTypePermission(CommonUtil.getStaffIdFromCache(product.getLogin_token()), product.getProduct_type_id())) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "您没有商品类型:[" + ProductTypeCacheHandler.getProductTypeDescription(product.getProduct_type_id()) + "]的权限");
        }

        ProductInfo productInfo;
        try {
            productInfo = product.convertTo();
        } catch (Exception e) {
            log.error("商品推介日期或销售终止日期格式错误, 要求格式为: yyyy-MM-dd HH:mm:ss", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "商品推介日期或销售终止日期格式错误, 要求格式为: yyyy-MM-dd HH:mm:ss");
        }

        productInfo.setProd_cate_path(productCategoryService.generateCategoryTreeDesc(product.getProd_cate_id()));

        //获取当前操作用户信息
        StaffAdminLoginVO admin_login = LoginCache.getStaffAdminLoginVO(product.getLogin_token());
        if (admin_login == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取当前请求用户信息失败, token=" + product.getLogin_token());
        }

        //从数据库加载修改前数据
        ProductInfo db_product_info = super.selectById(product.getProduct_id());
        if (db_product_info == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + product.getProduct_id() + "]商品已不存在");
        }
        CrossBorderProduct db_cross_border_prod = null;
        if (ProductTypeCacheHandler.isCrossBorder(db_product_info.getProduct_type_id())) {
            db_cross_border_prod = crossBorderProductMapper.selectById(db_product_info.getId());
        }

        //商品修改数据备份处理
        try {
            ProductBackupService.ProductBackupHandler before = new ProductBackupService.ProductBackupHandler();
            before.setProduct(db_product_info);
            before.setCrossBorderProduct(db_cross_border_prod);

            ProductBackupService.ProductBackupHandler after = new ProductBackupService.ProductBackupHandler();
            after.setProduct(productInfo);
            if (product.getCross_border_product() != null) {
                after.setCrossBorderProduct(product.getCross_border_product().convertTo());
            }
            after.setAttr_list(product.getAttr_list());

            backupService.productBackupProcess(before, after, ProductUtil.DATA_TYPE_CENTER, admin_login.getStaff_id(), admin_login.getStaff_name());
        } catch (Exception e) {
            log.error("商品审核备份失败", e);
            throw new BizException("商品审核备份失败", e);
        }

        return new Result(CodeEnum.SUCCESS);
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result configManyProductAttribute(ProductAttributeRequestVO productAttributeRequestVO) throws BizException {
        if (productAttributeRequestVO.getProduct_id() == null || productAttributeRequestVO.getProduct_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID不能是空值");
        }

        if (!checkProductExists(productAttributeRequestVO.getProduct_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不存在ID为:[" + productAttributeRequestVO.getProduct_id() + "]的商品:");
        }

        try {
            //删除商品的所有属性
            productAttributeMapper.deleteByMap(UtilMisc.toMap("product_id", productAttributeRequestVO.getProduct_id()));

            //循环新增属性
            if (UtilValidate.isNotEmpty(productAttributeRequestVO.getAttr_list())) {
                Result result;
                for (ProductAttributeItemRequestVO vo : productAttributeRequestVO.getAttr_list()) {
                    productAttributeRequestVO.setAttr(vo);
                    result = addOneProductAttribute(productAttributeRequestVO);
                    if (result.isFailed()) {
                        throw new BizException(result.getMsg());
                    }
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (BizException e) {
            log.error("处理商品属性失败:" + e.getMessage(), e);
            throw e;
        }  catch (Exception e) {
            log.error("处理商品属性失败", e);
            throw new BizException("处理商品属性失败", e);
        }
    }

    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result addOneProductAttribute(ProductAttributeRequestVO productAttributeRequestVO) throws BizException {
        if (productAttributeRequestVO == null || productAttributeRequestVO.getProduct_id() == null || productAttributeRequestVO.getProduct_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID不能是空值");
        }
        if (productAttributeRequestVO.getAttr() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品属性不能是空值，处理单个属性请求时请注意参数传送");
        }
        if (UtilValidate.isEmpty(productAttributeRequestVO.getAttr().getAttr_type())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品属性类型不能是空值");
        }
        if (!ProductUtil.PROD_ATTR_TYPE_CATE.equals(productAttributeRequestVO.getAttr().getAttr_type()) && !ProductUtil.PROD_ATTR_TYPE_DEFINE.equals(productAttributeRequestVO.getAttr().getAttr_type())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "无法识别的商品属性类型:" + productAttributeRequestVO.getAttr().getAttr_type());
        }

        if (!checkProductExists(productAttributeRequestVO.getProduct_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不存在ID为:[" + productAttributeRequestVO.getProduct_id() + "]的商品:");
        }

        ProductCategoryAttr categoryAttr = null;
        ProductCategoryAttrItem categoryAttrItem = null;
        if (ProductUtil.PROD_ATTR_TYPE_CATE.equals(productAttributeRequestVO.getAttr().getAttr_type())) {
            if (productAttributeRequestVO.getAttr().getProd_cate_attr_id() == null || productAttributeRequestVO.getAttr().getProd_cate_attr_id() <= 0) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "商品属性为继承分类属性时,分类属性ID不能是空值");
            }

            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("prod_cate_attr_id", productAttributeRequestVO.getAttr().getProd_cate_attr_id());
            wrapper.eq("product_id", productAttributeRequestVO.getProduct_id());

            if (productAttributeMapper.selectCount(wrapper) > 0) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "不能重复新增同一个ID为:[" + productAttributeRequestVO.getAttr().getProd_cate_attr_id() + "]的分类属性, 继承的分类属性只能配置一次");
            }

            categoryAttr = categoryAttrMapper.selectById(productAttributeRequestVO.getAttr().getProd_cate_attr_id());
            if (categoryAttr == null) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "ID为:[" + productAttributeRequestVO.getAttr().getProd_cate_attr_id() + "]的继承分类属性ID不存在");
            }
            if (ProductUtil.ENUM_ATTR_TYPE.equals(categoryAttr.getAttr_type())) {
                if (productAttributeRequestVO.getAttr().getProd_cate_attr_item_id() == null || productAttributeRequestVO.getAttr().getProd_cate_attr_item_id() <= 0) {
                    return new Result(CodeEnum.SUCCESS);
                }
                categoryAttrItem = attrItemMapper.selectById(productAttributeRequestVO.getAttr().getProd_cate_attr_item_id());
                if (categoryAttrItem == null) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "ID为:[" + productAttributeRequestVO.getAttr().getProd_cate_attr_id() + "]的分类枚举属性的ID为:[" + productAttributeRequestVO.getAttr().getProd_cate_attr_item_id() + "]的条目不存在");
                }
            } else if (UtilValidate.isEmpty(productAttributeRequestVO.getAttr().getAttr_value())) {
                return new Result(CodeEnum.SUCCESS, "字符串类型的分类属性:[" + categoryAttr.getAttr_name() + "]没有属性值,已被忽略");
            }
        } else if (UtilValidate.isEmpty(productAttributeRequestVO.getAttr().getAttr_name())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "自定义属性时属性名称不能是空值");
        } else if (UtilValidate.isEmpty(productAttributeRequestVO.getAttr().getAttr_value())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "自定义属性时属性值不能是空值");
        }

        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct_id(productAttributeRequestVO.getProduct_id());
        productAttribute.setAttr_type(productAttributeRequestVO.getAttr().getAttr_type());
        if (ProductUtil.PROD_ATTR_TYPE_CATE.equals(productAttributeRequestVO.getAttr().getAttr_type())) {
            productAttribute.setAttr_name(categoryAttr.getAttr_name());
            productAttribute.setProd_cate_attr_id(categoryAttr.getId());
            if (ProductUtil.ENUM_ATTR_TYPE.equals(categoryAttr.getAttr_type())) {
                productAttribute.setProd_cate_attr_item_id(categoryAttrItem.getId());
                productAttribute.setAttr_value(categoryAttrItem.getAttr_value());
            } else {
                productAttribute.setAttr_value(productAttributeRequestVO.getAttr().getAttr_value());
            }
        } else {
            productAttribute.setAttr_name(productAttributeRequestVO.getAttr().getAttr_name());
            productAttribute.setAttr_value(productAttributeRequestVO.getAttr().getAttr_value());
        }

        try {
            productAttributeMapper.insert(productAttribute);
        } catch (Exception e) {
            log.error("保存新增商品属性失败", e);
            throw new BizException("保存新增商品属性失败", e);
        }
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return com.hryj.common.Result
     * @author 王光银
     * @methodName: downProduct
     * @methodDesc: 上架商品（一个或多个）
     * @description:
     * @param: [idsRequestVO]
     * @create 2018-07-13 9:31
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result upProducts(@RequestBody ProductIdsRequestVO idsRequestVO) throws ServerException {
        //验证商品是否通过审核
        if (idsRequestVO == null || UtilValidate.isEmpty(idsRequestVO.getProduct_id_list())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID集合不能是空值");
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.in("id", idsRequestVO.getProduct_id_list());
        wrapper.setSqlSelect("id", "up_down_status", "audit_status", "forbid_sale_flag");
        List<ProductInfo> prodList = super.selectList(wrapper);
        if (UtilValidate.isNotEmpty(prodList)) {
            for (ProductInfo info : prodList) {
                if (ProductUtil.UP_STATUS.equals(info.getForbid_sale_flag())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + info.getId() + "]的商品已全网禁售,不能上架销售");
                }
                if (!ProductUtil.UP_STATUS.equals(info.getAudit_status())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + info.getId() + "]的商品未审核,不能上架销售");
                }

                //加载最后一条审核记录，验证审核是否通过
                wrapper = new EntityWrapper();
                wrapper.eq("product_id", info.getId());
                wrapper.setSqlSelect("handle_result");
                wrapper.orderBy("handle_time", false);
                List<ProductAuditRecord> auditRecordList = productAuditRecordMapper.selectPage(new RowBounds(0, 1), wrapper);
                if (UtilValidate.isEmpty(auditRecordList)
                        || !ProductUtil.UP_STATUS.equals(auditRecordList.get(0).getHandle_result())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "审核未通过,不能上架销售");
                }
            }
            return upDownProducts(idsRequestVO, ProductUtil.UP_STATUS);
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "这些商品不存在");
        }
    }

    /**
     * @return com.hryj.common.Result
     * @author 王光银
     * @methodName: downProduct
     * @methodDesc: 下架商品（一个或多个）
     * @description:
     * @param: [idsRequestVO]
     * @create 2018-07-13 9:31
     **/
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result downProducts(@RequestBody ProductIdsRequestVO idsRequestVO) throws ServerException {
        return upDownProducts(idsRequestVO, ProductUtil.DOWN_STATUS);
    }

    /**
     * @author 王光银
     * @methodName: isCrossBorderProduct
     * @methodDesc:
     * @description:
     * @param: [product_id]
     * @return boolean
     * @create 2018-09-19 16:40
     **/
    public boolean isCrossBorderProduct(Long product_id) {
        if (product_id == null || product_id <= 0L) {
            return false;
        }
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", product_id);
        wrapper.setSqlSelect("product_type_id");
        ProductInfo info = super.selectOne(wrapper);
        if (info == null || UtilValidate.isEmpty(info.getProduct_type_id())) {
            return false;
        }
        return ProductTypeCacheHandler.isCrossBorder(info.getProduct_type_id());
    }

    public String getProductName(Long prod_id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", prod_id);
        wrapper.setSqlSelect("product_name");
        List<ProductInfo> list = super.selectList(wrapper);
        return UtilValidate.isEmpty(list) ? "ID为:[" + prod_id + "]的商品不存在" : list.remove(0).getProduct_name();
    }

    private Result upDownProducts(ProductIdsRequestVO idsRequestVO, Integer up_down) {
        if (idsRequestVO == null || UtilValidate.isEmpty(idsRequestVO.getProduct_id_list())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品ID不能是空值");
        }

        try {
            List<ProductInfo> prod_list = new ArrayList<>(idsRequestVO.getProduct_id_list().size());
            for (Long id : idsRequestVO.getProduct_id_list()) {
                if (!checkProductExists(id)) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "不存在ID为[" + id + "]的商品");
                }
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("id", id);
                wrapper.setSqlSelect("id", "product_type_id");
                ProductInfo prod = super.selectOne(wrapper);
                if (!ProductTypeCacheHandler.hasProductTypePermission(CommonUtil.getStaffIdFromCache(idsRequestVO.getLogin_token()), prod.getProduct_type_id())) {
                    return new Result<>(CodeEnum.FAIL_BUSINESS, "您没有商品类型:[" + ProductTypeCacheHandler.getProductTypeDescription(prod.getProduct_type_id()) + "]的权限");
                }
                prod.setUp_down_status(up_down);
                prod_list.add(prod);
            }
            super.updateBatchById(prod_list);
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error((ProductUtil.UP_STATUS.equals(up_down) ? "上架": "下架") + "商品处理失败", e);
            throw new BizException((ProductUtil.UP_STATUS.equals(up_down) ? "上架": "下架") + "商品处理失败", e);
        }
    }

    private Boolean checkProductExists(Long product_id) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", product_id);
        return super.selectCount(wrapper) > 0;
    }

    private Result checkProductData(ProductRequestVO product) {
        if (UtilValidate.isEmpty(product.getProduct_type_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品类型不能是空值");
        }
        if (!PermissionManageHandler.PermissionSupport.containsProductType(product.getProduct_type_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "不能识别的商品类型:[" + product.getProduct_type_id() + "]");
        }

        if (UtilValidate.isEmpty(product.getProduct_name())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品名称不能是空值");
        }
        if (UtilValidate.isEmpty(product.getBrand_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "品牌名称不能是空值");
        }
        if (UtilValidate.isEmpty(product.getList_image_url())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品列表图URL不能是空值");
        }
        if (product.getProd_cate_id() == null || product.getProd_cate_id() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品所属分类不能是空值");
        }
        //验证分类是否最后一级
        if (productCategoryService.categoryHasSon(product.getProd_cate_id())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品所属分类只能是末级分类");
        }
        if (UtilValidate.isEmpty(product.getSpecification())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品规格不能是空值");
        }
        if (product.getCost_price() == null || product.getCost_price().doubleValue() <= 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品成本价不能为空或小于0");
        }

        if (UtilValidate.isNotEmpty(product.getAttr_list())) {
            Iterator<ProductAttributeItemRequestVO> it = product.getAttr_list().iterator();
            while (it.hasNext()) {
                ProductAttributeItemRequestVO item = it.next();
                if (!ProductUtil.PROD_ATTR_TYPE_CATE.equals(item.getAttr_type())
                        && !ProductUtil.PROD_ATTR_TYPE_DEFINE.equals(item.getAttr_type())) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "不能识别的属性类型:" + item.getAttr_type());
                }
                if (ProductUtil.PROD_ATTR_TYPE_DEFINE.equals(item.getAttr_type())) {
                    if (UtilValidate.isEmpty(item.getAttr_name())
                            || UtilValidate.isEmpty(item.getAttr_value())) {
                        return new Result(CodeEnum.FAIL_PARAMCHECK, "自定义属性名称与值不能是空值");
                    }
                }
                if (ProductUtil.PROD_ATTR_TYPE_CATE.equals(item.getAttr_type())) {
                    if (item.getProd_cate_attr_id() == null || item.getProd_cate_attr_id() <= 0L) {
                        it.remove();
                        continue;
                    }
                    //加载枚举属性以及条目
                    ProductCategoryAttr categoryAttr = categoryAttrMapper.selectById(item.getProd_cate_attr_id());
                    if (categoryAttr == null) {
                        return new Result(CodeEnum.FAIL_PARAMCHECK, "不存在ID为:[" + item.getProd_cate_attr_id() + "]的分类属性");
                    }
                    item.setAttr_name(categoryAttr.getAttr_name());
                    if (ProductUtil.ENUM_ATTR_TYPE.equals(categoryAttr.getAttr_type())) {
                        if (item.getProd_cate_attr_item_id() == null || item.getProd_cate_attr_item_id() <= 0L) {
                            it.remove();
                            continue;
                        }
                        ProductCategoryAttrItem attrItem = attrItemMapper.selectById(item.getProd_cate_attr_item_id());
                        if (attrItem == null) {
                            return new Result(CodeEnum.FAIL_PARAMCHECK, "不存在ID为:[" + item.getProd_cate_attr_item_id() + "]的枚举属性条目");
                        }
                        item.setAttr_value(attrItem.getAttr_value());
                    } else if (UtilValidate.isEmpty(item.getAttr_value())) {
                        it.remove();
                        continue;
                    }

                }
            }
        }

        if (ProductTypeCacheHandler.isCrossBorder(product.getProduct_type_id())) {
            CrossBorderProductRequestVO cross_border_prod = product.getCross_border_product();
            if (cross_border_prod == null) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "商品类型为跨境商品时, 跨境商品数据不能是空值");
            }
            if (UtilValidate.isEmpty(cross_border_prod.getChannel())) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "跨境商品发货仓库不能是空值");
            }

            if (UtilValidate.isEmpty(cross_border_prod.getHs_code())) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "跨境商品HSCODE不能是空值");
            }

            if (UtilValidate.isEmpty(cross_border_prod.getThird_sku_id())) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "跨境商品第三方SKU-ID不能是空值");
            }

            if (cross_border_prod.getDeclare_price() == null || cross_border_prod.getDeclare_price().compareTo(BigDecimal.ZERO) <= 0) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "跨境商品报关价不能是空值且必须大于0");
            }

            if (cross_border_prod.getInventory_quantity() == null || cross_border_prod.getInventory_quantity().intValue() <= 0) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "跨境商品库存不能是空值且必须大于0");
            }

            TaxRate taxRate = CrossBorderProductTaxRateCacheHandler.getTaxRate(cross_border_prod.getHs_code());
            if (taxRate == null) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "HSCODE错误,没有找到对应的配置");
            }

            if (CommonConstantPool.UPPER_Y.equals(taxRate.getHas_consume_tax())) {
                if (cross_border_prod.getUnit_1() == null || cross_border_prod.getUnit_1().compareTo(BigDecimal.ZERO) <= 0) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "HSCODE对应的商品可能会产生额外消费税,必须指定商品的净含量值");
                }

                if (UtilValidate.isEmpty(cross_border_prod.getUnit_2())) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "HSCODE对应的商品可能会产生额外消费税,必须指定商品的净含量单位");
                }
            }

        }

        return new Result(CodeEnum.SUCCESS);
    }
}
