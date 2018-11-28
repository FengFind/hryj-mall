package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.product.ProductGeo;
import com.hryj.entity.vo.product.common.request.SearchProductGeoRequestVO;
import com.hryj.entity.vo.product.common.response.ProductGeoResponseVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 汪豪
 * @className: ProductGeoMapper
 * @description:
 * @create 2018/9/11 0011 14:34
 **/
@Component
public interface ProductGeoMapper extends BaseMapper<ProductGeo> {

    List<ProductGeoResponseVO> getProductGeoList();
}
