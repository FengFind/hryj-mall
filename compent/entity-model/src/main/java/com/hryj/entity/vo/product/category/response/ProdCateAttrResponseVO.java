package com.hryj.entity.vo.product.category.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdCateAttrResponseVO
 * @description:
 * @create 2018/6/25 0025 19:36
 **/
@ApiModel(value = "商品分类属性完整数据VO", description = "商品分类属性完整数据VO")
@Data
public class ProdCateAttrResponseVO {

    @ApiModelProperty(value = "商品分类属性ID(前端要求返的，返回nanoTime)", required = true)
    private Long prod_attr_id;

    @ApiModelProperty(value = "商品分类属性ID", required = true)
    private Long prod_cate_attr_id;

    @ApiModelProperty(value = "属性名称", required = true)
    private String attr_name;

    @ApiModelProperty(value = "属性类型, enum枚举, str字符串", required = true)
    private String attr_type;

    @ApiModelProperty(value = "属性为枚举类型时的枚举条目集合")
    private List<ProdCateAttrEnumItemResponseVO> attr_item_list;
}
