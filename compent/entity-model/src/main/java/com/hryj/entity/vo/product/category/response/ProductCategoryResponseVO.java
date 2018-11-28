package com.hryj.entity.vo.product.category.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductCategoryResponseVO
 * @description:
 * @create 2018/6/25 0025 19:25
 **/
@ApiModel(value = "商品分类完整数据VO", description = "商品分类完整数据VO")
@Data
public class ProductCategoryResponseVO {

    @ApiModelProperty(value = "商品分类ID")
    private Long product_category_id;

    @ApiModelProperty(value = "商品分类名称")
    private String category_name;

    @ApiModelProperty(value = "上级商品分类ID，一级分类该字段没有值")
    private Long category_pid;

    @ApiModelProperty(value = "上级商品分类名称，一级分类该字段没有值")
    private String parent_category_name;

    @ApiModelProperty(value = "分类图片的URL请求地址")
    private String category_url;

    @ApiModelProperty(value = "商品分类序号，该序号影响分类的展示")
    private Integer sort_num;

    @ApiModelProperty(value = "商品分类的属性集合")
    private List<ProdCateAttrResponseVO> attr_list;

    @ApiModelProperty(value = "下级商品分类集合")
    private List<ProductCategoryResponseVO> sub_list;
}
