package com.hryj.entity.vo.order.response;

import com.hryj.entity.vo.order.ReturnVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: ReturnResponseVO
 * @description: 退货单响应信息VO
 * @create 2018/6/30 15:48
 **/
@Data
@ApiModel(value = "ReturnResponseVO")
public class ReturnResponseVO {

    @ApiModelProperty(value = "退货单信息列表", required = true)
    private List<ReturnVO> returnList;

    @ApiModelProperty(value = "总共多少条数据", required = true)
    private String count;
}
