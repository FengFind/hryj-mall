package com.hryj.controller.app.v1_1;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.activity.request.OnlyActivityIdRequestVO;
import com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityDataResponseVO;
import com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityResponseVO;
import com.hryj.service.app.v1_1.AppActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ActivityForAppController
 * @description: 活动管理
 * @create 2018/6/27 0027 21:43
 **/
@RestController("v1.1-ActivityForAppController")
@RequestMapping("/v1-1/activityForApp")
public class ActivityForAppController {

    @Autowired
    private AppActivityService activityService;

    /**
     * @author 汪豪
     * @methodName: findPromotionActivity
     * @methodDesc: APP端加载促销活动
     * @description: 不分页
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityResponseVO>
     * @create 2018-06-28 19:44
     **/
    @PostMapping("/findPromotionActivity")
    public Result<PageResponseVO<AppPromotionActivityResponseVO>> findPromotionActivity(@RequestBody RequestVO requestVO) {
        return activityService.findPromotionActivity(requestVO);
    }

    /**
     * @author 汪豪
     * @methodName: getPromotionActivityData
     * @methodDesc: APP端加载活动的详细信息
     * @description:
     * @param: [onlyActivityIdRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityDataResponseVO>
     * @create 2018-06-29 11:52
     **/
    @PostMapping("/getPromotionActivityData")
    public Result<AppPromotionActivityDataResponseVO> getPromotionActivityData(@RequestBody OnlyActivityIdRequestVO onlyActivityIdRequestVO) {
        return activityService.getPromotionActivityData(onlyActivityIdRequestVO);
    }
}
