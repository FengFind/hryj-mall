package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: SendOrdersRequestVO
 * @description: 分派配送单或取货单VO
 * @create 2018/7/5 0005 11:19
 **/
@Data
@ApiModel(value = "分派配送单或取货单VO")
public class SendOrdersRequestVO extends RequestVO {

    @ApiModelProperty(value = "单编号集合，逗号隔开",required = true)
    private String distribution_ids;

    @ApiModelProperty(value = "配送人Id",required = true)
    private Long staff_id;

    @ApiModelProperty(value = "配送人姓名",required = true)
    private String staff_name;

    @ApiModelProperty(value = "配送人电话",required = true)
    private String staff_phone;
}
