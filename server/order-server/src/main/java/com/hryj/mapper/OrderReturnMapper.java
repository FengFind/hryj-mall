package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderReturn;
import com.hryj.entity.vo.order.OrderReturnProductVO;
import com.hryj.entity.vo.order.request.DistributionOrderIdVO;
import com.hryj.entity.vo.order.response.AdminOrderReturnResponseVO;
import com.hryj.entity.vo.order.response.ReturnOrderDetailsResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderReturnMapper
 * @description:订单退货信息
 * @create 2018/7/4 0004 17:45
 **/
@Component
public interface OrderReturnMapper  extends BaseMapper<OrderReturn> {
    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description:  根据订单ID查询退货单
     * @param:
     * @return
     * @create 2018-07-09 11:42
     **/
    List<AdminOrderReturnResponseVO> getAdminOrderDetailResponseVO(Long orderId);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc: 统计退货处理
     * @description:
     * @param:
     * @return
     * @create 2018-07-09 19:28
     **/
    int countReturnToDo(Long distribution_staff_id);

    /**
     * 获取退货信息
     * @param order_id
     * @return
     */
    OrderReturn getOrderReturnInfo(@Param("order_id") Long order_id);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单id 更新退货单状态
     * @param:
     * @return
     * @create 2018-07-11 12:00
     **/
    void updateReturnStatus(DistributionOrderIdVO vo);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description:  根据用户获取配送金额
     * @param:
     * @return
     * @create 2018-07-12 13:40
     **/
    Double getOrderInfoByOrderId(@Param("distribution_staff_id") Long distribution_staff_id);

    /**
     * 根据订单编号获取退货单详情
     * @param order_id
     * @return
     */
    ReturnOrderDetailsResponseVO getReturnDetails(Long order_id);

    /**
     * 查询门店退货单数量
     * @param return_status
     * @param dept_id
     * @return
     */
    Integer countReturnFroWarehouse(@Param("return_status") String return_status,@Param("dept_id") Long dept_id);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 获取退货商品信息
     * @param:
     * @return
     * @create 2018-08-02 9:59
     **/
    List<OrderReturnProductVO> getReturnOrderProductMessage(@Param("order_id") Long order_id,@Param("order_product_id") Long order_product_id);
}
