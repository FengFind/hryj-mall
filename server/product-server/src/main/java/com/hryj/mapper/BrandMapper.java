package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.vo.product.common.request.SearchProductBrandRequestVO;
import com.hryj.entity.vo.product.common.response.ProductBrandResponseVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 汪豪
 * @className: BrandMapper
 * @description:
 * @create 2018/9/11 0011 14:35
 **/
@Component
public interface BrandMapper extends BaseMapper<Brand> {

    List<ProductBrandResponseVO> getBrandList();
}
