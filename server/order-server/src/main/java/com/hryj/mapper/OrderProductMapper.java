package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderProduct;
import com.hryj.entity.vo.order.OrderPorductVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderProductMapper
 * @description:订单产品信息
 * @create 2018/7/4 0004 17:44
 **/
@Component
    public interface OrderProductMapper  extends BaseMapper<OrderProduct> {

    List<OrderPorductVO> getOrderPorductVOList(Long order_id);


    void insertBatch(@Param("orderProducts") List<OrderProduct> orderProducts);

}
