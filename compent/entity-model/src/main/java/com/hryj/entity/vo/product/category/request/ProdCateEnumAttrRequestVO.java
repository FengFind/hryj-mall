package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdCateEnumAttrRequestVO
 * @description:
 * @create 2018/7/5 0005 14:38
 **/
@ApiModel(value = "商品分类枚举类型属性的属性值VO", description = "商品分类枚举类型属性的属性值VO")
@Data
public class ProdCateEnumAttrRequestVO extends RequestVO {

    @ApiModelProperty(value = "属性ID")
    private Long category_attr_id;

    @ApiModelProperty(value = "枚举属性条目")
    private ProdCateAttrEnumItemRequestVO one_item;

    @ApiModelProperty(value = "枚举属性条目集合")
    private List<ProdCateAttrEnumItemRequestVO> many_items;
}
