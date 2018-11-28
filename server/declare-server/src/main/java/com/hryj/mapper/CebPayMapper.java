package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.declare.CebPay;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author 白飞
 * @className: CebPayMapper
 * @description:
 * @create 2018/9/26 11:23
 **/

@Component
public interface CebPayMapper extends BaseMapper<CebPay> {

    /**
     * 根据ID修改状态
     *
     * @param paramMap
     *      参数
     */
    void updateStatus(Map<String, Object> paramMap);
}
