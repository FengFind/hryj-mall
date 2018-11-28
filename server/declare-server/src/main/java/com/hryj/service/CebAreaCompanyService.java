package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebAreaCompany;
import com.hryj.mapper.CebAreaCompanyMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebAreaCompanyService
 * @description:
 * @create 2018/9/26 14:22
 **/
@Service
public class CebAreaCompanyService extends ServiceImpl<CebAreaCompanyMapper, CebAreaCompany> {


    @Autowired
    private  CebAreaCompanyMapper cebAreaCompanyMapper;

    /**
     * 根据内部编号查询
     *
     * @param sysAreaCode
     *          系统编号
     * @return
     */
    public CebAreaCompany findBySysAreaCode(String sysAreaCode){
        if(StringUtils.isEmpty(sysAreaCode)){
            return null;
        }
        EntityWrapper<CebAreaCompany> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("sys_code", sysAreaCode);
        return super.selectOne(entityWrapper);
    }


    /**
     * 根据账册编号编号查询
     *
     * @param emsNo
     *          账册编号编号
     * @return
     */
    public CebAreaCompany findByEmsNo(String emsNo){
        if(StringUtils.isEmpty(emsNo)){
            return null;
        }
        EntityWrapper<CebAreaCompany> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("ems_no", emsNo);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据系统编号和账册编号编号查询
     *
     * @param sysAreaCode
     *          系统编号
     * @param emsNo
     *          账册编号编号
     * @return
     */
    public CebAreaCompany findCebAreaCompany(String sysAreaCode, String emsNo){
        if(StringUtils.isEmpty(sysAreaCode) && StringUtils.isEmpty(emsNo)){
            return null;
        }

        EntityWrapper<CebAreaCompany> entityWrapper = new EntityWrapper<>();
        if(StringUtils.isNotEmpty(sysAreaCode)){
            entityWrapper.eq("sys_code", sysAreaCode);
            return super.selectOne(entityWrapper);
        }
        entityWrapper.eq("ems_no", emsNo);
        return super.selectOne(entityWrapper);
    }
}
