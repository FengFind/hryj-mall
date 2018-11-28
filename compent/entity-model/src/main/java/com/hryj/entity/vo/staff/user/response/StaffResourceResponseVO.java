package com.hryj.entity.vo.staff.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: StaffResourceResponseVO
 * @description:
 * @create 2018/6/26 0026-15:23
 **/
@Data
@ApiModel(value = "员工对应的资源文件")
public class StaffResourceResponseVO {

    @ApiModelProperty(value = "资源文件id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long resource_id;

    @ApiModelProperty(value = "资源文件名称")
    private String perm_name;
}
