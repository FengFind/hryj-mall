package com.hryj.entity.vo.promotion.advertisingposition.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: AdvertisingPositionScopeRequestVO
 * @description:
 * @create 2018/6/27 0027 22:50
 **/
@ApiModel(value = "广告位应用范围的门店或仓库条目VO", description = "广告位应用范围的门店或仓库条目VO")
@Data
public class AdvertisingPositionScopeRequestVO {

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "开始展示时间，目前暂时未用到", required = false)
    private String start_date;

    @ApiModelProperty(value = "结束展示时间，目前暂时未用到", required = false)
    private String end_date;

    @ApiModelProperty(value = "置顶标识:1-置顶,0-不置顶, 缺省0", required = false)
    private String top_flag;
}
