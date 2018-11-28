package com.hryj.entity.vo.staff.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "员工组织关系响引VO")
public class StaffDeptRelationResponseVO {

    @ApiModelProperty(value = "员工id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long staff_id;

    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "门店id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long store_id;

    @ApiModelProperty(value = "门店名称")
    private String store_name;

    @ApiModelProperty(value = "员工岗位")
    private String staff_job;

    @ApiModelProperty(value = "工资金额")
    private BigDecimal salary_amt;

    @ApiModelProperty(value = "代例下单提成比")
    private BigDecimal help_order_ratio;

    @ApiModelProperty(value = "配送提成")
    private BigDecimal distribution_amt;

    @ApiModelProperty(value = "推荐码:只要用于显示")
    private String referral_code;

    @ApiModelProperty(value = "员工类型;01-普通员工,02-内置员工")
    private String staff_type;

    @ApiModelProperty(value = "服务提成比例")
    private BigDecimal service_ratio;

    @ApiModelProperty(value = "成本分摊比例")
    private BigDecimal share_cost_ratio;

}