package com.hryj.entity.vo.order.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: DistributionListReponseVO
 * @description: 配送列表响应VO
 * @create 2018/6/30 10:59
 **/
@Data
@ApiModel(value = "配送列表响应VO")
public class DistributionListReponseVO {

    @ApiModelProperty(value = "用户配送单列表", required = true)
    private List<UserDistributionInfoVO> userDistributionList;

    @ApiModelProperty(value = "总共多少条数据", required = true)
    private String count;

}
