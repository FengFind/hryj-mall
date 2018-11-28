package com.hryj.entity.vo.product.common.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ShareProductRequestVO
 * @description:
 * @create 2018/8/23 0023 10:25
 **/
@ApiModel(value = "商品分享数据获取请求VO", description = "商品分享数据获取请求VO")
@Data
public class ShareProductRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;
}
