package com.hryj.service.app.v1_2;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.cache.util.CalculateTaxUtil;
import com.hryj.common.BizCodeEnum;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.constant.CommonConstantPool;
import com.hryj.constant.DataDictionaryGroup;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.product.TaxRate;
import com.hryj.entity.bo.product.crossborder.CrossBorderProduct;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductValidateRequestVO;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.response.CrossBorderSimpleInfoResponseVO;
import com.hryj.entity.vo.product.common.response.ProductValidateResponseItem;
import com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO;
import com.hryj.entity.vo.product.request.app.AppSearchProductRequestVO;
import com.hryj.entity.vo.product.response.ProdPromResponseVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO;
import com.hryj.entity.vo.promotion.activity.request.ActivityIdProductIdRequestVO;
import com.hryj.entity.vo.promotion.activity.request.common.CommonProdCheckRequestVO;
import com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO;
import com.hryj.entity.vo.promotion.activity.response.common.CommonProdCheckResponseVO;
import com.hryj.entity.vo.promotion.activity.response.common.ProdCheckItem;
import com.hryj.entity.vo.promotion.recommend.request.app.PartyRecommendProdSearchRequestVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.feign.PartyFeignClient;
import com.hryj.feign.PromotionFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.*;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.service.PartyProductUtilService;
import com.hryj.service.ProductCommonService;
import com.hryj.service.inventory.cache.InventoryCacheUtil;
import com.hryj.service.inventory.cache.ProductInventorySummary;
import com.hryj.service.prodcate.ProductCategoryUtilService;
import com.hryj.service.util.*;
import com.hryj.service.worktask.SetProductSearchResultCacheTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 王光银
 * @className: AppPartyProductService
 * @description: APP端商品接口 v1.2 实现
 * @create 2018/7/9 0009 22:59
 **/
@Slf4j
@Service("v1.2-AppPartyProductService")
public class AppPartyProductService extends ServiceImpl<PartyProductMapper, PartyProduct> {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private PartyRecommendProductMapper partyRecommendProductMapper;

    @Autowired
    private ProductCategoryUtilService productCategoryUtilService;

    @Autowired
    private PartyProductMapper partyProductMapper;

    @Autowired
    private PartyProductUtilService partyProductUtilService;

    @Autowired
    private ProductCommonService productCommonService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private PartyFeignClient partyFeignClient;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Autowired
    private PromotionFeignClient promotionFeignClient;

    @Autowired
    private CrossBorderProductMapper crossBorderProductMapper;

    @Autowired
    private TaxRateMapper taxRateMapper;

    /**
     * @author 王光银
     * @methodName: findRecommendProductList
     * @methodDesc: App端查询推荐位商品列表
     * @description: 推荐商品返回列表不分页，商品返回数量的多少，排序都在接口内控制
     * @param: [requestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @create 2018-06-28 20:12
     **/
    public Result<ListResponseVO<AppProdListItemResponseVO>> findRecommendProductList(PartyRecommendProdSearchRequestVO requestVO) {
        /**
         * 1、获取当前请求用户的对应门店
         * 2、检查缓存中是否有对应门店的推荐位，如果有直接返回，没有则从数据库加载同时放入缓存
         * 3、返回数据
         */

        //步骤1
        Long target_party_id = requestVO.getParty_id();
        if (target_party_id == null || target_party_id <= 0L) {
            //步骤1
            Result<Long> result = UserCoveredByPartyUtil.getCoveredUserUniqueParty(null, requestVO.getLogin_token(), userFeignClient, partyProductUtilService);
            if (result.isFailed()) {
                log.error("商品分类搜索接口 - 获取用户默认门店失败:" + result.getMsg());
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            if (result.getData() == null || result.getData() <= 0L) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            target_party_id = result.getData();
            requestVO.setParty_id(target_party_id);
        }

        //步骤2  检查缓存中是否有数据，如果有数据，直接返回
        List<AppProdListItemResponseVO> list = RedisCacheUtil.PartyRecommendProdCacheUtil.getCacheData(target_party_id);
        if (UtilValidate.isNotEmpty(list)) {
            //检查库存
            boolean return_cache_data = true;
            for (AppProdListItemResponseVO vo : list) {
                boolean inventory_check = InventoryCacheUtil.check(new ProductInventorySummary(vo.getProduct_id(), vo.getParty_id(), vo.getProduct_type_id(), null));
                if (!inventory_check) {
                    return_cache_data = false;
                    break;
                }
            }
            if (return_cache_data) {
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(list));
            }
        }

        //从数据库加载数据
        //组装查询条件
        Map<String, Object> condition = ProductSearchConfigCacheUtil.generateSearchCondition();
        condition.put("party_id", target_party_id);
        condition.put("limit_start", 0);
        condition.put("limit_end", AppProductUtil.getAppRecommendProdReturnLimit());

        //设置商品类型查询条件
        if (UtilValidate.isNotEmpty(requestVO.getProduct_type_id())) {
            if (!CommonConstantPool.STR_ALL.equals(requestVO.getProduct_type_id())) {
                condition.put("product_type_id", requestVO.getProduct_type_id());
            }
        }

        try {
            //获取推荐位商品返回数量配置
            list = partyRecommendProductMapper.selectManyPartyRecommendProd(condition);
        } catch (Exception e) {
            log.error("APP端加载推荐位商品数据失败", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "加载推荐位商品数据失败");
        }

        //设置商品类型打头标识
        if (UtilValidate.isNotEmpty(list)) {
            for (AppProdListItemResponseVO itemResponseVO : list) {
                itemResponseVO.setTitle_mark_image_list(ProductTypeCacheHandler.getProductTypeTitleMark(itemResponseVO.getProduct_type_id()));
            }
        }

        //将数据加入缓存
        final Long cache_party_id = target_party_id;
        final List<AppProdListItemResponseVO> cache_list = list;
        ThreadPoolUtil.submitTask(() -> {
            RedisCacheUtil.PartyRecommendProdCacheUtil.setCacheData(cache_party_id, cache_list);
        });

        //步骤3 （数据库已排序）
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(list));
    }

    /**
     * @author 王光银
     * @methodName: searchProductList
     * @methodDesc: App端商品搜素
     * @description:
     * @param: [appSearchProductRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @create 2018-06-30 9:33
     **/
    public Result<ListResponseVO<AppProdListItemResponseVO>> searchProduct(AppSearchProductRequestVO requestVO) {
        /**
         * 1、验证请求参数，主要是分页参数
         * 2、处理搜索中的商品分类参数
         * 3、计算获取当前请求用户应该搜索的门店
         * 4、加载商品数据
         * 5、返回
         */

        //步骤1
        requestVO.checkPageNum(AppProductUtil.APP_PROD_SEARCH_DEFAULT_PAGE_INDEX);
        requestVO.checkPageSize(AppProductUtil.APP_PROD_SEARCH_DEFAULT_PAGE_SIZE, AppProductUtil.APP_PROD_SEARCH_MAX_PAGE_SIZE);
        Integer limit = AppProductUtil.getAppProdSearchLimit();
        if (requestVO.getPage_num() * requestVO.getPage_size() > limit) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        //步骤2   当根据商品分类查询商品时，必须要保证商品分类为最末级分类
        Set<Long> cate_set = productCategoryUtilService.getLastProdCate(requestVO.getCategory_id(), true);

        //步骤3
        Long target_party_id = requestVO.getParty_id();
        if (target_party_id == null || target_party_id <= 0L) {
            //步骤1
            Result<Long> result = UserCoveredByPartyUtil.getCoveredUserUniqueParty(requestVO.getUser_id(), requestVO.getLogin_token(), userFeignClient, partyProductUtilService);
            if (result.isFailed()) {
                log.error("商品搜索接口 - 获取用户默认门店失败:" + result.getMsg());
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            if (result.getData() == null || result.getData() <= 0L) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            target_party_id = result.getData();
            requestVO.setParty_id(target_party_id);
        }

        //步骤4
        /**
         * 4.1、首先检查目标门店的商品搜索是否有缓存
         * 4.2、如果有缓存直接返回数据
         * 4.3、没有缓存则加载数据
         * 4.4、将加载数据同步到缓存服务
         * 4.5、返回数据
         */

        //步骤 4.1
        String cache_key = calculateCacheKey(requestVO, cate_set);
        List<AppProdListItemResponseVO> prod_list = RedisCacheUtil.ProductSearchCacheUtil.getCacheData(requestVO.getParty_id(), cache_key);

        //步骤 4.2
        if (prod_list != null) {
            // 有缓存数据的情况下，检查库存，如果有任意一个商品的库存为0，当前缓存数据则判定为失效
            boolean inventory_check = true;

            for (AppProdListItemResponseVO item : prod_list) {
                inventory_check = InventoryCacheUtil.check(new ProductInventorySummary(item.getProduct_id(), item.getParty_id(), item.getProduct_type_id(), null));
                if (!inventory_check) {
                    break;
                }
            }

            if (inventory_check) {
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(prod_list));
            }
        }

        //步骤 4.3 活动商品与普通商品一起查询
        //组装查询条件
        Page pageCondition = new Page(requestVO.getPage_num(), requestVO.getPage_size());

        Map<String, Object> condition = ProductSearchConfigCacheUtil.generateSearchCondition();

        if (UtilValidate.isNotEmpty(requestVO.getProduct_type_id()) && !CommonConstantPool.STR_ALL.equals(requestVO.getProduct_type_id())) {
            condition.put("product_type_id", requestVO.getProduct_type_id());
        }
        condition.put("party_id", target_party_id);
        if (UtilValidate.isNotEmpty(cate_set)) {
            if (cate_set.size() > 1) {
                condition.put("cate_id_set", cate_set);
            } else {
                condition.put("cate_id", cate_set.iterator().next());
            }
        }
        if (UtilValidate.isNotEmpty(requestVO.getCategory_name())) {
            condition.put("cate_name", requestVO.getCategory_name().trim());
        }

        if (UtilValidate.isNotEmpty(requestVO.getSearch_key())) {
            condition.put("prod_name", requestVO.getSearch_key().trim());
        }

        List<AppProdListItemResponseVO> page_list = partyProductMapper.pageFindPartyActivityAndNormalProduct(condition, pageCondition);

        //设置商品类型打头标记
        if(UtilValidate.isNotEmpty(page_list)){
            for (AppProdListItemResponseVO appProdListItemResponseVO : page_list) {
                appProdListItemResponseVO.setTitle_mark_image_list(ProductTypeCacheHandler.getProductTypeTitleMark(appProdListItemResponseVO.getProduct_type_id()));
            }
        } else {
            page_list = new LinkedList<>();
        }

        //步骤 4.5
        ThreadPoolUtil.submitTask(new SetProductSearchResultCacheTask(target_party_id, cache_key, page_list));

        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(page_list));
    }

    /**
     * @author 王光银
     * @methodName: getProductInfo
     * @methodDesc: APP端获取商品的详细信息
     * @description: 商品下架返回 biz_code=11000,  活动结束返回 biz_code=11010，库存数量不足返回 biz_code=11020，这三个业务上的非正常情况编码不会影响其他数据的正常返回
     * @param: [productValidateRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO>
     * @create 2018-06-30 18:29
     **/
    public Result<AppProductInfoResponseVO> getProductInfo(ProductValidateRequestVO requestVO) {
        if (requestVO.getParty_id() == null || requestVO.getParty_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "门店或仓库ID不能是空值");
        }
        if (requestVO.getProduct_id() == null || requestVO.getProduct_id() <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "商品ID不能是空值");
        }
        UserCoveredByPartyUtil.UserCoveredPartyHandler userPartyHandler = new UserCoveredByPartyUtil.UserCoveredPartyHandler();
        userPartyHandler.default_party = new UserPartyVO();
        userPartyHandler.default_party.setParty_id(requestVO.getParty_id());
        return getProductInfo(requestVO, userPartyHandler);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO>
     * @author 王光银
     * @methodName: getProductInfo
     * @methodDesc: APP端获取商品的详细信息
     * @description: 商品下架返回 biz_code=11000,  活动结束返回 biz_code=11010，库存数量不足返回 biz_code=11020，这三个业务上的非正常情况编码不会影响其他数据的正常返回
     * @param: [productValidateRequestVO]
     * @create 2018-06-30 18:29
     **/
    public Result<AppProductInfoResponseVO> getProductInfo(ProductValidateRequestVO validateRequestVO, UserCoveredByPartyUtil.UserCoveredPartyHandler userPartyHandler) {
        //首先对商品进行验证
        ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
        ProductValidateItem validateItem = new ProductValidateItem();
        validateItem.setParty_id(validateRequestVO.getParty_id());
        validateItem.setProduct_id(validateRequestVO.getProduct_id());
        validateItem.setActivity_id(validateRequestVO.getActivity_id());
        productsValidateRequestVO.setProd_summary_list(UtilMisc.toList(validateItem));

        Result<ProductsValidateResponseVO> validate_result;
        try {
            validate_result = productCommonService.productsValidate(productsValidateRequestVO);
            if (validate_result.isFailed()
                    || UtilValidate.isEmpty(validate_result.getData().getProd_validate_result_list())) {
                log.error("APP端获取商品详情：调用商品验证公共服务返回结果失败, 返回值为:" + JSON.toJSONString(validate_result));
                return new Result(CodeEnum.FAIL_BUSINESS, validate_result.getMsg());
            }
            if (!validate_result.getData().isValidatePassed()) {
                Result result = new Result();
                result.setCode(CodeEnum.FAIL_BUSINESS.getCode());
                result.setBiz_code(validate_result.getBiz_code());
                result.setMsg(validate_result.getData().getFailedMsg());
                return result;
            }
        } catch (Exception e) {
            log.error("APP端获取商品详情：调用商品验证公共服务出现异常:", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "老铁！不好意思异常啦！不要慌张，日志已经记起啦!");
        }

        Result<AppProductInfoResponseVO> return_result = new Result<>(CodeEnum.SUCCESS, new AppProductInfoResponseVO());

        ProductValidateResponseItem validateResponse = validate_result.getData().getProd_validate_result_list().get(0);

        if (!validateResponse.getIs_valid()) {
            return_result.setBiz_code(validateResponse.getValidate_status_code());
            return_result.setCode(CodeEnum.FAIL_BUSINESS.getCode());
            return_result.setMsg(validateResponse.getOther_comments());
        }

        //加载商品的详细信息
        ProductInfo productInfo = productMapper.selectById(validateRequestVO.getProduct_id());
        if (productInfo == null) {
            return_result.setBiz_code(BizCodeEnum.PRODUCT_NOT_EXISTS.getCode());
            return_result.setCode(CodeEnum.FAIL_BUSINESS.getCode());
            return_result.setMsg("商品已下架");
            return return_result;
        }

        //返回的商品详情对象
        AppProductInfoResponseVO return_vo = productInfo.convertToAppResponse(ProductUtil.PROD_TYPE_NAME_GETTER, ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER, ProductUtil.PROD_LONG_DESC_GETTER);
        return_vo.setParty_id(validateRequestVO.getParty_id());
        return_vo.setSale_price(validateResponse.getNormal_price() == null ? "0" : validateResponse.getNormal_price().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        return_vo.setInventory_quantity(validateResponse.getInventory_quantity() == null ? "0" : validateResponse.getInventory_quantity().toString());
        return_vo.setTitle_mark_image_list(ProductTypeCacheHandler.getProductTypeTitleMark(productInfo.getProduct_type_id()));
        //加载跨境商品信息
        if(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_BONDED.getPermission_id().equals(productInfo.getProduct_type_id())){
            CrossBorderProduct crossBorderProduct = crossBorderProductMapper.selectById(productInfo.getId());
            return_vo.setInventory_quantity(crossBorderProduct.getInventory_quantity() == null ? "0" : crossBorderProduct.getInventory_quantity().toString());
            CrossBorderSimpleInfoResponseVO crossBorderSimpleInfoResponseVO = new CrossBorderSimpleInfoResponseVO();
            crossBorderSimpleInfoResponseVO.setChannel_name(CodeCache.getNameByKey(DataDictionaryGroup.CrossBorderProductDeliveryWarehouse, CommonConstantPool.S_ZERO_ONE));
            if(null != return_vo.getMade_where()){
                crossBorderSimpleInfoResponseVO.setGeo_name(return_vo.getMade_where().getMade_where());
                crossBorderSimpleInfoResponseVO.setLogo_image(return_vo.getMade_where().getLogo_image());
            }
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("hs_code",crossBorderProduct.getHs_code());
            wrapper.setSqlSelect("consume_tax","increment_tax");
            List<TaxRate> taxRate = taxRateMapper.selectList(wrapper);
            //计算税费
            BigDecimal tax_money = CalculateTaxUtil.calculateTax(new BigDecimal(return_vo.getSale_price()), 1, crossBorderProduct.getUnit_1(), crossBorderProduct.getUnit_2(), taxRate.get(0).getIncrement_tax(), taxRate.get(0).getConsume_tax());
            crossBorderSimpleInfoResponseVO.setTax_money(NumberUtil.roundHalfEven(tax_money, 2).toString());
            return_vo.setCrossBorderSimpleInfoResponseVO(crossBorderSimpleInfoResponseVO);
        }

        //加载商品的配送信息
        try {
            ProductUtil.loadProdDistributionInfo(return_vo, partyFeignClient);
        } catch (Exception e) {
            //加载失败记录日志，不影响正常返回
            log.error("APP端获取商品的详细信息 - 加载商品的配送信息", e);
        }

        //加载商品属性
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("product_id", productInfo.getId());
        wrapper.setSqlSelect("attr_name", "attr_value");
        wrapper.orderBy("attr_type", true);
        wrapper.orderBy("id", true);
        List<Map<String, Object>> prod_attr_list = productAttributeMapper.selectMaps(wrapper);
        if (UtilValidate.isNotEmpty(prod_attr_list)) {
            ProductUtil.copyProdAttr(return_vo, prod_attr_list);
        }

        //调用接口获取当前商品的促销活动信息
        if (validateRequestVO.getActivity_id() != null && validateRequestVO.getActivity_id() > 0L) {
            ActivityIdProductIdRequestVO checkPromotionProd = new ActivityIdProductIdRequestVO();
            checkPromotionProd.setActivity_id(validateRequestVO.getActivity_id());
            checkPromotionProd.setProduct_id(validateRequestVO.getProduct_id());

            try {
                Result<ListResponseVO<ActivityInProgressProductItemResponseVO>> checkPromotionProdResult = promotionFeignClient.checkPromotionProd(UtilMisc.toList(checkPromotionProd));
                if (checkPromotionProdResult.isSuccess()
                        && checkPromotionProdResult.getData() != null
                        && UtilValidate.isNotEmpty(checkPromotionProdResult.getData().getRecords())) {
                    ActivityInProgressProductItemResponseVO checkResponseVO = checkPromotionProdResult.getData().getRecords().get(0);
                    if (ProductUtil.UP_STATUS.equals(checkResponseVO.getActivity_status())) {
                        ProdPromResponseVO current_promotion = new ProdPromResponseVO();
                        return_vo.setCurrent_prom_ifn(current_promotion);
                        current_promotion.setActivity_id(checkResponseVO.getActivity_id() == null || checkResponseVO.getActivity_id() <= 0L ? null : checkResponseVO.getActivity_id().toString());
                        current_promotion.setActivity_name(checkResponseVO.getActivity_name());
                        current_promotion.setActivity_mark_image(checkResponseVO.getActivity_mark_image());
                        current_promotion.setActivity_type(checkResponseVO.getActivity_type());
                        current_promotion.setActivity_type_name(ActivityInfo.getActivityTypeName(checkResponseVO.getActivity_type()));

                        current_promotion.setStart_date(checkResponseVO.getStart_date() == null ? "" : DateUtil.format(checkResponseVO.getStart_date(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
                        current_promotion.setEnd_date(checkResponseVO.getEnd_date() == null ? "" : DateUtil.format(checkResponseVO.getEnd_date(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
                        current_promotion.setPromotion_price(checkResponseVO.getPromotion_price().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

                        //计算出优惠价金额
                        BigDecimal old_price = new BigDecimal(return_vo.getSale_price());
                        BigDecimal promotion_price = new BigDecimal(current_promotion.getPromotion_price());
                        BigDecimal discount_num = old_price.subtract(promotion_price).setScale(2, BigDecimal.ROUND_HALF_UP);
                        Map<String, Object> context = UtilMisc.toMap(PromotionActivityUtil.BK.PARAMETER_NAME_DISCOUNT, discount_num);
                        context.put(PromotionActivityUtil.BK.PARAMETER_NAME_DISCOUNT, discount_num);
                        current_promotion.setActivity_model_desc(PromotionActivityUtil.expandActivityTempleteString(current_promotion.getActivity_type(), context));

                        current_promotion.setActivity_page_url(checkResponseVO.getActivity_page_url());

                        if(PermissionManageHandler.PermissionSupport.PRODUCT_TYPE_BONDED.getPermission_id().equals(productInfo.getProduct_type_id())){
                            CrossBorderProduct crossBorderProduct = crossBorderProductMapper.selectById(productInfo.getId());
                            EntityWrapper taxRateWrapper = new EntityWrapper();
                            wrapper.eq("hs_code",crossBorderProduct.getHs_code());
                            wrapper.setSqlSelect("consume_tax","increment_tax");
                            List<TaxRate> taxRate = taxRateMapper.selectList(taxRateWrapper);
                            //计算税费
                            BigDecimal tax_money = CalculateTaxUtil.calculateTax(new BigDecimal(current_promotion.getPromotion_price()), 1, crossBorderProduct.getUnit_1(), crossBorderProduct.getUnit_2(), taxRate.get(0).getIncrement_tax(), taxRate.get(0).getConsume_tax());
                            return_vo.getCrossBorderSimpleInfoResponseVO().setTax_money(tax_money.toString());
                        }
                    }
                }
            } catch (Exception e) {
                log.error("APP端获取商品详细信息时，调用促销活动接口获取当前商品的促销活动信息异常", e);
            }
        }

        //获取覆盖用户的门店和仓库
        if (userPartyHandler == null) {
            Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> result = UserCoveredByPartyUtil.calculateAndGetUserCoveredParty(validateRequestVO.getLogin_token(), partyFeignClient, false);
            if (result.isFailed()) {
                log.error("APP端获取商品的详细信息 - 获取覆盖用户的门店和仓库失败:" + result.getMsg());
                return_result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
                return_result.setCode(CodeEnum.FAIL_BUSINESS.getCode());
                return_result.setMsg("没有可服务的小店");
                return return_result;
            }
            userPartyHandler = result.getData();
        }

        //调用接口获取当前商品的所有活动信息, 活动的返回取决于用户的覆盖门店与仓库，如果没有获取到用户的覆盖门店与仓库则不进行活动接口的调用
        if (userPartyHandler != null && userPartyHandler.hasParty()) {
            Set<Long> user_party_id_set = new LinkedHashSet<>();
            if (UtilValidate.isNotEmpty(userPartyHandler.store)) {
                for (UserPartyVO userPartyVO : userPartyHandler.store) {
                    if (userPartyVO.getParty_id() != null && userPartyVO.getParty_id() > 0L) {
                        user_party_id_set.add(userPartyVO.getParty_id());
                    }
                }
            }
            if (userPartyHandler.warehouse != null) {
                Long party_id = userPartyHandler.warehouse.getParty_id();
                if (party_id != null && party_id > 0L) {
                    user_party_id_set.add(party_id);
                }
            }
            if (userPartyHandler.default_party != null) {
                Long party_id = userPartyHandler.default_party.getParty_id();
                if (party_id != null && party_id > 0L) {
                    user_party_id_set.add(party_id);
                }
            }

            if (UtilValidate.isNotEmpty(user_party_id_set)) {
                try {
                    CommonProdCheckRequestVO commonProdCheckRequestVO = new CommonProdCheckRequestVO();
                    commonProdCheckRequestVO.setProduct_id_list(UtilMisc.toList(validateRequestVO.getProduct_id()));
                    Result<CommonProdCheckResponseVO> commonProdCheckResponseVO = promotionFeignClient.checkProduct(commonProdCheckRequestVO);
                    if (commonProdCheckResponseVO.isSuccess() && commonProdCheckResponseVO.getData() != null && UtilValidate.isNotEmpty(commonProdCheckResponseVO.getData().getCheck_result())) {
                        CommonProdCheckResponseVO prodCheckResponseVO = commonProdCheckResponseVO.getData();
                        List<ProdCheckItem> activity_list = prodCheckResponseVO.getCheck_result().get(validateRequestVO.getProduct_id());
                        if (UtilValidate.isNotEmpty(activity_list)) {
                            //根据当前请求的APP用户的覆盖门店与仓库实际情况 对这些活动进行选择
                            Iterator<ProdCheckItem> it = activity_list.iterator();
                            while (it.hasNext()) {
                                ProdCheckItem prodCheckItem = it.next();
                                boolean matched = false;
                                for (Long party_id : user_party_id_set) {
                                    if (UtilValidate.isNotEmpty(prodCheckItem.getScope_party()) && prodCheckItem.getScope_party().contains(party_id)) {
                                        matched = true;
                                        break;
                                    }
                                }
                                if (!matched) {
                                    it.remove();
                                }
                            }
                        }

                        if (UtilValidate.isNotEmpty(activity_list)) {
                            List<ProdPromResponseVO> prod_prom_info_list = new ArrayList<>(activity_list.size());
                            return_vo.setProd_prom_info_list(prod_prom_info_list);

                            for (ProdCheckItem checkItem : activity_list) {
                                ProdPromResponseVO prod_activity_response_item = new ProdPromResponseVO();
                                prod_prom_info_list.add(prod_activity_response_item);
                                prod_activity_response_item.setActivity_id(checkItem.getActivity_id() == null || checkItem.getActivity_id() <= 0L ? null : checkItem.getActivity_id().toString());
                                prod_activity_response_item.setActivity_name(checkItem.getActivity_name());
                                prod_activity_response_item.setActivity_mark_image(checkItem.getActivity_mark_image());
                                prod_activity_response_item.setActivity_type(checkItem.getActivity_type());
                                prod_activity_response_item.setActivity_type_name(ActivityInfo.getActivityTypeName(checkItem.getActivity_type()));
                                prod_activity_response_item.setStart_date(checkItem.getStart_date() == null ? "" : DateUtil.format(checkItem.getStart_date(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
                                prod_activity_response_item.setEnd_date(checkItem.getEnd_date() == null ? "" : DateUtil.format(checkItem.getEnd_date(), DatePattern.NORM_DATETIME_MINUTE_PATTERN));
                                prod_activity_response_item.setPromotion_price(checkItem.getActivity_price().setScale(2, BigDecimal.ROUND_HALF_UP).toString());

                                //计算出优惠价金额
                                BigDecimal old_price = new BigDecimal(return_vo.getSale_price());
                                BigDecimal promotion_price = new BigDecimal(prod_activity_response_item.getPromotion_price());
                                BigDecimal discount_num = old_price.subtract(promotion_price).setScale(2, BigDecimal.ROUND_HALF_UP);
                                Map<String, Object> context = UtilMisc.toMap("discount_num", discount_num);
                                prod_activity_response_item.setActivity_model_desc(PromotionActivityUtil.expandActivityTempleteString(prod_activity_response_item.getActivity_type(), context));

                                prod_activity_response_item.setActivity_page_url(checkItem.getTemplete_data() + "?activity_id=" + checkItem.getActivity_id());
                            }
                        }
                    }
                } catch (Exception e) {
                    //异常时只记日志，其他数据正常返回
                    log.error("APP商品获取商品详细信息时，调用促销活动接口获取当前商品的所有活动信息异常", e);
                }
            }
        }
        log.info("APP商品获取商品详细信息result:"+JSON.toJSONString(return_vo));
        return_result.setData(return_vo);
        return return_result;
    }

    /**
     * 计算商品搜索请求参数的缓存键
     * @param requestVO
     * @param cate_set
     * @return
     */
    private String calculateCacheKey(AppSearchProductRequestVO requestVO, Set<Long> cate_set) {
        if (UtilValidate.isEmpty(cate_set)) {
            return String.valueOf(requestVO.hashCode());
        }
        int hashCodeSum = 0;
        for (Long id : cate_set) {
            hashCodeSum += id.hashCode();
        }
        return String.valueOf(hashCodeSum + requestVO.hashCode());
    }
}
