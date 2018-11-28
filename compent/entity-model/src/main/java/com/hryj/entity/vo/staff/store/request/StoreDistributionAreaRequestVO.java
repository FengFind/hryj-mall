package com.hryj.entity.vo.staff.store.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 李道云
 * @className:
 * @description: 配送区域请求VO
 * @create 2018-06-26 9:09
 **/
@Data
@ApiModel(value = "门店配送区域请求VO")
public class StoreDistributionAreaRequestVO{


    @ApiModelProperty(value = "位置类型", required = true)
    private String type;

    @ApiModelProperty(value = "位置名称", required = true)
    private String name;

    @ApiModelProperty(value = "位置地址", required = true)
    private String address;

    @ApiModelProperty(value = "区域坐标,经纬度\",\"分隔", required = true)
    private String locations;

    @ApiModelProperty(value = "距离(米)", required = true)
    private BigDecimal distance;

    @ApiModelProperty(value = "poi位置id", required = true)
    private String id;


}
