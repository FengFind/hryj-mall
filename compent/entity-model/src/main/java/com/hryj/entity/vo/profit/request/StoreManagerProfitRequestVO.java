package com.hryj.entity.vo.profit.request;

import com.hryj.entity.vo.PageRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StoreManagerProfitRequestVO
 * @description: 店长分润查询请求VO
 * @create 2018/7/9 20:13
 **/
@ApiModel(value = "店长分润查询请求VO")
@Data
public class StoreManagerProfitRequestVO extends PageRequestVO {

    @ApiModelProperty(value = "查询月份")
    private String query_month;

    @ApiModelProperty(value = "门店名称")
    private String store_name;

    @ApiModelProperty(value = "店长姓名")
    private String staff_name;

    @ApiModelProperty(value = "结算状态", notes = "1-已结算,0-未结算")
    private Integer balance_status;

    @ApiModelProperty(value = "部门路径", hidden = true)
    private String dept_path;
}
