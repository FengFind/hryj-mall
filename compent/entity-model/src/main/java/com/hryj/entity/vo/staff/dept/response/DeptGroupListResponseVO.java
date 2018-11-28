package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptGroupListResponseVO
 * @description: 
 * @create 2018/07/07 17:19
 **/
@Data
@ApiModel(value="根据当前部门id向上的组织列表VO")
public class DeptGroupListResponseVO {

    @ApiModelProperty(value = "部门id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long dept_id;
    

    @ApiModelProperty(value = "部门名称")
    private String dept_name;
    
}

