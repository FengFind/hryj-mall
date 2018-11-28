package com.hryj.entity.vo.cart;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: InvalidCartProductVO
 * @description: 失效的购车商品信息
 * @create 2018/7/2 16:43
 **/
@ApiModel(value = "失效的购车商品信息")
@Data
public class InvalidCartProductVO {

    @ApiModelProperty(value = "购物车记录id", required = true)
    private Long cart_record_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品名称", required = true)
    private String product_name;

    @ApiModelProperty(value = "商品图片", required = true)
    private String list_image_url;

    @ApiModelProperty(value = "加入时单价", required = true)
    private String into_cart_price;

    @ApiModelProperty(value = "销售价格", required = true)
    private String sale_price;

    @ApiModelProperty(value = "数量", required = true)
    private String quantity;

    @ApiModelProperty(value = "活动价格", required = true)
    private String activity_price;

}
