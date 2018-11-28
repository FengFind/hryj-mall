package com.hryj.entity.vo.inventory.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: ProductsInventoryLockRollBackRequestVO
 * @description:
 * @create 2018/8/20 0020 17:01
 **/
@Data
@ApiModel(value = "回滚补偿商品库存锁定请求VO", description = "回滚补偿商品库存锁定请求VO")
public class ProductsInventoryLockRollBackRequestVO {

    @ApiModelProperty(value = "回滚补偿商品锁定结果的事务码, 当事务码不存在时返回错误", required = true)
    private String transaction_code;
}
