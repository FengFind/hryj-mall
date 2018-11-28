package com.hryj.entity.vo.profit.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: NonFixedCostSetRequestVO
 * @description: 非固定成本设置请求VO
 * @create 2018/7/9 21:02
 **/
@ApiModel(value = "非固定成本设置请求VO")
@Data
public class NonFixedCostSetRequestVO extends RequestVO {

    @ApiModelProperty(value = "结算汇总id", required = true)
    private Long summary_id;

    @ApiModelProperty(value = "非固定成本", required = true)
    private String non_fixed_cost;

    @ApiModelProperty(value = "设置状态", notes = "1-已设置,0-未设置", required = true)
    private Integer setup_status;
}
