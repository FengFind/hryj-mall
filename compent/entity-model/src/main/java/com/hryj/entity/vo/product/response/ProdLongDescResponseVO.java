package com.hryj.entity.vo.product.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProdLongDescResponseVO
 * @description:
 * @create 2018/6/27 0027 10:25
 **/
@ApiModel(value = "商品详细图文描述查询响应VO", description = "商品详细图文描述查询响应VO")
@Data
public class ProdLongDescResponseVO {

    @ApiModelProperty(value = "商品图文详情富文本信息")
    private String long_description;
}
