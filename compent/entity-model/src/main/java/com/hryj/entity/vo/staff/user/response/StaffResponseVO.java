package com.hryj.entity.vo.staff.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hryj.entity.vo.staff.role.response.RolePermissionTreeResponseVO;
import com.hryj.entity.vo.staff.role.response.RoleResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 员工用户信息表
 *
 * @author daitingbo
 * @since 2018-06-25
 */
@Data
@ApiModel(value = "员工返回用户信息")
public class StaffResponseVO {


    @ApiModelProperty(value = "员工id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long staff_id;

    @ApiModelProperty(value = "员工账号")
    private String staff_account;

    @ApiModelProperty(value = "手机号码")
    private String phone_num;

    @ApiModelProperty(value = "员员工姓名")
    private String staff_name;

    @ApiModelProperty(value = "身份证号码")
    private String id_card;

    @ApiModelProperty(value = "性别")
    private String sex;

    @ApiModelProperty(value = "学历")
    private String education;

    @ApiModelProperty(value = "员工照片")
    private String staff_pic;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "家庭地址")
    private String home_address;

    @ApiModelProperty(value = "紧急联系人")
    private String contact_name;

    @ApiModelProperty(value = "紧急联系电话")
    private String contact_tel;

    @ApiModelProperty(value = "员工类型")
    private String staff_type;

    @ApiModelProperty(value = "员工权限资源")
    private List<StaffResourceResponseVO> resourceRepostVoList;

    @ApiModelProperty(value = "员工权限资源对象")
    private RolePermissionTreeResponseVO rolePermissionTreeResponseVO;

    @ApiModelProperty(value = "角色列表")
    private List<RoleResponseVO> roleResponseVOList;

}
