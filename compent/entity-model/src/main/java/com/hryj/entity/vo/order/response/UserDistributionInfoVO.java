package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.DistributionInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: UserDistributionInfoVO
 * @description:
 * @create 2018/7/3 0003 20:37
 **/
@Data
@ApiModel(value = "用户配送列表响应VO")
public class UserDistributionInfoVO {

    @ApiModelProperty(value = "配送单信息列表", required = true)
    private List<DistributionInfoVO> distributionList;
}
