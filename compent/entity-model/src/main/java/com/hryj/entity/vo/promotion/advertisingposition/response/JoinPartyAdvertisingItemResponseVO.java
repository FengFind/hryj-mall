package com.hryj.entity.vo.promotion.advertisingposition.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: JoinPartyAdvertisingItemResponseVO
 * @description:
 * @create 2018/7/16 0016 20:47
 **/
@ApiModel(value = "广告位参与门店或仓库条目响应VO", description = "广告位参与门店或仓库条目响应VO")
@Data
public class JoinPartyAdvertisingItemResponseVO {

    @ApiModelProperty(value = "广告位参与门店或仓库条目ID")
    private Long advertising_scope_item_id;

    @ApiModelProperty(value = "门店或仓库ID")
    private Long party_id;

    @ApiModelProperty(value = "门店或仓库名称")
    private String party_name;

    @ApiModelProperty(value = "门店店长名字")
    private String party_leader;

    @ApiModelProperty(value = "所在省")
    private String province;

    @ApiModelProperty(value = "所在市")
    private String city;

    @ApiModelProperty(value = "所在区")
    private String area;

    @ApiModelProperty(value = "置顶标识:1-置顶,0-不置顶")
    private String top_flag;
}
