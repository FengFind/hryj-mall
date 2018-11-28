package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebLogistics;
import com.hryj.mapper.CebLogisticsMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebLogisticsService
 * @description:
 * @create 2018/9/26 15:17
 **/
@Service
public class CebLogisticsService extends ServiceImpl<CebLogisticsMapper, CebLogistics> {


    @Autowired
    private  CebLogisticsMapper cebLogisticsMapper;

    /**
     * 根据编号查询
     *
     * @param logisticsNo
     *          编号
     * @param ebcCode
     *          电商企业备案编码
     * @return CebLogistics
     */
    public  CebLogistics findByLogisticsNo(String logisticsNo, String ebcCode){
        if(StringUtils.isEmpty(logisticsNo)){
            return null;
        }

        EntityWrapper<CebLogistics> cebLogisticsEntityWrapper = new EntityWrapper<>();
        cebLogisticsEntityWrapper.eq("logistics_no", logisticsNo);
        if(StringUtils.isNotEmpty(ebcCode)){
            cebLogisticsEntityWrapper.eq("ebc_code", ebcCode);
        }
        return super.selectOne(cebLogisticsEntityWrapper);
    }


}
