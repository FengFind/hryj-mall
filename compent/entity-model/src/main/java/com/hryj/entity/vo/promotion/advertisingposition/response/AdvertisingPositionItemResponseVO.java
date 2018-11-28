package com.hryj.entity.vo.promotion.advertisingposition.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: AdvertisingPositionSearchResponseVO
 * @description:
 * @create 2018/6/27 0027 22:30
 **/
@ApiModel(value = "广告位分页查询响应VO", description = "广告位分页查询响应VO")
@Data
public class AdvertisingPositionItemResponseVO {

    @ApiModelProperty(value = "广告位ID")
    private Long advertising_position_id;

    @ApiModelProperty(value = "广告位名称")
    private String advertising_name;

    @ApiModelProperty(value = "创建人")
    private String operator_name;

    @ApiModelProperty(value = "类型 01-仓库,02-门店")
    private String advertising_scope;

    @ApiModelProperty(value = "展示开始时间")
    private String start_date;

    @ApiModelProperty(value = "展示结束时间")
    private String end_date;

    @ApiModelProperty(value = "状态，1正常，0停用")
    private String advertising_status;

    @ApiModelProperty(value = "广告位类型, 01 banner广告")
    private String advertising_type;

}
