package com.hryj.entity.vo.order;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 罗秋涵
 * @className: SingleParamVO
 * @description:
 * @create 2018/7/12 0012 17:44
 **/
@Data
@ApiModel(value = "订单模块共同单一参数请求VO")
public class SingleParamVO extends RequestVO {

    @ApiModelProperty(value = "String类型参数")
    private String stringParam;
    @ApiModelProperty(value = "Integer类型参数")
    private Integer intParam;
    @ApiModelProperty(value = "Date类型参数")
    private Date dateParam;
    @ApiModelProperty(value = "Long类型参数")
    private Long longParam;
    @ApiModelProperty(value = "Boolean类型参数")
    private Boolean booleanParam;
    @ApiModelProperty(value = "Double类型参数")
    private Double doubleParam;
    @ApiModelProperty(value = "BigDecimal类型参数")
    private BigDecimal bigDecimalParam;


}
