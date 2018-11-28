package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductBrand
 * @description:
 * @create 2018/9/10 0010 17:23
 **/
@ApiModel(value = "商品品牌", description = "商品品牌")
@Data
public class ProductBrand {

    @ApiModelProperty(value = "品牌ID")
    private Long id;

    @ApiModelProperty(value = "品牌名称")
    private String brand_name;

    @ApiModelProperty(value = "品牌LOGO链接")
    private String logo_image;
}
