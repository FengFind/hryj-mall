package com.hryj.entity.vo.product.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductTopSalesItemResponseVO
 * @description:
 * @create 2018/7/3 0003 21:44
 **/
@ApiModel(value = "TOP销量商品响应VO", description = "TOP销量商品响应VO")
@Data
public class ProductTopSalesItemResponseVO {

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID")
    private Long party_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "品牌")
    private String brand_name;

    @ApiModelProperty(value = "品牌ID")
    private String brand;

    @ApiModelProperty(value = "库存数量")
    private String inventory_quantity;

    @ApiModelProperty(value = "商品售价")
    private String sale_price;

    @ApiModelProperty(value = "总销量")
    private Integer total_sales;
}
