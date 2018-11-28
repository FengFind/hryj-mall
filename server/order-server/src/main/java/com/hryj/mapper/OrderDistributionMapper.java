package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderDistribution;
import com.hryj.entity.bo.staff.user.StaffDeptRelation;
import com.hryj.entity.vo.order.*;
import com.hryj.entity.vo.order.request.DistributionRequestVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderDistributionMapper
 * @description:订单配送信息
 * @create 2018/7/4 0004 17:37
 **/
@Component
public interface OrderDistributionMapper extends BaseMapper<OrderDistribution> {
    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description:  根据条件获取配送/取件 数量
     * @param:
     * @return
     * @create 2018-07-09 18:40
     **/
     int countTheStatus(OrderStatusCountVO orderStatusCountVO);


     /**
      * @author 叶方宇
      * @methodName:
      * @methodDesc:
      * @description: 根据订单状态查询订单列表（门店端）
      * @param:
      * @return
      * @create 2018-07-09 19:48
      **/
     List<OrderDistribution> selectDistributionList(DistributionRequestVO distributionRequestVO);


    /**
     * 查询配送单详情
     * @param order_id
     * @param distribution_type
     * @return
     */
    OrderDistribution getOrderDistributionInfo(@Param("order_id") Long order_id
            , @Param("distribution_type") String distribution_type);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 确认配送完成更新配送单表状态
     * @param:
     * @return
     * @create 2018-07-10 16:15
     **/
    void confirmDistributionDetail(ConfirmDistributionVO confirmDistributionVO);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 确认配送完成更新订单状态
     * @param:
     * @return
     * @create 2018-07-10 17:06
     **/
    void confirmDistributionDetailForOrder(ConfirmDistributionVO confirmDistributionVO);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据orderid查询配送状态
     * @param:
     * @return
     * @create 2018-07-11 10:56
     **/
    DistributionInfoVO selectDistributionInfoByOrderId(Long order_id);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单ID更新配送单状态
     * @param:
     * @return
     * @create 2018-07-12 14:36
     **/
    void updateDistributionStatusByOrderId(DistributionStatusVO vo);

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据id查询订单id
     * @param:
     * @return
     * @create 2018-07-12 19:51
     **/
    Long getOrderIdById(Long id);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description:  通过配送员ID获取配送费
     * @param:
     * @return
     * @create 2018-07-16 15:05
     **/
    StaffDeptRelation getStaffDeptRelationByStaffId(Long staff_id);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据ID 查询配送单信息
     * @param:
     * @return
     * @create 2018-07-21 16:27
     **/
    DistributionInfoVO getOrderDistributionById(Long id);

    /**
     * @author 罗秋涵
     * @description: 根据门店编号获取门店待分配送货取货数量
     * @param:party_id
     * @return
     * @create 2018-08-16 11:08
     **/
    Integer counttWaitDistributionNum(Long party_id);

}
