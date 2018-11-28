package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.product.common.request.ShareProductRequestVO;
import com.hryj.entity.vo.product.common.response.ShareProductResponseVO;
import com.hryj.entity.vo.product.partyprod.mapping.PartyProductMappingRow;
import com.hryj.entity.vo.product.partyprod.request.*;
import com.hryj.entity.vo.product.partyprod.response.*;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.exception.BizException;
import com.hryj.exception.ServerException;
import com.hryj.feign.PartyFeignClient;
import com.hryj.mapper.CrossBorderProductMapper;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.mapper.ProductMapper;
import com.hryj.mapper.condition.PartyProdIntersectionCondition;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.service.inventory.cache.InventoryCacheUtil;
import com.hryj.service.inventory.cache.ProductInventorySummary;
import com.hryj.service.prodcate.ProductCategoryUtilService;
import com.hryj.service.util.*;
import com.hryj.service.worktask.PartyProductUpdateTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 王光银
 * @className: PartyProductService
 * @description: 门店仓库商品管理服务
 * @create 2018/7/4 0004 14:15
 **/
@Slf4j
@Service
public class PartyProductService extends ServiceImpl<PartyProductMapper, PartyProduct> {

    private static final BigDecimal ZERO = new BigDecimal("0");

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PartyFeignClient partyFeignClient;

    @Autowired
    private ProductCategoryUtilService productCategoryUtilService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CrossBorderProductMapper crossBorderProductMapper;

    public ProductService getProductService() {
        return this.productService;
    }

    /**
     * @author 王光银
     * @methodName: getProductShareContent
     * @methodDesc: 获取商品分享内容
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.common.response.ShareProductResponseVO>
     * @create 2018-08-23 15:01
     **/
    @SuppressWarnings("unchecked")
    public Result<ShareProductResponseVO> getProductShareContent(ShareProductRequestVO requestVO) throws ServerException {
        if (requestVO.getParty_id() == null || requestVO.getParty_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "门店、仓库ID不能是空值");
        }
        if (requestVO.getProduct_id() == null || requestVO.getProduct_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "商品ID不能是空值");
        }

        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(requestVO.getLogin_token());
        if (userLoginVO == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "用户token过期, token=" + requestVO.getLogin_token());
        }


        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("id", requestVO.getProduct_id());
        wrapper.setSqlSelect("product_name", "list_image_url", "product_info");
        ProductInfo prod = productService.selectOne(wrapper);
        if (prod == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "商品已下架");
        }

        //获取商品分享的H5页面访问路径配置
        String product_share_page_url_config_key = "ProductShareLink";
        String share_page_url = CodeCache.getValueByKey(product_share_page_url_config_key, "S01");
        if (UtilValidate.isEmpty(share_page_url)) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "未配置分享页面的链接");
        }

        //获取商品分享描述配置
        //String product_share_content_config_key = "ProductShareDesc";
        //String product_share_content = CodeCache.getValueByKey(product_share_content_config_key, "S01");
        //if (UtilValidate.isEmpty(product_share_content)) {
        //    return new Result<>(CodeEnum.FAIL_BUSINESS, "未配置分享描述");
        //}

        ShareProductResponseVO responseVO = new ShareProductResponseVO();
        responseVO.setParty_id(requestVO.getParty_id());
        responseVO.setProduct_id(requestVO.getProduct_id());
        responseVO.setActivity_id(requestVO.getActivity_id());
        responseVO.setMedia_url(prod.getList_image_url());
        responseVO.setTitle(prod.getProduct_name());
        responseVO.setDescription(prod.getProduct_info());
        responseVO.setUrl(share_page_url);
        responseVO.setUser_id(userLoginVO.getUser_id());

        return new Result<>(CodeEnum.SUCCESS, responseVO);
    }

    /**
     * @author 王光银
     * @methodName: searchManyPartyPolymerizationProduct
     * @methodDesc: 分页查询多个门店或仓库的聚合商品
     * @description: 支持并集查询和交集查询两种方式
     * @param: [partyPolymerizationProductSearchRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.partyprod.response.PartyIntersectionProductItem>>
     * @create 2018-06-30 9:19
     **/
    @SuppressWarnings("unchecked")
    public Result<PageResponseVO<PartyIntersectionProductItem>> searchManyPartyPolymerizationProduct(
            SearchPartyPolymerizationProductRequestVO condReqVO) throws ServerException {
        List<String> permission_list = condReqVO.getPermission_list((Object source_value) -> (ProductTypeCacheHandler.getProductTypePermission(CommonUtil.getStaffIdFromCache(source_value.toString()))));
        if (UtilValidate.isEmpty(permission_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        //查询条件参数
        Map<String, Object> params_map = new HashMap<>(10);
        ProductTypeConditionUtil.setProductTypeCondition(condReqVO, params_map, permission_list);

        if (condReqVO.getIs_all_party() == null) {
            condReqVO.setIs_all_party(false);
        }
        if (!condReqVO.getIs_all_party() && UtilValidate.isEmpty(condReqVO.getParty_id_list())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店或仓库ID集合数据不能是空值");
        }

        //当事组织的范围为全部时计算得到当前用户在组织结构中能够看到的所有门店与仓库集合
        if (condReqVO.getIs_all_party()) {
            if (UtilValidate.isEmpty(condReqVO.getParty_type())) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "当事组织范围为全部时,必须指定当事组织类型：门店或仓库");
            }
            Result<List<Long>> party_id_result = UserCoveredByPartyUtil.calculateAndGetAdminVisibleParty(partyFeignClient, condReqVO.getLogin_token(), false, condReqVO.getParty_type());
            if (party_id_result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, party_id_result.getMsg());
            }
            if (UtilValidate.isEmpty(party_id_result.getData())) {
                return new Result<>(CodeEnum.SUCCESS, new PageResponseVO<>(0L, 0L, new ArrayList<>(0)));
            }
            condReqVO.setParty_id_list(party_id_result.getData());
        }


        int in_size = 1000;

        //计算出交集查询需要的条件
        List<PartyProdIntersectionCondition> cond_list = new ArrayList<>(condReqVO.getParty_id_list().size() / in_size + 1);
        int i = 0;
        StringBuilder value = new StringBuilder();
        for (Long party_id : condReqVO.getParty_id_list()) {
            value.append("\'").append(party_id).append("\'");
            i ++;
            if (i >= in_size) {
                cond_list.add(new PartyProdIntersectionCondition(value.toString(), i));
                i = 0;
                value.delete(0, value.length());
                continue;
            }
            value.append(",");
        }
        if (value.length() > 0) {
            cond_list.add(new PartyProdIntersectionCondition(value.deleteCharAt(value.length() - 1).toString(), i));
        }

        params_map.put("party_id_list", cond_list);

        if (UtilValidate.isNotEmpty(condReqVO.getProduct_name())) {
            params_map.put("prod_name", condReqVO.getProduct_name().trim());
        }
        if (UtilValidate.isNotEmpty(condReqVO.getBrand_name())) {
            params_map.put("brand_name", condReqVO.getBrand_name().trim());
        }
        if (condReqVO.getBrand_id() != null && condReqVO.getBrand_id() > 0L) {
            params_map.put("brand_id", condReqVO.getBrand_id());
        }
        if (UtilValidate.isNotEmpty(condReqVO.getCategory_name())) {
            params_map.put("cate_name", condReqVO.getCategory_name().trim());
        }
        if (condReqVO.getCategory_id() != null && condReqVO.getCategory_id() >= 0L) {
            //当根据商品分类查询商品时，必须要保证商品分类为最末级分类
            Set<Long> cate_set = productCategoryUtilService.getLastProdCate(condReqVO.getCategory_id(), true);
            if (UtilValidate.isNotEmpty(cate_set)) {
                if (cate_set.size() == 1) {
                    params_map.put("cate_id", cate_set.iterator().next());
                } else {
                    params_map.put("cate_id_set", cate_set);
                }
            }
        }

        if (condReqVO.getInclude_down_status() == null) {
            condReqVO.setInclude_down_status(false);
        }
        if (!condReqVO.getInclude_down_status()) {
            params_map.put("up_down_status", ProductUtil.UP_STATUS);
        }

        params_map.put("forbid_sale_limit", 1);
        params_map.put("inventory_limit", 0);

        try {
            Page pageCondition = new Page(condReqVO.getPage_num(), condReqVO.getPage_size());
            List<ProductInfo> res = super.baseMapper.selectPartyIntersectionProduct(params_map, pageCondition);
            if (UtilValidate.isEmpty(res)) {
                return new Result<>(CodeEnum.SUCCESS, new PageResponseVO<>(0L, 0L, new ArrayList<>(0)));
            }

            List<PartyIntersectionProductItem> return_list = new LinkedList<>();

            for (ProductInfo info : res) {
                return_list.add(info.convertToPartyProdListItem(ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER, ProductUtil.PROD_TYPE_NAME_GETTER));
            }

            return new Result<>(CodeEnum.SUCCESS, new PageResponseVO<>(pageCondition.getTotal(), pageCondition.getPages(), return_list));
        } catch (Exception e) {
            log.error("查询多门店或仓库交集商品失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "查询多门店或仓库交集商品失败");
        }
    }

    /**
     * @author 王光银
     * @methodName: searchPartyProduct
     * @methodDesc: 返回指定门店或仓库的商品数据和基础信息
     * @description:
     * @param: [partyProductDataRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.partyprod.response.PartyProductDataResponseVO>
     * @create 2018-06-27 20:23
     **/
    @SuppressWarnings("unchecked")
    public Result<PartyProductDataResponseVO> searchPartyProduct(PartyProductDataRequestVO requestVO) throws ServerException {
        //商品数据权限处理
        List<String> permission_list = requestVO.getPermission_list((Object token) -> (ProductTypeCacheHandler.getProductTypePermission(CommonUtil.getStaffIdFromCache(token.toString()))));
        if (UtilValidate.isEmpty(permission_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        //组装查询条件
        Map<String, Object> params_map = new HashMap<>(5);
        ProductTypeConditionUtil.setProductTypeCondition(requestVO, params_map, permission_list);

        if (requestVO == null || requestVO.getParty_id() == null || requestVO.getParty_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "门店或仓库ID不能是空值");
        }
        if (requestVO.getUp_down_status() == null) {
            requestVO.setUp_down_status(ProductUtil.UP_STATUS);
        } else if (!ProductUtil.UP_STATUS.equals(requestVO.getUp_down_status()) && !ProductUtil.DOWN_STATUS.equals(requestVO.getUp_down_status())) {
            requestVO.setUp_down_status(ProductUtil.UP_STATUS);
        }

        params_map.put("party_id", requestVO.getParty_id());
        if (requestVO.getUp_down_status() != null) {
            params_map.put("up_down_status", requestVO.getUp_down_status());
        }
        if (UtilValidate.isNotEmpty(requestVO.getProduct_name())) {
            params_map.put("prod_name", requestVO.getProduct_name().trim());
        }
        if (UtilValidate.isNotEmpty(requestVO.getCategory_name())) {
            params_map.put("cate_name", requestVO.getCategory_name().trim());
        }
        if (UtilValidate.isNotEmpty(requestVO.getCategory_id_list())) {
            Set<Long> cate_id_set = new HashSet<>(requestVO.getCategory_id_list().size() * 3);
            for (Long cate_id : requestVO.getCategory_id_list()) {
                Set<Long> cate_set = productCategoryUtilService.getLastProdCate(cate_id, true);
                if (UtilValidate.isNotEmpty(cate_set)) {
                    cate_id_set.addAll(cate_set);
                }
            }
            params_map.put("cate_id_list", cate_id_set);
        }
        if (UtilValidate.isNotEmpty(requestVO.getBrand_name())) {
            params_map.put("brand_name", requestVO.getBrand_name());
        }
        if (requestVO.getBrand_id() != null && requestVO.getBrand_id() > 0L) {
            params_map.put("brand_id", requestVO.getBrand_id());
        }

        Result<PartyProductDataResponseVO> result = new Result<>(CodeEnum.SUCCESS, new PartyProductDataResponseVO());

        try {
            Page pageCondition = new Page(requestVO.getPage_num(), requestVO.getPage_size());
            List<PartyProductMappingRow> page_result = super.baseMapper.selectPartyProduct(params_map, pageCondition);
            if (UtilValidate.isNotEmpty(page_result)) {
                GenericConverter<Boolean> cross_border_prod_check = (Object product_type_id) -> (ProductTypeCacheHandler.isCrossBorder(product_type_id.toString()));
                List<PartyProductListItemResponseVO> return_list = new LinkedList<>();
                for (PartyProductMappingRow row : page_result) {
                    return_list.add(row.convertToPartyProductListItem(cross_border_prod_check, ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER, ProductUtil.PROD_TYPE_NAME_GETTER));
                }

                result.getData().setPage_result(new PageResponseVO<>(pageCondition.getTotal(), pageCondition.getPages(), return_list));
            } else {
                result.getData().setPage_result(new PageResponseVO<>(0L, 0L, new ArrayList<>(0)));
            }
        } catch (Exception e) {
            log.error("返回指定门店或仓库的商品数据和基础信息 - 查询门店仓库商品失败", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "查询门店仓库商品失败");
        }

        if (requestVO.getReturn_party_info()) {
            //(invoke-api) 调用接口获取门店或仓库的基本信息
            Result<PartySimpleInfoResponseVO> party_simple_info_result = PartyUtil.getPartySimpleInfo(requestVO.getParty_id(), partyFeignClient);
            if (party_simple_info_result.isSuccess()) {
                result.getData().setParty_info(party_simple_info_result.getData());
            }
        }

        return result;
    }


    /**
     * @author 王光银
     * @methodName: searchPartySelectableProduct
     * @methodDesc: 加载指定门店或仓库的可添加销售的商品列表数据
     * @description:
     * @param: [partySelectableProdsPoolRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.partyprod.response.PartySelectableProdItemResponseVO>>
     * @create 2018-06-30 9:18
     **/
    @SuppressWarnings("unchecked")
    public Result<PageResponseVO<PartyProductListItemResponseVO>> searchPartySelectableProduct(PartySelectableProdsPoolRequestVO requestVO) throws ServerException {
        List<String> permission_list = requestVO.getPermission_list((Object source_value) -> (ProductTypeCacheHandler.getProductTypePermission(CommonUtil.getStaffIdFromCache(source_value.toString()))));
        if (UtilValidate.isEmpty(permission_list)) {
            return new Result<>(CodeEnum.SUCCESS);
        }
        if (UtilValidate.isEmpty(requestVO.getParty_type())) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "当事组织类型不能是空值,必须指定为:门店(store)或仓库(warehouse)");
        }
        if (!ProductUtil.PARTY_TYPE_STORE.equals(requestVO.getParty_type()) && !ProductUtil.PARTY_TYPE_WAREHOUSE.equals(requestVO.getParty_type())) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "不能识别的组织类型参数: party_type=" + requestVO.getParty_type());
        }
        if (requestVO.getParty_id() == null || requestVO.getParty_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "门店或仓库ID不能是空值");
        }

        //组装查询条件
        Map<String, Object> params_map = new HashMap<>(9);
        ProductTypeConditionUtil.setProductTypeCondition(requestVO, params_map, permission_list);

        params_map.put("party_id", requestVO.getParty_id());
        if (UtilValidate.isNotEmpty(requestVO.getProduct_name())) {
            params_map.put("prod_name", requestVO.getProduct_name().trim());
        }
        if (UtilValidate.isNotEmpty(requestVO.getCategory_name())) {
            params_map.put("cate_name", requestVO.getCategory_name().trim());
        }
        if (UtilValidate.isNotEmpty(requestVO.getBrand_name())) {
            params_map.put("brand_name", requestVO.getBrand_name().trim());
        }
        if (requestVO.getBrand_id() != null && requestVO.getBrand_id() > 0L) {
            params_map.put("brand_id", requestVO.getBrand_id());
        }
        if (requestVO.getProduct_id() != null && requestVO.getProduct_id() > 0L) {
            params_map.put("prod_id", requestVO.getProduct_id());
        }
        if (requestVO.getFilter_selected() != null && requestVO.getFilter_selected().equals(Integer.valueOf(1))) {
            params_map.put("filter_selected", 1);
        }
        if (requestVO.getCategory_id() != null && requestVO.getCategory_id() > 0L) {
            //当根据商品分类查询商品时，必须要保证商品分类为最末级分类
            Set<Long> cate_set = productCategoryUtilService.getLastProdCate(requestVO.getCategory_id(), true);
            if (UtilValidate.isNotEmpty(cate_set)) {
                if (cate_set.size() == 1) {
                    params_map.put("cate_id", cate_set.iterator().next());
                } else {
                    params_map.put("cate_id_set", cate_set);
                }
            }

        }
        params_map.put("forbid_sale_flag", ProductUtil.UP_STATUS);

        Result<PageResponseVO<PartyProductListItemResponseVO>> result = new Result<>(CodeEnum.SUCCESS);

        //当事组织为仓库时，可选择商品从商品中心池查询
        if (ProductUtil.PARTY_TYPE_WAREHOUSE.equals(requestVO.getParty_type())) {
            params_map.put("up_down_status", ProductUtil.UP_STATUS);
            try {
                Page page_cond = new Page(requestVO.getPage_num(), requestVO.getPage_size());
                List<PartyProductMappingRow> page_result = super.baseMapper.selectWarehouseSelectableProduct(params_map, page_cond);
                return generateResult(page_result, page_cond);
            } catch (Exception e) {
                log.error("从商品中心查询门店可选商品失败", e);
                return new Result<>(CodeEnum.FAIL_BUSINESS, "从商品中心查询门店可选商品失败");
            }
        }

        //当事组织为门店时，可选择商品从覆盖其范围的仓库查询
        //(invoke-api)调用接口获取覆盖当前门店的仓库
        Result<List<Long>> warehouse_id_list_result = PartyUtil.getCoveredBy(requestVO.getParty_id(), partyFeignClient);
        if (warehouse_id_list_result.isFailed()) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, warehouse_id_list_result.getMsg());
        }

        params_map.put("warehouse_id_list", warehouse_id_list_result.getData());
        params_map.put("check_sale_price", true);
        try {
            Page page_cond = new Page(requestVO.getPage_num(), requestVO.getPage_size());
            List<PartyProductMappingRow> page_result = super.baseMapper.selectStoreSelectableProduct(params_map, page_cond);
            return generateResult(page_result, page_cond);
        } catch (Exception e) {
            log.error("从仓库商品池查询当前门店的可选择商品失败", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "从仓库商品池查询当前门店的可选择商品失败");
        }
    }

    private Result<PageResponseVO<PartyProductListItemResponseVO>> generateResult(List<PartyProductMappingRow> page_result, Page pageCond) {
        Result<PageResponseVO<PartyProductListItemResponseVO>> result = new Result<>(CodeEnum.SUCCESS);
        if (UtilValidate.isNotEmpty(page_result)) {
            List<PartyProductListItemResponseVO> return_list = new LinkedList<>();
            for (PartyProductMappingRow row : page_result) {
                return_list.add(row.convertToPartyProductListItem(ProductUtil.PROD_IS_CROSS_BORDER_GETTER, ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER, ProductUtil.PROD_TYPE_NAME_GETTER));
            }
            result.setData(new PageResponseVO<>(pageCond.getTotal(), pageCond.getPages(), return_list));
        } else {
            result.setData(new PageResponseVO<>(0L, 0L, new ArrayList<>(0)));
        }
        return result;
    }

    /**
     * @author 王光银
     * @methodName: appendProductToPartySaleablePool
     * @methodDesc:  添加指定门店或仓库的销售商品
     * @description:
     * @param: [partySaleableProdPoolAppendRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:22
     **/
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result appendProductToPartySaleablePool(PartySaleableProdPoolAppendRequestVO appendRequestVO) throws ServerException {
        //参数检查
        if (appendRequestVO == null || appendRequestVO.getParty_id() == null || appendRequestVO.getParty_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店仓库ID不能是空值");
        }
        if (UtilValidate.isEmpty(appendRequestVO.getParty_prod_list())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "添加的门店仓库商品数据不能是空值");
        }

        StaffAdminLoginVO admin_login = LoginCache.getStaffAdminLoginVO(appendRequestVO.getLogin_token());
        if (admin_login == null) {
            return new Result(CodeEnum.FAIL_SERVER, "获取当前操作用户数据失败,token=" + appendRequestVO.getLogin_token());
        }
        Calendar current = Calendar.getInstance();
        Map<Long, PartyProductItem> append_prod_map = new HashMap<>(appendRequestVO.getParty_prod_list().size());
        for (PartyProductItem item : appendRequestVO.getParty_prod_list()) {
            if (item.getProduct_id() == null || item.getProduct_id() <= 0L) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "存在商品ID为空的错误数据");
            }

            if (UtilValidate.isNotEmpty(item.getIntroduction_date()) && UtilValidate.isNotEmpty(item.getSales_end_date())) {
                try {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(item.getIntroduction_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(DateUtil.parseDateTime(item.getSales_end_date()));
                    if (!c1.before(c2)) {
                        return new Result(CodeEnum.FAIL_PARAMCHECK, "商品推介销售日期必须在销售终止日期之前");
                    }
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error("推介销售日期或销售终止日期格式错误，必须是: yyyy-MM-dd HH:mm:ss", e);
                    }
                    throw new ServerException("保存修改门店仓库商品维护数据处理失败", e);
                }
            }
            if (UtilValidate.isNotEmpty(item.getSales_end_date())) {
                Calendar c1 = Calendar.getInstance();
                c1.setTime(DateUtil.parseDateTime(item.getSales_end_date()));
                if (!c1.after(current)) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "商品销售终止日期必须当前日期之后");
                }
            }
            //提交数据的重复验证
            if (Integer.valueOf(1).equals(appendRequestVO.getWhen_prod_duplicate()) && append_prod_map.containsKey(item.getProduct_id())) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "ID为:[" + item.getProduct_id() + "]的商品重复添加");
            }
            append_prod_map.put(item.getProduct_id(), item);
        }
        //与已经添加的商品做对比重复验证
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.setSqlSelect("product_id");
        wrapper.eq("party_id", appendRequestVO.getParty_id());
        List<PartyProduct> party_prod_list = super.selectList(wrapper);
        Set<Long> db_prod_id_set = null;
        if (UtilValidate.isNotEmpty(party_prod_list)) {
            db_prod_id_set = new HashSet<>(party_prod_list.size());
            for (PartyProduct party_prod : party_prod_list) {
                db_prod_id_set.add(party_prod.getProduct_id());
            }
        }
        Iterator<Long> it = append_prod_map.keySet().iterator();
        while (it.hasNext()) {
            Long prod_id = it.next();
            if (UtilValidate.isNotEmpty(db_prod_id_set) && db_prod_id_set.contains(prod_id)) {
                if (Integer.valueOf(1).equals(appendRequestVO.getWhen_prod_duplicate().intValue())) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "ID为:[" + prod_id + "]的商品重复添加");
                }
                it.remove();
            }
        }

        /**
         * 根据本次提交的商品ID加载商品中心的数据进行对比，不管组织类型都需要验证商品是否全网禁售，
         * 如果当前组织类型为仓库，需要判断商品是否下架，以及销售价与成本价的对比
         */
        wrapper = new EntityWrapper();
        wrapper.in("id", append_prod_map.keySet());
        wrapper.setSqlSelect("id", "forbid_sale_flag", "up_down_status", "cost_price");
        List<ProductInfo> center_prod_list = productMapper.selectList(wrapper);
        Map<Long, ProductInfo> center_prod_map = null;
        if (UtilValidate.isNotEmpty(center_prod_list)) {
            center_prod_map = new HashMap<>(center_prod_list.size());
            for (ProductInfo prod : center_prod_list) {
                center_prod_map.put(prod.getId(), prod);
            }
        }

        if (UtilValidate.isEmpty(center_prod_map)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "商品池中没有这些商品");
        }

        //验证商品池中商品是否依然存在，以及是否全网禁售
        it = append_prod_map.keySet().iterator();
        while (it.hasNext()) {
            Long prod_id = it.next();
            ProductInfo center_prod = center_prod_map.get(prod_id);
            if (center_prod == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品池中没有找到ID为:[" + prod_id + "]的商品");
            }
            if (ProductUtil.UP_STATUS.equals(center_prod.getForbid_sale_flag())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id + "]的商品已经全网禁售");
            }
        }
        //(invoke-api)调用接口获取当前组织的类型，门店和仓库对销售商品的添加验证处理是不一样的
        //部门类型:01-门店,02-仓库,03-普通部门
        String store = "01";
        String party_type;

        Result<PartySimpleInfoResponseVO> party_simple_info_result = PartyUtil.getPartySimpleInfo(appendRequestVO.getParty_id(), partyFeignClient);
        if (party_simple_info_result.isFailed()) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取当事组织信息失败:" + party_simple_info_result.getMsg());
        }

        party_type = party_simple_info_result.getData().getParty_type();
        if (UtilValidate.isEmpty(party_type)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取当事组织类型(门店或仓库)失败");
        }

        if (store.equals(party_type)) {
            //(invoke-api)调用接口获取覆盖门店的仓库
            Result<List<Long>> covered_warehouse_id_list_result = PartyUtil.getCoveredBy(appendRequestVO.getParty_id(), partyFeignClient);
            if (covered_warehouse_id_list_result.isFailed()) {
                return new Result(CodeEnum.FAIL_BUSINESS, covered_warehouse_id_list_result.getMsg());
            }

            //加载这些仓库的所有商品数据，用来判断当前门店添加的商品是否已经下架,以及取得门店的商品销售价格，商品销售的允许时间段
            wrapper = new EntityWrapper();
            wrapper.in("party_id", covered_warehouse_id_list_result.getData());
            wrapper.setSqlSelect("id", "party_id", "introduction_date", "sales_end_date", "up_down_status", "sale_price", "product_id");
            List<PartyProduct> warehouse_prod_list = super.selectList(wrapper);
            if (UtilValidate.isEmpty(warehouse_prod_list)) {
                return new Result(CodeEnum.FAIL_BUSINESS, "覆盖当前门店的仓库商品池中没有任何商品");
            }
            Map<Long, PartyProduct> warehouse_prod_map = new HashMap<>(5);
            for (PartyProduct product : warehouse_prod_list) {
                warehouse_prod_map.put(product.getProduct_id(), product);
            }
            //验证当前门店选择添加的商品是否是从覆盖门店的仓库商品池中选择的, 以及销售时间段，上下架状态的验证
            it = append_prod_map.keySet().iterator();
            while (it.hasNext()) {
                Long prod_id = it.next();
                PartyProductItem party_prod_item = append_prod_map.get(prod_id);
                if (party_prod_item.getParty_product_id() == null || party_prod_item.getParty_product_id() <= 0) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "商品:[" + productService.getProductName(prod_id) + "]缺少仓库商品ID值,当前组织为门店,所选商品应该来自于覆盖这家门店的仓库商品池");
                }
                if (!warehouse_prod_map.containsKey(prod_id)) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "覆盖当前门店的仓库的商品池中不包含商品ID为:[" + prod_id + "]的商品");
                }
                PartyProduct warehouse_prod = warehouse_prod_map.get(prod_id);

                /**
                 * 门店从仓库选择商品添加到销售池时，暂时取消对这个商品在仓库中的上下架状态的验证
                 */
                //if (!ProductUtil.UP_STATUS.equals(warehouse_prod.getUp_down_status())) {
                //    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id + "]的商品在仓库商品池中已下架");
                //}

                /**
                 * 暂时取消商品在仓库的销售时间的验证
                 */
                //ProductUtil.CheckResult res = ProductUtil.checkProductIntroductionAndEndDate(warehouse_prod.getIntroduction_date(), warehouse_prod.getSales_end_date(), current);
                //if (!res.res) {
                //    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id + "]的商品在仓库商品池中" + res.msg);
                //}

                /**
                 *
                 * 暂时取消对销售日期的验证
                 *
                 * 如果门店单独设置了销售推介日期与销售终止日期，此时要考虑仓库是否也设置了销售时间段，
                 * 在仓库也设置的情况下要进行时间段上的比较，门店的销售时间段必须在仓库的时间段之内
                 */
                //if (UtilValidate.isNotEmpty(party_prod_item.getIntroduction_date()) && warehouse_prod.getIntroduction_date() != null) {
                //    Calendar c1 = Calendar.getInstance();
                //    c1.setTime(DateUtil.parseDateTime(party_prod_item.getIntroduction_date()));
                //    Calendar c2 = Calendar.getInstance();
                //    c2.setTime(warehouse_prod.getIntroduction_date());
                //    if (c1.before(c2)) {
                //        return new Result(CodeEnum.FAIL_BUSINESS, "商品:[" + productService.getProductName(prod_id) +"]的销售推介日期[" + party_prod_item.getIntroduction_date() + "]不能早于覆盖其仓库的商品销售推介日期:[" + DateUtil.formatDateTime(warehouse_prod.getIntroduction_date()) + "]");
                //    }
                //}
                //if (UtilValidate.isNotEmpty(party_prod_item.getSales_end_date()) && warehouse_prod.getSales_end_date() != null) {
                //    Calendar c1 = Calendar.getInstance();
                //    c1.setTime(DateUtil.parseDateTime(party_prod_item.getSales_end_date()));
                //    Calendar c2 = Calendar.getInstance();
                //    c2.setTime(warehouse_prod.getSales_end_date());
                //    if (c1.after(c2)) {
                //        return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id + "]的商品销售终止日期[" + party_prod_item.getSales_end_date() + "]不能晚于覆盖其仓库的商品销售终止日期:[" + DateUtil.formatDateTime(warehouse_prod.getSales_end_date()) + "]");
                //    }
                //}


                /**
                 * 取消了上下架状态的验证后，增加对商品销售价格的验证
                 */
                if (warehouse_prod.getSale_price() == null || warehouse_prod.getSale_price().compareTo(BigDecimal.ZERO) <= 0) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "商品:[" + productService.getProductName(prod_id) +"]在仓库商品管理端未设置销售价格");
                }

                //将销售单价设置为仓库的销售单价
                party_prod_item.setSale_price(warehouse_prod.getSale_price());
            }
        } else {
            //仓库添加商品验证，验证商品的销售价格与成本价
            it = append_prod_map.keySet().iterator();
            while (it.hasNext()) {
                Long prod_id = it.next();
                PartyProductItem append_prod_item = append_prod_map.get(prod_id);
                ProductInfo center_prod = center_prod_map.get(prod_id);
                if (!ProductUtil.UP_STATUS.equals(center_prod.getUp_down_status())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id + "]的商品已在商品中心下架");
                }
                if (append_prod_item.getSale_price() != null && append_prod_item.getSale_price().compareTo(ZERO) <= 0) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id + "]的商品销售价格不能小于0");
                }
                if (append_prod_item.getSale_price() != null && append_prod_item.getSale_price().compareTo(center_prod.getCost_price()) <= 0) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + prod_id + "]的商品销售价格不能低于成本价[" + center_prod.getCost_price() + "]");
                }
                /**
                 * 如果仓库单独设置了销售推介日期与销售终止日期，此时要考虑商品中心是否也设置了销售时间段，
                 * 在商品中心也设置的情况下要进行时间段上的比较，仓库的销售时间段必须在商品中心的时间段之内
                 */
                if (UtilValidate.isNotEmpty(append_prod_item.getIntroduction_date()) && center_prod.getIntroduction_date() != null) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(append_prod_item.getIntroduction_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(center_prod.getIntroduction_date());
                    if (c1.before(c2)) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "商ID:[" + prod_id + "]的品销售推介日期[" + append_prod_item.getIntroduction_date() + "]不能早于商品中心设置的销售推介日期:[" + DateUtil.formatDateTime(center_prod.getIntroduction_date()) + "]");
                    }
                }
                if (UtilValidate.isNotEmpty(append_prod_item.getSales_end_date()) && center_prod.getSales_end_date() != null) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(append_prod_item.getSales_end_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(center_prod.getSales_end_date());
                    if (c1.after(c2)) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "ID:[" + prod_id + "]的商品销售终止日期[" + append_prod_item.getSales_end_date() + "]不能晚于商品中心设置的销售终止日期:[" + DateUtil.formatDateTime(center_prod.getSales_end_date()) + "]");
                    }
                }

            }
        }
        try {
            List<PartyProduct> need_to_append = new ArrayList<>(append_prod_map.size());
            it = append_prod_map.keySet().iterator();
            while (it.hasNext()) {
                PartyProductItem party_prod_item = append_prod_map.get(it.next());
                PartyProduct insert_party_product = new PartyProduct();
                need_to_append.add(insert_party_product);
                insert_party_product.setProduct_id(party_prod_item.getProduct_id());
                insert_party_product.setParty_id(appendRequestVO.getParty_id());
                insert_party_product.setInventory_quantity(party_prod_item.getInventory_quantity());
                insert_party_product.setSale_price(party_prod_item.getSale_price());
                insert_party_product.setIntroduction_date(UtilValidate.isEmpty(party_prod_item.getIntroduction_date()) ? current.getTime() : DateUtil.parseDateTime(party_prod_item.getIntroduction_date()));
                insert_party_product.setSales_end_date(UtilValidate.isEmpty(party_prod_item.getSales_end_date()) ? ProductUtil.FIVE_HUNDRED_YEARS_LATER.getTime() : DateUtil.parseDateTime(party_prod_item.getSales_end_date()));
                insert_party_product.setOperator_id(admin_login.getStaff_id());
                insert_party_product.setCreate_time(current.getTime());
                insert_party_product.setUpdate_time(current.getTime());
                insert_party_product.setUp_down_status(ProductUtil.DOWN_STATUS);
            }
            if (UtilValidate.isNotEmpty(need_to_append)) {
                super.insertBatch(need_to_append);
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("保存添加当事组织（门店或仓库）销售商品失败", e);
            throw new BizException("保存添加当事组织（门店或仓库）销售商品失败", e);
        }
    }

    /**
     * @author 王光银
     * @methodName: updateManyPartyProduct
     * @methodDesc: 批量更新维护门店或仓库的商品销售数据（销售价格和库存数量）
     * @description: 门店不能维护销售价格，即使传参数也不会处理
     * @param: [partyUpdatePriceInventoryQuantityRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:22
     **/
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateManyPartyProduct(PartyUpdatePriceInventoryQuantityRequestVO valueRequestVO) throws ServerException {
        //参数验证
        if (valueRequestVO == null || UtilValidate.isEmpty(valueRequestVO.getMany_item())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店仓库商品集合数据不能是空值");
        }
        BigDecimal zero = new BigDecimal("0");
        Map<Long, PartyUpdatePriceInventoryQuantityItemRequestVO> params_map = new HashMap<>(valueRequestVO.getMany_item().size());

        for (PartyUpdatePriceInventoryQuantityItemRequestVO party_prod : valueRequestVO.getMany_item()) {
            if (party_prod.getParty_product_id() == null || party_prod.getParty_product_id() <= 0L) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "门店仓库商品集合数据中存在ID为空的数据,参数名:[party_product_id]");
            }
            if (party_prod.getInventory_quantity() != null && party_prod.getInventory_quantity().intValue() <= 0) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "库存量必须大于0");
            }
            if (UtilValidate.isNotEmpty(party_prod.getIntroduction_date()) && UtilValidate.isNotEmpty(party_prod.getSales_end_date())) {
                Calendar c1 = Calendar.getInstance();
                c1.setTime(DateUtil.parseDateTime(party_prod.getIntroduction_date()));
                Calendar c2 = Calendar.getInstance();
                c2.setTime(DateUtil.parseDateTime(party_prod.getSales_end_date()));
                if (c1.after(c2)) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "销售推介日期:[" + party_prod.getIntroduction_date() + "]必须在销售终止日期:[" + party_prod.getSales_end_date() + "]之前");
                }
            }
            params_map.put(party_prod.getParty_product_id(), party_prod);
        }

        //加载这些数据在修改前的数据
        List<PartyProduct> db_party_prod_list = super.selectBatchIds(params_map.keySet());
        if (UtilValidate.isEmpty(db_party_prod_list)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "提交的门店仓库商品维护数据已全部不存在");
        }

        //检查提交数据与数据库数据的匹配情况
        Map<Long, PartyProduct> party_prod_map = new HashMap<>(db_party_prod_list.size());
        Set<Long> prod_id_set = new HashSet<>(db_party_prod_list.size());
        Set<Long> party_id_set = new HashSet<>(1);
        for (PartyProduct party_prod : db_party_prod_list) {
            party_prod_map.put(party_prod.getId(), party_prod);
            prod_id_set.add(party_prod.getProduct_id());
            party_id_set.add(party_prod.getParty_id());
        }

        for (Long party_prod_id : params_map.keySet()) {
            if (!party_prod_map.containsKey(party_prod_id)) {
                return new Result(CodeEnum.FAIL_BUSINESS, "不存在ID[" + party_prod_id + "]的 当事组织(门店或仓库)商品");
            }
        }

        if (UtilValidate.isEmpty(party_id_set)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "检查到存在丢失了关联组织(门店或仓库)ID的错误数据,本次操作提交的门店仓库商品ID集合为:[" + params_map.keySet() + "]");
        }
        if (party_id_set.size() > 1) {
            return new Result(CodeEnum.FAIL_BUSINESS, "检查到提交的销售商品属于一个以上(一次操作的数据范围必须是一个组织)的关联组织(门店或仓库),本次操作提交的门店仓库商品ID集合为:[" + params_map.keySet() + "]");
        }
        if (UtilValidate.isEmpty(prod_id_set) || prod_id_set.size() != params_map.size()) {
            return new Result(CodeEnum.FAIL_BUSINESS, "检查到提交的数据不能与组织(门店或仓库)的销售商品池找到对应的错误数据,本次操作提交的门店仓库商品ID集合为:[" + params_map.keySet() + "]");
        }

        //(invoke-api)调用组织管理的接口获取组织的类型，是门店还是仓库，需要根据这个类型来决定当前请求是否有权限进行商品的价格设置
        Result<PartySimpleInfoResponseVO> party_simple_info_result = PartyUtil.getPartySimpleInfo(party_id_set.iterator().next(), partyFeignClient);
        if (party_simple_info_result.isFailed()) {
            return new Result(CodeEnum.FAIL_BUSINESS, party_simple_info_result.getMsg());
        }

        String party_type = party_simple_info_result.getData().getParty_type();
        if (UtilValidate.isEmpty(party_type)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "获取当事组织类型失败");
        }

        /**
         * 用于处理缓存任务的数据
         */
        List<ProductInventorySummary> inventory_cache_sync_list = new LinkedList<>();
        Set<Long> notify_party_id_set = new HashSet<>();
        notify_party_id_set.add(party_id_set.iterator().next());

        if (SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(party_type)) {
            //(invoke-api)当组织类型为门店时，调用接口获取覆盖这家门店的仓库
            Result<List<Long>> covered_warehouse_id_list_result = PartyUtil.getCoveredBy(party_id_set.iterator().next(), partyFeignClient);
            if (covered_warehouse_id_list_result.isFailed()) {
                return new Result(CodeEnum.FAIL_BUSINESS, covered_warehouse_id_list_result.getMsg());
            }

            //加载门店的与本次提交相关的商品池数据
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.in("party_id", covered_warehouse_id_list_result.getData());
            wrapper.in("product_id", prod_id_set);
            wrapper.setSqlSelect("introduction_date", "sales_end_date", "product_id", "id", "sale_price");

            List<PartyProduct> warehouse_prod_list = super.selectList(wrapper);
            if (UtilValidate.isEmpty(warehouse_prod_list)) {
                return new Result(CodeEnum.FAIL_BUSINESS, "在覆盖当前门店的仓库商品池中没有找到本次提交的商品, 仓库ID:"
                        + covered_warehouse_id_list_result.getData()
                        + ", 当前门店ID为:[" + party_id_set.iterator().next() + "]");
            }

            Map<Long, PartyProduct> warehouse_prod_map = new HashMap<>(warehouse_prod_list.size());
            for (PartyProduct party_prod : warehouse_prod_list) {
                warehouse_prod_map.put(party_prod.getProduct_id(), party_prod);
            }

            Iterator<Long> it = params_map.keySet().iterator();
            while (it.hasNext()) {
                Long key = it.next();
                if (!warehouse_prod_map.containsKey(party_prod_map.get(key).getProduct_id())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "在覆盖当前门店的仓库商品池中没有找到ID为:[" + key + "]的商品");
                }

                PartyUpdatePriceInventoryQuantityItemRequestVO param_item = params_map.get(key);
                PartyProduct warehouse_item = warehouse_prod_map.get(party_prod_map.get(key).getProduct_id());

                if (UtilValidate.isNotEmpty(param_item.getIntroduction_date()) && warehouse_item.getIntroduction_date() != null) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(param_item.getIntroduction_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(warehouse_item.getIntroduction_date());
                    if (c1.before(c2)) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品推介销售日期["
                                + param_item.getIntroduction_date()
                                + "]不能早于覆盖仓库商品池的推介销售日期[" + DateUtil.formatDateTime(warehouse_item.getIntroduction_date()) + "]");
                    }
                }
                if (UtilValidate.isNotEmpty(param_item.getSales_end_date()) && warehouse_item.getSales_end_date() != null) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(param_item.getSales_end_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(warehouse_item.getSales_end_date());
                    if (c1.after(c2)) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品销售终止日期["
                                + param_item.getSales_end_date()
                                + "]不能晚于覆盖仓库商品池的销售终止日期[" + DateUtil.formatDateTime(warehouse_item.getSales_end_date()) + "]");
                    }
                }
                param_item.setSale_price(warehouse_item.getSale_price());
            }
        } else {
            //从商品中心加载这些商品数据，对销售时间段和成本价的验证
            EntityWrapper<ProductInfo> wrapper = new EntityWrapper<>();
            wrapper.in("id", prod_id_set);
            wrapper.setSqlSelect("cost_price", "introduction_date", "sales_end_date", "id", "product_type_id");
            List<ProductInfo> center_prod_list = productMapper.selectList(wrapper);
            if (UtilValidate.isEmpty(center_prod_list) || center_prod_list.size() != params_map.size()) {
                return new Result(CodeEnum.FAIL_BUSINESS, "检查到本次提交的商品在商品中心不存在,商品ID集合为:[" + params_map.keySet() + "]");
            }
            Map<Long, ProductInfo> center_prod_map = new HashMap<>(center_prod_list.size());
            for (ProductInfo info : center_prod_list) {
                if (UtilValidate.isEmpty(info.getProduct_type_id())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "数据错误: 商品类型ID丢失, id=" + info.getId());
                }
                center_prod_map.put(info.getId(), info);
            }

            Iterator<Long> it = params_map.keySet().iterator();
            while (it.hasNext()) {
                Long key = it.next();
                if (!center_prod_map.containsKey(party_prod_map.get(key).getProduct_id())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品在商品中心不存在");
                }
                ProductInfo center_prod = center_prod_map.get(party_prod_map.get(key).getProduct_id());
                if (center_prod.getCost_price() == null || center_prod.getCost_price().compareTo(ZERO) <= 0) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品在商品中心的成本价丢失");
                }
                PartyUpdatePriceInventoryQuantityItemRequestVO param_item = params_map.get(key);

                //验证销售价、成本价，销售价格必须大于成本价
                if (param_item.getSale_price() != null && param_item.getSale_price().compareTo(center_prod.getCost_price()) <= 0) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品销售价格["
                            + param_item.getSale_price().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                            + "]必须大于成本价[" + center_prod.getCost_price().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "]");
                }

                //验证销售价、报关价，销售价格必须大于报关价
                if (ProductTypeCacheHandler.isCrossBorder(center_prod.getProduct_type_id())) {
                    CrossBorderProduct crossBorderProduct = crossBorderProductMapper.selectById(center_prod.getId());
                    if (crossBorderProduct == null) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "数据错误: 跨境数据丢失, id=" + center_prod.getId());
                    }

                    if (crossBorderProduct.getDeclare_price() == null || crossBorderProduct.getDeclare_price().doubleValue() <= 0d) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "数据错误: 报关价丢失, id=" + center_prod.getId());
                    }

                    if (param_item.getSale_price().compareTo(crossBorderProduct.getDeclare_price()) <= 0) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品销售价格["
                                + param_item.getSale_price().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                                + "]必须大于报关价[" + crossBorderProduct.getDeclare_price().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue() + "]");
                    }
                }

                if (UtilValidate.isNotEmpty(param_item.getIntroduction_date()) && center_prod.getIntroduction_date() != null) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(param_item.getIntroduction_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(center_prod.getIntroduction_date());
                    if (c1.before(c2)) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品推介销售日期["
                                + param_item.getIntroduction_date()
                                + "]不能早于产品中心的推介销售日期[" + DateUtil.formatDateTime(center_prod.getIntroduction_date()) + "]");
                    }
                }

                if (UtilValidate.isNotEmpty(param_item.getSales_end_date()) && center_prod.getSales_end_date() != null) {
                    Calendar c1 = Calendar.getInstance();
                    c1.setTime(DateUtil.parseDateTime(param_item.getSales_end_date()));
                    Calendar c2 = Calendar.getInstance();
                    c2.setTime(center_prod.getSales_end_date());
                    if (c1.after(c2)) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + key + "]的商品销售终止日期["
                                + param_item.getSales_end_date()
                                + "]不能晚于产品中心的销售终止日期[" + DateUtil.formatDateTime(center_prod.getSales_end_date()) + "]");
                    }
                }
            }
        }

        try {
            //如果是仓库 要同步更新这个仓库覆盖的所有门店的这些商品的销售价格
            Set<Long> warehouse_covered_store_id_set = new HashSet<>(20);
            Map<String, Object> maps = new HashMap<>(3);
            if (SysCodeEnmu.DEPTETYPE_02.getCodeValue().equals(party_type)) {
                //(invoke-api) 调用接口获取这个仓库覆盖的所有门店ID集合
                Result<List<Long>> res = PartyUtil.getCoveredParty(party_id_set.iterator().next(), partyFeignClient);
                if (res.isFailed()) {
                    return new Result(CodeEnum.FAIL_BUSINESS, res.getMsg());
                }
                if (UtilValidate.isNotEmpty(res.getData())) {
                    warehouse_covered_store_id_set.addAll(res.getData());
                    maps.put("party_id_list", warehouse_covered_store_id_set);
                    notify_party_id_set.addAll(res.getData());
                }
            }

            for (PartyProduct party_product : db_party_prod_list) {
                PartyUpdatePriceInventoryQuantityItemRequestVO item = params_map.get(party_product.getId());

                boolean is_cross_border = productService.isCrossBorderProduct(party_product.getProduct_id());

                //跨境商品将门店端库存强制设置为0， 此策略很重要，影响到用户端对商品搜索的结果
                if (is_cross_border) {
                    party_product.setInventory_quantity(0);
                } else if (item.getInventory_quantity() != null && item.getInventory_quantity().intValue() > 0) {
                    party_product.setInventory_quantity(item.getInventory_quantity());
                }

                if (UtilValidate.isNotEmpty(item.getIntroduction_date())) {
                    party_product.setIntroduction_date(DateUtil.parseDateTime(item.getIntroduction_date()));
                }
                if (UtilValidate.isNotEmpty(item.getSales_end_date())) {
                    party_product.setSales_end_date(DateUtil.parseDateTime(item.getSales_end_date()));
                }
                if (item.getSale_price() != null && item.getSale_price().compareTo(zero) > 0) {
                    party_product.setSale_price(item.getSale_price());
                    //如果是仓库同步覆盖门店商品的价格
                    if (SysCodeEnmu.DEPTETYPE_02.getCodeValue().equals(party_type) && UtilValidate.isNotEmpty(warehouse_covered_store_id_set)) {
                        //同步更新门店商品销售价格
                        maps.put("sales_price", item.getSale_price());
                        maps.put("product_id", party_product.getProduct_id());
                        super.baseMapper.updateStoreProdSalesPrice(maps);
                    }
                }
                party_product.updateById();

                if (!is_cross_border) {
                    inventory_cache_sync_list.add(new ProductInventorySummary(party_product.getProduct_id(), party_product.getParty_id(), PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_NEW_RETAIL.getPermission_id(), party_product.getInventory_quantity()));
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("保存修改门店仓库商品维护数据处理失败", e);
            }
            throw new BizException("保存修改门店仓库商品维护数据处理失败", e);
        } finally {
            //启动任务清除缓存数据
            ThreadPoolUtil.submitTask(new PartyProductUpdateTask(notify_party_id_set));
            //启动任务刷新商品缓存
            if (UtilValidate.isNotEmpty(inventory_cache_sync_list)) {
                ThreadPoolUtil.submitTask(() -> {
                    for (ProductInventorySummary summary : inventory_cache_sync_list) {
                        InventoryCacheUtil.addCache(summary);
                    }
                });
            }
        }

    }

    /**
     * @author 王光银
     * @methodName: updateOnePartyProduct
     * @methodDesc: 更新维护一个门店或仓库的销售商品（销售价格和库存量）
     * @description: 门店没有设置销售价格的权限，即使传递也不会进行处理
     * @param: [valueRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:21
     **/
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result updateOnePartyProduct(PartyUpdatePriceInventoryQuantityItemRequestVO valueRequestVO) throws ServerException {
        PartyUpdatePriceInventoryQuantityRequestVO requestVO = new PartyUpdatePriceInventoryQuantityRequestVO();
        requestVO.setMany_item(UtilMisc.toList(valueRequestVO));
        return updateManyPartyProduct(requestVO);
    }

    /**
     * @author 王光银
     * @methodName: upPartyProduct
     * @methodDesc: 上架指定的仓库或门店商品
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:21
     **/
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result upPartyProduct(@RequestBody IdRequestVO idRequestVO) throws ServerException {
        if (idRequestVO == null || idRequestVO.getId() == null || idRequestVO.getId() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店或仓库商品ID不能是空值");
        }
        Long party_id = null;
        try {
            PartyProduct partyProduct = super.selectById(idRequestVO.getId());
            if (partyProduct == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + idRequestVO.getId() + "]门店或仓库商品不存在");
            }
            if (partyProduct.getSale_price() == null || partyProduct.getSale_price().compareTo(BigDecimal.ZERO) <= 0) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品销售价格未设置不能上架销售");
            }

            //加载商品类型
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("id", partyProduct.getProduct_id());
            wrapper.setSqlSelect("id", "product_type_id");
            ProductInfo prod = productService.selectOne(wrapper);
            if (prod == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品已不存在,ID=" + partyProduct.getProduct_id());
            }
            if (UtilValidate.isEmpty(prod.getProduct_type_id())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品类型数据丢失,ID=" + partyProduct.getProduct_id());
            }
            if (ProductTypeCacheHandler.isCrossBorder(prod.getProduct_type_id())) {
                CrossBorderProduct crossBorderProduct = productService.getCrossBorderProductMapper().selectById(prod.getId());
                if (crossBorderProduct == null) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "跨境商品数据丢失,ID=" + partyProduct.getProduct_id());
                }
                if (crossBorderProduct.getInventory_quantity() == null || crossBorderProduct.getInventory_quantity().intValue() <= 0) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "跨境商品库存不足,不能上架销售");
                }
            } else if (partyProduct.getInventory_quantity() == null || partyProduct.getInventory_quantity().intValue() <= 0) {
                return new Result(CodeEnum.FAIL_BUSINESS, "商品库存未设置不能上架销售");
            }

            partyProduct.setUp_down_status(ProductUtil.UP_STATUS);
            partyProduct.setUpdate_time(new Date());
            partyProduct.updateById();
            party_id = partyProduct.getParty_id();
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("上架门店或仓库指定商品处理失败", e);
            throw new ServerException("上架门店或仓库指定商品处理失败", e);
        } finally {
            //清除缓存
            RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(party_id);
            RedisCacheUtil.ProductSearchCacheUtil.cleanByPartyId(party_id);
            RedisCacheUtil.PartyProductStatisticsCacheUtil.cleanByPartyId(party_id);
        }
    }

    /**
     * @author 王光银
     * @methodName: downPartyProduct
     * @methodDesc: 下架指定仓库或门店商品
     * @description:
     * @param: [idRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-06-27 20:20
     **/
    @SuppressWarnings("unchecked")
    @Transactional(rollbackFor = {ServerException.class, BizException.class, RuntimeException.class})
    public Result downPartyProduct(@RequestBody IdRequestVO idRequestVO) throws ServerException {
        if (idRequestVO == null || idRequestVO.getId() == null || idRequestVO.getId() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店或仓库商品ID不能是空值");
        }
        Long party_id = null;
        try {
            PartyProduct partyProduct = super.selectById(idRequestVO.getId());
            if (partyProduct == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "ID为:[" + idRequestVO.getId() + "]门店或仓库商品不存在");
            }
            partyProduct.setUp_down_status(ProductUtil.DOWN_STATUS);
            partyProduct.setUpdate_time(new Date());
            partyProduct.updateById();
            party_id = partyProduct.getParty_id();
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("下架门店或仓库指定商品处理失败", e);
            throw new ServerException("下架门店或仓库指定商品处理失败", e);
        } finally {
            //清除缓存
            RedisCacheUtil.PartyRecommendProdCacheUtil.cleanByPartyId(party_id);
            RedisCacheUtil.ProductSearchCacheUtil.cleanByPartyId(party_id);
            RedisCacheUtil.PartyProductStatisticsCacheUtil.cleanByPartyId(party_id);
        }
    }

    /**
     * @author 王光银
     * @methodName: partyProdStatistics
     * @methodDesc: 门店或仓库商品简单统计接口
     * @description:
     * @param: [partyProductStatisticsRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.product.partyprod.response.PartyProductStatisticsItem>>
     * @create 2018-07-05 21:13
     **/
    @SuppressWarnings("unchecked")
    public Result<ListResponseVO<PartyProductStatisticsItem>> partyProdSimpleStatistics(PartyProductStatisticsRequestVO requestVO) {
        if (UtilValidate.isEmpty(requestVO.getParty_id_list())) {
            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
        }
        Map<Long, PartyProductStatisticsItem> map = new HashMap<>(requestVO.getParty_id_list().size());
        for (Long party_id : requestVO.getParty_id_list()) {
            PartyProductStatisticsItem item = new PartyProductStatisticsItem();
            item.setParty_id(party_id);
            map.put(party_id, item);
        }
        Map<String, Object> params_map = new HashMap<>(2);
        params_map.put("party_id_list", requestVO.getParty_id_list());
        if (requestVO.getReturn_all()) {
            List<PartyProductStatisticsItem> list = super.baseMapper.selectPartyProdCount(params_map);
            if (UtilValidate.isNotEmpty(list)) {
                for (PartyProductStatisticsItem item : list) {
                    map.get(item.getParty_id()).setAll_num(item.getAll_num());
                }
            }
        }
        if (requestVO.getReturn_up()) {
            params_map.put("up_down_status", ProductUtil.UP_STATUS);
            List<PartyProductStatisticsItem> list = super.baseMapper.selectPartyProdCount(params_map);
            if (UtilValidate.isNotEmpty(list)) {
                for (PartyProductStatisticsItem item : list) {
                    map.get(item.getParty_id()).setUp_num(item.getAll_num());
                }
            }
        }
        if (requestVO.getReturn_down()) {
            if (!(requestVO.getReturn_all() && requestVO.getReturn_up())) {
                params_map.put("up_down_status", ProductUtil.DOWN_STATUS);
                List<PartyProductStatisticsItem> list = super.baseMapper.selectPartyProdCount(params_map);
                if (UtilValidate.isNotEmpty(list)) {
                    for (PartyProductStatisticsItem item : list) {
                        map.get(item.getParty_id()).setDown_num(item.getAll_num());
                    }
                }
            } else {
                for (PartyProductStatisticsItem item : map.values()) {
                    item.setDown_num(item.getAll_num() - item.getUp_num());
                }
            }

        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(UtilMisc.toList(map.values())));
    }
}
