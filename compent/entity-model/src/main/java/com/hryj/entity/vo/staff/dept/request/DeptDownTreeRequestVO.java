package com.hryj.entity.vo.staff.dept.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: DeptDownTreeRequestVO
 * @description: 根据 部门id|员工id|登录token 查询组织数请求Vo
 * @create 2018/7/23 0023-14:43
 **/
@Data
@ApiModel(value = "根据 部门id|员工id|登录token 查询组织数请求Vo")
public class DeptDownTreeRequestVO extends RequestVO {

    @ApiModelProperty(value = "部门id")
    private Long dept_id;

    @ApiModelProperty(value = "员工")
    private Long staff_id;

}
