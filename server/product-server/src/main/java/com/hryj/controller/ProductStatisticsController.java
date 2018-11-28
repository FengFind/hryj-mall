package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.product.statistics.request.ProductStatisticsRequestVO;
import com.hryj.entity.vo.product.statistics.request.ProductViewStatisticsRequestVO;
import com.hryj.service.ProductStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: ProductStatisticsController
 * @description: 商品统计接口
 * @create 2018/7/3 0003 21:21
 **/
@RestController
@RequestMapping("/productStatistics")
public class ProductStatisticsController {

    @Autowired
    private ProductStatisticsService statisticsService;

    /**
     * @author 王光银
     * @methodName: salesStatistics
     * @methodDesc: 商品销售统计接口
     * @description:
     * @param: [productStatisticsRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-04 11:42
     **/
    @PostMapping("/salesStatistics")
    public Result salesStatistics(@RequestBody ProductStatisticsRequestVO productStatisticsRequestVO) {
        return statisticsService.salesStatistics(productStatisticsRequestVO);
    }

    /**
     * @author 王光银
     * @methodName: viewStatistics
     * @methodDesc: 商品PV统计记录接口
     * @description:
     * @param: [productViewStatisticsRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-04 11:50
     **/
    @PostMapping("/viewStatistics")
    public Result viewStatistics(@RequestBody ProductViewStatisticsRequestVO productViewStatisticsRequestVO) {
        return statisticsService.viewStatistics(productViewStatisticsRequestVO);
    }
}
