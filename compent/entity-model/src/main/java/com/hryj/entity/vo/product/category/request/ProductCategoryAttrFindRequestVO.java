package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductCategoryAttrFindRequestVO
 * @description:
 * @create 2018/7/5 0005 10:58
 **/
@ApiModel(value = "商品分类属性查询请求VO", description = "商品分类属性查询请求VO")
@Data
public class ProductCategoryAttrFindRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品分类ID", required = true)
    private Long product_category_id;

    @ApiModelProperty(value = "返回属性类型， 01枚举属性， 02字符串属性, 缺省返回所有")
    private String attr_type;

    @ApiModelProperty(value = "是否返回当前分类的父级分类的属性，true 返回, false不返回, 缺省不返回")
    private Boolean return_parent_attr;
}
