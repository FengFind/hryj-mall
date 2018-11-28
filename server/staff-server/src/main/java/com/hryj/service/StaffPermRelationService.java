package com.hryj.service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.staff.role.RolePermRelation;
import com.hryj.entity.bo.staff.role.StaffPermRelation;
import com.hryj.mapper.RoleMapper;
import com.hryj.mapper.StaffPermRelationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 代廷波
 * @className: StaffPermRelationService
 * @description:
 * @create 2018/6/26 0026-11:50
 **/
@Service
public class StaffPermRelationService extends ServiceImpl<StaffPermRelationMapper, StaffPermRelation> {
    @Autowired
    private RoleMapper roleMapper;
    public List<RolePermRelation> getRoleNamePermRelationList(String role_name) {

        return roleMapper.getRoleNamePermRelationList(role_name);
    }
}
