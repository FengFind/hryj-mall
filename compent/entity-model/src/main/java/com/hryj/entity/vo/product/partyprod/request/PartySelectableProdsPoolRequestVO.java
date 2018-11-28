package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.product.common.PageProductTypePermissionRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartySelectableProdsPoolRequestVO
 * @description:
 * @create 2018/6/27 0027 17:16
 **/
@ApiModel(value = "查询门店或仓库可选择销售的商品列表请求VO", description = "查询门店或仓库可选择销售的商品列表请求VO")
@Data
public class PartySelectableProdsPoolRequestVO extends PageProductTypePermissionRequestVO {

    @ApiModelProperty(value = "商品类型ID，all所有(用户商品类型数据权限下的所有)，bonded跨境商品， new_retail新零售， 缺省默认：all")
    private String product_type_id;

    @ApiModelProperty(value = "组织类型，store门店，warehouse仓库, 如果参数为空将返回错误", required = true)
    private String party_type;

    @ApiModelProperty(value = "组织类型为store该参数是必须的，否则将返回错误")
    private Long party_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "分类名称")
    private String category_name;

    @ApiModelProperty(value = "分类ID")
    private Long category_id;

    @ApiModelProperty(value = "品牌名称")
    private String brand_name;

    @ApiModelProperty(value = "品牌ID")
    private Long brand_id;

    @ApiModelProperty(value = "是否过虑掉已选择的商品，1过滤，0不过滤，缺省0")
    private Integer filter_selected;

    @Override
    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    @Override
    public String getProduct_type_id() {
        return this.product_type_id;
    }
}
