package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderStatusRecord;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderStatusRecordMapper
 * @description: 订单状态记录表
 * @create 2018/7/4 0004 17:47
 **/
@Component
public interface OrderStatusRecordMapper extends BaseMapper<OrderStatusRecord> {

    List<OrderStatusRecord> getOrderStatusRecordVOList(Long order_id);

}
