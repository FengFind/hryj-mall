package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: ProductGeoResponse
 * @description:
 * @create 2018/9/10 0010 17:09
 **/
@ApiModel(value = "商品产地集合响应包装VO", description = "商品产地集合响应包装VO")
@Data
public class ProductGeoResponseVO {

    @ApiModelProperty(value = "国家、区域ID")
    private Long geo_id;

    @ApiModelProperty(value = "类型， 1 国家，2地区或城市")
    private int type;

    @ApiModelProperty(value = "国家或地区的代码")
    private String code;

    @ApiModelProperty(value = "国家或区域名称")
    private String name;

    @ApiModelProperty(value = "国家或区域LOGO图片")
    private String logo_image;
}
