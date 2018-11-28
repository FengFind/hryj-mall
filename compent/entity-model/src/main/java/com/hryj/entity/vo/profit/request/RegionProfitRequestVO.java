package com.hryj.entity.vo.profit.request;

import com.hryj.entity.vo.PageRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: RegionProfitRequestVO
 * @description: 区域分润查询请求VO
 * @create 2018/7/9 20:06
 **/
@ApiModel(value = "区域分润查询请求VO")
@Data
public class RegionProfitRequestVO extends PageRequestVO {

    @ApiModelProperty(value = "查询月份", notes = "格式：yyyy-MM")
    private String query_month;

    @ApiModelProperty(value = "部门名称")
    private String dept_name;

    @ApiModelProperty(value = "设置状态", notes = "1-已设置,0-未设置")
    private Integer setup_status;

    @ApiModelProperty(value = "门店设置状态", notes = "1-已全部设置,0-未全部设置")
    private Integer store_setup_status;

    @ApiModelProperty(value = "部门路径", hidden = true)
    private String dept_path;
}
