package com.hryj.entity.vo.product.partyprod.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyProductStatisticsResponseVO
 * @description:
 * @create 2018/7/5 0005 21:06
 **/
@ApiModel(value = "门店或仓库商品统计请求VO", description = "门店或仓库商品统计请求VO")
@Data
public class PartyProductStatisticsItem {

    @ApiModelProperty(value = "门店或仓库的ID")
    private Long party_id;

    @ApiModelProperty(value = "所有商品数量")
    private Integer all_num = 0;

    @ApiModelProperty(value = "上架的商品数量")
    private Integer up_num = 0;

    @ApiModelProperty(value = "下架的商品数量")
    private Integer down_num = 0;
}
