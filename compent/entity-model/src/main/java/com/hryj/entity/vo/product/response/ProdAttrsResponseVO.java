package com.hryj.entity.vo.product.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdAttrsResponseVO
 * @description:
 * @create 2018/6/27 0027 10:18
 **/
@ApiModel(value = "商品所有属性查询响应VO", description = "商品属性查询响应VO")
@Data
public class ProdAttrsResponseVO {

    @ApiModelProperty(value = "继承的分类属性集合")
    private List<ProdAttrItemResponseVO> category_attr_list;

    @ApiModelProperty(value = "商品自定义属性集合")
    private List<ProdAttrItemResponseVO> attr_list;
}
