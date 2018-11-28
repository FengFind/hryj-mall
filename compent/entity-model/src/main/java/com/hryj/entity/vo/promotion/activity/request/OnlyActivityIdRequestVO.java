package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: OnlyActivityIdRequestVo
 * @description:
 * @create 2018/7/5 0005 21:09
 **/
@ApiModel(value = "活动id请求VO", description = "如业务只需活动id时使用")
@Data
public class OnlyActivityIdRequestVO extends RequestVO {

    @ApiModelProperty(value = "用户id", required = false)
    private Long user_id;

    @ApiModelProperty(value = "门店或仓库id", required = false)
    private Long party_id;

    @ApiModelProperty(value = "活动id", required = true)
    private Long activity_id;
}
