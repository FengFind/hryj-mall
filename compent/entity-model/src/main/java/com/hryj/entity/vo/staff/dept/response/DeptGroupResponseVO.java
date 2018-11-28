package com.hryj.entity.vo.staff.dept.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptRequestVO
 * @description:
 * @create 2018/6/27 0027-10:57
 **/
@Data
@ApiModel(value = "部门基本信息VO")
public class DeptGroupResponseVO {

    @ApiModelProperty(value = "部门id")
    private Long dept_id;

    @ApiModelProperty(value = "部门名称")
    private String dept_name;

    @ApiModelProperty(value = "部门上级id")
    private Long dept_pid;

    @ApiModelProperty(value = "部门级别")
    private Integer dept_level;

    @ApiModelProperty(value = "部门类型:01-门店,02-仓库,03-普通部门")
    private String dept_type;

    @ApiModelProperty(value = "是否为区域公司:1-是,0-否")
    private Integer company_flag;

    @ApiModelProperty(value = "是否末级节点:1-是,0-否")
    private Integer end_flag;

    @ApiModelProperty(value = " 部门状态:1-正常,0-关闭")
    private Integer dept_status;

    @ApiModelProperty(value = "组织路径:\",\"分隔")
    private String dept_path;

    @ApiModelProperty(value = "操作人id")
    private Long operator_id;

    @ApiModelProperty(value = "创建时间")
    private String create_time;

    @ApiModelProperty(value = "更新时间")
    private String update_time;


}
