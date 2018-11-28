package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hryj.entity.bo.declare.CebPay;
import com.hryj.mapper.CebPayMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author 白飞
 * @className: CebPayService
 * @description:
 * @create 2018/9/26 11:24
 **/
@Service
public class CebPayService extends ServiceImpl<CebPayMapper, CebPay> {

    @Autowired
    private CebPayMapper cebPayMapper;

    /**
     * 根据订单编号查询支付单
     *
     * @param orderNo
     *           订单编号
     * @param ebpCode
     *           平台编号
     * @return 运单对象
     */
    public CebPay findCebPay(String orderNo, String ebpCode){
        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<CebPay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no",orderNo);
        if(StringUtils.isNotEmpty(ebpCode)){
            entityWrapper.eq("ebp_code",ebpCode);
        }
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据订单编号查询支付单
     *
     * @param orderNo
     *           订单编号
     * @param payCode
     *           支付编号
     * @return 运单对象
     */
    public CebPay findByOrderNo(String orderNo, String payCode){
        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<CebPay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no",orderNo);
        entityWrapper.eq("pay_code",payCode);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据订单编号查询支付单
     *
     * @param payTransactionId
     *           流水号
     * @return 运单对象
     */
    public CebPay findByPayTransactionId(String payTransactionId){
        if(StringUtils.isEmpty(payTransactionId)){
            return null;
        }
        EntityWrapper<CebPay> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("pay_transaction_id",payTransactionId);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据ID修改状态和支付申报号
     * @param id
     *      ID
     * @param payStatus
     *          状态
     * @param payDeclareNo
     *          支付申报号
     */
    public void updateStatus(Long id, Integer payStatus, String payDeclareNo){
        if(null == id || payStatus == null){
            return;
        }
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("id", id);
        paramMap.put("payStatus", payStatus);
        paramMap.put("payDeclareNo", payDeclareNo);
        cebPayMapper.updateStatus(paramMap);
    }

}
