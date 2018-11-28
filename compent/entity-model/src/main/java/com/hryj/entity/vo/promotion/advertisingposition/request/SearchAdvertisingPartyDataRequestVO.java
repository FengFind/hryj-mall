package com.hryj.entity.vo.promotion.advertisingposition.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: SearchAdvertisingPartyDataRequestVO
 * @description:
 * @create 2018/7/17 0017 9:56
 **/
@ApiModel(value = "查询广告位的参与门店请求VO", description = "查询广告位的参与门店请求VO")
@Data
public class SearchAdvertisingPartyDataRequestVO {

    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;

    @ApiModelProperty(value = "广告位ID, 参数为空时直接返回，不做任何处理", required = true)
    private Long advertising_id;
}
