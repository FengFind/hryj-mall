package com.hryj.entity.vo.product.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: SearchHSCodeRequestVO
 * @description:
 * @create 2018/9/11 0011 9:02
 **/
@ApiModel(value = "商品HSCODE搜索请求VO", description = "商品HSCODE搜索请求VO")
@Data
public class SearchHSCodeRequestVO {

    @ApiModelProperty(value = "HSCODE")
    private String hs_code;
}
