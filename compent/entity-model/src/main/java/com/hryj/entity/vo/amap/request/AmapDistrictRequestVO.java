package com.hryj.entity.vo.amap.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: AmapAroundRequestVO
 * @description:
 * @create 2018/7/13 0013-15:35
 **/
@ApiModel(description = "行政搜索VO")
@Data
public class AmapDistrictRequestVO {


    @ApiModelProperty(value = "规则：只支持单个关键词语搜索关键词支持：行政区名称、citycode、adcode")
    private String keywords;

    @ApiModelProperty(value = "最外层返回数据个数,可选20")
    private String offset;

    @ApiModelProperty(value = "需要第几页数据,最外层的districts最多会返回20个数据，若超过限制，请用page请求下一页数据。例如page=2；page=3。默认page=1")
    private String page;

    @ApiModelProperty(value = "规则：设置显示下级行政区级数（行政区级别包括：国家、省/直辖市、市、区/县、乡镇/街道多级数据） 可选值：0、1、2、3等数字，并以此类推 0：不返回下级行政区1：返回下一级行政区；2：返回下两级行政区；3：返回下三级行政区")
    private String subdistrict;

    @ApiModelProperty(value = "项控制行政区信息中返回行政区边界坐标点； 可选值：base、all;base:不返回行政区边界坐标点；all:只返回当前查询district的边界值，不返回子节点的边界值；")
    private String extensions;


    @ApiModelProperty(value = "根据区划过滤按照指定行政区划进行过滤，填入后则只返回该省/直辖市信息需填入adcode，为了保证数据的正确，强烈建议填入此参数")
    private String filter;

    @ApiModelProperty(value = "排序规则 规定返回结果的排序规则。按距离排序：distance；综合排序：weight")
    private String sortrule;

    @ApiModelProperty(value = "返回数据格式类型 可选值：JSON，XML")
    private String output;

    @ApiModelProperty(value = "回调函数callback值是用户定义的函数名称，此参数只在output=JSON时有效")
    private String callback;



}
