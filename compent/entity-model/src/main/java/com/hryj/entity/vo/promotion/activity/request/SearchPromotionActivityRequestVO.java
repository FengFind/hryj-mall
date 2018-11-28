package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: SearchPromotionActivityRequestVO
 * @description:
 * @create 2018/6/28 0028 9:46
 **/
@ApiModel(value = "分页查询促销活动请求VO", description = "分页查询促销活动请求VO")
@Data
public class SearchPromotionActivityRequestVO extends RequestVO {

    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;

    @ApiModelProperty(value = "活动名称", required = false)
    private String activity_name;

    @ApiModelProperty(value = "活动类型:01-爆款,02-团购，目前只有爆款，缺省处理为 01", required = false)
    private String activity_type;

    @ApiModelProperty(value = "活动审核状态:0-待审核,1-审核通过,2-审核未通过", required = false)
    private String audit_status;

    @ApiModelProperty(value = "活动状态:1-正常,0-关闭", required = false)
    private Integer activity_status;

    @ApiModelProperty(value = "活动开始时间范围起", required = false)
    private String start_date_begin;

    @ApiModelProperty(value = "活动开始时间范围止", required = false)
    private String start_date_end;

    @ApiModelProperty(value = "活动结束时间范围起", required = false)
    private String end_date_begin;

    @ApiModelProperty(value = "活动结束时间范围止", required = false)
    private String end_date_end;

    @ApiModelProperty(value = "创建人", required = false)
    private String operator_name;

    @ApiModelProperty(value = "活动创建时间范围起", required = false)
    private String create_time_begin;

    @ApiModelProperty(value = "活动创建时间范围止", required = false)
    private String create_time_end;

    @ApiModelProperty(value = "审核时间范围起", required = false)
    private String audit_time_begin;

    @ApiModelProperty(value = "审核时间范围止", required = false)
    private String audit_time_end;

    @ApiModelProperty(value = "审核时间范围止", required = false)
    private List<Long> party_id_list;

    @ApiModelProperty(value = "前端不用管", required = false)
    private List<String> s_w_id_list;
}
