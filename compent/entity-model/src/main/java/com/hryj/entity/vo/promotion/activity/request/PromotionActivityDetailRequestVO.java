package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: PromotionActivityDetailRequestVO
 * @description:
 * @create 2018/7/5 0005 21:29
 **/
@ApiModel(value = "活动详情请求VO", description = "活动详情请求VO")
@Data
public class PromotionActivityDetailRequestVO {

    @ApiModelProperty(value = "活动id", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "是否返回参与门店或仓库，true返回，false不返回", required = true)
    private Boolean include_party;

    @ApiModelProperty(value = "是否返回参与产品信息，true返回，false不返回", required = true)
    private Boolean include_product;

    @ApiModelProperty(value = "是否返回审核记录，true返回，false不返回", required = true)
    private Boolean include_audit_record;

}
