package com.hryj.entity.vo.staff.user.request;

import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.staff.role.request.ResourceRequestVO;
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
@ApiModel(value = "员工用户信息")
public class StaffUserInfoRequestVO extends RequestVO {


    @ApiModelProperty(value = "员工id,新增不填，修改必填", required = false)
    private Long staff_id;

    @ApiModelProperty(value = "手机号码", required = true)
    private String phone_num;

    @ApiModelProperty(value = "员工姓名", required = true)
    private String staff_name;

    @ApiModelProperty(value = "身份证号码", required = true)
    private String id_card;

    @ApiModelProperty(value = "性别", required = true)
    private String sex;

    @ApiModelProperty(value = "学历", required = true)
    private String education;

    @ApiModelProperty(value = "员工照片", required = true)
    private String staff_pic;

    @ApiModelProperty(value = "邮箱", required = true)
    private String email;

    @ApiModelProperty(value = "家庭地址", required = true)
    private String home_address;

    @ApiModelProperty(value = "紧急联系人", required = true)
    private String contact_name;

    @ApiModelProperty(value = "紧急联系电话", required = true)
    private String contact_tel;

    @ApiModelProperty(value = "员工类型", required = true)
    private String staff_type;

   /* @ApiModelProperty(value = "资源集合数组,可以为空", required = true)
    private Long [] resource_ids;*/

    @ApiModelProperty(value = "角色集合数组,可以为空", required = true)
    private Long [] role_ids;

    @ApiModelProperty(value = "资源列表", required = true)
    private List<ResourceRequestVO> resourceList;

}
