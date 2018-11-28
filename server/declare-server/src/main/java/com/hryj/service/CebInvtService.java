package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebInvt;
import com.hryj.mapper.CebInvtMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebInvtService
 * @description:
 * @create 2018/9/26 11:13
 **/
@Service
public class CebInvtService extends ServiceImpl<CebInvtMapper, CebInvt> {

    /**
     * 根据内部编号查询清单
     *
     * @param copNo
     *           内部编号
     * @param ebcCode
     *           电商备案编号
     * @return 清单对象
     */
    public CebInvt findByCopNo(String copNo, String ebcCode){
        if(StringUtils.isEmpty(copNo)){
            return null;
        }
        EntityWrapper<CebInvt> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("cop_no", copNo);
        if(StringUtils.isNotEmpty(ebcCode)){
            entityWrapper.eq("ebc_code", ebcCode);
        }
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据订单编号查询清单-主要用于本地回执-本地回执没有携带电商编号
     *
     * @param orderNo
     *           订单编号
     * @return 清单对象
     */
    public CebInvt findByOrderNo(String orderNo, String ebcCode){
        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<CebInvt> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        if(StringUtils.isNotEmpty(ebcCode)){
            entityWrapper.eq("ebc_code", ebcCode);
        }
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据订单编号和电商备案编码查询清单
     *
     * @param orderNo
     *           订单编号
     * @return 清单对象
     */
    public CebInvt findInvt(String orderNo, String ebcCode){
        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<CebInvt> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        entityWrapper.eq("ebc_code", ebcCode);
        return super.selectOne(entityWrapper);
    }
}
