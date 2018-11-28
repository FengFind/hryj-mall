package com.hryj.entity.vo.order;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: handelReturnOrderRequestVO
 * @description:
 * @create 2018/7/5 0005 12:17
 **/
@Data
@ApiModel(value = "处理退货单参数")
public class handelReturnOrderRequestVO extends RequestVO {

    @ApiModelProperty(value = "退货单id", required = true)
    private Long return_id;
    @ApiModelProperty(value = "退货处理状态", required = true)
    private String return_status;
    @ApiModelProperty(value = "退货处理说明", required = true)
    private String return_handel_remark;

}
