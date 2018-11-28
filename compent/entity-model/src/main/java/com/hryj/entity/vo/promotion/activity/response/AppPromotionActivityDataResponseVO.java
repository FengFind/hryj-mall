package com.hryj.entity.vo.promotion.activity.response;

import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: AppPromotionActivityDataResponseVO
 * @description: APP端查询促销活动详情响应VO
 * @create 2018/6/28 0028 19:49
 **/
@ApiModel(value = "APP端加载活动详细配置数据响应VO", description = "APP端加载活动详细配置数据响应VO")
@Data
public class AppPromotionActivityDataResponseVO {

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

    @ApiModelProperty(value = "活动类型:01-爆款,02-团购")
    private String activity_type;

    @ApiModelProperty(value = "活动角标图")
    private String activity_mark_image;

    @ApiModelProperty("活动状态 0.停用或活动未开始 1.正常可用")
    private String activity_status;

    @ApiModelProperty(value = "活动商品列表")
    private List<AppProdListItemResponseVO> product_list;
}
