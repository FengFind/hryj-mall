package com.hryj.entity.vo.promotion.advertisingposition.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 汪豪
 * @className: AdvertisingPositionJoinPartyDataResponseVO
 * @description:
 * @create 2018/7/16 0016 20:51
 **/
@ApiModel(value = "广告位参与门店或仓库详情数据响应VO", description = "广告位参与门店或仓库详情数据响应VO")
@Data
public class AdvertisingPositionJoinPartyDataResponseVO {

    @ApiModelProperty(value = "页码")
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小")
    private Integer page_size = 10;

    @ApiModelProperty(value = "总条数")
    private Long total_count;

    @ApiModelProperty(value = "总页数")
    private Long total_page;

    @ApiModelProperty(value = "门店或仓库集合")
    private List<JoinPartyAdvertisingItemResponseVO> party_list;
}
