package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: ReturnProductVO
 * @description: 退货单商品信息
 * @create 2018/6/30 15:56
 **/
@Data
@ApiModel(value = "退货单商品信息")
public class ReturnProductVO {

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "商品名称", required = true)
    private String product_name;

    @ApiModelProperty(value = "商品图片", required = true)
    private String list_image_url;

    @ApiModelProperty(value = "实际价格", required = true)
    private String return_amt;

    @ApiModelProperty(value = "数量", required = true)
    private String quantity;
}
