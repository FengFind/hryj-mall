package com.hryj.entity.vo.product.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: SearchProductGeoRequestVO
 * @description:
 * @create 2018/9/10 0010 17:25
 **/
@ApiModel(value = "商品产地搜索请求VO", description = "商品品牌搜索请求VO")
@Data
public class SearchProductGeoRequestVO {


    @ApiModelProperty(value = "限定查找范围: 1 国家，2地区或城市，不传值查所有")
    private Integer type;

    @ApiModelProperty(value = "产地名称")
    private String geo_name;
}
