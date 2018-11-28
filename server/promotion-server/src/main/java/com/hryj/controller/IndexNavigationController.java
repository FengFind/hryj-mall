package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.indexNavigation.request.IndexNavigationListRequestVO;
import com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO;
import com.hryj.service.IndexNavigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 汪豪
 * @className: IndexNavigationController
 * @description: 后台管理首页导航Controller
 * @create 2018/8/23 0023 14:00
 **/
@RestController
@RequestMapping("/indexNavigationMgr")
public class IndexNavigationController {

    @Autowired
    private IndexNavigationService indexNavigationService;

    /**
     * @author 汪豪
     * @methodName: createOrUpdateIndexNavigation
     * @methodDesc:
     * @description:
     * @param: [indexNavigationListRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-23 14:15
     **/
    @PostMapping("/createOrUpdateIndexNavigation")
    public Result createOrUpdateIndexNavigation(@RequestBody IndexNavigationListRequestVO indexNavigationListRequestVO){
        return indexNavigationService.createOrUpdateIndexNavigation(indexNavigationListRequestVO);
    }

    /**
     * @author 汪豪
     * @methodName: showIndexNavigation
     * @methodDesc:
     * @description:
     * @param: []
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.promotion.indexNavigation.response.IndexNavigationResponseVO>>
     * @create 2018-08-23 14:43
     **/
    @PostMapping("/showIndexNavigation")
    public Result<ListResponseVO<IndexNavigationResponseVO>> showIndexNavigation(@RequestBody RequestVO requestVO){
        return indexNavigationService.showIndexNavigation();
    }
}
