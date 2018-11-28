package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebShopEnterprise;
import com.hryj.mapper.CebShopEnterpriseMapper;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebShopEnterpriseService
 * @description:
 * @create 2018/9/26 14:23
 **/
@Service
public class CebShopEnterpriseService extends ServiceImpl<CebShopEnterpriseMapper, CebShopEnterprise> {

    public CebShopEnterprise findByEbcCode(String ebcCode){
        if(null == ebcCode){
            return null;
        }
        EntityWrapper<CebShopEnterprise> enterpriseEntityWrapper = new EntityWrapper<>();
        enterpriseEntityWrapper.eq("ebc_code", ebcCode);
        return super.selectOne(enterpriseEntityWrapper);
    }
}
