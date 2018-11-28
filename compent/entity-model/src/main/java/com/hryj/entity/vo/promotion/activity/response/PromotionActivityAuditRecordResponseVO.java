package com.hryj.entity.vo.promotion.activity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PromotionActivityAuditRecordResponseVO
 * @description:
 * @create 2018/6/28 0028 16:48
 **/
@ApiModel(value = "促销活动审核记录响应VO", description = "促销活动审核记录响应VO")
@Data
public class PromotionActivityAuditRecordResponseVO {

    @ApiModelProperty(value = "审核记录ID")
    private Long audit_record_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动开始时间，格式:yyyy-MM-dd HH:mm:ss")
    private String start_date;

    @ApiModelProperty(value = "活动结束时间，格式:yyyy-MM-dd HH:mm:ss")
    private String end_date;

    @ApiModelProperty(value = "活动类型:01-爆款 02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动提交人姓名")
    private String submit_staff_name;

    @ApiModelProperty(value = "提交时间,格式: yyyy-MM-dd HH:mm:ss")
    private String submit_time;

    @ApiModelProperty(value = "审核处理人姓名")
    private String handle_staff_name;

    @ApiModelProperty(value = "活动审核处理结果，1-通过,2-驳回")
    private Boolean handle_result;

    @ApiModelProperty(value = "审核处理时间,格式: yyyy-MM-dd HH:mm:ss")
    private String handle_time;

    @ApiModelProperty(value = "审核处理意见")
    private String audit_remark;
}
