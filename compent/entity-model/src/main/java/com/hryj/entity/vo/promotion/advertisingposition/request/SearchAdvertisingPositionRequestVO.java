package com.hryj.entity.vo.promotion.advertisingposition.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: SearchAdvertisingPositionRequestVO
 * @description:
 * @create 2018/6/27 0027 22:23
 **/
@ApiModel(value = "广告位管理分页查询请求VO", description = "广告位管理分页查询请求VO")
@Data
public class SearchAdvertisingPositionRequestVO extends RequestVO {

    @ApiModelProperty(value = "页码")
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小")
    private Integer page_size = 10;

    @ApiModelProperty(value = "创建人姓名，前后模糊匹配")
    private String operator_name;

    @ApiModelProperty(value = "广告位名称，前后模糊匹配")
    private String advertising_name;

    @ApiModelProperty(value = "创建时间起")
    private String create_time_begin;

    @ApiModelProperty(value = "创建时间止")
    private String create_time_end;

    @ApiModelProperty(value = "开始展示时间起")
    private String start_time_begin;

    @ApiModelProperty(value = "开始展示时间止")
    private String start_time_end;

    @ApiModelProperty(value = "结束展示时间起")
    private String end_time_begin;

    @ApiModelProperty(value = "结束展示时间止")
    private String end_time_end;

    @ApiModelProperty(value = "状态，1-正常,0-停用")
    private String advertising_status;

    @ApiModelProperty(value = "广告位类型")
    private String advertising_type;

    @ApiModelProperty(value = "前端不用管")
    private List<String> s_w_id_list;
}
