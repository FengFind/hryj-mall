package com.hryj.entity.vo.sys.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: CityAreaVO
 * @description: 城市区域VO
 * @create 2018/7/9 18:18
 **/
@Data
@ApiModel(value="城市区域VO")
public class CityAreaVO implements Serializable {

    @ApiModelProperty(value = "城市id", required = true)
    private Long city_id;

    @ApiModelProperty(value = "城市名称", required = true)
    private String city_name;

    @ApiModelProperty(value = "父级id", required = true)
    private Long pid;

    @ApiModelProperty(value = "城市路径", required = true)
    private String path_name;

    @ApiModelProperty(value = "字母", required = true)
    private String letter;

    @ApiModelProperty(value = "经纬度", required = true)
    private String locations;


}
