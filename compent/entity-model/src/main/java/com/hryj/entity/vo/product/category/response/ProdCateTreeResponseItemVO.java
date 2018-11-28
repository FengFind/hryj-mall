package com.hryj.entity.vo.product.category.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdCateTreeResponseItemVO
 * @description: 商品分类树节点响应VO
 * @create 2018/7/11 0011 9:16
 **/
@ApiModel(value = "商品分类树节点响应VO", description = "商品分类树节点响应VO")
@Data
public class ProdCateTreeResponseItemVO {

    @ApiModelProperty(value = "商品分类ID", required = true)
    private String value;

    @ApiModelProperty(value = "商品分类名称", required = true)
    private String title;

    @ApiModelProperty(value = "父节点名称", required = true)
    private String parent_value;

    @ApiModelProperty(value = "子节点集合", required = true)
    private List<ProdCateTreeResponseItemVO> children;
}
