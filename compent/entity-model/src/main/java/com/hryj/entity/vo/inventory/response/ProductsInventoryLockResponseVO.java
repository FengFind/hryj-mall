package com.hryj.entity.vo.inventory.response;

import com.hryj.constant.CommonConstantPool;
import com.hryj.entity.vo.inventory.request.ProductInventoryLockItem;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRollBackRequestVO;
import com.hryj.utils.UtilValidate;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ProductsInventoryLockResponseVO
 * @description:
 * @create 2018/8/20 0020 11:14
 **/
@Data
public class ProductsInventoryLockResponseVO {

    @ApiModelProperty(value = "库存锁定结果，当且仅当该字段为：Y时，锁定成功")
    private String lock_result;

    @ApiModelProperty(value = "当且仅 lock_result = Y时该字段有值， 使用该事务码可以回滚补偿库存锁定结果")
    private String transaction_code;

    @ApiModelProperty(value = "库存不足的商品条目, 只返回库存不足的商品")
    private List<ProductInventoryLockItem> inventory_out_item;

    /**
     * @author 王光银
     * @methodName: isSuccess
     * @methodDesc: 返回库存锁定是否成功
     * @description:
     * @param: []
     * @return boolean
     * @create 2018-08-21 9:19
     **/
    public boolean isSuccess() {
        return CommonConstantPool.UPPER_Y.equals(lock_result);
    }

    /**
     * @author 王光银
     * @methodName: getCompensateProductsLock
     * @methodDesc: 返回回滚补偿请求参数
     * @description:
     * @param: []
     * @return com.hryj.entity.vo.inventory.request.ProductsInventoryLockRollBackRequestVO
     * @create 2018-08-21 10:28
     **/
    public ProductsInventoryLockRollBackRequestVO getCompensateProductsLock() {
        if (!isSuccess() || UtilValidate.isEmpty(transaction_code)) {
            return null;
        }
        ProductsInventoryLockRollBackRequestVO requestVO = new ProductsInventoryLockRollBackRequestVO();
        requestVO.setTransaction_code(this.transaction_code);
        return requestVO;
    }

}
