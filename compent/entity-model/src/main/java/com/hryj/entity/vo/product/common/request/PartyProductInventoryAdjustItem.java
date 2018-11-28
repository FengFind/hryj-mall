package com.hryj.entity.vo.product.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: PartyProductInventoryAdjustItem
 * @description:
 * @create 2018/7/9 0009 16:36
 **/
@ApiModel(value = "当事组织（门店或仓库）库存调整条目", description = "当事组织（门店或仓库）库存调整条目")
@Data
public class PartyProductInventoryAdjustItem {

    @ApiModelProperty(value = "门店或仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "调整数量，增加用正数，减少用负数", required = true)
    private Integer adjust_num;
}
