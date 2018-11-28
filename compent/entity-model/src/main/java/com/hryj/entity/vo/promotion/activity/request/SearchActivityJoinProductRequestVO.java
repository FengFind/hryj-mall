package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: SearchActivityJoinProductRequestVO
 * @description:
 * @create 2018/6/28 0028 13:57
 **/
@ApiModel(value = "查询促销活动的参与商品请求VO", description = "查询促销活动的参与商品请求VO")
@Data
public class SearchActivityJoinProductRequestVO extends RequestVO {

    @ApiModelProperty(value = "页码")
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小")
    private Integer page_size = 10;

    @ApiModelProperty(value = "促销活动ID, 参数为空时直接返回，不做任何处理", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "商品名称，前后模糊匹配")
    private String product_name;

    @ApiModelProperty(value = "商品分类名称，前后模糊匹配")
    private String category_name;

    @ApiModelProperty(value = "商品分类ID")
    private Long category_id;
}
