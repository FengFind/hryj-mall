package com.hryj.entity.vo.order.response.opentime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: TimeQuantum
 * @description: 时间段
 * @create 2018/7/13 0013 17:51
 **/
@Data
@ApiModel(value = "时间段VO")
public class TimeQuantum {

    @ApiModelProperty(value = "开始时间", required = true)
    private Long beginTime;

    @ApiModelProperty(value = "结束时间", required = true)
    private Long endTime;
}
