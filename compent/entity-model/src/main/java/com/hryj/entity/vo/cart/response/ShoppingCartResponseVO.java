package com.hryj.entity.vo.cart.response;

import com.hryj.entity.vo.cart.InvalidCartProductVO;
import com.hryj.entity.vo.cart.ShoppingCartVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 李道云
 * @className: ShoppingCartResponseVO
 * @description: 购物车响应VO
 * @create 2018/6/29 18:48
 **/
@ApiModel(description = "购物车响应VO")
@Data
public class ShoppingCartResponseVO {

    @ApiModelProperty(value = "购物车列表")
    private List<ShoppingCartVO> cartList;

    @ApiModelProperty(value = "失效商品列表")
    private List<InvalidCartProductVO> invalidCartList;

}
