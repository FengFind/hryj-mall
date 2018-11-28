package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderCrossBorder;
import com.hryj.entity.vo.order.OderForGCRequestVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderForGCMapper
 * @description:
 * @create 2018/9/14 0014 14:13
 **/
@Component
public interface OrderForGCMapper  extends BaseMapper<OrderCrossBorder> {
    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单ID和光彩订单ID查询报税订单信息
     * @param:
     * @return
     * @create 2018-09-14 15:54
     **/
    OrderCrossBorder findCrossBorderOrder(@Param("order_id") Long order_id
            , @Param("third_order_code") String third_order_code);


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 轮询查询待发货订单
     * @param:
     * @return
     * @create 2018-09-17 14:40
     **/
    List<OderForGCRequestVO> findCrossBorderOrderList();
}
