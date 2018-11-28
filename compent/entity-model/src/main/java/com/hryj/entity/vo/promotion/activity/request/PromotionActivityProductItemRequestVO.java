package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PromotionActivityProductItemRequestVO
 * @description:
 * @create 2018/6/28 0028 11:26
 **/
@ApiModel(value = "活动商品请求VO", description = "活动商品请求VO")
@Data
public class PromotionActivityProductItemRequestVO {

    @ApiModelProperty(value = "活动商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "活动价格，最多两位小数，超过两位小数会进行四舍五入处理为两位小数", required = true)
    private String activity_price;
}
