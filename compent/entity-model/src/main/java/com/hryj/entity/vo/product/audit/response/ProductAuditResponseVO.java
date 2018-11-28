package com.hryj.entity.vo.product.audit.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductAuditResponseVO
 * @description:
 * @create 2018/6/26 0026 14:44
 **/
@ApiModel(value = "商品审核记录数据VO", description = "商品审核记录数据VO")
@Data
public class ProductAuditResponseVO {

    @ApiModelProperty(value = "审核记录ID")
    private Long id;

    @ApiModelProperty(value = "提交人姓名")
    private String submit_staff;

    @ApiModelProperty(value = "提交时间, 格式: yyyy-MM-dd HH:mm:ss")
    private String submit_time;

    @ApiModelProperty(value = "商品数据提交类型， new新增，modify修改")
    private String submit_type;

    @ApiModelProperty(value = "审核处理人姓名")
    private String handle_staff;

    @ApiModelProperty(value = "审核处理时间, 格式: yyyy-MM-dd HH:mm:ss")
    private String handle_time;

    @ApiModelProperty(value = "审核处理结果，是否通过审核")
    private Boolean handle_result;

    @ApiModelProperty(value = "审核处理备注")
    private String audit_remark;

}
