package com.hryj.entity.vo.product.category.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductCategoryItemResponseVO
 * @description:
 * @create 2018/6/28 0028 17:26
 **/
@ApiModel(value = "适用于APP端的商品分类条目响应VO", description = "适用于APP端的商品分类条目响应VO")
@Data
public class ProductCategoryItemResponseVO {

    @ApiModelProperty(value = "分类ID")
    private Long category_id;

    @ApiModelProperty(value = "分类名称")
    private String category_name;

    @ApiModelProperty(value = "分类图片URL")
    private String image_url;
}
