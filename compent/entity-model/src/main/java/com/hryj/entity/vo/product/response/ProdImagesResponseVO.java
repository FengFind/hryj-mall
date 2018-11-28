package com.hryj.entity.vo.product.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProdImagesResponseVO
 * @description: 商品图片信息响应VO
 * @create 2018/6/27 0027 10:22
 **/
@ApiModel(value = "商品图片信息响应VO", description = "商品图片信息响应VO")
@Data
public class ProdImagesResponseVO {

    @ApiModelProperty(value = "列表图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "详情图片URL集合")
    private List<String> detail_image_list;
}
