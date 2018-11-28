package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.DistributionInfoVO;
import com.hryj.entity.vo.order.DistributionProductVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: DistributionDetailResponseVO
 * @description: 配送单详情响应VO
 * @create 2018/6/30 11:08
 **/
@Data
@ApiModel(value = "配送单详情响应VO")
public class DistributionDetailResponseVO {

    @ApiModelProperty(value = "配送单信息VO", required = true)
    private DistributionInfoVO distributionInfoVO;

    @ApiModelProperty(value = "剩余时间", required = true)
    private String lastTime;

    @ApiModelProperty(value = "配送单商品列表", required = true)
    private List<DistributionProductVO> distributionProductList;

}
