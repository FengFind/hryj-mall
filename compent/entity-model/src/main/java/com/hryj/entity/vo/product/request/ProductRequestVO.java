package com.hryj.entity.vo.product.request;

import com.hryj.entity.bo.product.ProductInfo;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.product.crossborder.request.CrossBorderProductRequestVO;
import com.hryj.utils.UtilValidate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 王光银
 * @className: ProductBuyRequestVO
 * @description:
 * @create 2018/6/26 0026 13:46
 **/
@Data
@ApiModel(value = "创建或修改商品请求VO", description = "创建或修改商品请求VO")
public class ProductRequestVO extends RequestVO {

    public static final Calendar FIVE_HUNDRED_YEARS_LATER = Calendar.getInstance();

    @ApiModelProperty(value = "商品ID， 新增时不需要，修改时必须")
    private Long product_id;

    @ApiModelProperty(value = "商品名称", required = true)
    private String product_name;

    @ApiModelProperty(value = "商品分类", required = true)
    private Long prod_cate_id;

    @ApiModelProperty(value = "保质期", required = true)
    private String shelf_life;

    @ApiModelProperty(value = "规格", required = true)
    private String specification;

    @ApiModelProperty(value = "商品成本价", required = true)
    private BigDecimal cost_price;

    @ApiModelProperty(value = "商品特色描述", required = true)
    private String comments;

    @ApiModelProperty(value = "商品列表图URL", required = true)
    private String list_image_url;

    @ApiModelProperty(value = "商品详情图URL集合", required = true)
    private List<String> detail_image_list;

    @ApiModelProperty(value = "商品详细描述")
    private String long_description;

    @ApiModelProperty(value = "积分")
    private Integer integral;

    @ApiModelProperty(value = "ERP编码")
    private String erp_code;

    @ApiModelProperty(value = "推介日期，在此日期前门店和仓库将不能选择销售, 缺省处理为商品创建日期")
    private String introduction_date;

    @ApiModelProperty(value = "销售结束日期，在此日期后门店和仓库将不能选择销售，缺省处理为永久销售")
    private String sales_end_date;

    @ApiModelProperty(value = "商品类型ID, bonded 跨境商品，new_retail新零售商品")
    private String product_type_id;

    @ApiModelProperty(value = "品牌ID(是数字ID)", required = true)
    private Long brand_id;

    @ApiModelProperty(value = "产地ID(是数字ID)", required = true)
    private Long made_where_id;

    @ApiModelProperty(value = "跨境商品数据, product_type_id=bonded时必须")
    private CrossBorderProductRequestVO cross_border_product;

    @ApiModelProperty(value = "商品属性集合")
    private List<ProductAttributeItemRequestVO> attr_list;

    public ProductInfo convertTo() {
        ProductInfo info = new ProductInfo();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        info.setProduct_type_id(this.getProduct_type_id());

        info.setProduct_name(this.getProduct_name());
        info.setProd_cate_id(this.getProd_cate_id());
        info.setBrand(this.getBrand_id());
        info.setMade_where(this.getMade_where_id());
        info.setShelf_life(this.getShelf_life());
        info.setSpecification(this.getSpecification());
        info.setIntegral(this.getIntegral());
        info.setList_image_url(this.getList_image_url());

        if (UtilValidate.isNotEmpty(this.getDetail_image_list())) {
            int i = 0;
            for (String image_url : this.getDetail_image_list()) {
                info.setDetailImage(++i, image_url);
            }
        }

        info.setProduct_info(this.getComments());
        info.setProduct_detail(this.getLong_description());
        info.setCost_price(this.getCost_price().setScale(2, BigDecimal.ROUND_HALF_UP));
        info.setErp_code(this.getErp_code());
        try {
            info.setIntroduction_date(UtilValidate.isEmpty(this.getIntroduction_date()) ? new Date() : sdf.parse(this.getIntroduction_date()));
            info.setSales_end_date(UtilValidate.isEmpty(this.getSales_end_date()) ? FIVE_HUNDRED_YEARS_LATER.getTime() : sdf.parse(this.getSales_end_date()));
        } catch (ParseException e) {
            throw new RuntimeException("推介销售日期、销售终止日期格式错误");
        }
        info.setId(this.getProduct_id());
        return info;
    }

}
