package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductOneImageRequestVO
 * @description: 商品一张详情图片请求VO
 * @create 2018/7/5 0005 9:35
 **/
@ApiModel(value = "商品一张详情图片请求VO", description = "商品一张详情图片请求VO")
@Data
public class ProductOneImageRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品详情图序号，1-5， 小于1按1处理，大于5按5处理", required = true)
    private Integer image_sort;

    @ApiModelProperty(value = "商品详情图", required = true)
    private String image_url;
}
