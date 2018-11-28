package com.hryj.controller.app.v1_1;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AppAdvertisingPositionResponseVO;
import com.hryj.service.app.v1_1.AppAdvertisingPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: AdvertisingForAppController
 * @description: 广告查询
 * @create 2018/6/27 0027 21:43
 **/
@RestController("v1.1-AdvertisingForAppController")
@RequestMapping("/v1-1/advertisingForApp")
public class AdvertisingForAppController {

    @Autowired
    private AppAdvertisingPositionService advertisingPositionService;

    /**
     * @author 王光银
     * @methodName: findAdvertisingPosition
     * @methodDesc: APP端加载广告位
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.advertisingposition.response.AppAdvertisingPositionResponseVO>>
     * @create 2018-06-29 11:13
     **/
    @PostMapping("/findAdvertisingPosition")
    public Result<PageResponseVO<AppAdvertisingPositionResponseVO>> findAdvertisingPosition(@RequestBody RequestVO requestVO) {
        return advertisingPositionService.findAdvertisingPosition(requestVO);
    }
}
