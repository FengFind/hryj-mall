package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.promotion.activity.request.common.CommonProdCheckRequestVO;
import com.hryj.entity.vo.promotion.activity.response.common.CommonProdCheckResponseVO;
import com.hryj.exception.ServerException;
import com.hryj.service.ActivityCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ActivityCommonController
 * @description:
 * @create 2018/7/10 0010 17:46
 **/
@RestController
@RequestMapping("/promotionActivityCommon")
public class ActivityCommonController {

    @Autowired
    private ActivityCommonService activityCommonService;

    /**
     * @author 王光银
     * @methodName: checkProduct
     * @methodDesc: 从商品角度返回这个商品所有的有效的促销活动
     * @description:
     * @param: [commonProdCheckRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.common.CommonProdCheckResponseVO>
     * @create 2018-07-10 19:05
     **/
    @PostMapping("/checkProduct")
    public Result<CommonProdCheckResponseVO> checkProduct(@RequestBody CommonProdCheckRequestVO commonProdCheckRequestVO) throws ServerException {
        return activityCommonService.checkProduct(commonProdCheckRequestVO);
    }
}
