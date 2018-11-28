package com.hryj.controller;

import com.hryj.cache.CodeCache;
import com.hryj.common.Result;
import com.hryj.entity.vo.cart.request.CartOperationRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartForStoreRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartRequestVO;
import com.hryj.entity.vo.cart.response.ShoppingCartResponseVO;
import com.hryj.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: ShoppingCartForUserController
 * @description: 用户端购物车服务
 * @create 2018/6/29 18:38
 **/
@Slf4j
@RestController
@RequestMapping(value = "/userCart")
public class ShoppingCartForUserController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: addShoppingCart
     * @methodDesc: 加入购物车
     * @description: 购物车商品数量编辑也用此接口
     * @param: [shoppingCartRequestVO]
     * @create 2018-06-29 18:46
     **/
    @PostMapping("/addShoppingCart")
    public Result addShoppingCart(@RequestBody ShoppingCartRequestVO shoppingCartRequestVO) {
        return shoppingCartService.addShoppingCart(shoppingCartRequestVO, CodeCache.getValueByKey("ShoppingCartType","S01"));
    }

    /**
     * @author 罗秋涵
     * @description: 修改购物车
     * @param: [shoppingCartRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-07-17 16:31
     **/
    @PostMapping("/updateShoppingCart")
    public Result updateShoppingCart(@RequestBody ShoppingCartRequestVO shoppingCartRequestVO){
        return shoppingCartService.updateShoppingCart(shoppingCartRequestVO,CodeCache.getValueByKey("ShoppingCartType","S01"));
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.cart.response.ShoppingCartResponseVO>
     * @author 李道云
     * @methodName: findShoppingCartList
     * @methodDesc: 查询购物车列表
     * @description:
     * @param: [requestVO]
     * @create 2018-06-29 19:06
     **/
    @PostMapping("/findShoppingCartList")
    public Result<ShoppingCartResponseVO> findShoppingCartList(@RequestBody ShoppingCartForStoreRequestVO shoppingCartForStoreRequestVO) {

        return shoppingCartService.findShoppingCartList(shoppingCartForStoreRequestVO.getLogin_token(),null);
    }

    /**
     * @return com.hryj.common.Result
     * @author 李道云
     * @methodName: deleteShoppingCartItem
     * @methodDesc: 批量删除购物车商品
     * @description: 购物车条目id用逗号拼接, 并返回购物车列表信息
     * @param: [cartItemIds]
     * @create 2018-06-29 19:16
     **/
    @PostMapping("/deleteShoppingCartItem")
    public Result<ShoppingCartResponseVO> deleteShoppingCartItem(@RequestBody CartOperationRequestVO cartoPerationRequestVO) {

        return shoppingCartService.deleteShoppingCartItem(cartoPerationRequestVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.cart.response.ShoppingCartResponseVO>
     * @author 罗秋涵
     * @description: 用户在购物车里清除无效商品
     * @param: [requestVO]
     * @create 2018-07-02 22:21
     **/
    @PostMapping("/clearInvalidProduct")
    public Result<ShoppingCartResponseVO> clearInvalidProduct(@RequestBody  CartOperationRequestVO cartoPerationRequestVO) {

        return shoppingCartService.clearInvalidProduct(cartoPerationRequestVO);
    }


    /**
     * @author 罗秋涵
     * @description: 查询购物车商品数量
     * @param: [shoppingCartForStoreRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.cart.response.ShoppingCartResponseVO>
     * @create 2018-07-17 11:21
     **/
    @PostMapping("/getCartProductNum")
    public Result<Integer> getCartProductNum(@RequestBody ShoppingCartForStoreRequestVO shoppingCartForStoreRequestVO) {

        return shoppingCartService.getCartProductNum(shoppingCartForStoreRequestVO);
    }

}
