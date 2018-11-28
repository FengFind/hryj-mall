package com.hryj.entity.vo.profit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 李道云
 * @className: StoreProfitDetailResponseVO
 * @description: 门店分润明细响应VO
 * @create 2018/7/9 21:20
 **/
@ApiModel(value = "门店分润明细响应VO")
@Data
public class StoreProfitDetailResponseVO implements Serializable {

    @ApiModelProperty(value = "店员数据列表", required = true)
    private List<StaffBalanceSummaryVO> staffDataList;

    @ApiModelProperty(value = "成本数据列表", required = true)
    private List<StoreCostDetailVO> costDataList;

    @ApiModelProperty(value = "配送数据列表", required = true)
    private List<StaffDistributionVO> distributionList;

}
