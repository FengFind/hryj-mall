package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.entity.bo.order.OrderCrossBorder;
import com.hryj.entity.vo.order.OderForGCRequestVO;
import com.hryj.mapper.OrderForGCMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 叶方宇
 * @className: OrderForGCService
 * @description:
 * @create 2018/9/14 0014 14:09
 **/
@Service
public class OrderForGCService {
    @Autowired
    OrderForGCMapper orderForGCMapper;

    public OrderCrossBorder findCrossBorderOrder(Long order_id, String third_order_code){
        return orderForGCMapper.findCrossBorderOrder(order_id,third_order_code);
    }

    public void updateCrossBorderOrder(OrderCrossBorder crossBorderOrder, EntityWrapper wrapper){
        orderForGCMapper.update(crossBorderOrder,wrapper);
    }

    public List<OderForGCRequestVO> findCrossBorderOrderList(){
        return orderForGCMapper.findCrossBorderOrderList();
    }

}
