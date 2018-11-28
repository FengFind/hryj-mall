package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptGroupTreeResponseVO
 * @description:
 * @create 2018/6/27 0027-11:54
 **/
@Data
@ApiModel(value = "当前部门的所有父节点列表VO")
public class DeptParentListResponseVO {

    @ApiModelProperty(value = "部分id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;//主键id

    @ApiModelProperty(value = "部门名称")
    private String dept_name;
/*

    @ApiModelProperty(value = "部门上级id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_pid;

    @ApiModelProperty(value = "组织路径:\",\"分隔")
    private String dept_path;

    @ApiModelProperty(value = "是否末级节点:1-是,0-否")
    private Integer end_flag;

    @ApiModelProperty(value = "部门级别")
    private Integer dept_level;

    @ApiModelProperty(value = "部门类别:01-门店,02-仓库,03-普通部门")
    private String dept_type;

    @ApiModelProperty(value = "否为区域公司:1-是,0-否")
    private Integer company_flag;
*/


}
