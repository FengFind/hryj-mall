package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 汪豪
 * @className: ActivityIdProductIdRequestVO
 * @description:
 * @create 2018/7/10 0010 11:45
 **/
@ApiModel(value = "活动id和商品id请求VO", description = "活动id和商品id请求VO")
@Data
public class ActivityIdProductIdRequestVO {

    @ApiModelProperty(value = "活动id", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;
}
