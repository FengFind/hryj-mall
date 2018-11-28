package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 王光银
 * @className: ProductSimpleInfoItem
 * @description:
 * @create 2018/8/17 0017 10:39
 **/
@ApiModel(value = "商品简单信息响应条目", description = "商品简单信息响应条目")
@Data
public class ProductSimpleInfoItem {

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID")
    private Long party_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品售价")
    private String sale_price;

    @ApiModelProperty(value = "成本价")
    private BigDecimal cost_price;

    @ApiModelProperty(value = "商品分类id")
    private Long prod_cate_id;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "品牌")
    private String brand_name;

}
