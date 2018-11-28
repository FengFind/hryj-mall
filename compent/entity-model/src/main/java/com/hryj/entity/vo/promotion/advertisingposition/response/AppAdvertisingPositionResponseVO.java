package com.hryj.entity.vo.promotion.advertisingposition.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @author 王光银
 * @className: AppAdvertisingPositionResponseVO
 * @description:
 * @create 2018/6/28 0028 19:29
 **/
@ApiModel(value = "APP端广告位查询响应VO", description = "APP端广告位查询响应VO")
@Data
public class AppAdvertisingPositionResponseVO {

    @ApiModelProperty(value = "广告位id")
    private Long advertising_id;

    @ApiModelProperty(value = "门店或仓库id")
    private Long party_id;

    @ApiModelProperty(value = "跳转类型:01-跳转url,02-跳转商品,03-跳转活动")
    private String jump_type;

    @ApiModelProperty(value = "跳转目标值:该字段值根据跳转类型存储对应的值")
    private String jump_value;

    @ApiModelProperty(value = "广告位图片")
    private String image_url;

    @ApiModelProperty(value = "广告开始时间")
    private String start_date;

    @ApiModelProperty(value = "广告结束时间")
    private String end_date;
}
