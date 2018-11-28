package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ReturnOrderListRequestVo
 * @description: 获取待处理退货列表VO
 * @create 2018/7/10 0010 20:08
 **/
@Data
@ApiModel(value = "获取待处理退货列表VO")
public class ReturnOrderListRequestVO extends RequestVO {

    @ApiModelProperty(value = "退货单状态", required = true)
    private String return_status;

    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;
}
