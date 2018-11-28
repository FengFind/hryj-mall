package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: StaffDistributionVO
 * @description: 店员配送数据VO
 * @create 2018/7/9 22:30
 **/
@ApiModel(value = "店员配送数据VO")
@Data
public class StaffDistributionVO {

    @ApiModelProperty(value = "店员配送数据id", required = true)
    private Long staff_distribution_id;

    @ApiModelProperty(value = "店员姓名", required = true)
    private String staff_name;

    @ApiModelProperty(value = "配送数量", required = true)
    private Integer distribution_num;

    @ApiModelProperty(value = "配送分润", required = true)
    private BigDecimal distribution_profit;
}
