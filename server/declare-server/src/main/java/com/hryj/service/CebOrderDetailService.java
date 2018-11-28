package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebOrderDetail;
import com.hryj.mapper.CebOrderDetailMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 白飞
 * @className: CebOrderDetailService
 * @description:
 * @create 2018/9/26 10:33
 **/
@Service
public class CebOrderDetailService extends ServiceImpl<CebOrderDetailMapper, CebOrderDetail> {

    /**
     * 根据订单ID查询订单明细
     *
     * @param orderId
     *           订单ID
     * @return 订单明细集合
     */
    public List<CebOrderDetail> findListByOrderId(Long orderId){
        if(null == orderId){
            return null;
        }
        EntityWrapper<CebOrderDetail> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_id", orderId);
        return super.selectList(entityWrapper);
    }
}
