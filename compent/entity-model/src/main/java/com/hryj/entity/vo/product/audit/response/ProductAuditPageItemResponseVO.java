package com.hryj.entity.vo.product.audit.response;

import com.hryj.entity.vo.product.common.response.ProductBrand;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductAuditPageItemResponseVO
 * @description:
 * @create 2018/6/27 0027 11:19
 **/
@ApiModel(value = "商品审核分页数据项目VO", description = "商品审核分页数据项目VO")
@Data
public class ProductAuditPageItemResponseVO {

    @ApiModelProperty(value = "审核数据ID")
    private Long product_backup_id;

    @ApiModelProperty(value = "商品ID")
    private String product_id;

    @ApiModelProperty(value = "端口类型ID")
    private String product_type_id;

    @ApiModelProperty(value = "商品类型名称")
    private String product_type_name;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "商品图片URL")
    private String list_image_url;

    @ApiModelProperty(value = "品牌")
    private ProductBrand brand;

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
}
