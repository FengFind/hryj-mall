package com.hryj.entity.vo.staff.warehouse.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: WarehuoseParamRequestVO
 * @description:  仓库列表查询条件VO
 * @create 2018/06/27 16:40
 **/
@Data
@ApiModel(description = "仓库列表查询条件VO")
public class WarehuoseParamRequestVO {


   
    @ApiModelProperty(value = "所在省代码", required = false)
    private String province_code;


    @ApiModelProperty(value = "所在市代", required = false)
    private String city_code;

    @ApiModelProperty(value = "所在区代码", required = false)
    private String area_code;


    @ApiModelProperty(value = "仓库名称", required = false)
    private String warehouse_name;


    @ApiModelProperty(value = "页码，默认为1", required = false)
    private int page_num=1;//页码，默认为1

    @ApiModelProperty(value = "每页大小，默认为10", required = false)
    private int page_size=10;// #每页大小，默认为10


}
