package com.hryj.entity.vo.staff.warehouse.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 代廷波
 * @className: WarehouseCityAreaResponseVO
 * @description:  仓库配送区域
 * @create 2018/07/17 11:48
 **/
@Data
@ApiModel(value="仓库城市区域VO")
public class WarehouseCityAreaResponseVO implements Serializable {

    @ApiModelProperty(value = "城市id")
    private Long city_id;

    @ApiModelProperty(value = "城市名称")
    private String city_name;




}
