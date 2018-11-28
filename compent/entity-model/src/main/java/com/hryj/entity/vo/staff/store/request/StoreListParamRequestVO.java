package com.hryj.entity.vo.staff.store.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: StoreInfoRequestVO
 * @description: 门店列表查询条件
 * @create 2018-06-26 9:09
 **/
@Data
@ApiModel(description = "门店列表查询条件VO")
public class StoreListParamRequestVO {


    @ApiModelProperty(value = "所在省代码", required = false)
    private String province_code;


    @ApiModelProperty(value = "所在市代", required = false)
    private String city_code;

    @ApiModelProperty(value = "所在区代码", required = false)
    private String area_code;


    @ApiModelProperty(value = "门店名称", required = false)
    private String store_name;

    @ApiModelProperty(value = "开业开始时间:格式年-月-日(yyyy-MM-dd)")
    private String open_date_start;

    @ApiModelProperty(value = "开业结束时间:格式年-月-日(yyyy-MM-dd)")
    private String open_date_end;

    @ApiModelProperty(value = "店长姓名", required = false)
    private String store_manager_name;

    @ApiModelProperty(value = " 门店编号", required = false)
    private String store_num;

    @ApiModelProperty(value = "分摊成本标识:1-分摊,0-不分摊")
    private Integer share_cost_flag;

    @ApiModelProperty(value = "页码，默认为1", required = false)
    private int page_num=1;//页码，默认为1

    @ApiModelProperty(value = "每页大小，默认为10", required = false)
    private int page_size=10;// #每页大小，默认为10


}
