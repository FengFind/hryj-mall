package com.hryj.service.app.v1_0;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.BizCodeEnum;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductValidateRequestVO;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
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
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.feign.PartyFeignClient;
import com.hryj.feign.PromotionFeignClient;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.mapper.PartyRecommendProductMapper;
import com.hryj.mapper.ProductAttributeMapper;
import com.hryj.mapper.ProductMapper;
import com.hryj.service.ProductCommonService;
import com.hryj.service.prodcate.ProductCategoryUtilService;
import com.hryj.service.util.*;
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
 * @description: APP端商品接口 v1.0 实现
 * @create 2018/7/9 0009 22:59
 **/
@Slf4j
@Service("v1.0-AppPartyProductService")
public class AppPartyProductService extends ServiceImpl<PartyProductMapper, PartyProduct> {

    @Autowired
    private PartyRecommendProductMapper partyRecommendProductMapper;

    @Autowired
    private ProductCommonService productCommonService;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductAttributeMapper productAttributeMapper;

    @Autowired
    private PromotionFeignClient promotionFeignClient;

    @Autowired
    private PartyFeignClient partyFeignClient;

    @Autowired
    private ProductCategoryUtilService productCategoryUtilService;

    /**
     * @return com.hryj.common.Result<java.util.List   <   com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @author 王光银
     * @methodName: findRecommendProductList
     * @methodDesc: App端查询推荐位商品列表
     * @description: 推荐商品返回列表不分页，商品返回数量的多少，排序都在接口内控制
     * @param: [requestVO]
     * @create 2018-06-28 20:12
     **/
    public Result<ListResponseVO<AppProdListItemResponseVO>> findRecommendProductList(RequestVO requestVO) {
        //获取用户登陆信息
        UserLoginVO user_login = LoginCache.getUserLoginVO(requestVO.getLogin_token());
        if (user_login == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取用户登陆信息失败, token=" + requestVO.getLogin_token());
        }

        //获得用户关联的门店与仓库
        boolean force_refresh = true;
        Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> getCoveredPartyResult = UserCoveredByPartyUtil.calculateAndGetUserCoveredParty(requestVO.getLogin_token(), partyFeignClient, force_refresh);

        if (getCoveredPartyResult.isFailed()) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, getCoveredPartyResult.getMsg());
        }
        if (UtilValidate.isEmpty(getCoveredPartyResult.getData().store) && getCoveredPartyResult.getData().warehouse == null) {
            Result result = new Result(CodeEnum.FAIL_BUSINESS);
            result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
            result.setMsg("没有覆盖用户的门店和仓库");
            return result;
        }

        List<UserPartyVO> storeList = getCoveredPartyResult.getData().store;
        UserPartyVO warehouse = getCoveredPartyResult.getData().warehouse;

        List<Long> party_id_list = new ArrayList<>((warehouse == null ? 0 : 1) + (storeList == null ? 0 : storeList.size()));
        if (UtilValidate.isNotEmpty(storeList)) {
            for (UserPartyVO partyVO : storeList) {
                if (partyVO.getParty_id() != null && partyVO.getParty_id() > 0L) {
                    party_id_list.add(partyVO.getParty_id());
                }
                if (partyVO.getDistance() == null) {
                    partyVO.setDistance(BigDecimal.ZERO);
                }
            }
        }
        if (warehouse != null && warehouse.getParty_id() != null && warehouse.getParty_id() > 0L) {
            party_id_list.add(warehouse.getParty_id());
        }

        //(invoke-api)调用接口得到门店与仓库的当前状态，已经关闭的门店与仓库的推荐位商品不能返回
        if (!force_refresh) {
            Result<Map<Long, DeptGroupResponseVO>> party_check_result = PartyUtil.getManyPartySimpleInfo(party_id_list, partyFeignClient);
            if (party_check_result.isFailed()
                    || UtilValidate.isEmpty(party_check_result.getData())) {
                log.error("APP推荐位商品加载 - 验证覆盖当前用户的当事组织状态失败" + party_check_result.getMsg());
                return new Result<>(CodeEnum.SUCCESS);
            }

            Set<Long> valid_party_id_set = new HashSet<>(party_check_result.getData().size());
            Map<Long, DeptGroupResponseVO> party_map = party_check_result.getData();
            for (Long party_id : party_map.keySet()) {
                DeptGroupResponseVO party = party_map.get(party_id);
                if (ProductUtil.UP_STATUS.equals(party.getDept_status())) {
                    valid_party_id_set.add(party.getDept_id());
                }
            }

            if (UtilValidate.isNotEmpty(storeList)) {
                Iterator<UserPartyVO> it = storeList.iterator();
                while (it.hasNext()) {
                    UserPartyVO this_party = it.next();
                    if (!valid_party_id_set.contains(this_party.getParty_id())) {
                        it.remove();
                        party_id_list.remove(this_party.getParty_id());
                    }
                }
            }

            if (warehouse != null && !valid_party_id_set.contains(warehouse.getParty_id())) {
                party_id_list.remove(warehouse.getParty_id());
                warehouse = null;
            }

            if (UtilValidate.isEmpty(party_id_list)) {
                Result result = new Result(CodeEnum.FAIL_BUSINESS);
                result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
                result.setMsg("没有覆盖用户的门店和仓库");
                return result;
            }

        }

        //加载出门店与仓库的所有推荐位商品（有效的推荐位商品）
        List<AppProdListItemResponseVO> list;
        try {
            Date current = new Date();
            Map<String, Object> params_map = new HashMap<>(5);
            params_map.put("party_id_list", party_id_list);
            params_map.put("start_date", current);
            params_map.put("end_date", current);
            params_map.put("forbid_sale_flag", ProductUtil.UP_STATUS);
            params_map.put("up_down_status", ProductUtil.UP_STATUS);
            params_map.put("inventory_quantity", BigDecimal.ZERO);

            list = partyRecommendProductMapper.selectManyPartyRecommendProd(params_map);
            if (UtilValidate.isEmpty(list)) {
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
            }
        } catch (Exception e) {
            log.error("APP端加载推荐位商品数据失败", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "加载推荐位商品数据失败");
        }

        //将门店按照与当前用户的距离进行排序
        TreeSet<UserPartyVO> store_tree_set = new TreeSet<>((UserPartyVO o1, UserPartyVO o2) -> (o1.getDistance().compareTo(o2.getDistance())));
        if (UtilValidate.isNotEmpty(storeList)) {
            store_tree_set.addAll(storeList);
        }

        //将这些推荐位商品按照当事组织进行分组
        Map<Long, List<AppProdListItemResponseVO>> group_by_party_map = new HashMap<>(5);
        for (AppProdListItemResponseVO item : list) {
            if (group_by_party_map.containsKey(item.getParty_id())) {
                group_by_party_map.get(item.getParty_id()).add(item);
            } else {
                group_by_party_map.put(item.getParty_id(), UtilMisc.toList(item));
            }
        }

        /**
         * 推荐位商品目前 最多返回默认5个（可在系统字典中配置），返回商品的优先级为：离用户地址最近的门店优先返回，
         * 如果优先级高的门店没有推荐位商品或数量不足5个，继续取下一家门店的推荐商品，
         * 所有门店数量都不足5个，由仓库的推荐商品补上，仍然不足5个，有多少返回多少
         */

        int returnLimit = AppProductUtil.getAppRecommendProdReturnLimit();
        List<AppProdListItemResponseVO> return_list = new ArrayList<>(returnLimit);
        if (UtilValidate.isNotEmpty(store_tree_set)) {
            for (UserPartyVO partyVO : store_tree_set) {
                if (return_list.size() >= returnLimit) {
                    break;
                }
                if (partyVO.getParty_id() != null && group_by_party_map.containsKey(partyVO.getParty_id())) {
                    List<AppProdListItemResponseVO> store_recommend_prod_list = group_by_party_map.get(partyVO.getParty_id());
                    if (UtilValidate.isNotEmpty(store_recommend_prod_list)) {
                        for (AppProdListItemResponseVO vo : store_recommend_prod_list) {
                            if (return_list.size() >= returnLimit) {
                                break;
                            }
                            if (!return_list.contains(vo)) {
                                return_list.add(vo);
                            }
                        }
                    }
                }
            }
        }

        if (return_list.size() < returnLimit && warehouse != null && group_by_party_map.containsKey(warehouse.getParty_id())) {
            List<AppProdListItemResponseVO> warehouse_recommend_prod_list = group_by_party_map.get(warehouse.getParty_id());
            for (AppProdListItemResponseVO item : warehouse_recommend_prod_list) {
                if (return_list.size() >= returnLimit) {
                    break;
                }
                if (!return_list.contains(item)) {
                    return_list.add(item);
                }
            }
        }

        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO(return_list));
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO   <   com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO>>
     * @author 王光银
     * @methodName: searchProductList
     * @methodDesc: App端商品搜素
     * @description:
     * @param: [appSearchProductRequestVO]
     * @create 2018-06-30 9:33
     **/
    public Result<ListResponseVO<AppProdListItemResponseVO>> searchProduct(AppSearchProductRequestVO requestVO,
                                                                           UserCoveredByPartyUtil.UserCoveredPartyHandler userPartyHandler) {
        Integer limit = AppProductUtil.getAppProdSearchLimit();
        if (requestVO.getPage_num() < AppProductUtil.APP_PROD_SEARCH_DEFAULT_PAGE_INDEX) {
            requestVO.setPage_num(AppProductUtil.APP_PROD_SEARCH_DEFAULT_PAGE_INDEX);
        }
        int default_page_size = AppProductUtil.APP_PROD_SEARCH_DEFAULT_PAGE_SIZE;
        if (requestVO.getPage_size() < default_page_size) {
            requestVO.setPage_size(default_page_size);
        }
        if (requestVO.getPage_num() * requestVO.getPage_size() > limit) {
            return new Result<>(CodeEnum.SUCCESS);
        }

        //当根据商品分类查询商品时，必须要保证商品分类为最末级分类
        Set<Long> cate_set = null;
        if (requestVO.getCategory_id() != null && requestVO.getCategory_id() > 0L) {
            cate_set = productCategoryUtilService.getLastProdCate(requestVO.getCategory_id(), true);
        }

        //获取覆盖用户的门店和仓库
        if (userPartyHandler == null) {
            Result<UserCoveredByPartyUtil.UserCoveredPartyHandler> userPartyHandlerResult = UserCoveredByPartyUtil.calculateAndGetUserCoveredParty(requestVO.getLogin_token(), partyFeignClient, true);
            if (userPartyHandlerResult.isFailed()) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, userPartyHandlerResult.getMsg());
            }
            userPartyHandler = userPartyHandlerResult.getData();
            if (!userPartyHandler.hasParty()) {
                Result result = new Result(CodeEnum.FAIL_BUSINESS);
                result.setBiz_code(BizCodeEnum.NO_COVERED_PARTY.getCode());
                result.setMsg("没有覆盖用户的门店和仓库");
                return result;
            }
        }

        /**
         * 对覆盖当前用户的当事组织进行排序，门店在前，仓库在后，再按照距离排近的在前，远的在后
         */
        TreeSet<PartyProdHandler> party_set = new TreeSet<>(new Comparator<PartyProdHandler>() {
            @Override
            public int compare(PartyProdHandler o1, PartyProdHandler o2) {
                if (!o1.party_type.equals(o2.party_type)) {
                    if (SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(o1.party_type)) {
                        return -1;
                    }
                    return 1;
                }
                if (o1.distance_to_user == null) {
                    o1.distance_to_user = BigDecimal.ZERO;
                }
                if (o2.distance_to_user == null) {
                    o2.distance_to_user = BigDecimal.ZERO;
                }
                return o1.distance_to_user.compareTo(o2.distance_to_user);
            }
        });

        Set<Long> party_id_set = new HashSet<>((userPartyHandler.hasStore() ? userPartyHandler.store.size() : 0) + (userPartyHandler.hasWarehouse() ? 1 : 0));
        if (userPartyHandler.hasStore()) {
            for (UserPartyVO userPartyVO : userPartyHandler.store) {
                party_id_set.add(userPartyVO.getParty_id());
                PartyProdHandler partyProdHandler = new PartyProdHandler();
                partyProdHandler.distance_to_user = userPartyVO.getDistance();
                partyProdHandler.party_id = userPartyVO.getParty_id();
                partyProdHandler.party_type = SysCodeEnmu.DEPTETYPE_01.getCodeValue();
                party_set.add(partyProdHandler);
            }
        }
        if (userPartyHandler.hasWarehouse()) {
            party_id_set.add(userPartyHandler.warehouse.getParty_id());
            PartyProdHandler partyProdHandler = new PartyProdHandler();
            partyProdHandler.distance_to_user = userPartyHandler.warehouse.getDistance();
            partyProdHandler.party_id = userPartyHandler.warehouse.getParty_id();
            partyProdHandler.party_type = SysCodeEnmu.DEPTETYPE_02.getCodeValue();
            party_set.add(partyProdHandler);
        }

        Date current = new Date();
        //组织查询条件
        Map<String, Object> base_params_map = new HashMap<>(8);
        if (UtilValidate.isNotEmpty(cate_set)) {
            base_params_map.put("cate_id_set", cate_set);
        }
        if (UtilValidate.isNotEmpty(requestVO.getCategory_name())) {
            base_params_map.put("cate_name", requestVO.getCategory_name().trim());
        }
        if (UtilValidate.isNotEmpty(requestVO.getSearch_key())) {
            base_params_map.put("prod_name", requestVO.getSearch_key().trim());
        }

        base_params_map.put("forbid_sale_flag", ProductInfo.FORBID_SALE_FLAG);
        base_params_map.put("activity_status", ActivityInfo.ACTIVITY_STATUS_NORMAL);
        base_params_map.put("audit_status", ActivityInfo.AUDIT_STATUS_PASS);
        base_params_map.put("start_date", current);
        base_params_map.put("end_date", current);
        base_params_map.put("up_down_status", ProductUtil.UP_STATUS);
        base_params_map.put("inventory_quantity", BigDecimal.ZERO);

        /**
         * 根据分页信息计算出至少 需要多少商品数据，以减少数据加载提高性能
         */
        int at_least_count = requestVO.getPage_size() * requestVO.getPage_num();
        int has_load_count = 0;

        /**
         * 加载这些门店和仓库的活动商品
         * 加载活动商品应该交由促销活动服务组件来做，目前暂时在这里先实现
         */
        Set<AppProdListItemResponseVO> prod_filter_set = new HashSet<>(at_least_count);
        for (PartyProdHandler partyProdHandler : party_set) {
            if (has_load_count >= at_least_count) {
                break;
            }
            try {
                base_params_map.put("party_id", partyProdHandler.party_id);
                List<AppProdListItemResponseVO> activity_prod_list = super.baseMapper.selectOnePartyActivityProd(base_params_map);
                if (UtilValidate.isEmpty(activity_prod_list)) {
                    continue;
                }
                partyProdHandler.activity_prod_list = new ArrayList<>(activity_prod_list.size());
                for (AppProdListItemResponseVO activity_prod : activity_prod_list) {
                    if (prod_filter_set.contains(activity_prod)) {
                        continue;
                    }
                    prod_filter_set.add(activity_prod);
                    partyProdHandler.activity_prod_list.add(activity_prod);
                    has_load_count++;
                    //当加载处理的数据达到了本次查询的最少数量时，立即跳出循环
                    if (has_load_count >= at_least_count) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("App端商品搜素 - 加载当事组织活动商品失败", e);
                return new Result<>(CodeEnum.FAIL_SERVER, "加载当事组织活动商品失败");
            }
        }

        //如果 在活动商品中已经取得了满足当前条件的商品数据，则直接返回，不需要再对正常商品进行查询
        if (has_load_count >= at_least_count) {
            List<AppProdListItemResponseVO> this_page_list = ProductUtil.calculateAndGetPageProdData(party_set, requestVO.getPage_size());
            return new Result(CodeEnum.SUCCESS, new ListResponseVO(this_page_list));
        }


        /**
         * 活动商品不在本次搜索范围，或者是活动商品不足够覆盖本次搜索的范围时,需要继续加载普通商品数据
         */
        for (PartyProdHandler partyProdHandler : party_set) {
            if (has_load_count >= at_least_count) {
                break;
            }
            try {
                base_params_map.put("party_id", partyProdHandler.party_id);
                List<AppProdListItemResponseVO> party_prod_list = super.baseMapper.selectManyPartyProd(base_params_map);
                if (UtilValidate.isEmpty(party_prod_list)) {
                    continue;
                }
                partyProdHandler.normal_prod_list = new ArrayList<>(party_prod_list.size());

                for (AppProdListItemResponseVO prod : party_prod_list) {
                    if (prod_filter_set.contains(prod)) {
                        continue;
                    }
                    prod_filter_set.add(prod);
                    partyProdHandler.normal_prod_list.add(prod);
                    has_load_count++;
                    //当加载处理的数据达到了本次查询的最少数量时，立即跳出循环
                    if (has_load_count >= at_least_count) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("App端商品搜素 - 加载当事组织普通商品失败", e);
                return new Result<>(CodeEnum.FAIL_SERVER, "加载当事组织普通商品失败");
            }
        }

        List<AppProdListItemResponseVO> this_page_list;
        if (has_load_count >= at_least_count) {
            this_page_list = ProductUtil.calculateAndGetPageProdData(party_set, requestVO.getPage_size());
        } else {
            int need_how_many;
            if (has_load_count < requestVO.getPage_size()) {
                need_how_many = has_load_count;
            } else if (at_least_count - has_load_count > requestVO.getPage_size()) {
                need_how_many = at_least_count - has_load_count - requestVO.getPage_size();
            } else {
                need_how_many = requestVO.getPage_size() - (at_least_count - has_load_count);
            }
            this_page_list = ProductUtil.calculateAndGetPageProdData(party_set, need_how_many);
        }

        //最后一步，检查是否有需要加载商品详情的数据，为了减少IO和提高性能，普通商品在计算分页过程中是没有加载商品详情数据的
        Set<Long> need_to_load_detail_prod = new HashSet<>(this_page_list.size());
        for (AppProdListItemResponseVO itemResponseVO : this_page_list) {
            if (UtilValidate.isEmpty(itemResponseVO.getProduct_name())) {
                need_to_load_detail_prod.add(itemResponseVO.getProduct_id());
            }
        }
        if (UtilValidate.isNotEmpty(need_to_load_detail_prod)) {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.in("id", need_to_load_detail_prod);
            wrapper.setSqlSelect("product_name", "list_image_url", "specification", "brand_name", "id");
            try {
                List<ProductInfo> productInfoList = productMapper.selectList(wrapper);
                if (UtilValidate.isNotEmpty(productInfoList)) {
                    Map<Long, ProductInfo> productInfoMap = new HashMap<>(productInfoList.size());
                    for (ProductInfo info : productInfoList) {
                        productInfoMap.put(info.getId(), info);
                    }
                    for (AppProdListItemResponseVO itemResponseVO : this_page_list) {
                        if (!productInfoMap.containsKey(itemResponseVO.getProduct_id())) {
                            continue;
                        }
                        itemResponseVO.setProduct_name(productInfoMap.get(itemResponseVO.getProduct_id()).getProduct_name());

                        //TODO WGY 从缓存数据中获取品牌，以保留之前版本的正常使用
                        //itemResponseVO.setBrand_name(productInfoMap.get(itemResponseVO.getProduct_id()).getBrand_name());
                        itemResponseVO.setSpecification(productInfoMap.get(itemResponseVO.getProduct_id()).getSpecification());
                        itemResponseVO.setList_image_url(productInfoMap.get(itemResponseVO.getProduct_id()).getList_image_url());
                    }
                }
            } catch (Exception e) {
                log.error("App端商品搜素 - 最后一步加载商品列表详情失败", e);
                return new Result(CodeEnum.FAIL_SERVER, "最后一步加载商品列表详情失败");
            }
        }
        return new Result(CodeEnum.SUCCESS, new ListResponseVO(this_page_list));
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
            return_result.setMsg("商品不存在");
            return return_result;
        }

        AppProductInfoResponseVO return_vo = productInfo.convertToAppResponse(ProductUtil.PROD_TYPE_NAME_GETTER, ProductUtil.PROD_BRAND_GETTER, ProductUtil.PROD_MADE_WHERE_GETTER, ProductUtil.PROD_LONG_DESC_GETTER);

        //返回的商品详情对象
        return_vo.setParty_id(validateRequestVO.getParty_id());
        return_vo.setSale_price(validateResponse.getNormal_price() == null ? "0" : validateResponse.getNormal_price().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        return_vo.setInventory_quantity(validateResponse.getInventory_quantity() == null ? "0" : validateResponse.getInventory_quantity().toString());

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
            }
            userPartyHandler = result.getData();
        }

        //调用接口获取当前商品的所有活动信息, 活动的返回取决于用户的覆盖门店与仓库，如果没有获取到用户的覆盖门店与仓库则不进行活动接口的调用
        if (userPartyHandler != null && userPartyHandler.hasParty()) {
            Set<Long> user_party_id_set = new HashSet<>((userPartyHandler.warehouse == null ? 0 : 1)
                    + (userPartyHandler.store == null ? 0 : userPartyHandler.store.size())
                    + (userPartyHandler.default_party == null ? 0 : 1));
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
        return_result.setData(return_vo);
        return return_result;
    }

}
