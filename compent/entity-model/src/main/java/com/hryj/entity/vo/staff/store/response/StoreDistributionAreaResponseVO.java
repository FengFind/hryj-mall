package com.hryj.entity.vo.staff.store.response;

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
@ApiModel(value = "门店配送区域响应VO")
public class StoreDistributionAreaResponseVO {

    @ApiModelProperty(value = "位置类型")
    private String type;

    @ApiModelProperty(value = "位置名称")
    private String name;

    @ApiModelProperty(value = "位置地址")
    private String address;

    @ApiModelProperty(value = "区域坐标,经纬度\",\"分隔")
    private String locations;

    @ApiModelProperty(value = "距离(米)")
    private BigDecimal distance;

    @ApiModelProperty(value = "poi位置id")
    private String id;


}
