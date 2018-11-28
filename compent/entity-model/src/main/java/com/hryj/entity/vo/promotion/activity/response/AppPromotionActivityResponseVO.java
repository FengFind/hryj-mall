package com.hryj.entity.vo.promotion.activity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: AppPromotionActivityResponseVO
 * @description:
 * @create 2018/6/28 0028 19:40
 **/
@ApiModel(value = "APP端查询活动响应VO", description = "APP端查询活动响应VO")
@Data
public class AppPromotionActivityResponseVO {

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动banner图")
    private String activity_image;

    @ApiModelProperty(value = "活动开始时间")
    private String start_date;

    @ApiModelProperty(value = "活动结束时间")
    private String end_date;

    @ApiModelProperty(value = "活动样式:01-活动模板,02-指定url")
    private String activity_style;

    @ApiModelProperty(value = "活动模板内容, 模板内容的存储有两种方式，一种是将H5模板的代码内容整个进行存储，另一种是存储一个H5模板的访问URL")
    private String templete_data;
}
