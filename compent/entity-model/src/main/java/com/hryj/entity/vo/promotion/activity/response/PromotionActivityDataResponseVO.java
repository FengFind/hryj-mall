package com.hryj.entity.vo.promotion.activity.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PromotionActivityDataResponseVO
 * @description:
 * @create 2018/6/28 0028 11:48
 **/
@ApiModel(value = "促销活动详细信息查询请求VO", description = "促销活动详细信息查询请求VO")
@Data
public class PromotionActivityDataResponseVO {

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "活动名称")
    private String activity_name;

    @ApiModelProperty(value = "活动开始时间，格式:yyyy-MM-dd HH:mm:ss")
    private String start_date;

    @ApiModelProperty(value = "活动结束时间，格式:yyyy-MM-dd HH:mm:ss")
    private String end_date;

    @ApiModelProperty(value = "活动banner图URL")
    private String activity_image;

    @ApiModelProperty(value = "活动样式:01-活动模板,02-指定url,  目前只支持01活动模板，该参数缺省处理为01")
    private String activity_style;

    @ApiModelProperty(value = "活动模板内容, 模板内容的存储有两种方式，一种是将H5模板的代码内容整个进行存储，另一种是存储一个H5模板的访问URL")
    private String templete_data;

    @ApiModelProperty(value = "活动详情")
    private String activity_detail;

    @ApiModelProperty(value = "活动范围:01-仓库,02-门店")
    private String activity_scope;

    @ApiModelProperty(value = "活动类型:01-爆款,02-团购， 目前只支持 01爆款，该参数缺省处理为01，即使传送了01之外的其他参数也会被处理为01")
    private String activity_type;

    @ApiModelProperty(value = "活动审核状态:0-待审核,1-审核通过,2-审核未通过")
    private String audit_status;

    @ApiModelProperty(value = "活动状态:1-正常,0-停用")
    private String activity_status;

    @ApiModelProperty(value = "创建人姓名")
    private String operator_name;

    @ApiModelProperty(value = "创建时间，格式:yyyy-MM-dd HH:mm:ss")
    private String create_time;

    @ApiModelProperty(value = "最后更新时间，格式:yyyy-MM-dd HH:mm:ss")
    private String update_time;

    @ApiModelProperty(value = "是否为当前操作员工数据权限上能看到的全部门店或仓库  0-指定 1-全部")
    private String all_party;

    @ApiModelProperty(value = "活动参与门店或仓库数据")
    private PromotionActivityJoinPartyDataResponseVO join_party_data;

    @ApiModelProperty(value = "活动参与商品数据")
    private PromotionActivityJoinProductDataResponseVO join_product_data;

    @ApiModelProperty(value = "活动审核记录")
    private List<PromotionActivityAuditRecordResponseVO> audit_record_list;
}
