package com.hryj.entity.vo.product.audit.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductBackupRequestVO
 * @description: 商品备份数据请求VO
 * @create 2018/7/5 0005 10:42
 **/
@ApiModel(value = "商品备份数据请求VO", description = "商品备份数据请求VO")
@Data
public class ProductBackupRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品备份数据ID", required = true)
    private Long product_backup_id;

    @ApiModelProperty(value = "是否返回审核记录数据, 缺省true")
    private Boolean include_audit_record = true;
}
