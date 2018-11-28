package com.hryj.entity.vo.staff.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: StaffUserVO
 * @description: 门店员工用户VO
 * @create 2018/7/5 13:46
 **/
@Data
@ApiModel(value = "门店员工用户VO")
public class StaffUserVO implements Serializable {

    @ApiModelProperty(value = "员工id")
    private Long staff_id;

    @ApiModelProperty(value = "门店id")
    private Long store_id;

    @ApiModelProperty(value = "员工账号")
    private String staff_account;

    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "员工岗位")
    private String staff_job;

    @ApiModelProperty(value = "员工岗位名称")
    private String staff_job_name;

    @ApiModelProperty(value = "手机号码")
    private String phone_num;
}
