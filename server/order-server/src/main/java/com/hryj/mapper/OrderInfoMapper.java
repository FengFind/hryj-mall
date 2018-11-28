package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.order.OrderInfo;
import com.hryj.entity.bo.order.OrderStatusRecord;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.entity.vo.cart.request.ShoppingCartRequestVO;
import com.hryj.entity.vo.order.*;
import com.hryj.entity.vo.order.crossOrder.CrossBorderOrderVo;
import com.hryj.entity.vo.order.request.AdminOrderListRequestVO;
import com.hryj.entity.vo.order.request.OrderSaveNowRequestVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 叶方宇
 * @className: OrderAdminMapper
 * @description: 订单详情信息
 * @create 2018/7/4 0004 17:11
 **/
@Component
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单ID查询订单信息和门店信息
     * @param:
     * @return
     * @create 2018-07-18 16:13
     **/
    OrderInfoVO getOrderInfoVO(Long order_id);



    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 分页查询订单数据
     * @param:
     * @return
     * @create 2018-07-18 16:13
     **/
    List<AdminOrderInfoVO> getOrderInfoVOListByPage(AdminOrderListRequestVO adminOrderListRequestVO, Page page);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单ID 查询订单信息和订单商品信息
     * @param:
     * @return
     * @create 2018-07-09 21:21
     **/
    List<DistributionProductVO> selectOrderInfoAndOrderProductMessage(Long order_id);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description:  立即下单
     * @param:
     * @return
     * @create 2018-07-07 11:50
     **/
    OrderCreateFromCartVO orderCreateNow(OrderSaveNowRequestVO orderSaveNowRequestVO);

    /**
     * 获取商品信息
     * @param shoppingCartRequestVO
     * @return
     */
    ImmediateBuyProductInfoVO getProductInfo(ShoppingCartRequestVO shoppingCartRequestVO);

    /**
     * 根据订单编号查询状态变更记录
     * @param order_id
     * @return
     */
    List<OrderStatusRecord> selectOrderStatusList(Long order_id);

    /**
     * 获取订单列表
     * @param numlist
     * @return
     */
    List<OrderInfo> findOrderInfoList(List<String> numlist);

    /**
     * 根据门店编号获取门店信息
     * @param party_id
     * @return
     */
    StoreInfo getStoreInfo(Long party_id);


    /**
     * 根据订单mun查询订单
     * @param orderNum
     * @return
     */
    OrderInfo getOrderInfoByNum(String orderNum);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据ID 更新订单状态
     * @param:
     * @return
     * @create 2018-07-11 12:09
     **/
    void updateOrderInfoStatus(OrderStatusVO vo);

    /**
     * @author 李道云
     * @methodName: statisTradeUserNum
     * @methodDesc: 统计交易用户数量
     * @description:
     * @param: [party_id, dept_path, start_date, end_date]
     * @return java.util.Map
     * @create 2018-07-17 12:39
     **/
    Map statisTradeUserNum(@Param("party_id") Long store_id, @Param("dept_path") String dept_path,
                           @Param("start_date") String start_date, @Param("end_date") String end_date);

    /**
     * @author 李道云
     * @methodName: findTradeUserList
     * @methodDesc: 查询交易用户列表
     * @description:
     * @param: [store_id, dept_path, start_date, end_date]
     * @return java.util.List<com.hryj.entity.vo.order.UserTradeVO>
     * @create 2018-07-17 12:39
     **/
    List<UserTradeVO> findTradeUserList(@Param("store_id") Long store_id, @Param("dept_path") String dept_path,
                                        @Param("start_date") String start_date, @Param("end_date") String end_date);

    /**
     * @author 李道云
     * @methodName: findNewTradeUserList
     * @methodDesc: 查询新增交易用户列表
     * @description:
     * @param: [store_id, dept_path, start_date, end_date]
     * @return java.util.List<com.hryj.entity.vo.order.UserTradeVO>
     * @create 2018-07-17 12:39
     **/
    List<UserTradeVO> findNewTradeUserList(@Param("store_id") Long store_id, @Param("dept_path") String dept_path,
                                           @Param("start_date") String start_date, @Param("end_date") String end_date);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 查询活动信息
     * @param:
     * @return
     * @create 2018-07-18 9:14
     **/
    OrderActivityInfoVO getActivityMessage(@Param("activity_id") Long activity_id,
                                           @Param("party_id") Long party_id, @Param("product_id") Long product_id);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据用户id查询是否新交易订单查询,返回为数值，大于0标识非新交易
     * @param:
     * @return
     * @create 2018-07-25 20:53
     **/
    Integer countNewTradeFlag(Long userId);

    /**
     * @author 罗秋涵
     * @description: 查询待同步订单列表
     * @param:
     * @return
     * @create 2018-09-17 11:19
     **/
    List<CrossBorderOrderVo> findAwaitSyncOrderList(@Param("pay_status") String pay_status, @Param("order_type")String order_type, @Param("third_order_status")int third_order_status, @Param("adjustment_type_id")String adjustment_type_id);

}
