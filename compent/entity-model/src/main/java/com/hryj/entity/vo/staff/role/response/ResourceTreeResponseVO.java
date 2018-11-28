package com.hryj.entity.vo.staff.role.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hryj.constant.StaffConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: ResourceTreeResponseVO
 * @description:
 * @create 2018/6/28 0028-20:17
 **/
@ApiModel(description = "资源树")
@Data
public class ResourceTreeResponseVO {

    @ApiModelProperty(value = "权限id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long key;//表的id

    @ApiModelProperty(value = "vuale,前端使用")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long vuale;//表的id

    @ApiModelProperty(value = "权限名称")
    private String title;//perm_name

    @ApiModelProperty(value = "权限标识")
    private String perm_flag;

    @ApiModelProperty(value = "权限url")
    private String perm_url;

    @ApiModelProperty(value = "权限上级id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long perm_pid;

    @ApiModelProperty(value = "权限类型:普通资源权限generalType,generalType以外为其它权限")
    private String permission_type = StaffConstants.GENERAL_TYPE;

    @ApiModelProperty(value = "资源树子对象")
    List<ResourceTreeResponseVO> children;

}

