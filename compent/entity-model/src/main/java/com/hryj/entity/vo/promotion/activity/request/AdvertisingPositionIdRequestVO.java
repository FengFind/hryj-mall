package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: AdvertisingPositionIdRequestVO
 * @description:
 * @create 2018/7/5 0005 21:51
 **/
@ApiModel(value = "广告位id请求VO", description = "如业务只需广告位id时使用")
@Data
public class AdvertisingPositionIdRequestVO {

    @ApiModelProperty(value = "广告位id请求VO",required = true)
    private Long advertising_position_id;
}
