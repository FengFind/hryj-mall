package com.hryj.entity.vo.staff.dept.request;

import com.hryj.entity.vo.RequestVO;
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
@ApiModel(value = "部门请求VO")
public class DeptRequestVO extends RequestVO {

    @ApiModelProperty(value = "部门名称", required = true)
    private String dept_name;

    @ApiModelProperty(value = "部门上级id", required = true)
    private Long dept_pid;

    @ApiModelProperty(value = "是否为区域公司:1-是,0-否", required = true)
    private Integer company_flag;


    //********引用对象开始****************************************************************
    /**
     * DeptStaffListRequestVO:部门员工对象
     */
    private List<DeptStaffListRequestVO> deptStaffDetRequestVOList;


    //********引用对象结束****************************************************************


    @Override
    public String toString() {
        return "DeptRequestVO{" +
                "dept_name='" + dept_name + '\'' +
                ", dept_pid=" + dept_pid +
                ", company_flag=" + company_flag +
                ", deptStaffDetRequestVOList=" + deptStaffDetRequestVOList +
                '}';
    }
}
