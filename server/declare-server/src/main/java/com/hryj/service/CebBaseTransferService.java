package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebBaseTransfer;
import com.hryj.mapper.CebBaseTransferMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 白飞
 * @className: CebBaseTransferService
 * @description:
 * @create 2018/9/27 9:22
 **/
@Service
public class CebBaseTransferService extends ServiceImpl<CebBaseTransferMapper, CebBaseTransfer> {

    @Autowired
    private CebBaseTransferMapper cebBaseTransferMapper;

    /**
     * 根据企业编码查询
     *
     * @param copCode
     *          企业备案code
     * @return 对象
     */
    public CebBaseTransfer findByCopCode(String copCode){
        if(StringUtils.isEmpty(copCode)){
            return null;
        }
        EntityWrapper<CebBaseTransfer> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("cop_code", copCode);
        return super.selectOne(entityWrapper);
    }

}
