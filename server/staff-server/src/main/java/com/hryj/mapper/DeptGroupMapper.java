package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.profit.StaffDeptChangeRecord;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.staff.dept.request.*;
import com.hryj.entity.vo.staff.dept.response.*;
import com.hryj.entity.vo.staff.team.response.AppTeamDataDeptResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 部门组织表 Mapper 接口
 *
 * @author daitingbo
 * @since 2018-06-27
 */
@Component
public interface DeptGroupMapper extends BaseMapper<DeptGroup> {

    /**
     * @return
     * @author 代廷波
     * @methodName: 查询部门组织树
     * @methodDesc:
     * @description:
     * @param:
     * @create 2018-07-05 17:31
     **/
    List<DeptGroupTreeResponseVO> getDeptTree(DeptTreeRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 根据当前部门pid_id查询所有父级
     * @param: dept_id 部门id
     * @create 2018/06/29 9:20
     **/
    List<DeptParentListResponseVO> getDeptParentList(@Param("ids") String[] ids);

    /**
     * @return com.hryj.common.Result
     * @author 代廷波
     * @description: 根据部门id查询员工列表
     * @param: dept_id 部门id
     * @create 2018/06/27 19:24
     **/
    List<DeptStaffListResponseVO> getStaffListByDeptId(DeptIdRequestVO deptIdRequestVO);

    /**
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author 李道云
     * @methodName: findChildPartyIdList
     * @methodDesc: 查询底下门店或者仓库的id集合
     * @description:
     * @param: [dept_type, dept_path]
     * @create 2018-07-05 17:39
     **/
    Map<String, Object> findChildPartyIdList(@Param("dept_type") String dept_type, @Param("dept_path") String dept_path);

    /**
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author 李道云
     * @methodName: findAllPartyIdList
     * @methodDesc: 查询所有的门店或者仓库的id集合
     * @description:
     * @param: [dept_type]
     * @create 2018-07-06 11:41
     **/
    Map<String, Object> findAllPartyIdList(@Param("dept_type") String dept_type);

    /**
     * @return
     * @author 代廷波
     * @description: 根据部门id查询向上的所有部门列表
     * @param: null
     * @create 2018/07/07 17:23
     **/
    List<DeptGroupListResponseVO> getDeptIdByUpDeptList(@Param("ids") String[] ids);

    /**
     * @return
     * @author 代廷波
     * @description: 根据组织id获取门店基本信息与配送范围VO
     * @param: null
     * @create 2018/07/09 19:56
     **/
    DeptDetAndDistributionAreaResponseVO getDeptGroupStoreArea(DeptIdRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 根据组织id获取仓库基本信息与配送范围VO
     * @param: null
     * @create 2018/07/09 19:56
     **/
    DeptDetAndDistributionAreaResponseVO getDeptGroupWHArea(DeptIdRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 组织列表
     * @param: dept_path 路径
     * @create 2018/07/10 16:09
     **/
    List<AppTeamDataDeptResponseVO> getAppTeamDataDept(@Param("dept_path") String dept_path);

    /**
     * @return
     * @author 代廷波
     * @description: app-根据部门id集合查询详情
     * @param: null
     * @create 2018/07/10 21:09
     **/
    List<DeptGroupResponseVO> getAppDeptGroupBasicInfo(DeptGroupBasicInfoRequestVO basicInfoRequestVO);

    /**
     * @return
     * @author 代廷波
     * @description: 根据当前登陆人查询所有门店或者仓库列表
     * @param: null
     * @create 2018/07/14 15:11
     **/
    List<DeptStoreOrWarehouseResponseVO> getDeptStoreOrWarehouseList(DeptStoreOrWarehouseRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 获取员工变更后的记录
     * @param: null
     * @create 2018/07/20 10:03
     **/

    StaffDeptChangeRecord getAfterStaffDeptChangeRecord(@Param("staff_id") Long staff_id, @Param("after_dept_id") Long after_dept_id);

    /**
     * @return
     * @author 代廷波
     * @description: 根据部门id查询下面所有组织
     * @param: null
     * @create 2018/07/23 15:01
     **/
    List<DeptGroupTreeResponseVO> getDeptIdByDownDeptTree(@Param("dept_id") Long dept_id);
    /**
     * @author 代廷波
     * @description: 查询分流员工列表 内置员工不能设置分润
     * @param: null
     * @return
     * @create 2018/07/31 16:20
     **/
    List<DeptStaffListResponseVO> getDeptIdByShareList(DeptShareRequestVO deptShareRequestVO);

    List<DeptIdByStoreOrWarehouseResponseVO> getAppDeptIdsByStoreOrWarehouseDet(DeptIdsRequestVO vo);
    /**
     * @author 代廷波
     * @description: 根据当前部门id查询下一级部门
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @create 2018/09/28 10:52
     **/
    List<DeptGroupPidResponseVO> getDeptPidList(DeptIdRequestVO vo);

    List<DeptGroupTreeResponseVO> getDeptIdsList(@Param("ids") Set<Long> ids);
}
