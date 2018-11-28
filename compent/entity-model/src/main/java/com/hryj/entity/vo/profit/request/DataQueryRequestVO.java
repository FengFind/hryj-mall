package com.hryj.entity.vo.profit.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: DataQueryRequestVO
 * @description: 数据查询请求VO
 * @create 2018/7/7 11:16
 **/
@ApiModel(value = "数据查询请求VO")
@Data
public class DataQueryRequestVO extends RequestVO {

    @ApiModelProperty(value = "查询开始日期", required = true)
    private String start_date;

    @ApiModelProperty(value = "查询截止日期", required = true)
    private String end_date;

    @ApiModelProperty(value = "员工id", notes = "选择员工的时候有值")
    private Long staff_id;

    @ApiModelProperty(value = "门店id", notes = "选择门店的时候有值")
    private Long store_id;

    @ApiModelProperty(value = "部门id", notes = "选择部门的时候有值")
    private Long dept_id;

    @ApiModelProperty(value = "仓库id", notes = "选择仓库的时候有值")
    private Long wh_id;
}
