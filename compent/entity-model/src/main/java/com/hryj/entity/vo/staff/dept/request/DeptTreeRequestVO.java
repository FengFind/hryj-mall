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
@ApiModel(value = "组织结构搜索请求VO")
public class DeptTreeRequestVO extends RequestVO {

    @ApiModelProperty(value = "部门名称")
    private String dept_name;

    @ApiModelProperty(value = "部门类别:01-门店,02-仓库,03-普通部门")
    private String dept_type;

    @ApiModelProperty(value = "是否末级节点,只能查询全部(值为空)和不是未节点的树(值为0):1-是,0-否")
    private Integer end_flag;

}
