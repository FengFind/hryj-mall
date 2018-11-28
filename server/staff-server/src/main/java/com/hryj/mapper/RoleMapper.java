package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.staff.role.RolePermRelation;
import com.hryj.entity.bo.staff.role.StaffRole;
import com.hryj.entity.vo.staff.role.request.RoleIdRequestVO;
import com.hryj.entity.vo.staff.role.request.RoleListParamRequestVO;
import com.hryj.entity.vo.staff.role.request.RoleNameRequestVO;
import com.hryj.entity.vo.staff.role.response.ResourceTreeResponseVO;
import com.hryj.entity.vo.staff.role.response.RoleListParamResponseVO;
import com.hryj.entity.vo.staff.role.response.RoleNameListResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 员工角色表 Mapper 接口
 * </p>
 *
 * @author daitingbo
 * @since 2018-06-23
 */
@Component
public interface RoleMapper extends BaseMapper<StaffRole> {

    /**
     * @author 代廷波
     * @description: 获取角色列表
     * @param: null
     * @return
     * @create 2018/06/23 17:56
     **/
    List<RoleListParamResponseVO> getRoleList(RoleListParamRequestVO staffRoleDto, Page page);
    /**
     * @author 代廷波
     * @description: 获取资源树
     * @param: null
     * @return
     * @create 2018/07/04 11:58
     **/
    List<ResourceTreeResponseVO> getResourceTree();

    /**
     * @author 代廷波
     * @description: 根据角色查找对应的资源
     * @param: role_id 角色id
     * @return
     * @create 2018/07/04 15:04
     **/
    List<ResourceTreeResponseVO> getRoleIdByResource(RoleIdRequestVO vo);
    /**
     * @author 代廷波
     * @description: 角色名列表
     * @param: role_name 角色名
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.role.response.RoleNameListResponseVO>
     * @create 2018/06/28 20:57
     **/
    List<RoleNameListResponseVO> getRoleNameList(RoleNameRequestVO vo);

    /**
     * @author 代廷波
     * @description: 角色资源
     * @param: null
     * @return
     * @create 2018/09/26 13:32
     **/
    List<RolePermRelation> getRoleNamePermRelationList(@Param("role_name") String role_name);
}
