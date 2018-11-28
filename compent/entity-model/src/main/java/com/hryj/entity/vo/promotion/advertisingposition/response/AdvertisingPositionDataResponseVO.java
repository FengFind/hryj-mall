package com.hryj.entity.vo.promotion.advertisingposition.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: AdvertisingPositionDataResponseVO
 * @description:
 * @create 2018/7/16 0016 20:26
 **/
@ApiModel(value = "广告位详细信息查询响应VO", description = "广告位详细信息查询响应VO")
@Data
public class AdvertisingPositionDataResponseVO {

    @ApiModelProperty(value = "广告位ID")
    private Long advertising_id;

    @ApiModelProperty(value = "广告位名称")
    private String advertising_name;

    @ApiModelProperty(value = "广告位范围 01-仓库 02-门店")
    private String advertising_scope;

    @ApiModelProperty(value = "广告位展示开始时间，格式:yyyy-MM-dd HH:mm:ss")
    private String start_date;

    @ApiModelProperty(value = "广告位展示结束时间，格式:yyyy-MM-dd HH:mm:ss")
    private String end_date;

    @ApiModelProperty(value = "广告banner图URL")
    private String advertising_image;

    @ApiModelProperty(value = "跳转类型:01-跳转url,02-跳转商品,03-跳转活动")
    private String jump_type;

    @ApiModelProperty(value = "跳转目标值:该字段值根据跳转类型存储对应的值")
    private String jump_value;

    @ApiModelProperty(value = "广告参与门店或仓库数据")
    private AdvertisingPositionJoinPartyDataResponseVO join_party_data;
}
