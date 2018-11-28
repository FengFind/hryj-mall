package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PromotionActivityRequestVO
 * @description:
 * @create 2018/6/28 0028 10:13
 **/
@ApiModel(value = "维护（新增或修改）促销活动请求VO", description = "维护（新增或修改）促销活动请求VO")
@Data
public class PromotionActivityRequestVO extends RequestVO {

    @ApiModelProperty(value = "活动ID， 新增不需要，修改必须")
    private Long activity_id;

    @ApiModelProperty(value = "活动名称", required = true)
    private String activity_name;

    @ApiModelProperty(value = "活动开始时间，格式:yyyy-MM-dd HH:mm:ss", required = true)
    private String start_date;

    @ApiModelProperty(value = "活动结束时间，格式:yyyy-MM-dd HH:mm:ss", required = true)
    private String end_date;

    @ApiModelProperty(value = "活动banner图URL", required = true)
    private String activity_image;

    @ApiModelProperty(value = "活动样式:01-活动模板,02-指定url,  目前只支持01活动模板，该参数缺省处理为01")
    private String activity_style = "01";

    @ApiModelProperty(value = "活动模板内容, 模板内容的存储有两种方式，一种是将H5模板的代码内容整个进行存储，另一种是存储一个H5模板的访问URL", required = true)
    private String templete_data;

    @ApiModelProperty(value = "活动详情", required = true)
    private String activity_detail;

    @ApiModelProperty(value = "活动范围:01-仓库,02-门店", required = true)
    private String activity_scope;

    @ApiModelProperty(value = "活动类型:01-爆款,02-团购， 目前只支持 01爆款，该参数缺省处理为01，即使传送了01之外的其他参数也会被处理为01")
    private String activity_type;

    @ApiModelProperty(value = "是否为数据权限上能看到的全部门店或仓库", required = true)
    private Boolean all_party;

    @ApiModelProperty(value = "活动范围的门店或仓库ID集合, 如果是选择的全部则不需要传")
    private List<Long> party_id_list;

    @ApiModelProperty(value = "参加活动的商品ID和活动价格集合，这些商品必须是活动范围中的门店或仓库的销售商品库的商品", required = true)
    private List<PromotionActivityProductItemRequestVO> product_list;
}
