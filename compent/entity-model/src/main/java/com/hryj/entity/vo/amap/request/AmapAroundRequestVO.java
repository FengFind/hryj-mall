package com.hryj.entity.vo.amap.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: AmapAroundRequestVO
 * @description: 周边搜索
 * @create 2018/7/13 0013-15:35
 **/
@ApiModel(description = "周边搜索VO")
@Data
public class AmapAroundRequestVO {

    @ApiModelProperty(value = "中心点坐标-规则： 经度和纬度用\",\"分割，经度在前，纬度在后，经纬度小数点后不得超过6位")
    private String location;

    @ApiModelProperty(value = "查询关键字-规则： 多个关键字用“|”分割")
    private String keywords;


    @ApiModelProperty(value = "查询POI类型:当keywords和types均为空的时候，默认指定types为050000（餐饮服务）、070000（生活服务）、120000（商务住宅）")
    private String types;

    @ApiModelProperty(value = "查询城市")
    private String city;

    @ApiModelProperty(value = "查询半径 取值范围:0-50000。规则：大于50000按默认值，单位：米")
    private String radius;

    @ApiModelProperty(value = "排序规则 规定返回结果的排序规则。按距离排序：distance；综合排序：weight")
    private String sortrule;

    @ApiModelProperty(value = "每页记录数据 强烈建议不超过25，若超过25可能造成访问报错")
    private String offset;

    @ApiModelProperty(value = "当前页数最大翻页数100")
    private String page;

    @ApiModelProperty(value = "返回结果控制 此项默认返回基本地址信息；取值为all返回地址信息、附近POI、道路以及道路交叉口信息")
    private String extensions;

    @ApiModelProperty(value = "数字签名")
    private String sig;

    @ApiModelProperty(value = "返回数据格式类型 可选值：JSON，XML")
    private String output;

    @ApiModelProperty(value = "回调函数callback值是用户定义的函数名称，此参数只在output=JSON时有效")
    private String callback;


}
