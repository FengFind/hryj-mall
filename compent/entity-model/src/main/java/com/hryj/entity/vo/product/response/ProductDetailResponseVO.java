package com.hryj.entity.vo.product.response;

import com.hryj.entity.vo.product.audit.response.ProductAuditResponseVO;
import com.hryj.entity.vo.product.common.response.ProductBrand;
import com.hryj.entity.vo.product.common.response.ProductMadeWhere;
import com.hryj.entity.vo.product.crossborder.response.CrossBorderProductResponseVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 王光银
 * @className: ProductDetailResponseVO
 * @description:
 * @create 2018/6/26 0026 13:46
 **/
@ApiModel(value = "单个商品完整信息查询响应VO", description = "单个商品完整信息查询响应VO")
@Data
public class ProductDetailResponseVO {

    @ApiModelProperty(value = "商品ID")
    private Long product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品分类ID")
    private Long prod_cate_id;

    @ApiModelProperty(value = "商品分类名称")
    private String prod_cate_name;

    @ApiModelProperty(value = "保质期")
    private String shelf_life;

    @ApiModelProperty(value = "规格")
    private String specification;

    @ApiModelProperty(value = "积分")
    private Integer integral;

    @ApiModelProperty(value = "商品特色描述")
    private String comments;

    @ApiModelProperty(value = "商品详细描述")
    private String long_description;

    @ApiModelProperty(value = "商品列表图URL")
    private String list_image_url;

    @ApiModelProperty(value = "商品详情图URL集合")
    private List<String> detail_image_list;

    @ApiModelProperty(value = "商品成本价")
    private BigDecimal cost_price;

    @ApiModelProperty(value = "ERP编码")
    private String erp_code;

    @ApiModelProperty(value = "推介日期，在此日期前门店和仓库将不能选择销售, 缺省处理为商品创建日期")
    private String introduction_date;

    @ApiModelProperty(value = "销售结束日期，在此日期后门店和仓库将不能选择销售，缺省处理为永久销售")
    private String sales_end_date;

    @ApiModelProperty(value = "是否全网禁售，1禁售，0未禁售")
    private Integer forbid_sale_flag;

    @ApiModelProperty(value = "是否上架， 1上架，0下架")
    private Integer up_down_status;

    @ApiModelProperty(value = "创建操作人ID")
    private Long operator_id;

    @ApiModelProperty(value = "创建操作人")
    private String operator_name;

    @ApiModelProperty(value = "创建时间")
    private String create_time;

    @ApiModelProperty(value = "最后更新时间")
    private String update_time;

    @ApiModelProperty(value = "审核状态，1已审核，1未审核")
    private Integer audit_status;

    @ApiModelProperty(value = "商品类型ID")
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称")
    private String product_type_name;

    @ApiModelProperty(value = "品牌")
    private ProductBrand brand;

    @ApiModelProperty(value = "产地")
    private ProductMadeWhere made_where;

    @ApiModelProperty(value = "跨境商品信息, product_type_id = bonded时有值")
    private CrossBorderProductResponseVO cross_border_product;

    @ApiModelProperty(value = "商品的审核记录数据")
    private List<ProductAuditResponseVO> audit_record_list;

    @ApiModelProperty(value = "分类属性集合")
    private List<ProdAttrItemResponseVO> category_attr_list;

    @ApiModelProperty(value = "自定义商品属性集合")
    private List<ProdAttrItemResponseVO> attr_list;

}
