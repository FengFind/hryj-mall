package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: SubmitActivityRequestVO
 * @description:
 * @create 2018/7/5 0005 21:32
 **/
@ApiModel(value = "提交审核结果请求VO", description = "提交审核结果请求VO")
@Data
public class SubmitActivityRequestVO extends RequestVO {

    @ApiModelProperty(value = "活动id", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "处理结果:1-通过,2-不通过", required = true)
    private String handle_result;

    @ApiModelProperty(value = "审核描述", required = false)
    private String handle_remark;
}
