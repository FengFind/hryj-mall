package com.hryj.entity.vo.staff.storewarehouse.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartySearchItemResponseVO
 * @description:
 * @create 2018/6/27 0027 16:15
 **/
@ApiModel(value = "门店或仓库查询条目响应VO", description = "门店或仓库查询条目响应VO")
@Data
public class PartySearchItemResponseVO {

    @ApiModelProperty(value = "门店或仓库的ID")
    private Long party_id;

    @ApiModelProperty(value = "门店或仓库的名称")
    private String party_name;

    @ApiModelProperty(value = "店长姓名，只有查询类型为门店时该字段有值")
    private String party_leader;

    @ApiModelProperty(value = "商品数量， 该字段暂不统计")
    private Integer product_total_num;

    @ApiModelProperty(value = "所在省")
    private String province;

    @ApiModelProperty(value = "所在市")
    private String city;

    @ApiModelProperty(value = "所在区")
    private String area;
}
