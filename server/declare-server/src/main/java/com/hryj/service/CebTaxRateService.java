package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.hryj.entity.bo.declare.CebTaxRate;
import com.hryj.mapper.CebTaxRateMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 白飞
 * @className: CebTaxRateService
 * @description:
 * @create 2018/10/11 8:53
 **/
@Service
public class CebTaxRateService extends ServiceImpl<CebTaxRateMapper, CebTaxRate> {

    /**
     * 根据HSCODE查询
     *
     * @param hsCode
     *          HSCODE
     * @return
     */
    public CebTaxRate findByHsCode(String hsCode){
        if(StringUtils.isEmpty(hsCode)){
            return null;
        }
        EntityWrapper<CebTaxRate> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("hs_code",hsCode);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据HSCODE查询
     *
     * @param hsCodes
     *          多HSCODE
     * @return
     */
    public List<CebTaxRate> findByHsCode(List<String> hsCodes){
        if(null == hsCodes || hsCodes.size() == 0){
            return null;
        }
        EntityWrapper<CebTaxRate> entityWrapper = new EntityWrapper<>();
        entityWrapper.in("hs_code",hsCodes);
        return super.selectList(entityWrapper);
    }

    /**
     * 根据HSCODE查询
     *
     * @param hsCodes
     *          多HSCODE
     * @return hscode分组Map
     */
    public Map<String, CebTaxRate> findByHsCodeToMap(List<String> hsCodes){
        if(null == hsCodes || hsCodes.size() == 0){
            return null;
        }
        List<CebTaxRate> cebTaxRates = findByHsCode(hsCodes);
        if(null == cebTaxRates){
            return null;
        }
        Map<String, CebTaxRate> cebTaxRateMap = Maps.newHashMap();
        for(CebTaxRate cebTaxRate : cebTaxRates){
            cebTaxRateMap.put(cebTaxRate.getHsCode(), cebTaxRate);
        }
        return cebTaxRateMap;
    }

}
