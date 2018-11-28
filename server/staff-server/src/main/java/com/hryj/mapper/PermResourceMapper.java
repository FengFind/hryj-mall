package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.staff.role.PermResource;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author 李道云
 * @className: PermResourceMapper
 * @description: 权限资源mapper
 * @create 2018/6/26 8:44
 **/
@Component
public interface PermResourceMapper extends BaseMapper<PermResource> {

    /**
     * @author 李道云
     * @methodName: findAllPermResource
     * @methodDesc: 查询所有权限资源
     * @description:
     * @param: []
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-05 18:26
     **/
    Map<String, Object> findAllPermResource();

    /**
     * @author 王光银
     * @methodName: findAll
     * @methodDesc:加载所有的权限资源
     * @description:
     * @param: 
     * @return 
     * @create 2018-08-04 15:09
     **/
    List<PermResource> findAll();

    /**
     * @author 王光银
     * @methodName: findRolePermissions
     * @methodDesc: 加载角色的权限资源
     * @description:
     * @param:
     * @return
     * @create 2018-08-04 15:13
     **/
    List<PermResource> findRolePermissions(@Param("role_id") Long role_id);
}
