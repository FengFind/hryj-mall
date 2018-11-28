package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 叶方宇
 * @className: ConfirmDistributionVO
 * @description:
 * @create 2018/7/10 0010 21:03
 **/
@Data
@ApiModel(value = "配送/取货完成参数VO")
public class ConfirmDistributionVO {

    @ApiModelProperty(value = "配送单状态:01-待分配,02-待配送,03-配送完成,04-配送超时", required = true)
    private String distribution_status;

    @ApiModelProperty(value = "配送单id", required = true)
    private Long id;

    @ApiModelProperty(value = "配送完成时间", hidden = true)
    private Date complete_time;
}
