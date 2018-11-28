package com.hryj.entity.vo.product.partyprod.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PartySimpleInfoResponseVO
 * @description:
 * @create 2018/6/27 0027 16:52
 **/
@ApiModel(value = "门店或仓库简单基础信息查询响应VO", description = "门店或仓库简单基础信息查询响应VO")
@Data
public class PartySimpleInfoResponseVO {

    @ApiModelProperty(value = "门店或仓库名称")
    private String party_name;

    @ApiModelProperty(value = "所在省")
    private String province;

    @ApiModelProperty(value = "所在市")
    private String city;

    @ApiModelProperty(value = "所在区域")
    private String area;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "当事组织类型: 01门店， 02仓库， 03普通部门")
    private String party_type;

    @ApiModelProperty(value = "配送区域")
    private List<String> delivery_area;
}
