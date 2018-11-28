package com.hryj.entity.vo.product.response;

import com.hryj.entity.vo.product.category.response.ProdCateAttrEnumItemResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdAttrItemResponseVO
 * @description:
 * @create 2018/6/27 0027 9:42
 **/
@ApiModel(value = "商品单个属性查询响应VO", description = "商品单个属性查询响应VO")
@Data
public class ProdAttrItemResponseVO {

    @ApiModelProperty(value = "商品属性ID")
    private Long prod_attr_id;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "属性类型， prod商品自身的属性， prod_cate继承自商品分类的属性")
    private String attr_type;

    @ApiModelProperty(value = "属性名称")
    private String attr_name;

    @ApiModelProperty(value = "属性值")
    private String attr_value;

    @ApiModelProperty(value = "商品分类属性ID，如果属性类型为prod_cate,那么该字段值表示继承的商品分类的哪个属性")
    private Long prod_cate_attr_id;

    @ApiModelProperty(value = "商品分类枚举属性的枚举条目ID，如果属性类型为prod_cate,并且继承的属性是枚举属性，那么该字段值表示使用的是哪一个枚举值")
    private Long prod_cate_attr_item_id;

    @ApiModelProperty(value = "对应的分类的枚举属性的所有枚举条目")
    private List<ProdCateAttrEnumItemResponseVO> attr_item_list;
}
