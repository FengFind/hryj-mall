package com.hryj.entity.vo.staff.store.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className: StaffDeptRelationResponseVO
 * @description: 员工组织关系请求VO
 * @create 2018-06-26 9:09
 **/
@Data
@ApiModel(value = "门店员工组织关系请求VO")
public class StoreStaffRelationRequestVO {

    @ApiModelProperty(value = "员工组织关系主建id", required = false)
    private Long staff_dept_relation_id;//主建

    @ApiModelProperty(value = "员工id", required = true)
    private Long staff_id;

    @ApiModelProperty(value = "门店id,门店转移员工必须带门店员工", required = false)
    private Long store_id;

    @ApiModelProperty(value = "员工岗位", notes = "01-店长,02-店员", required = false)
    private String staff_job;

    @ApiModelProperty(value = "工资金额", required = false)
    private BigDecimal salary_amt;

    @ApiModelProperty(value = "代例下单提成比", required = false)
    private BigDecimal help_order_ratio;

    @ApiModelProperty(value = "配送提成", required = false)
    private BigDecimal distribution_amt;

    @ApiModelProperty(value = "推荐码:只要用于显示", required = false)
    private String referral_code;

    @ApiModelProperty(value = "员工类型;01-普通员工,02-内置员工", required = true)
    private String staff_type;

    @ApiModelProperty(value = "服务提成比例", required = false)
    private BigDecimal service_ratio;

    @ApiModelProperty(value = "成本分摊比例", required = false)
    private BigDecimal share_cost_ratio;


}
