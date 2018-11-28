package com.hryj.entity.vo.staff.dept.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: DeptShareConfigReqestVO
 * @description:部门组织节点分润对象
 * @create 2018/6/27 0027-11:24
 **/
@Data
@ApiModel(value = "部门组织节点批量分润对象VO")
public class DeptShareBatchReqestVO extends RequestVO {

    @ApiModelProperty(value = "分润节点部门id", required = true)
    private Long dept_id;

    @ApiModelProperty(value = "分润列表数据")
   List<DeptShareBatchDataReqestVO> deptShareBatchDataReqestVOList;

}