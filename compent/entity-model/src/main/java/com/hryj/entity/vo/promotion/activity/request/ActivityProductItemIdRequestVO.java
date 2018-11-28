package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: ActivityProductItemIdRequestVO
 * @description:
 * @create 2018/7/5 0005 21:13
 **/
@ApiModel(value = "活动商品id请求VO", description = "如业务只需活动商品id时使用")
@Data
public class ActivityProductItemIdRequestVO {

    @ApiModelProperty(value = "活动商品id", required = true)
    private Long activity_product_item_id;
}
