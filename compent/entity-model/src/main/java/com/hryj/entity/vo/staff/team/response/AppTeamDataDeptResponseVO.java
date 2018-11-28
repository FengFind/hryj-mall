package com.hryj.entity.vo.staff.team.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: AppTeamDataResponseVO
 * @description:
 * @create 2018/7/9 0009-22:24
 **/
@Data
@ApiModel(value = "组织数据VO")
public class AppTeamDataDeptResponseVO {

    @ApiModelProperty(value = "组织id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;

    @ApiModelProperty(value = "组织名称")
    private String dept_name;

}
