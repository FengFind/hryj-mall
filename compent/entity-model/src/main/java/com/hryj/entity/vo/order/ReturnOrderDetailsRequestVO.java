package com.hryj.entity.vo.order;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ReturnOrderDetailsRequestVO
 * @description:
 * @create 2018/7/5 0005 12:04
 **/
@Data
@ApiModel(value = "退货信息请求VO")
public class ReturnOrderDetailsRequestVO extends RequestVO {

    @ApiModelProperty(value = "退货单编号")
    private Long  return_id;

}
