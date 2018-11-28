package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductMadeWhere
 * @description:
 * @create 2018/9/10 0010 17:24
 **/
@ApiModel(value = "商品产地", description = "商品产地")
@Data
public class ProductMadeWhere {

    @ApiModelProperty(value = "产地ID")
    private Long id;

    @ApiModelProperty(value = "产地名称")
    private String made_where;

    @ApiModelProperty(value = "产地logo图片链接")
    private String logo_image;
}
