package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.promotion.activity.request.*;
import com.hryj.entity.vo.promotion.activity.response.*;
import com.hryj.entity.vo.promotion.activity.response.common.ProdCheckItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 汪豪
 * @className: ActivityMapper
 * @description: 活动相关Mapper
 * @create 2018/7/4 0004 21:12
 **/
@Component
public interface ActivityMapper extends BaseMapper<ActivityInfo> {

    List<SearchPromotionActivityItemResponseVO> searchPromotionActivityPage(Map<String,Object> param, Page page);

    PromotionActivityDataResponseVO getPromotionActivityById(Long activity_id);

    /**
     * @author 汪豪
     * @methodName: getPartyDataByActivityId
     * @methodDesc: 根据活动id分页查找活动范围内的门店信息
     * @description:
     * @param: [activity_id,page]
     * @return List<JoinPartyItemResponseVO>
     * @create 2018-07-06 11:05
     **/
    List<JoinPartyItemResponseVO> getPartyStoreDataByActivityId(Long activity_id, Page page);

    /**
     * @author 汪豪
     * @methodName: getPartyDataByActivityId
     * @methodDesc: 根据活动id分页查找活动范围内的仓库信息
     * @description:
     * @param: [activity_id,page]
     * @return List<JoinPartyItemResponseVO>
     * @create 2018-07-06 11:05
     **/
    List<JoinPartyItemResponseVO> getPartyWarehouseDataByActivityId(Long activity_id, Page page);

    /**
     * @author 汪豪
     * @methodName: getPartyProductDataByActivityId
     * @methodDesc: 根据活动id分页查询参与促销的商品
     * @description:
     * @param: [activity_id,page]
     * @return List<JoinProductItemResponseVO>
     * @create 2018-07-06 16:47
     **/
    List<JoinProductItemResponseVO> getPartyProductDataByActivityId(Long activity_id);
    
    /**
     * @author 汪豪
     * @methodName: getActivityAuditRecordByActivityId
     * @methodDesc: 根据活动id查询促销活动审核记录
     * @description: 
     * @param: [activity_id]
     * @return List<PromotionActivityAuditRecordResponseVO>
     * @create 2018-07-06 17:15
     **/
    List<PromotionActivityAuditRecordResponseVO> getActivityAuditRecordByActivityId(Long activity_id);

    /**
     * @author 汪豪
     * @methodName: getPartyStoreDataByPage
     * @methodDesc: 按条件分页查询参与活动的门店信息
     * @description:
     * @param: [searchActivityJoinPartyRequestVO,page]
     * @return List<JoinPartyItemResponseVO>
     * @create 2018-07-06 20:28
     **/
    List<JoinPartyItemResponseVO> getPartyStoreDataByPage(SearchActivityJoinPartyRequestVO searchActivityJoinPartyRequestVO,Page page);

    /**
     * @author 汪豪
     * @methodName: getPartyWarehouseDataByPage
     * @methodDesc: 按条件分页查询参与活动的仓库信息
     * @description:
     * @param: [searchActivityJoinPartyRequestVO,page]
     * @return List<JoinPartyItemResponseVO>
     * @create 2018-07-06 20:28
     **/
    List<JoinPartyItemResponseVO> getPartyWarehouseDataByPage(SearchActivityJoinPartyRequestVO searchActivityJoinPartyRequestVO,Page page);

    /**
     * @author 汪豪
     * @methodName: getPromotionActivityJoinProductData
     * @methodDesc: 按条件分页查询参与活动的商品信息
     * @description:
     * @param: [searchActivityJoinProductRequestVO,page]
     * @return List<JoinProductItemResponseVO>
     * @create 2018-07-07 10:05
     **/
    List<JoinProductItemResponseVO> getPromotionActivityJoinProductData(SearchActivityJoinProductRequestVO searchActivityJoinProductRequestVO, Page page);

    /**
     * @author 王光银
     * @methodName: selectCheckActivity
     * @methodDesc: 根据商品查询包含了这些商品的有效的所有活动
     * @description:
     * @param:
     * @return
     * @create 2018-07-10 19:40
     **/
    List<ProdCheckItem> selectCheckActivity(Map<String, Object> params_map);
    /**
     * @author 汪豪
     * @methodName: getActivityListToApp
     * @methodDesc: APP端加载促销活动
     * @description: 不分页
     * @param: [currTime]
     * @return List<AppPromotionActivityResponseVO>
     * @create 2018-06-28 19:44
     **/
    List<AppPromotionActivityResponseVO> getActivityListToApp(@Param("party_id") Long party_id, @Param("currTime") String currTime);

    List<AppPromotionActivityResponseVO> getActivityListToAppPlus(Long party_id);

    /**
     * @author 汪豪
     * @methodName: getActivityDataByActivityIdToApp
     * @methodDesc: APP端加载活动的详细信息
     * @description:
     * @param: [activity_id]
     * @return AppPromotionActivityDataResponseVO
     * @create 2018-06-29 11:52
     **/
    AppPromotionActivityDataResponseVO getActivityDataByActivityIdToApp(Long activity_id);

    List<AppProdListItemResponseVO> getActivityProductDataListToApp(Long activity_id);

    List<AppProdListItemResponseVO> getActivityProductDataListByPartyIdToApp(@Param("activity_id")Long activity_id,@Param("party_id")Long party_id);

    /**
     * @author 汪豪
     * @methodName: 通过条件查询参加活动的商品信息
     * @methodDesc: getPartyProductDataByCondition
     * @description:
     * @param: [activityIdProductIdRequestVO]
     * @return ActivityInProgressProductItemResponseVO
     * @create 2018-07-10 14:17
     **/
    ActivityInProgressProductItemResponseVO getPartyProductDataByCondition(ActivityIdProductIdRequestVO activityIdProductIdRequestVO);

    OrderActivityInfoResponseVO getOrderProductJoinActivityInfo(PartyProductActivityRequestVO productActivityRequestVO);

    List<OrderActivityInfoResponseVO> getActivityInfoById(@Param("activity_id_list")List<Long> activity_id_list);

    /**
     * @author 王光银
     * @methodName: 查询门店即将开始的最早的活动开始日期
     * @methodDesc:
     * @description:
     * @param:
     * @return
     * @create 2018-08-29 10:53
     **/
    List<ActivityInfo> findPartyEarliestStartActivityDate(@Param("party_id") Long party_id, @Param("curr") Date curr);
}
