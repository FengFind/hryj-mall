package com.hryj.entity.vo.profit.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StoreProfitDetailRequestVO
 * @description: 门店分润明细请求VO
 * @create 2018/7/9 21:19
 **/
@ApiModel(value = "门店分润明细请求VO")
@Data
public class StoreProfitDetailRequestVO extends RequestVO {

    @ApiModelProperty(value = "查询月份")
    private String query_month;

    @ApiModelProperty(value = "门店id", required = true)
    private Long store_id;
}
