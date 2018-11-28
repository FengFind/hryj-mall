package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductAttributeItemRequestVO
 * @description:
 * @create 2018/6/26 0026 14:16
 **/
@ApiModel(value = "批量新增修改商品属性请求VO", description = "批量新增修改商品属性请求VO")
@Data
public class ProductAttributeRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品的单个属性，单个属性处理时该参数有效")
    private ProductAttributeItemRequestVO attr;

    @ApiModelProperty(value = "商品的属性集合,批量属性处理时，该参数有效")
    private List<ProductAttributeItemRequestVO> attr_list;
}
