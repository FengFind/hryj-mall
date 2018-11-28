package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductCategoryFindRequestVO
 * @description: 商品分类查询请求VO
 * @create 2018/7/5 0005 10:53
 **/
@ApiModel(value = "商品分类查询请求VO", description = "商品分类查询请求VO")
@Data
public class ProductCategoryFindRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品分类名称， 前后模糊匹配")
    private String category_name;

    @ApiModelProperty(value = "是否返回分类的属性数据，true返回  false不返回， 目前并未实现属性返回逻辑")
    private Boolean include_attr;
}
