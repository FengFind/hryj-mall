package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.staff.dept.DeptShareConfig;
import com.hryj.mapper.DeptShareConfigMapper;
import org.springframework.stereotype.Service;

/**
 * 组织节点分润配置(末级节点组织不参与分润)
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Service
public class DeptShareConfigService extends ServiceImpl<DeptShareConfigMapper, DeptShareConfig> {

}