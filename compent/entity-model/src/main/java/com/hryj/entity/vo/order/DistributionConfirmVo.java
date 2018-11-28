package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: DistributionConfirmRequestVo
 * @description:
 * @create 2018/7/20 0020 10:45
 **/
@Data
@ApiModel(value = "配送,取货完成参数VO")
public class DistributionConfirmVo {

    @ApiModelProperty(value = "配送单id",required = true)
    private Long distribution_id;

    @ApiModelProperty(value="订单id",required = true)
    private Long order_id;

    @ApiModelProperty(value = "配送类别:01-送货,02-取货", required = true)
        private String distribution_type;
}
