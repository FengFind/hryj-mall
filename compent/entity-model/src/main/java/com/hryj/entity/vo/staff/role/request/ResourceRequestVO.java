package com.hryj.entity.vo.staff.role.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: ResourceRequestVO
 * @description:
 * @create 2018/9/10 0010-17:14
 **/
@Data
@ApiModel(description = "资源文件")
public class ResourceRequestVO {

    @ApiModelProperty(value = "资源id", required = true)
    private Long id;

    @ApiModelProperty(value = "权限类型,generalType:普通权限,generalType以外为其它类型权限", required = true)
    private String permission_type;

}
