package com.hryj.entity.vo.profit.request;

import com.hryj.entity.vo.PageRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StoreProfitRequestVO
 * @description: 门店分润查询请求VO
 * @create 2018/7/9 20:06
 **/
@ApiModel(value = "门店分润查询请求VO")
@Data
public class StoreProfitRequestVO extends PageRequestVO {

    @ApiModelProperty(value = "查询月份")
    private String query_month;

    @ApiModelProperty(value = "门店名称")
    private String store_name;

    @ApiModelProperty(value = "设置状态")
    private Integer setup_status;

    @ApiModelProperty(value = "部门路径", hidden = true)
    private String dept_path;
}
