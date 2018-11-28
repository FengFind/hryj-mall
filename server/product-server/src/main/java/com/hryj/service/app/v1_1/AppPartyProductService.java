package com.hryj.service.app.v1_1;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.ProductTypeCacheHandler;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.PartyProduct;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.common.request.ProductValidateRequestVO;
import com.hryj.entity.vo.product.request.app.AppSearchProductRequestVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO;
import com.hryj.entity.vo.promotion.recommend.request.app.PartyRecommendProdSearchRequestVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.PartyProductMapper;
import com.hryj.mapper.PartyRecommendProductMapper;
import com.hryj.service.PartyProductUtilService;
import com.hryj.service.prodcate.ProductCategoryUtilService;
import com.hryj.service.util.AppProductUtil;
import com.hryj.service.util.ProductUtil;
import com.hryj.service.util.RedisCacheUtil;
import com.hryj.service.util.UserCoveredByPartyUtil;
import com.hryj.service.worktask.SetProductSearchResultCacheTask;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 王光银
 * @className: AppPartyProductService
 * @description: APP端商品接口 v1.1 实现
 * @create 2018/7/9 0009 22:59
 **/
@Slf4j
@Service("v1.1-AppPartyProductService")
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
    private com.hryj.service.app.v1_0.AppPartyProductService appPartyProductService;

    @Autowired
    private PartyProductUtilService partyProductUtilService;

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
        Long this_party_id = requestVO.getParty_id();
        if (this_party_id == null || this_party_id <= 0L) {
            Result<Long> result = UserCoveredByPartyUtil.getCoveredUserUniqueParty(null, requestVO.getLogin_token(), userFeignClient, partyProductUtilService);
            if (result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, result.getMsg());
            }
            if (result.getData() == null || result.getData() <= 0L) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            this_party_id = result.getData();
        }

        //步骤2  检查缓存中是否有数据，如果有数据，直接返回
        List<AppProdListItemResponseVO> list = RedisCacheUtil.PartyRecommendProdCacheUtil.getCacheData(this_party_id);
        if (UtilValidate.isNotEmpty(list)) {
            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(list));
        }

        //从数据库加载数据
        Date current = new Date();
        try {
            //获取推荐位商品返回数量配置
            int returnLimit = AppProductUtil.getAppRecommendProdReturnLimit();
            Map<String, Object> params_map = new HashMap<>(5);
            params_map.put("party_id", this_party_id);
            params_map.put("start_date", current);
            params_map.put("end_date", current);
            params_map.put("forbid_sale_flag", ProductUtil.UP_STATUS);
            params_map.put("up_down_status", ProductUtil.UP_STATUS);
            params_map.put("inventory_quantity", BigDecimal.ZERO);
            params_map.put("limit_start", 0);
            params_map.put("limit_end", returnLimit);
            list = partyRecommendProductMapper.selectManyPartyRecommendProd(params_map);
            if (UtilValidate.isEmpty(list)) {
                return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
            }
        } catch (Exception e) {
            log.error("APP端加载推荐位商品数据失败", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "加载推荐位商品数据失败");
        }

        //将数据加入缓存
        RedisCacheUtil.PartyRecommendProdCacheUtil.setCacheData(this_party_id, list);

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

        //步骤2   当根据商品分类查询商品时，必须要保证商品分类为最末级分类
        Set<Long> cate_set = null;
        if (requestVO.getCategory_id() != null && requestVO.getCategory_id() > 0L) {
            cate_set = productCategoryUtilService.getLastProdCate(requestVO.getCategory_id(), true);
        }

        //步骤3
        Long this_party_id = requestVO.getParty_id();
        if (this_party_id == null || this_party_id <= 0L) {
            Result<Long> result = UserCoveredByPartyUtil.getCoveredUserUniqueParty(null, requestVO.getLogin_token(), userFeignClient, partyProductUtilService);
            if (!result.isSuccess()) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, result.getMsg());
            }
            if (result.getData() == null || result.getData() <= 0L) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "您的周围暂无小店");
            }
            this_party_id = result.getData();
            requestVO.setParty_id(this_party_id);
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
        if (UtilValidate.isNotEmpty(prod_list)) {
            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(prod_list));
        }

        //步骤 4.3 活动商品与普通商品一起查询
        //组装查询条件
        Date curr = new Date();
        Map<String, Object> params_map = new HashMap<>(6);
        params_map.put("party_id", requestVO.getParty_id());
        if (UtilValidate.isNotEmpty(cate_set)) {
            if (cate_set.size() > 1) {
                params_map.put("cate_id_set", cate_set);
            } else {
                params_map.put("cate_id", cate_set.iterator().next());
            }
        }
        if (UtilValidate.isNotEmpty(requestVO.getCategory_name())) {
            params_map.put("cate_name", requestVO.getCategory_name().trim());
        }
        if (UtilValidate.isNotEmpty(requestVO.getSearch_key())) {
            params_map.put("prod_name", requestVO.getSearch_key().trim());
        }
        params_map.put("date_time_begin", curr);
        params_map.put("date_time_end", curr);

        Page pageCondition = new Page(requestVO.getPage_num(), requestVO.getPage_size());

        List<AppProdListItemResponseVO> page_list = partyProductMapper.pageFindPartyActivityAndNormalProduct(params_map, pageCondition);
        /**
         * 商品类型标识
         */
        if(null!=page_list && page_list.size()>0){
            for (AppProdListItemResponseVO appProdListItemResponseVO : page_list) {
                appProdListItemResponseVO.setTitle_mark_image_list(ProductTypeCacheHandler.getProductTypeTitleMark(appProdListItemResponseVO.getProduct_type_id()));
            }

        }
        //步骤 4.5
        if (UtilValidate.isNotEmpty(page_list)) {
            //步骤 4.4
            ThreadPoolUtil.submitTask(new SetProductSearchResultCacheTask(requestVO.getParty_id(), cache_key, page_list));
            return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>(page_list));
        }
        return new Result<>(CodeEnum.SUCCESS, new ListResponseVO<>());
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
        return appPartyProductService.getProductInfo(requestVO, userPartyHandler);
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
