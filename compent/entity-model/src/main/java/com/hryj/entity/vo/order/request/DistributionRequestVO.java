package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: DistributionRequestVO
 * @description: 配送单
 * @create 2018/7/5 0005 11:04
 **/

@Data
@ApiModel(value = "配送单vo")
public class DistributionRequestVO extends RequestVO {

    @ApiModelProperty(value = ":01-待分配,02-待配送,03-配送完成,04-配送超时",required = true)
    private String distribution_status;
    @ApiModelProperty(value = "配送类别:01-送货,02-取货",required = true)
    private String distribution_type;
    @ApiModelProperty(value = "配送人ID", hidden = true)
    private Long distribution_staff_id;
    @ApiModelProperty(value = "查询开始日期")
    private String start_date;
    @ApiModelProperty(value = "查询截止日期")
    private  String end_date;
    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;
    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;
}
