package com.hryj.entity.vo.inventory.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductInventoryLockItem
 * @description:
 * @create 2018/8/20 0020 11:09
 **/
@Data
@ApiModel(value = "商品库存锁定条目", description = "商品库存锁定条目")
public class ProductInventoryLockItem {

    @ApiModelProperty(value = "门店、仓库ID", required = true)
    private Long party_id;

    @ApiModelProperty(value = "商品ID", required = true)
    private Long product_id;

    @ApiModelProperty(value = "活动ID，有则传，没有则不传")
    private Long activity_id;

    @ApiModelProperty(value = "库存锁定数据, 必须是大于0的正整数，否则接口将返回错误", required = true)
    private Integer lock_quantity;
}
