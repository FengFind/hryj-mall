package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.sys.response.CodeInfoVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: ReturnReasonResponseVO
 * @description: 退货原因响应
 * @create 2018/7/3 0003 16:33
 **/
@Data
@ApiModel(value = "退货原因响应VO")
public class ReturnReasonResponseVO {

    @ApiModelProperty(value = "退货申请原因")
    private List<CodeInfoVO> return_reason_list;

}
