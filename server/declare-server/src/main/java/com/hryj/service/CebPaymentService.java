package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebPayment;
import com.hryj.mapper.CebPaymentMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebPaymentService
 * @description:
 * @create 2018/9/26 15:18
 **/
@Service
public class CebPaymentService extends ServiceImpl<CebPaymentMapper, CebPayment> {

    @Autowired
    private  CebPaymentMapper cebPaymentMapper;

    /**
     * 根据支付编号查询
     *
     * @param payNo
     *          支付编号
     * @param ebcCode
     *          电商企业备案代码
     * @return CebPayment
     */
    public CebPayment findByPayNo(String payNo, String ebcCode){
        if(StringUtils.isEmpty(payNo)){
            return  null;
        }
        EntityWrapper<CebPayment> cebPaymentEntityWrapper = new EntityWrapper<>();
        cebPaymentEntityWrapper.eq("pay_no", payNo);
        if(StringUtils.isNotEmpty(ebcCode)){
            cebPaymentEntityWrapper.eq("ebc_code", ebcCode);
        }
        return super.selectOne(cebPaymentEntityWrapper);
    }
}
