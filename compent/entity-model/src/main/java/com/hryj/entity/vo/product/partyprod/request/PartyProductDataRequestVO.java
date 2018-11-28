package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.product.common.PageProductTypePermissionRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PartyProductDataRequestVO
 * @description:
 * @create 2018/6/27 0027 16:44
 **/
@ApiModel(value = "查询仓库或门店商品和基本信息请求VO", description = "查询仓库或门店商品和基本信息请求VO")
@Data
public class PartyProductDataRequestVO extends PageProductTypePermissionRequestVO {

    @ApiModelProperty(value = "商品类型ID，all所有(用户商品类型数据权限下的所有)，bonded跨境商品， new_retail新零售， 缺省默认：all")
    private String product_type_id;

    @ApiModelProperty(value = "仓库或门店的ID, 该字段为空时将返回错误", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品上下架状态，1上架，0下架，缺省为1")
    private Integer up_down_status = 1;

    @ApiModelProperty(value = "商品名称，有值时做前后模糊匹配")
    private String product_name;

    @ApiModelProperty(value = "商品分类名称，前后模糊匹配")
    private String category_name;

    @ApiModelProperty(value = "商品品牌ID")
    private Long brand_id;

    @ApiModelProperty(value = "商品品牌名称")
    private String brand_name;

    @ApiModelProperty(value = "商品分类的集合")
    private List<Long> category_id_list;

    @ApiModelProperty(value = "是否返回门店或仓库的基本信息，true返回， false不返回，缺省true")
    private Boolean return_party_info = true;

    @Override
    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    @Override
    public String getProduct_type_id() {
        return this.product_type_id;
    }
}
