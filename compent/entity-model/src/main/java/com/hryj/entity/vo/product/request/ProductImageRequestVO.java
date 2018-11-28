package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductImageRequestVO
 * @description:
 * @create 2018/7/5 0005 9:25
 **/
@ApiModel(value = "商品图片请求VO", description = "商品图片请求VO")
@Data
public class ProductImageRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品列表图", required = true)
    private String list_image;

    @ApiModelProperty(value = "商品详情图", required = true)
    private List<String> detail_image;
}
