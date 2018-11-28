package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderExpress;
import com.hryj.entity.vo.order.OrderLogisticsVO;
import org.springframework.stereotype.Component;

/**
 * @author 叶方宇
 * @className: OrderExpressMapper
 * @description:订单快递信息表
 * @create 2018/7/4 0004 17:41
 **/
@Component
public interface OrderExpressMapper  extends BaseMapper<OrderExpress> {

    OrderLogisticsVO getOrderExpressMessage(Long order_id);
}
