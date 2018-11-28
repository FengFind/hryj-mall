package com.hryj.entity.vo.profit.request;

import com.hryj.entity.vo.PageRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: RegionProfitDetailRequestVO
 * @description: 区域公司分润明细查询请求VO
 * @create 2018/7/9 20:45
 **/
@ApiModel(value = "区域公司分润明细查询请求VO")
@Data
public class RegionProfitDetailRequestVO extends PageRequestVO {

    @ApiModelProperty(value = "结算汇总id", required = true)
    private Long summary_id;

    @ApiModelProperty(value = "部门id")
    private Long dept_id;

    @ApiModelProperty(value = "员工姓名")
    private String staff_name;

}
