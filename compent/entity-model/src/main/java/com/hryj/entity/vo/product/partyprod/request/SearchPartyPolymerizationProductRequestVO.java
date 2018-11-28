package com.hryj.entity.vo.product.partyprod.request;

import com.hryj.entity.vo.product.common.PageProductTypePermissionRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: SearchPartyPolymerizationProductRequestVO
 * @description:
 * @create 2018/6/28 0028 9:23
 **/
@ApiModel(value = "分页查询多个门店仓库的聚会商品请求VO", description = "分页查询多个门店仓库的聚会商品请求VO")
@Data
public class SearchPartyPolymerizationProductRequestVO extends PageProductTypePermissionRequestVO {

    @ApiModelProperty(value = "商品类型ID，all所有(用户在商品类型数据权限下可见的所有)，bonded跨境商品， new_retail新零售， 缺省:all")
    private String product_type_id;

    @ApiModelProperty(value = "商品名称，做前后模糊匹配")
    private String product_name;

    @ApiModelProperty(value = "商品分类名称，做前后模糊匹配")
    private String category_name;

    @ApiModelProperty(value = "商品分类ID")
    private Long category_id;

    @ApiModelProperty(value = "品牌名称")
    private String brand_name;

    @ApiModelProperty(value = "品牌ID")
    private Long brand_id;

    @ApiModelProperty(value = "是否包含已下架商品, true包含， false不包含, 缺省false")
    private Boolean include_down_status;

    @ApiModelProperty(value = "门店或仓库的ID集合,该参数为空时直接返回不做查询处理", required = true)
    private List<Long> party_id_list;

    @ApiModelProperty(value = "商品查询结果的处理类型，1交集，0并集,缺省1", required = true)
    private Integer polymerize_type = 1;

    @ApiModelProperty(value = "是否为当前操作用户权限下的所有门店或仓库")
    private Boolean is_all_party;

    @ApiModelProperty(value = "当事组织类型, 01门店， 02仓库，该参数与 is_all_party配套使用, 当is_all_party=true时，必须指定该参数")
    private String party_type;

    @Override
    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

    @Override
    public String getProduct_type_id() {
        return this.product_type_id;
    }
}
