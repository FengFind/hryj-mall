package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.sys.PaymentConfig;
import com.hryj.mapper.PaymentConfigMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 李道云
 * @className: PaymentConfigService
 * @description: 支付配置service
 * @create 2018/7/6 16:23
 **/
@Service
public class PaymentConfigService extends ServiceImpl<PaymentConfigMapper,PaymentConfig> {

    /**
     * @author 李道云
     * @methodName: findPaymentConfigList
     * @methodDesc: 查询有效的支付配置信息
     * @description:
     * @param: []
     * @return java.util.List<com.hryj.entity.bo.sys.PaymentConfig>
     * @create 2018-07-06 18:01
     **/
    public List<PaymentConfig> findPaymentConfigList(){
        EntityWrapper<PaymentConfig> wrapper = new EntityWrapper<>();
        wrapper.eq("config_status","1");
        return super.selectList(wrapper);
    }
}
