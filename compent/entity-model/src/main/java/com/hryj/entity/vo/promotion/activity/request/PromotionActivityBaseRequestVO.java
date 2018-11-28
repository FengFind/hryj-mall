package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PromotionActivityBaseRequestVO
 * @description:
 * @create 2018/6/28 0028 11:41
 **/
@ApiModel(value = "修改活动基本信息请求VO", description = "修改活动基本信息请求VO")
@Data
public class PromotionActivityBaseRequestVO extends RequestVO {

    @ApiModelProperty(value = "活动ID", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "活动名称", required = true)
    private String activity_name;

    @ApiModelProperty(value = "活动开始时间，格式:yyyy-MM-dd HH:mm:ss", required = true)
    private String start_date;

    @ApiModelProperty(value = "活动结束时间，格式:yyyy-MM-dd HH:mm:ss", required = true)
    private String end_date;

    @ApiModelProperty(value = "活动banner图URL", required = true)
    private String activity_image;

    @ApiModelProperty(value = "活动样式:01-活动模板,02-指定url,  目前只支持01活动模板，该参数缺省处理为01", required = false)
    private String activity_style;

    @ApiModelProperty(value = "活动模板内容, 模板内容的存储有两种方式，一种是将H5模板的代码内容整个进行存储，另一种是存储一个H5模板的访问URL", required = true)
    private String templete_data;

    @ApiModelProperty(value = "活动详情", required = true)
    private String activity_detail;

}
