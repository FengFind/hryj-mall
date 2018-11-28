package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: PartyProductActivityRequestVO
 * @description:
 * @create 2018/8/16 0016 8:56
 **/
@ApiModel(value = "门店或仓库id、活动id和商品id请求VO", description = "门店或仓库id、活动id和商品id请求VO")
@Data
public class PartyProductActivityRequestVO {

    @ApiModelProperty(value = "门店或仓库id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品id", required = true)
    private Long product_id;

    @ApiModelProperty(value = "活动id", required = true)
    private Long activity_id;
}
