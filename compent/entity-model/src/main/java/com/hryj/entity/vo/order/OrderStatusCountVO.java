package com.hryj.entity.vo.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 叶方宇
 * @className: OrderStatusCountVO
 * @description:
 * @create 2018/7/9 0009 18:41
 **/

@Data
@ApiModel(value = "根据条件获取配送/取件 数量")
public class OrderStatusCountVO {

    @ApiModelProperty(value = "配送员ID", required = true)
    private Long distribution_staff_id;

    @ApiModelProperty(value = "配送状态", required = true)
    private String distribution_status;

    @ApiModelProperty(value = "配送信息", required = true)
    private String distribution_type;
}
