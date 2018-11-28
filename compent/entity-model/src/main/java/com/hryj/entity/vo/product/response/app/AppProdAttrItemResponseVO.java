package com.hryj.entity.vo.product.response.app;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: AppProdAttrItemResponseVO
 * @description:
 * @create 2018/6/28 0028 20:42
 **/
@ApiModel(value = "APP端商品属性条件响应VO", description = "APP端商品属性条件响应VO")
@Data
public class AppProdAttrItemResponseVO {

    @ApiModelProperty(value = "属性名称")
    private String attr_name;

    @ApiModelProperty(value = "属性值")
    private String attr_value;
}
