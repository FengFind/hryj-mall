package com.hryj.service.util;

import com.alibaba.fastjson.JSON;
import com.hryj.cache.RedisService;
import com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityResponseVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AppAdvertisingPositionResponseVO;
import com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO;
import com.hryj.threadpool.ThreadPoolUtil;
import com.hryj.utils.UtilValidate;
import com.hryj.worktask.CleanAdvertisingShowResultPartyIdTask;
import com.hryj.worktask.CleanPromotionShowResultByPartyIdTask;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 汪豪
 * @className: PromotionRedisCacheUtil
 * @description:
 * @create 2018/8/29 0029 10:47
 **/
@Slf4j
@Component
@Data
public class PromotionRedisCacheUtil {
    public static PromotionRedisCacheUtil promotionRedisCacheUtil;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        promotionRedisCacheUtil = this;
        promotionRedisCacheUtil.redisService = this.redisService;
    }

    public enum PromotionRedisCacheKey {
        
        NAVIGATION_SHOW_RESULT_CACHE_KEY("navigation_show_result_cache_key","用户端导航展示结果缓存组KEY"),
        PROMOTION_SHOW_RESULT_CACHE_KEY("promotion_show_result_cache_key","用户端活动展示结果缓存组KEY"),
        PROMOTION_DETAIL_RESULT_CACHE_KEY("promotion_detail_result_cache_key","用户端活动详情结果缓存组KEY"),
        ADVERTISING_SHOW_RESULT_CACHE_KEY("advertising_show_result_cache_key","用户端广告展示结果缓存组KEY");

        private String cacheName;
        private String desc;

        PromotionRedisCacheKey(String cacheName, String desc) {
            this.cacheName = cacheName;
            this.desc = desc;
        }

        public String get() {
            return this.cacheName;
        }

        public String getDesc() {
            return this.desc;
        }
    }

    

    /**
     * @author 汪豪
     * @className: NavigationCacheUtil
     * @description: 导航缓存结果缓存工具类
     * @create 2018-08-16 17:21
     **/
    public static class NavigationCacheUtil {

        private static final String KEY_NAME = "index_navigation";

        public static void cleanNavigationInCache() {
            promotionRedisCacheUtil.redisService.delete2(PromotionRedisCacheUtil.PromotionRedisCacheKey.NAVIGATION_SHOW_RESULT_CACHE_KEY.get(), KEY_NAME);
        }

        public static void setCacheData(List<IndexNavigationResponseVO> data_list){
            if (UtilValidate.isEmpty(data_list)) {
                return;
            }
            promotionRedisCacheUtil.redisService.put2(PromotionRedisCacheUtil.PromotionRedisCacheKey.NAVIGATION_SHOW_RESULT_CACHE_KEY.get(), KEY_NAME, JSON.toJSONString(data_list), 86400);
        }

        public static List<IndexNavigationResponseVO> getCacheData(){
            String cache_data_value = promotionRedisCacheUtil.redisService.get2(PromotionRedisCacheUtil.PromotionRedisCacheKey.NAVIGATION_SHOW_RESULT_CACHE_KEY.get(), KEY_NAME);
            if (UtilValidate.isEmpty(cache_data_value)) {
                return null;
            }
            try {
                return JSON.parseArray(cache_data_value, IndexNavigationResponseVO.class);
            } catch (Exception e) {
                promotionRedisCacheUtil.redisService.delete2(PromotionRedisCacheUtil.PromotionRedisCacheKey.NAVIGATION_SHOW_RESULT_CACHE_KEY.get(), KEY_NAME);
                return null;
            }
        }
    }

    /**
     * @author 汪豪
     * @className: PromotionCacheUtil
     * @description: 活动相关缓存工具类
     * @create 2018-08-16 17:21
     **/
    public static class PromotionCacheUtil {

        //活动列表
        public static void cleanPromotionListByPartyId(Long target_party_id) {
            if (target_party_id == null || target_party_id <= 0L) {
                return;
            }
            //异步处理缓存数据的清除
            ThreadPoolUtil.submitTask(new CleanPromotionShowResultByPartyIdTask(target_party_id));
        }

        public static void setPromotionListCacheData(List<AppPromotionActivityResponseVO> data_list, Long target_party_id){
            if (target_party_id == null || target_party_id <= 0L || UtilValidate.isEmpty(data_list)) {
                return;
            }
            promotionRedisCacheUtil.redisService.put2(PromotionRedisCacheUtil.PromotionRedisCacheKey.PROMOTION_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString(), JSON.toJSONString(data_list), null);
        }

        public static List<AppPromotionActivityResponseVO> getPromotionListCacheData(Long target_party_id){
            if (target_party_id == null || target_party_id <= 0L) {
                return null;
            }
            String cache_data_value = promotionRedisCacheUtil.redisService.get2(PromotionRedisCacheUtil.PromotionRedisCacheKey.PROMOTION_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString());
            if (UtilValidate.isEmpty(cache_data_value)) {
                return null;
            }
            try {
                return JSON.parseArray(cache_data_value, AppPromotionActivityResponseVO.class);
            } catch (Exception e) {
                promotionRedisCacheUtil.redisService.delete2(PromotionRedisCacheUtil.PromotionRedisCacheKey.PROMOTION_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString());
                return null;
            }
        }

        public static void setExpirationTime(Long target_party_id,Integer exT){
            if (target_party_id == null || target_party_id <= 0 || exT == null) {
                return;
            }
            promotionRedisCacheUtil.redisService.expire2(PromotionRedisCacheUtil.PromotionRedisCacheKey.PROMOTION_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString(),exT);
        }
    }

    /**
     * @author 汪豪
     * @className: PromotionCacheUtil
     * @description: 广告相关缓存工具类
     * @create 2018-08-16 17:21
     **/
    public static class AdvertisingCacheUtil {

        public static void cleanByPartyId(Long target_party_id) {
            if (target_party_id == null || target_party_id <= 0L) {
                return;
            }
            //异步处理缓存数据的清除
            ThreadPoolUtil.submitTask(new CleanAdvertisingShowResultPartyIdTask(target_party_id));
        }

        public static void setCacheData(List<AppAdvertisingPositionResponseVO> data_list, Long target_party_id){
            if (target_party_id == null || target_party_id <= 0L || UtilValidate.isEmpty(data_list)) {
                return;
            }
            promotionRedisCacheUtil.redisService.put2(PromotionRedisCacheUtil.PromotionRedisCacheKey.ADVERTISING_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString(), JSON.toJSONString(data_list), null);
        }

        public static List<AppAdvertisingPositionResponseVO> getCacheData(Long target_party_id){
            if (target_party_id == null || target_party_id <= 0L) {
                return null;
            }
            String cache_data_value = promotionRedisCacheUtil.redisService.get2(PromotionRedisCacheUtil.PromotionRedisCacheKey.ADVERTISING_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString());
            if (UtilValidate.isEmpty(cache_data_value)) {
                return null;
            }
            try {
                return JSON.parseArray(cache_data_value, AppAdvertisingPositionResponseVO.class);
            } catch (Exception e) {
                promotionRedisCacheUtil.redisService.delete2(PromotionRedisCacheUtil.PromotionRedisCacheKey.ADVERTISING_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString());
                return null;
            }
        }

        public static void setExpirationTime(Long target_party_id,Integer exT){
            if (target_party_id == null || target_party_id <= 0 || exT == null) {
                return;
            }
            promotionRedisCacheUtil.redisService.expire2(PromotionRedisCacheUtil.PromotionRedisCacheKey.ADVERTISING_SHOW_RESULT_CACHE_KEY.get(), target_party_id.toString(),exT);
        }
    }
}
