package com.hryj.entity.vo.staff.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StoreListRepostVo
 * @description: 员工列表查询
 * @create 2018/6/26 0026-14:59
 **/
@Data
@ApiModel(value = "员工列表查询")
public class StaffListResponseVO {

    @ApiModelProperty(value = "员工id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long staff_id;

    @ApiModelProperty(value = "员工账号")
    private String staff_account;

    @ApiModelProperty(value = "手机号码")
    private String phone_num;

    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "员工类型")
    private String staff_type;

    @ApiModelProperty(value = "所属部门id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;

    @ApiModelProperty(value = "所属部门名称")
    private String dept_name;

    @ApiModelProperty(value = "推荐码:6位的数字")
    private String referral_code;
}
