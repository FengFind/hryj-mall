package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.Result;
import com.hryj.entity.bo.product.ProductSummary;
import com.hryj.entity.vo.product.statistics.request.ProductStatisticsRequestVO;
import com.hryj.entity.vo.product.statistics.request.ProductViewStatisticsRequestVO;
import com.hryj.mapper.ProductSummaryMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author 王光银
 * @className: ProductStatisticsService
 * @description: 商品统计服务实现
 * @create 2018/7/4 0004 11:51
 **/
@Slf4j
@Service
public class ProductStatisticsService extends ServiceImpl<ProductSummaryMapper, ProductSummary> {

    public Result salesStatistics(ProductStatisticsRequestVO productStatisticsRequestVO) {
        return null;
    }

    public Result viewStatistics(ProductViewStatisticsRequestVO productViewStatisticsRequestVO) {
        return null;
    }

}
