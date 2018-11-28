package com.hryj.entity.vo.staff.dept.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 代廷波
 * @className: DeptStaffShareListResPonseVO
 * @description:
 * @create 2018/6/28 0028-15:44
 **/
@ApiModel(description = "部门分润员工VO")
@Data
public class DeptStaffShareResPonseVO {

    @ApiModelProperty(value = "分润员工id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long staff_id;

    @ApiModelProperty(value = "分润比例")
    private BigDecimal share_ratio;

    @ApiModelProperty(value = "员工列表")
    private List<DeptStaffListResponseVO> deptStaffShareList;
}
