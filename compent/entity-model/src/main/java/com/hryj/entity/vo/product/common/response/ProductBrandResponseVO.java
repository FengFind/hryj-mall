package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: ProductBrandResponse
 * @description:
 * @create 2018/9/10 0010 17:16
 **/
@ApiModel(value = "商品品牌集合响应包装VO", description = "商品品牌集合响应包装VO")
@Data
public class ProductBrandResponseVO {

    @ApiModelProperty(value = "品牌ID")
    private Long brand_id;

    @ApiModelProperty(value = "品牌中文名称")
    private String brand_name;

    @ApiModelProperty(value = "品牌名称辅助,可以是品牌的英文名称或字母简写等")
    private String brand_name_assist;

    @ApiModelProperty(value = "品牌LOGO图片地址")
    private String logo_image;
}
