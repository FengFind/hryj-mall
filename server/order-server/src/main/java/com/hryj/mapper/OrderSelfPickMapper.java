package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.order.OrderSelfPick;
import com.hryj.entity.vo.order.OrderSelfPickUserVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderSelfPickMapper
 * @description: 订单自提信息表
 * @create 2018/7/4 0004 17:45
 **/
@Component
public interface OrderSelfPickMapper  extends BaseMapper<OrderSelfPick> {

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 返回自提信息表需要的客户信息和门店信息
     * @param:
     * @return
     * @create 2018-07-11 20:26
     **/
    List<OrderSelfPickUserVO> findAllUserMessage(List<Long> orderIdList);
}
