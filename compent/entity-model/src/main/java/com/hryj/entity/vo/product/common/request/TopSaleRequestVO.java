package com.hryj.entity.vo.product.common.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: TopSaleRequestVO
 * @description:
 * @create 2018/7/9 0009 20:13
 **/
@ApiModel(value = "商品TOP销量查询请求VO", description = "商品TOP销量查询请求VO")
@Data
public class TopSaleRequestVO extends RequestVO {

    @ApiModelProperty(value = "当事门店或仓库ID（如果要传该参数，请确保参数值确实是一个门店或仓库的ID）， 指定该ID时查询该组织的商品TOP销售数据，不传时根据请求用户所在组织的数据权限（能够看到多少门店和仓库）进行查询")
    private Long party_id;

    @ApiModelProperty(value = "查询起始日期，格式：yyyy-MM-dd")
    private String from_date;

    @ApiModelProperty(value = "查询截止日期，格式：yyyy-MM-dd")
    private String to_date;
}
