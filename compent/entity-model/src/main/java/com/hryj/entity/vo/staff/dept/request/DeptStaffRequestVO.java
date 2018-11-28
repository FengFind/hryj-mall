package com.hryj.entity.vo.staff.dept.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: DeptStaffListResponseVO
 * @description:
 * @create 2018/6/28 0028-15:33
 **/
@ApiModel(description = "部门员工信息VO")
@Data
public class DeptStaffRequestVO extends RequestVO {

    @ApiModelProperty(value = "部门id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "部门员工列表", required = true)
    List<DeptStaffListRequestVO> deptStaffListRequestVOList;

}
