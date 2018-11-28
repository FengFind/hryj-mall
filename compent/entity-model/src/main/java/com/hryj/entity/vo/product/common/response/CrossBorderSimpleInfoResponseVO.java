package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: CrossBorderDataResponseVO
 * @description:
 * @create 2018/9/11 0011 14:57
 **/
@ApiModel(value = "跨境商品简单信息响应vo", description = "跨境商品简单信息响应vo")
@Data
public class CrossBorderSimpleInfoResponseVO {

    @ApiModelProperty(value = "预计税费")
    private String tax_money;

    @ApiModelProperty(value = "国家或区域LOGO图片")
    private String logo_image;

    @ApiModelProperty(value = "国家或区域名")
    private String geo_name;

    @ApiModelProperty(value = "发货仓库")
    private String channel_name;

}
