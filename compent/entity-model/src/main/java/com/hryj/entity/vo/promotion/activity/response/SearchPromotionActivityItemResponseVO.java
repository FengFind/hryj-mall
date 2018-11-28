package com.hryj.entity.vo.promotion.activity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: SearchPromotionActivityItemResponseVO
 * @description:
 * @create 2018/6/28 0028 10:02
 **/
@ApiModel(value = "促销活动条目响应VO", description = "促销活动条目响应VO")
@Data
public class SearchPromotionActivityItemResponseVO {

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动类型:01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动审核状态:0-待审核,1-审核通过,2-审核未通过")
    private Long audit_status;

    @ApiModelProperty(value = "活动状态:1-正常,0-停用")
    private Long activity_status;

    @ApiModelProperty(value = "活动开始时间")
    private String start_date;

    @ApiModelProperty(value = "活动结束时间")
    private String end_date;

    @ApiModelProperty(value = "活动创建人")
    private String operator_name;

    @ApiModelProperty(value = "活动创建时间")
    private String create_time;
}
