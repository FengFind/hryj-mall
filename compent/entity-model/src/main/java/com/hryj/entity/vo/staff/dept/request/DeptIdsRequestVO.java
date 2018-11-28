package com.hryj.entity.vo.staff.dept.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: DeptRequestVO
 * @description:
 * @create 2018/6/27 0027-10:57
 **/
@Data
@ApiModel(value = "部门ids请求Vo")
public class DeptIdsRequestVO {


    @ApiModelProperty(value = "部门ids", required = true)
    private List<Long> dept_ids;

}
