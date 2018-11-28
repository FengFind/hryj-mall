package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: DistributionForStoreRequestVO
 * @description: 查询配送单或取货单列表VO
 * @create 2018/7/5 0005 11:13
 **/
@Data
@ApiModel(value = "查询配送单或取货单列表VO")
public class DistributionForStoreRequestVO extends RequestVO {

    @ApiModelProperty(value = "配送单类型",required = true)
    private String distribution_type;

    @ApiModelProperty(value = "配送单状态",required = true)
    private String distribution_status;

    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;
}
