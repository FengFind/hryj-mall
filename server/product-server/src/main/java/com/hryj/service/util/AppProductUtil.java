package com.hryj.service.util;

import com.hryj.cache.CodeCache;

/**
 * @author 王光银
 * @className: AppProductUtil
 * @description:
 * @create 2018/7/21 0021 21:38
 **/
public class AppProductUtil {

    /**
     * APP端推荐位商品返回的数量限制
     */
    private static final String APP_RECOMMEND_PROD_RETURN_LIMIT_CODE_TYPE = "AppRecommendProdReturnLimit";
    private static final String APP_RECOMMEND_PROD_RETURN_LIMIT_KEY = "S01";
    private static final Integer APP_RECOMMEND_PROD_RETURN_LIMIT = 5;


    /**
     * APP端商品搜索的数量限制，配置在系统字典中
     */
    private static final String APP_PROD_SEARCH_PAGE_LIMIT_CODE_TYPE = "SearchPageLimit";
    private static final String APP_PROD_SEARCH_PAGE_LIMIT_CODE_KEY = "S01";
    private static final Integer APP_DEFAULT_APP_PROD_SEARCH_PAGE_LIMIT = 1000;

    /**
     * APP端端口搜索的默认的分页参数
     */
    public static final Integer APP_PROD_SEARCH_DEFAULT_PAGE_INDEX = 1;
    public static final Integer APP_PROD_SEARCH_DEFAULT_PAGE_SIZE = 10;
    public static final Integer APP_PROD_SEARCH_MAX_PAGE_SIZE = 50;
    private static final String APP_PROD_SEARCH_DEFAULT_PAGE_SIZE_CODE_TYPE = "AppProdSearchDefaultPageSize";
    private static final String APP_PROD_SEARCH_DEFAULT_PAGE_SIZE_KEY = "S01";




    public static Integer getAppRecommendProdReturnLimit() {
        int limit = APP_RECOMMEND_PROD_RETURN_LIMIT;
        try {
            limit = Integer.valueOf(CodeCache.getValueByKey(APP_RECOMMEND_PROD_RETURN_LIMIT_CODE_TYPE, APP_RECOMMEND_PROD_RETURN_LIMIT_KEY));
        } catch (Exception e) {}
        return limit;
    }

    public static Integer getAppProdSearchLimit() {
        int limit = APP_DEFAULT_APP_PROD_SEARCH_PAGE_LIMIT;
        try {
            limit = Integer.valueOf(CodeCache.getValueByKey(APP_PROD_SEARCH_PAGE_LIMIT_CODE_TYPE, APP_PROD_SEARCH_PAGE_LIMIT_CODE_KEY));
        } catch (Exception e) {}
        return limit;
    }

    public static Integer getAppProdSearchDefaultPageSize() {
        int limit = APP_PROD_SEARCH_DEFAULT_PAGE_SIZE;
        try {
            limit = Integer.valueOf(CodeCache.getValueByKey(APP_PROD_SEARCH_DEFAULT_PAGE_SIZE_CODE_TYPE, APP_PROD_SEARCH_DEFAULT_PAGE_SIZE_KEY));
        } catch (Exception e) {}
        return limit;
    }
}
