package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.product.TaxRate;
import com.hryj.entity.vo.product.common.request.SearchHSCodeRequestVO;
import com.hryj.entity.vo.product.common.response.ProductTaxRateResponseVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 汪豪
 * @className: TaxRateMapper
 * @description:
 * @create 2018/9/11 0011 14:34
 **/
@Component
public interface TaxRateMapper extends BaseMapper<TaxRate>{

    List<ProductTaxRateResponseVO> findTaxRateListByHSCode(SearchHSCodeRequestVO searchHSCodeRequestVO);
}
