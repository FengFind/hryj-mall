package com.hryj.entity.vo.inventory.request;

import com.hryj.constant.CommonConstantPool;
import com.hryj.utils.UtilValidate;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductsInventoryLockRequestVO
 * @description:
 * @create 2018/8/20 0020 11:06
 **/
@Data
@ApiModel(value = "商品库存锁定请求VO", description = "商品库存锁定请求VO")
public class ProductsInventoryLockRequestVO {

    @ApiModelProperty(value = "商品库存锁定模式，sub减库存， add加库存，参数值不匹配时返回错误", required = true)
    private String lock_model;

    @ApiModelProperty(value = "商品库存锁定条目集合", required = true)
    private List<ProductInventoryLockItem> lock_items;

    public boolean checkLockModel() {
        if (UtilValidate.isEmpty(this.lock_model)) {
            return false;
        }
        return CommonConstantPool.SUB.equals(this.lock_model) || CommonConstantPool.ADD.equals(this.lock_model);
    }
}
