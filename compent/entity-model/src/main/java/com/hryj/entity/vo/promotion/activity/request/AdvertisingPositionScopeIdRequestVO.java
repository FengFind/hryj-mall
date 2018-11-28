package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: AdvertisingPositionScopeIdRequestVO
 * @description:
 * @create 2018/7/5 0005 21:55
 **/
@ApiModel(value = "广告位范围id请求VO", description = "如业务只需广告位范围id时使用")
@Data
public class AdvertisingPositionScopeIdRequestVO {

    @ApiModelProperty(value = "广告位范围id请求VO",required = true)
    private Long advertising_position_scope_id;
}
