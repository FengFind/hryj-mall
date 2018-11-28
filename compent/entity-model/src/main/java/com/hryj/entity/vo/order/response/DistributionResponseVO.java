package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.DistributionInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: DistributionResponseVO
 * @description:
 * @create 2018/7/17 0017 21:41
 **/
@Data
@ApiModel(value = "配送列表响应VO")
public class DistributionResponseVO {

    @ApiModelProperty(value = "配送单信息列表", required = true)
    private List<DistributionInfoVO> distributionList;

    @ApiModelProperty(value = "总共多少条数据", required = true)
    private String count;


}
