package com.hryj.entity.vo.cart.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ShoppingCartForStoreRequestVO
 * @description:
 * @create 2018/7/5 0005 20:53
 **/
@Data
@ApiModel(description = "查询购物车列表")
public class ShoppingCartForStoreRequestVO extends RequestVO {

    @ApiModelProperty(value = "用户id", notes = "代下单功能时有值，需要知道是谁的购物车")
    private Long user_id;
}
