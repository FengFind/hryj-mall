package com.hryj.entity.vo.promotion.activity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: JoinPartyItemResponseVO
 * @description:
 * @create 2018/6/28 0028 11:49
 **/
@ApiModel(value = "活动参与门店或仓库条目响应VO", description = "活动参与门店或仓库条目响应VO")
@Data
public class JoinPartyItemResponseVO {

    @ApiModelProperty(value = "活动参与门店或仓库条目ID，该参数可用于请求删除一个活动的参与者(门店或仓库)")
    private Long activity_scope_item_id;

    @ApiModelProperty(value = "门店或仓库ID")
    private Long party_id;

    @ApiModelProperty(value = "门店或仓库名称")
    private String party_name;

    @ApiModelProperty(value = "店长姓名，只有门店时才有")
    private String party_leader;

    @ApiModelProperty(value = "所在省")
    private String province;

    @ApiModelProperty(value = "所在市")
    private String city;

    @ApiModelProperty(value = "所在区")
    private String area;
}
