package com.hryj.entity.vo.staff.dept.request;

import com.hryj.entity.vo.RequestVO;
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
@ApiModel(value = "修改部门名称请求VO")
public class DeptUpdataNameRequestVO extends RequestVO {

    @ApiModelProperty(value = "部门id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "部门名称", required = true)
    private String dept_name;

    @ApiModelProperty(value = "是否为区域公司:1-是,0-否", required = true)
    private Integer company_flag;


    @Override
    public String toString() {
        return "DeptUpdataNameRequestVO{" +
                "dept_id=" + dept_id +
                ", dept_name='" + dept_name + '\'' +
                '}';
    }
}
