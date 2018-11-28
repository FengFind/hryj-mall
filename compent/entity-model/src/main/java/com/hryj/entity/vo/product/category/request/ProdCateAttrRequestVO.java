package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdCateAttrRequestVO
 * @description: 商品分类属性VO
 * @create 2018/6/25 0025 19:36
 **/
@ApiModel(value = "商品分类属性VO", description = "商品分类属性VO")
@Data
public class ProdCateAttrRequestVO extends RequestVO {

    @ApiModelProperty(value = "属性ID", notes = "修改一个或一组同类型属性时需要")
    private Long prod_cate_attr_id;

    @ApiModelProperty(value = "属性名称", required = true)
    private String attr_name;

    @ApiModelProperty(value = "属性类型, 01-枚举,02-字符串", required = true)
    private String attr_type;

    @ApiModelProperty(value = "属性为枚举类型时的枚举条目集合")
    private List<ProdCateAttrEnumItemRequestVO> attr_item_list;
}
