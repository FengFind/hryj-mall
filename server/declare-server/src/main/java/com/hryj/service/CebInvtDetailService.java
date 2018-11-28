package com.hryj.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.CebInvtDetail;
import com.hryj.mapper.CebInvtDetailMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 白飞
 * @className: CebInvtDetailService
 * @description:
 * @create 2018/9/26 11:14
 **/
@Service
public class CebInvtDetailService extends ServiceImpl<CebInvtDetailMapper, CebInvtDetail> {

    /**
     * 根据清单ID查询订单明细
     *
     * @param invtId
     *           清单ID
     * @return 清单明细集合
     */
    public List<CebInvtDetail> findListByInvtId(Long invtId){
        if(null == invtId){
            return null;
        }
        EntityWrapper<CebInvtDetail> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("invt_id", invtId);
        return super.selectList(entityWrapper);
    }
}
