package com.hryj.entity.vo.product.category.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProdCateAttrEnumItemResponseVO
 * @description:
 * @create 2018/6/25 0025 19:36
 **/
@ApiModel(value = "商品分类枚举类型属性所有枚举条目响应VO", description = "商品分类枚举类型属性所有枚举条目响应VO")
@Data
public class ProdCateAttrEnumItemResponseVO {

    @ApiModelProperty(value = "枚举属性条目ID", required = true)
    private Long attr_enum_item_id;

    @ApiModelProperty(value = "分类属性枚举条目值", required = true)
    private String attr_value;
}
