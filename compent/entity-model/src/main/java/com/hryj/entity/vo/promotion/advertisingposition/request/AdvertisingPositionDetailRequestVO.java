package com.hryj.entity.vo.promotion.advertisingposition.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: AdvertisingPositionDetailRequestVO
 * @description:
 * @create 2018/7/16 0016 20:18
 **/
@ApiModel(value = "广告位详情请求VO", description = "广告位详情请求VO")
@Data
public class AdvertisingPositionDetailRequestVO {

    @ApiModelProperty(value = "广告位id", required = true)
    private Long advertising_id;

    @ApiModelProperty(value = "是否返回参与门店或仓库，true返回，false不返回", required = false)
    private Boolean include_party;

    @ApiModelProperty(value = "是否返回参与广告跳转配置，true返回，false不返回", required = false)
    private Boolean include_jump_config;
}
