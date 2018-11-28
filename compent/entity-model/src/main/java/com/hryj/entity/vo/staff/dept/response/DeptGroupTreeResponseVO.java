package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: DeptGroupTreeResponseVO
 * @description:
 * @create 2018/6/27 0027-11:54
 **/
@Data
@ApiModel(value="部门组织树型结构")
public class DeptGroupTreeResponseVO {

    @ApiModelProperty(value = "key")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long key;//主键id，key就是封装给前端用

    @ApiModelProperty(value = "value,前段使用")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long value;//主键id，key就是封装给前端用

    @ApiModelProperty(value = "部门名称")
    private String title;

    @ApiModelProperty(value = "部门上级id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_pid;

    @ApiModelProperty(value = "部门类别:01-门店,02-仓库,03-普通部门")
    private String dept_type;

    @ApiModelProperty(value = "组织路径:\",\"分隔")
    private String dept_path;

    @ApiModelProperty(value = "是否末级节点:1-是,0-否")
    private Integer end_flag;

    @ApiModelProperty(value = "分润节占:1-是,0-否")
    private Integer dept_share=0;

    @ApiModelProperty(value = "是否为区域公司:1-是0-否")
    private Integer company_flag;

    @ApiModelProperty(value = "是否还有子节点:true-有,false-否")
    private Boolean  isLeaf;

    @ApiModelProperty(value = "子对象")
    private List<DeptGroupTreeResponseVO> children;
}

