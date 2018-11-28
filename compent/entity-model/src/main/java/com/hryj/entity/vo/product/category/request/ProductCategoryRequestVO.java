package com.hryj.entity.vo.product.category.request;

import com.hryj.entity.vo.RequestVO;
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
@ApiModel(value = "创建商品分类请求VO", description = "创建商品分类请求VO")
@Data
public class ProductCategoryRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品分类ID", notes = "新增时不需要，修改时必须")
    private Long product_category_id;

    @ApiModelProperty(value = "商品分类名称", required = true)
    private String category_name;

    @ApiModelProperty(value = "上级商品分类ID，一级分类不需要，非一级分类需要")
    private Long category_pid;

    @ApiModelProperty(value = "分类图片的URL请求地址", required = true)
    private String category_url;

    @ApiModelProperty(value = "商品分类的属性集合")
    private List<ProdCateAttrRequestVO> attr_list;
}
