package com.hryj.entity.vo.product.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: SearchProductBrandRequestVO
 * @description:
 * @create 2018/9/10 0010 17:22
 **/
@ApiModel(value = "商品品牌搜索请求VO", description = "商品品牌搜索请求VO")
@Data
public class SearchProductBrandRequestVO {

    @ApiModelProperty(value = "品牌名称")
    private String brand_name;
}
