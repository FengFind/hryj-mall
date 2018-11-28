package com.hryj.entity.vo.product.request;

import com.hryj.entity.vo.product.common.PageProductTypePermissionRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: SearchProductRequestVO
 * @description:
 * @create 2018/6/26 0026 17:36
 **/
@ApiModel(value = "商品查询请求VO", description = "商品查询请求VO")
@Data
public class SearchProductRequestVO extends PageProductTypePermissionRequestVO {

    @ApiModelProperty(value = "商品类型，all全部类型，new_retail-新零售商品，bonded-跨境商品(跨境保税)，缺省处理为:new_retail")
    private String product_type_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "审核状态,1-已审核,0-未审核")
    private Integer audit_status;

    @ApiModelProperty(value = "商品分类")
    private Long category_id;

    @ApiModelProperty(value = "品牌名称")
    private String brand_name;

    @ApiModelProperty(value = "品牌ID")
    private Long brand_id;

    @ApiModelProperty(value = "上下架状态，1上架，0下架，也表示为是否停用，上架对应未停用，下架对应停用")
    private Integer up_down_status;

    @Override
    public String getProduct_type_id() {
        return this.product_type_id;
    }

    @Override
    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }
}
