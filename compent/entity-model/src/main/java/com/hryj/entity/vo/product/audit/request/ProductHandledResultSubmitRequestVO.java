package com.hryj.entity.vo.product.audit.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductHandledResultSubmitRequestVO
 * @description: 商品审核处理结果提交请求VO
 * @create 2018/7/5 0005 10:45
 **/
@ApiModel(value = "商品审核处理结果提交请求VO", description = "商品审核处理结果提交请求VO")
@Data
public class ProductHandledResultSubmitRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品备份数据ID", required = true)
    private Long product_backup_id;

    @ApiModelProperty(value = "处理结果， 1通过，0未通过", required = true)
    private Integer handle_result;

    @ApiModelProperty(value = "处理备注，驳回时，原因是必须的")
    private String audit_remark;
}
