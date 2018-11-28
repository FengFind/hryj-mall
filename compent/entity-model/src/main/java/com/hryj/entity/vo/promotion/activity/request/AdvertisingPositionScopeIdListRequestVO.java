package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 汪豪
 * @className: AdvertisingPositionScopeIdListRequestVO
 * @description:
 * @create 2018/7/18 0018 20:00
 **/
@ApiModel(value = "广告位范围id集合请求VO", description = "如业务只需广告位范围id集合时使用")
@Data
public class AdvertisingPositionScopeIdListRequestVO {

    @ApiModelProperty(value = "广告位范围id集合请求VO",required = true)
    private List<Long> advertising_scope_id_list;
}
