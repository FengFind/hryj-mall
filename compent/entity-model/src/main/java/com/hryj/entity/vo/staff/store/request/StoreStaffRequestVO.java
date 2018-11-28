package com.hryj.entity.vo.staff.store.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 员工用户信息表
 *
 * @author daitingbo
 * @since 2018-06-25
 */
@Data
@ApiModel(value = "门店员工请求VO")
public class StoreStaffRequestVO extends RequestVO {


    @ApiModelProperty(value = "员工id")
    private Long staff_id;

    @ApiModelProperty(value = "门店id,数组",required = true)
    private Long [] store_ids;

    @ApiModelProperty(value = "员员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "员工岗位")
    private String staff_job;


}
