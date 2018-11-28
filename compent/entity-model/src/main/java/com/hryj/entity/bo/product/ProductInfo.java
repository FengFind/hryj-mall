package com.hryj.entity.bo.product;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.product.partyprod.response.PartyIntersectionProductItem;
import com.hryj.entity.vo.product.response.ProdListItemResponseVO;
import com.hryj.entity.vo.product.response.ProductDetailResponseVO;
import com.hryj.entity.vo.product.response.app.AppProductInfoResponseVO;
import com.hryj.utils.UtilValidate;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 商品信息表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_info")
public class ProductInfo extends Model<ProductInfo> {

    public static final Integer FORBID_SALE_FLAG = 1;

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品名称
     */
    private String product_name;
    /**
     * 商品分类id
     */
    private Long prod_cate_id;
    /**
     * 商品分类名称路径
     */
    private String prod_cate_path;
    /**
     * 品牌
     */
    private Long brand;
    /**
     * 产地
     */
    private Long made_where;
    /**
     * 保质期
     */
    private String shelf_life;
    /**
     * 商品规格
     */
    private String specification;
    /**
     * 商品积分
     */
    private Integer integral;
    /**
     * 商品描述
     */
    private String product_info;
    /**
     * 商品详细信息
     */
    private String product_detail;
    /**
     * 商品列表图
     */
    private String list_image_url;
    /**
     * 商品详情图1
     */
    private String detail_image_url_one;
    /**
     * 商品详情图2
     */
    private String detail_image_url_two;
    /**
     * 商品详情图3
     */
    private String detail_image_url_three;
    /**
     * 商品详情图4
     */
    private String detail_image_url_four;
    /**
     * 商品详情图5
     */
    private String detail_image_url_five;
    /**
     * 商品成本
     */
    private BigDecimal cost_price;
    /**
     * 禁售标志:1-禁售,0-正常，该字段控制全网禁售
     */
    private Integer forbid_sale_flag;
    /**
     * 商品ERP编码
     */
    private String erp_code;
    /**
     * 推介日期
     */
    private Date introduction_date;
    /**
     * 销售终止日期
     */
    private Date sales_end_date;
    /**
     * 上下架状态:1-上架,0-下架
     */
    private Integer up_down_status;
    /**
     * 审核状态:1-已审核,0-未审核
     */
    private Integer audit_status;
    /**
     * 操作人id
     */
    private Long operator_id;

    /**
     * 操作人姓名
     */
    private String operator_name;

    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 商品类型
     */
    private String product_type_id;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

    public PartyIntersectionProductItem convertToPartyProdListItem(GenericConverter<Brand> brand_converter, GenericConverter<ProductGeo> geo_converter, GenericConverter<String> prod_type_getter) {
        PartyIntersectionProductItem item = new PartyIntersectionProductItem();
        item.setProduct_id(this.id);
        item.setCategory_name(this.prod_cate_path);
        item.setCost_price(this.cost_price == null ? "0.00" : this.cost_price.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        item.setList_image_url(this.list_image_url);
        item.setSpecification(this.specification);
        item.setProduct_name(this.product_name);
        item.setProduct_type_id(this.product_type_id);
        item.setProduct_type_name(prod_type_getter.convert(this.product_type_id));
        Brand brand = brand_converter.convert(this.brand);
        if (brand != null) {
            item.setBrand(brand.convertToProdBrand());
        }
        ProductGeo geo = geo_converter.convert(this.made_where);
        if (geo != null) {
            item.setMade_where(geo.convertToMadeWhere());
        }
        return item;
    }


    public ProdListItemResponseVO convertToListItem(GenericConverter<Brand> brand_getter, GenericConverter<ProductGeo> geo_getter) {
        ProdListItemResponseVO vo = new ProdListItemResponseVO();
        vo.setProduct_id(this.getId());
        vo.setProduct_name(this.getProduct_name());
        vo.setCategory_name(this.getProd_cate_path());
        vo.setProd_cate_id(this.getProd_cate_id());

        Brand brand = brand_getter.convert(this.brand);
        if (brand != null) {
            vo.setBrand(brand.convertToProdBrand());
        }

        ProductGeo geo = geo_getter.convert(this.made_where);
        if (geo != null) {
            vo.setMadeWhere(geo.convertToMadeWhere());
        }

        vo.setSpecification(this.getSpecification());
        vo.setList_image_url(this.getList_image_url());
        vo.setProduct_info(this.getProduct_info());
        vo.setForbid_sale_flag(this.getForbid_sale_flag());
        vo.setUp_down_status(this.getUp_down_status());
        vo.setAudit_status(this.getAudit_status());
        if(this.getCost_price() != null){
            vo.setCost_price(this.getCost_price().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        }
        vo.setIntroduction_date(DateUtil.formatDateTime(this.getIntroduction_date()));
        vo.setSales_end_date(DateUtil.formatDateTime(this.getSales_end_date()));
        vo.setOperator_name(this.getOperator_name());
        vo.setCreate_time(DateUtil.formatDateTime(this.getCreate_time()));
        vo.setShelf_life(this.getShelf_life());
        return vo;
    }

    public ProductDetailResponseVO convertToDetailResponse(GenericConverter<Brand> brand_getter, GenericConverter<ProductGeo> geo_getter) {
        ProductDetailResponseVO vo = new ProductDetailResponseVO();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        vo.setProduct_id(this.getId());
        vo.setProduct_name(this.getProduct_name());
        vo.setProd_cate_id(this.getProd_cate_id());
        vo.setProd_cate_name(this.getProd_cate_path());
        vo.setIntegral(this.getIntegral());
        vo.setShelf_life(this.getShelf_life());
        vo.setSpecification(this.getSpecification());
        vo.setComments(this.getProduct_info());
        vo.setLong_description(this.getProduct_detail());
        vo.setList_image_url(this.getList_image_url());
        vo.setProduct_type_id(this.product_type_id);

        Brand brand = brand_getter.convert(this.brand);
        if (brand != null) {
            vo.setBrand(brand.convertToProdBrand());
        }

        ProductGeo geo = geo_getter.convert(this.made_where);
        if (geo != null) {
            vo.setMade_where(geo.convertToMadeWhere());
        }

        vo.setDetail_image_list(getDetailImageList());
        vo.setCost_price(this.getCost_price());
        vo.setErp_code(this.getErp_code());
        vo.setIntroduction_date(this.getIntroduction_date() == null ? "" : sdf.format(this.getIntroduction_date()));
        vo.setSales_end_date(this.getSales_end_date() == null ? "" : sdf.format(this.getSales_end_date()));
        vo.setForbid_sale_flag(this.getForbid_sale_flag());
        vo.setUp_down_status(this.getUp_down_status());
        vo.setAudit_status(this.getAudit_status());
        vo.setOperator_id(this.getOperator_id());
        vo.setOperator_name(this.getOperator_name());
        vo.setUpdate_time(this.getUpdate_time() == null ? "" : sdf.format(this.getUpdate_time()));
        vo.setCreate_time(this.getCreate_time() == null ? "" : sdf.format(this.getCreate_time()));
        return vo;
    }

    public AppProductInfoResponseVO convertToAppResponse(GenericConverter<String> prod_type_getter, GenericConverter<Brand> brand_getter, GenericConverter<ProductGeo> geo_getter, GenericConverter<String> long_desc_getter) {
        AppProductInfoResponseVO vo = new AppProductInfoResponseVO();
        vo.setProduct_id(this.getId());
        vo.setProduct_name(this.getProduct_name());
        vo.setCategory_name(this.getProd_cate_path());
        vo.setCategory_id(this.getProd_cate_id());

        Brand brand = brand_getter.convert(this.brand);
        if (brand != null) {
            vo.setBrand(brand.convertToProdBrand());
        }

        ProductGeo geo = geo_getter.convert(this.made_where);
        if (geo != null) {
            vo.setMade_where(geo.convertToMadeWhere());
        }

        vo.setProduct_type_id(this.product_type_id);
        vo.setProduct_type_name(prod_type_getter.convert(this.product_type_id));

        vo.setShelf_life(this.getShelf_life());
        vo.setSpecification(this.getSpecification());
        vo.setComments(this.getProduct_info());

        if (UtilValidate.isNotEmpty(this.getProduct_detail())) {
            vo.setLong_description(long_desc_getter.convert(this.getProduct_detail()));
        }

        vo.setList_image_url(this.getList_image_url());
        List<String> detail_image_list = new ArrayList<>(5);
        vo.setDetail_image_list(detail_image_list);
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_one())) {
            detail_image_list.add(this.getDetail_image_url_one());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_two())) {
            detail_image_list.add(this.getDetail_image_url_two());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_three())) {
            detail_image_list.add(this.getDetail_image_url_three());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_four())) {
            detail_image_list.add(this.getDetail_image_url_four());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_five())) {
            detail_image_list.add(this.getDetail_image_url_five());
        }
        return vo;
    }

    public void setDetailImage(int image_sort, String image_url) {
        switch (image_sort) {
            case 1:
                this.setDetail_image_url_one(image_url);
                break;
            case 2:
                this.setDetail_image_url_two(image_url);
                break;
            case 3:
                this.setDetail_image_url_three(image_url);
                break;
            case 4:
                this.setDetail_image_url_four(image_url);
                break;
            case 5:
                this.setDetail_image_url_five(image_url);
                break;
            default:
                this.setDetail_image_url_one(image_url);
        }
    }

    public List<String> getDetailImageList() {
        List<String> detail_image_list = new ArrayList<>(5);
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_one())) {
            detail_image_list.add(this.getDetail_image_url_one());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_two())) {
            detail_image_list.add(this.getDetail_image_url_two());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_three())) {
            detail_image_list.add(this.getDetail_image_url_three());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_four())) {
            detail_image_list.add(this.getDetail_image_url_four());
        }
        if (UtilValidate.isNotEmpty(this.getDetail_image_url_five())) {
            detail_image_list.add(this.getDetail_image_url_five());
        }
        return detail_image_list;
    }
}
