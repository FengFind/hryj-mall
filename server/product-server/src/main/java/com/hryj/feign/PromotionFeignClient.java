package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.promotion.activity.request.ActivityIdProductIdRequestVO;
import com.hryj.entity.vo.promotion.activity.request.common.CommonProdCheckRequestVO;
import com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO;
import com.hryj.entity.vo.promotion.activity.response.common.CommonProdCheckResponseVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author 王光银
 * @className: PromotionFeignClient
 * @description: 促销活动服务调用客户端
 * @create 2018/7/9 0009 22:41
 **/
@FeignClient(name = "promotion-server")
public interface PromotionFeignClient {

    /**
     * @author 王光银
     * @methodName: checkProduct
     * @methodDesc: 从商品角度返回这个商品所有的有效的促销活动
     * @description:
     * @param: [commonProdCheckRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.common.CommonProdCheckResponseVO>
     * @create 2018-07-10 19:05
     **/
    @RequestMapping(value = "/promotionActivityCommon/checkProduct", method = RequestMethod.POST)
    Result<CommonProdCheckResponseVO> checkProduct(@RequestBody CommonProdCheckRequestVO commonProdCheckRequestVO);

    /**
     * @author 王光银
     * @methodName: activityJoinProductDetail
     * @methodDesc:  商品验证（批量）
     * @description:
     * @param: [activityIdProductIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO>
     * @create 2018-07-10 13:53
     **/
    @RequestMapping(value = "/promotionActivityMgr/activityJoinProductDetail", method = RequestMethod.POST)
    Result<ListResponseVO<ActivityInProgressProductItemResponseVO>> checkPromotionProd(@RequestBody List<ActivityIdProductIdRequestVO> activityIdProductIdRequestVOList);
}
