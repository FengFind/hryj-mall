package com.hryj.entity.vo.order.response.opentime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: OpenTimeResponse
 * @description: 门店营业时间段响应
 * @create 2018/7/13 0013 17:50
 **/
@Data
@ApiModel(value = "门店营业时间段响应")
public class OpenTimeResponse {

    @ApiModelProperty(value = "日期", required = true)
    private Long baseDate;

    @ApiModelProperty(value = "时间段列表", required = true)
    private List<TimeQuantum> quantumList;

}
