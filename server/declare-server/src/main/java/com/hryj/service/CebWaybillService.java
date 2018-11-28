package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebWaybill;
import com.hryj.mapper.CebWaybillMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebWaybillService
 * @description:
 * @create 2018/9/26 11:32
 **/
@Service
public class CebWaybillService extends ServiceImpl<CebWaybillMapper, CebWaybill> {

    /**
     * 根据订单编号查询运单
     *
     * @param orderNo
     *           运单编号
     * @return 运单对象
     */
    public CebWaybill findByOrderNo(String orderNo, String ebcCode){
        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<CebWaybill> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        entityWrapper.eq("ebc_code", ebcCode);
        return super.selectOne(entityWrapper);
    }


    /**
     * 根据物流备案编码和运单号查询
     *
     * @param logisticsNo
     *          运单号
     * @param logisticsCode
     *          物流备案编号
     * @return CebWaybill
     */
    public CebWaybill findCebWaybill(String logisticsNo, String logisticsCode){
        if(StringUtils.isEmpty(logisticsNo) && StringUtils.isEmpty(logisticsCode)){
            return null;
        }
        EntityWrapper<CebWaybill> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("logistics_no",logisticsNo);
        entityWrapper.eq("logistics_code",logisticsCode);
        return super.selectOne(entityWrapper);
    }
}
