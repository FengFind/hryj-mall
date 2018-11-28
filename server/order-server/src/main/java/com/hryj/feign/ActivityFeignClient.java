package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.promotion.activity.request.OnlyActivityIdRequestVO;
import com.hryj.entity.vo.promotion.activity.request.PartyProductActivityRequestVO;
import com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoResponseVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 叶方宇
 * @className: ActivityFeigClient
 * @description:
 * @create 2018/8/17 0017 9:29
 **/
@FeignClient(name = "promotion-server")
public interface ActivityFeignClient {

    /**
     * @author 罗秋涵
     * @description: 批量查询门店商品活动信息
     * @param: [partyProductActivityRequestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoResponseVO>>
     * @create 2018-08-21 14:53
     **/
    @RequestMapping("/promotionActivityMgr/orderProductJoinActivityInfo")
    Result<List<OrderActivityInfoResponseVO>> orderProductJoinActivityInfo(@RequestBody List<PartyProductActivityRequestVO> partyProductActivityRequestVO);

    /**
     * @author 罗秋涵
     * @description: 根据活动编号查询活动信息
     * @param: [onlyActivityIdRequestVOS]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoResponseVO>>
     * @create 2018-08-21 14:53
     **/
    @RequestMapping("/promotionActivityMgr/getActivityInfoById")
    Result<List<OrderActivityInfoResponseVO>> getActivityInfoById(@RequestBody List<OnlyActivityIdRequestVO> onlyActivityIdRequestVOS);

}
