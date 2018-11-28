package com.hryj.controller.app.v1_1;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO;
import com.hryj.service.IndexNavigationService;
import com.hryj.service.app.v1_1.AppIndexNavigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 汪豪
 * @className: IndexNavigationForAppController
 * @description: app端首页导航Controller
 * @create 2018/8/23 0023 16:37
 **/
@RestController("v1.1-IndexNavigationForAppController")
@RequestMapping("/v1-1/indexNavigationForApp")
public class IndexNavigationForAppController {

    @Autowired
    private AppIndexNavigationService appIndexNavigationService;

    /**
     * @author 汪豪
     * @methodName: findIndexNavigation
     * @methodDesc: APP端加载首页导航
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO>>
     * @create 2018-08-23 16:42
     **/
    @PostMapping("/findIndexNavigation")
    public Result<ListResponseVO<IndexNavigationResponseVO>> findIndexNavigation(@RequestBody RequestVO requestVO){
        return appIndexNavigationService.showIndexNavigation();
    }
}
