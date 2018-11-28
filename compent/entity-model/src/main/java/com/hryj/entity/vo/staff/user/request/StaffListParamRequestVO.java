package com.hryj.entity.vo.staff.user.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StoreListRepostVo
 * @description: 员工列表查询条件
 * @create 2018/6/26 0026-14:59
 **/
@Data
@ApiModel(description = "员工列表查询条件")
public class StaffListParamRequestVO {


    @ApiModelProperty(value = "员工账号")
    private String staff_account;

    @ApiModelProperty(value = "手机号码")
    private String phone_num;

    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "员工类型")
    private String staff_type;

    @ApiModelProperty(value = "所属组织id")
    private Long dept_id;

    @ApiModelProperty(value = "员工是否有组织,0:默认查询所有,1:有组织,2:无组织")
    private int staff_dept_type;

    @ApiModelProperty(value = "页码，默认为1")
    private int page_num=1;//页码，默认为1

    @ApiModelProperty(value = "每页大小，默认为10")
    private int page_size=10;// #每页大小，默认为10

}
