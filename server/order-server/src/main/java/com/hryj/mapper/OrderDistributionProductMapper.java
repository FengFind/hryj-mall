package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.DistributionProduct;
import com.hryj.entity.vo.order.OrderInfoToDistributionVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderDistributionMapper
 * @description:订单配送信息
 * @create 2018/7/4 0004 17:37
 **/
@Component
public interface OrderDistributionProductMapper extends BaseMapper<DistributionProduct> {

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 获得订单信息和订单商品信息
     * @param:
     * @return
     * @create 2018-07-09 10:29
     **/
    List<OrderInfoToDistributionVO> getOrderProductMessage(Long orderId);

}
