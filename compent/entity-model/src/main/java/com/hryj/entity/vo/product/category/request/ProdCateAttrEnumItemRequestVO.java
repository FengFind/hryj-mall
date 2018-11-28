package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProdCateAttrEnumItemResponseVO
 * @description: 商品分类枚举属性-枚举条目VO
 * @create 2018/6/25 0025 19:36
 **/
@ApiModel(value = "商品分类枚举属性-枚举条目VO", description = "商品分类枚举属性-枚举条目VO")
@Data
public class ProdCateAttrEnumItemRequestVO extends RequestVO {

    @ApiModelProperty(value = "枚举属性条目ID，在修改单个条目时需要, 其他情况下不需要")
    private Long attr_enum_item_id;

    @ApiModelProperty(value = "分类属性枚举条目值", required = true)
    private String attr_value;
}
