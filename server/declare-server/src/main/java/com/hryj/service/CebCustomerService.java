package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebCustomer;
import com.hryj.mapper.CebCustomerMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebCustomerService
 * @description:
 * @create 2018/9/26 14:19
 **/
@Service
public class CebCustomerService extends ServiceImpl<CebCustomerMapper, CebCustomer> {

    @Autowired
    private CebCustomerMapper cebCustomerMapper;

    /**
     * 根据关区代码和分拣线查询
     *
     * @param customerCode
     *          海关编码
     * @param sortlineId
     *          分拣线
     * @return CebCustomer
     */
    public CebCustomer findByCustomerCode(String customerCode, String sortlineId){
        if(StringUtils.isEmpty(customerCode)){
            return null;
        }
        EntityWrapper<CebCustomer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("customer_code", customerCode);
        if(StringUtils.isNotEmpty(sortlineId)){
            entityWrapper.eq("sortline_id", sortlineId);
        }
        return super.selectOne(entityWrapper);
    }
}
