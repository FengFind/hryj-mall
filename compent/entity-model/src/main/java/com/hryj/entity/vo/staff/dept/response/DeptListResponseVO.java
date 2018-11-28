package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptListResponseVO
 * @description:
 * @create 2018/7/7 0007-14:51
 **/
@Data
@ApiModel(description = "组织列表响应VO")
public class DeptListResponseVO {

    @ApiModelProperty(value = "部门id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;

    @ApiModelProperty(value = "部门名称")
    private String dept_name;

}
