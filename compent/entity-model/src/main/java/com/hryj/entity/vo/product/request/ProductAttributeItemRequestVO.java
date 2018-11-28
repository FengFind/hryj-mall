package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductAttributeItemRequestVO
 * @description:
 * @create 2018/6/26 0026 14:16
 **/
@ApiModel(value = "新增一个商品属性请求VO", description = "新增一个商品属性请求VO")
@Data
public class ProductAttributeItemRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品属性ID， 修改单个商品属性时必须, 单个属性新增，批量新增和批量修改时不需要")
    private Long product_attribute_id;

    @ApiModelProperty(value = "属性类型, 01-分类属性,02-自定义属性", required = true)
    private String attr_type;

    @ApiModelProperty(value = "属性名称", required = true)
    private String attr_name;

    @ApiModelProperty(value = "属性值", required = true)
    private String attr_value;

    @ApiModelProperty(value = "商品分类属性ID, 属性为继承的分类属性时需要")
    private Long prod_cate_attr_id;

    @ApiModelProperty(value = "商品分类枚举属性的枚举条目ID, 属性为继承的分类属性，并且属性类型为枚举类型时需要")
    private Long prod_cate_attr_item_id;
}
