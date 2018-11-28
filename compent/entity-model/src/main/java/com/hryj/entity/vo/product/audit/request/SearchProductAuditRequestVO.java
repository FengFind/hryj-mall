package com.hryj.entity.vo.product.audit.request;

import com.hryj.entity.vo.product.common.PageProductTypePermissionRequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: SearchProductAuditRequestVO
 * @description:
 * @create 2018/6/27 0027 10:52
 **/
@ApiModel(value = "商品审核查询VO", description = "商品审核查询VO")
@Data
public class SearchProductAuditRequestVO extends PageProductTypePermissionRequestVO {

    @ApiModelProperty(value = "商品类型: new_retail新零售，bonded跨境商品")
    private String product_type_id;

    @ApiModelProperty(value = "商品编号")
    private String product_id;

    @ApiModelProperty(value = "商品名称")
    private String product_name;

    @ApiModelProperty(value = "审核状态，1已审核，0未审核, 缺省处理为0")
    private Integer audit_status;

    @ApiModelProperty(value = "商品分类ID")
    private Long category_id;

    @ApiModelProperty(value = "商品分类名称")
    private String category_name;

    @ApiModelProperty(value = "品牌名称")
    private String brand_name;

    @ApiModelProperty(value = "品牌ID")
    private Long brand_id;

    @ApiModelProperty(value = "提交人")
    private String submit_by;

    @ApiModelProperty(value = "提交时间范围起")
    private String submit_time_begin;

    @ApiModelProperty(value = "提交时间范围止")
    private String submit_time_end;

    @ApiModelProperty(value = "审核处理人")
    private String handled_by;

    @ApiModelProperty(value = "审核结果, 1通过，0未通过")
    private Integer handled_result;

    @ApiModelProperty(value = "审核时间范围起")
    private String handled_time_begin;

    @ApiModelProperty(value = "审核时间范围止")
    private String handled_time_end;

    @Override
    public String getProduct_type_id() {
        return this.product_type_id;
    }

    @Override
    public void setProduct_type_id(String product_type_id) {
        this.product_type_id = product_type_id;
    }

}
