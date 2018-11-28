package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.cart.ShoppingCartRecord;
import com.hryj.entity.vo.cart.InvalidCartProductVO;
import com.hryj.entity.vo.cart.ShoppingCartPoductVO;
import com.hryj.entity.vo.cart.ShoppingCartVO;
import com.hryj.entity.vo.cart.request.ShoppingCartRequestVO;
import com.hryj.entity.vo.order.OrderCreateFromCartVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author 罗秋涵
 * @className: ShoppingCartMapper
 * @description:
 * @create 2018/7/2 0002 10:09
 **/
@Component
public interface ShoppingCartMapper extends BaseMapper<ShoppingCartRecord> {

    /**
     * 查询购物车记录
     *
     * @param shoppingCartRequestVO
     * @return
     */
    ShoppingCartRecord getShoppingCartRecord(@Param("cart") ShoppingCartRequestVO shoppingCartRequestVO,@Param("help_staff_id")Long help_staff_id);

    /**
     * 加入购物车时获取商品的价格
     *
     * @param shoppingCartRequestVO
     * @return
     */
    Map<String, BigDecimal> getProductPrice(ShoppingCartRequestVO shoppingCartRequestVO);

    /**
     * 获取购物车记录对应门店信息
     *
     * @param user_id
     * @param help_staff_id
     * @return
     */
    List<ShoppingCartRecord> findPartyList(@Param("user_id") Long user_id, @Param("help_staff_id") Long help_staff_id);

    /**
     * 根据门店和用户ID查询商品信息
     *
     * @param shoppingCartVO
     * @return
     */
    List<ShoppingCartPoductVO> findCartPoductList(@Param("VO") ShoppingCartVO shoppingCartVO);

    /**
     * 获取无效的购物车列表
     *
     * @param user_id
     * @param help_staff_id
     * @param upDownStatus
     * @return
     */
    List<InvalidCartProductVO> findInvalidCartProductList(@Param("user_id") Long user_id, @Param("help_staff_id") Long help_staff_id,
                                                          @Param("upDownStatus") String upDownStatus);

    /**
     * 删除购物车商品
     *
     * @param idList
     * @return
     */
    int deleteShoppingCartItem(List<String> idList);


    /**
     * 删除购物车商品
     *
     * @param list
     * @return
     */
    void deleteShoppingCartByIds(List<Long> list);

    /**
     * 清除购物车无效商品
     *
     * @param user_id
     * @param help_staff_id
     * @param upDownStatus
     * @return
     */
    int clearInvalidProduct(@Param("user_id") Long user_id, @Param("help_staff_id") Long help_staff_id,
                            @Param("upDownStatus") String upDownStatus);

    /**
     * 根据购物车编号获取购物车记录
     *
     * @param idsList
     * @return
     */
    List<ShoppingCartRecord> findShoppingCartRecordList(List<String> idsList);

    /**
     * 根据购物车ids查询购物车list
     * @param idsList
     * @return
     */
    List<OrderCreateFromCartVO> findShoppingCartRecordListByIdsCreateOrder(List<Long> idsList);

    /**
     * 根据购物车ids查询购物车list
     * @param idsList
     * @return
     */
    List<OrderCreateFromCartVO> findShoppingCartRecordListByIdsCreateOrderNew(List<Long> idsList);


    /**
     * 查询用户购物车记录总数
     * @param user_id
     * @param help_staff_id
     * @return
     */
    Integer getCartProductNum(@Param("user_id")Long user_id, @Param("help_staff_id")Long help_staff_id);

    /**
     * 查询用户购物车列表
     * @param user_id
     * @param help_staff_id
     * @return
     */
    List<ShoppingCartRecord> findCartRecordList(@Param("user_id")Long user_id, @Param("help_staff_id")Long help_staff_id);

    /**
     * 获取购物车记录及门店，商品，用户对于的商品数量
     * @param shoppingCartRequestVO
     * @param help_staff_id
     * @param cart_type
     * @return
     */
    Integer getUserPartyProductNum(@Param("cart") ShoppingCartRequestVO shoppingCartRequestVO,
                                                   @Param("help_staff_id") Long help_staff_id,
                                                   @Param("cart_type") String cart_type);

    /**
     * 根据购物车信息查询商品部分信息
     *
     * @param cartRecord
     *          购物车信息
     * @return 对象
     */
    ShoppingCartPoductVO findCartProductInfo(ShoppingCartRecord cartRecord);
}
