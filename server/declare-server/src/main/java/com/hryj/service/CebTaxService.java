package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebTax;
import com.hryj.mapper.CebTaxMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebTax
 * @description: 扣税
 * @create 2018/10/11 15:26
 **/
@Service
public class CebTaxService extends ServiceImpl<CebTaxMapper, CebTax> {

    /**
     * 根据清单编号和电商编号查询
     *
     * @param invtNo
     *          清单编号
     * @param ebcCode
     *          电商企业编号
     * @return 对象
     */
    public CebTax findByInvtNo(String invtNo, String ebcCode){
        if(StringUtils.isEmpty(invtNo) || StringUtils.isEmpty(ebcCode)){
            return null;
        }
        EntityWrapper<CebTax> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("invt_no", invtNo);
        entityWrapper.eq("ebc_code", ebcCode);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据清单编号和电商编号查询是否存在
     *
     * @param invtNo
     *          清单编号
     * @param ebcCode
     *          电商企业编号
     * @return 对象
     */
    public boolean isExistCebTax(String invtNo, String ebcCode){
        if(StringUtils.isEmpty(invtNo) || StringUtils.isEmpty(ebcCode)){
            return false;
        }
        EntityWrapper<CebTax> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("invt_no", invtNo);
        entityWrapper.eq("ebc_code", ebcCode);
        CebTax cebTax = super.selectOne(entityWrapper);
        return cebTax != null;
    }

}
