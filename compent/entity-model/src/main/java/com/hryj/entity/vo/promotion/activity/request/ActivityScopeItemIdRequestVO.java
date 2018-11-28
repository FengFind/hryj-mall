package com.hryj.entity.vo.promotion.activity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: ActivityScopeItemIdRequestVO
 * @description:
 * @create 2018/7/5 0005 21:43
 **/
@ApiModel(value = "活动范围id请求VO", description = "如业务只需活动范围id时使用")
@Data
public class ActivityScopeItemIdRequestVO {

    @ApiModelProperty(value = "活动范围id",required = true)
    private Long activity_scope_item_id;
}
