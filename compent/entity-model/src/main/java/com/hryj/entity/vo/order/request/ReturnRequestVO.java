package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ReturnRequest
 * @description:
 * @create 2018/7/18 0018 20:54
 **/
@Data
@ApiModel(value = "退货信息VO")
public class ReturnRequestVO extends RequestVO {

    @ApiModelProperty(value = "查询类型：01：查询待分配退货单，02：查询已取货退货单",required = true)
    private String find_type;

    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;
}
