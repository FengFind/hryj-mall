package com.hryj.entity.vo.product.audit.mapping;

import com.hryj.entity.bo.product.Brand;
import com.hryj.entity.vo.delegator.GenericConverter;
import com.hryj.entity.vo.product.audit.response.ProductAuditPageItemResponseVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductAuditPageMappingItem
 * @description:
 * @create 2018/9/17 0017 9:58
 **/
@Data
public class ProductAuditPageMappingItem {

    @ApiModelProperty(value = "审核数据ID")
    private Long product_backup_id;

    @ApiModelProperty(value = "商品ID")
    private String product_id;

    @ApiModelProperty(value = "端口类型ID")
    private String product_type_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "品牌")
    private Long brand;

    @ApiModelProperty(value = "分类名称")
    private String category_name;

    @ApiModelProperty(value = "提交人")
    private String submit_by;

    @ApiModelProperty(value = "提交时间,格式 yyyy-MM-dd HH:mm:ss")
    private String submit_time;

    @ApiModelProperty(value = "审核处理人")
    private String handled_by;

    @ApiModelProperty(value = "审核处理时间，格式：yyyy-MM-dd HH:mm:ss")
    private String handled_time;

    @ApiModelProperty(value = "审核处理结果，1通过，0未通过")
    private String handled_result;

    public ProductAuditPageItemResponseVO convertTo(GenericConverter<Brand> brand_getter, GenericConverter<String> prod_type_getter) {
        ProductAuditPageItemResponseVO vo = new ProductAuditPageItemResponseVO();
        vo.setProduct_backup_id(this.product_backup_id);
        vo.setProduct_id(this.product_id);
        vo.setProduct_type_id(this.product_type_id);
        vo.setProduct_name(this.product_name);
        vo.setList_image_url(this.list_image_url);
        vo.setCategory_name(this.category_name);
        vo.setSubmit_by(this.submit_by);
        vo.setSubmit_time(this.submit_time);
        vo.setHandled_by(this.handled_by);
        vo.setHandled_time(this.handled_time);
        vo.setHandled_result(this.handled_result);
        Brand brand = brand_getter.convert(this.brand);
        if (brand != null) {
            vo.setBrand(brand.convertToProdBrand());
        }
        vo.setProduct_type_name(prod_type_getter.convert(this.product_type_id));
        return vo;
    }
}
