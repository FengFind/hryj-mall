package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebOrder;
import com.hryj.mapper.CebOrderMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebOrderService
 * @description:
 * @create 2018/9/26 10:32
 **/
@Service
public class CebOrderService extends ServiceImpl<CebOrderMapper, CebOrder> {

    /**
     * 根据订单编号查询清单
     *
     * @param orderNo
     *           订单编号
     * @return 清单对象
     */
    public CebOrder findByOrderNo(String orderNo){
        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<CebOrder> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据订单编号查询订单
     *
     * @param orderNo
     *           订单编号
     * @param ebcCode
     *           电商备案编号
     * @return 订单对象
     */
    public CebOrder findCebOrder(String orderNo, String ebcCode){
        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<CebOrder> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        entityWrapper.eq("ebc_code", ebcCode);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据订单编号查询订单是否存在
     *
     * @param orderNo
     *           订单编号
     * @param ebcCode
     *           电商备案编号
     * @return 清单对象
     */
    public boolean isExistCebOrder(String orderNo, String ebcCode){
        if(StringUtils.isEmpty(orderNo)){
            return true;
        }
        EntityWrapper<CebOrder> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        entityWrapper.eq("ebc_code", ebcCode);
        CebOrder cebOrder = super.selectOne(entityWrapper);
        return cebOrder != null && StringUtils.isNotEmpty(cebOrder.getOrderNo());
    }

}
