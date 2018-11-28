package com.hryj.entity.vo.product.common.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ShareProductResponseVO
 * @description:
 * @create 2018/8/23 0023 14:53
 **/
@ApiModel(value = "商品分享数据获取响应VO", description = "商品分享数据获取响应VO")
@Data
public class ShareProductResponseVO {

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "门店或仓库ID")
    private Long party_id;

    @ApiModelProperty(value = "活动ID")
    private Long activity_id;

    @ApiModelProperty(value = "用户ID")
    private Long user_id;

    @ApiModelProperty(value = "分享标题")
    private String title;

    @ApiModelProperty(value = "分享商品描述")
    private String description;

    @ApiModelProperty(value = "分享链接")
    private String url;

    @ApiModelProperty(value = "分享图片、视频URL")
    private String media_url;
}
