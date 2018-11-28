package com.hryj.entity.vo.staff.user.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StaffAccountRequestVO
 * @description: 员工账号VO
 * @create 2018/7/5 9:43
 **/
@Data
@ApiModel(value = "员工账号VO")
public class StaffAccountRequestVO extends RequestVO {

    @ApiModelProperty(value = "员工账号", required = true)
    private String staff_account;
}
