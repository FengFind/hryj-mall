package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.cart.ShoppingCartRecord;
import com.hryj.entity.bo.order.OrderInfo;
import com.hryj.entity.vo.cart.ShoppingCartPoductVO;
import com.hryj.entity.vo.order.OrderInfoVO;
import com.hryj.entity.vo.order.OrderListInfoVO;
import com.hryj.entity.vo.order.OrderLogisticsVO;
import com.hryj.entity.vo.order.request.OrderListRequestVO;
import com.hryj.entity.vo.order.response.OrderDifferentStateNumResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: OrderForUserAppMapper
 * @description:
 * @create 2018/7/4 0004 20:28
 **/
@Component
public interface OrderForUserAppMapper extends BaseMapper<OrderInfo> {
    /**
     * 获取购物车商品详细
     * @param cartRecord
     * @return
     */
    ShoppingCartPoductVO getCartPoductDetails(ShoppingCartRecord cartRecord);

    /**
     * 查询用户订单记录
     * @param orderListRequestVO
     * @return
     */
    List<OrderListInfoVO> findWaitPayOrderList(OrderListRequestVO orderListRequestVO);

    /**
     * 查询用户订单（分页）
     * @param orderListRequestVO
     * @param page
     * @return
     */
    List<OrderListInfoVO> findOrderListByOrderStatus(OrderListRequestVO orderListRequestVO, Page page);

    /**
     * 根据订单编号获取订单基本信息
     * @param order_id
     * @return
     */
    OrderInfoVO getOrderInfo(Long order_id);

    /**
     * 根据订单编号获取订单物流信息
     * @param order_id
     * @return
     */
    OrderLogisticsVO getOrderLogistics(@Param("order_id") Long order_id,@Param("distributionType") String distributionType);

    /**
     * @author 罗秋涵
     * @description: 查询各
     * @param:
     * @return
     * @create 2018-07-09 16:44
     **/
    OrderDifferentStateNumResponseVO getOrderDifferentStateNum(@Param("value1") String value1, @Param("value2")String value2, @Param("value3")String value3, @Param("value4")String value4,@Param("userId")Long userId);

}
