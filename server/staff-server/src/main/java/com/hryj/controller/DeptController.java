package com.hryj.controller;

import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.*;
import com.hryj.entity.vo.staff.dept.response.*;
import com.hryj.exception.GlobalException;
import com.hryj.service.DeptService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 代廷波
 * @className: Deptcontroller
 * @description: 部门管理
 * @create 2018/6/27 0027-17:08
 **/
@RestController
@RequestMapping("/dept")
public class DeptController {

    @Autowired
    private DeptService deptService;


    /**
     * @author 代廷波
     * @description: 获取部门树型结构
     * @param: null
     * @return
     * @create 2018/06/27 17:11
     **/
    @PostMapping("/getDeptTree")
    public Result<ListResponseVO<DeptGroupTreeResponseVO>> getDeptTree(@RequestBody DeptTreeRequestVO vo) throws GlobalException {

        return deptService.getDeptTree(vo);

    }

    /**
     * @author 代廷波
     * @description: 根据当前部门id查询下一级部门
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @create 2018/09/28 10:52
     **/
    @PostMapping("/getDeptPidList")
    public Result<ListResponseVO<DeptGroupPidResponseVO>> getDeptPidList(@RequestBody DeptIdRequestVO vo) throws GlobalException {
        return deptService.getDeptPidList(vo);
    }


    /**
     * @author 代廷波
     * @description: 根据当前根节点的dept_pid查询所有父节点
     * @param: 当前节点的dept_pid
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>
     * @create 2018/06/27 17:11
     **/
    //@PostMapping("/getDeptIdUpDeptTree")
    //public Result<DeptParentListResponseVO> getDeptIdUpDeptTree(@RequestBody  DeptPidRequestVO vo) throws GlobalException {
//
     //   return deptService.getDeptIdUpDeptTree(vo);

   // }

    /**
     * @author 代廷波
     * @description: 添加部门
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/27 18:44
     **/

    @PostMapping("/saveDept")
    public Result saveDept(@RequestBody DeptRequestVO vo) throws GlobalException {

        return deptService.saveDept(vo);

    }


    /**
     * @author 代廷波
     * @description: 修改部门名称
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/27 18:44
     **/

    @PostMapping("/updateDeptName")
    @ApiOperation(value="更新部门名称")
    public Result updateDeptName(@RequestBody DeptUpdataNameRequestVO vo) throws GlobalException {

        return deptService.updateDeptName(vo);

    }
    /**
     * @author 代廷波
     * @description: 根据部门id查询员工列表
     * @param: dept_id 部门id
     * @return com.hryj.common.Result
     * @create 2018/06/27 19:24
     **/
    @PostMapping("/getDeptIdByStaffList")
    public Result<ListResponseVO<DeptStaffListResponseVO>> getDeptIdByStaffList(@RequestBody DeptIdRequestVO vo) throws GlobalException {


        return deptService.getDeptIdByStaffList(vo);

    }
    /**
     * @author 代廷波
     * @description:  保存部门员工列表
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/28 18:54
     **/
    @PostMapping("/saveDeptStaff")
    public Result saveDeptStaff(@RequestBody DeptStaffRequestVO vo) throws GlobalException {


        return deptService.saveDeptStaff(vo);

    }

    /**
     * @author 代廷波
     * @description: 根据组织或者门店和仓库 id查询分润列表
     * @param: dept_id 部门id
     * @return com.hryj.common.Result
     * @create 2018/06/27 19:24
     **/
    @PostMapping("/getDeptIdByShareList")
    public Result<DeptStaffShareResPonseVO> getDeptIdByShareList(@RequestBody DeptShareRequestVO vo) throws GlobalException {


        return deptService.getDeptIdByShareList(vo);

    }
    /**
     * @author 代廷波
     * @description:  部门添加分润
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptStaffShareListResPonseVO>
     * @create 2018/06/28 18:51
     **/
   /* @PostMapping("/saveDeptShare")
    public Result saveDeptShare(@RequestBody DeptShareConfigReqestVO vo) throws GlobalException {

        return deptService.saveDeptShare(vo);

    }*/

    /**
     * @author 李道云
     * @methodName: getDeptGroupByDeptId
     * @methodDesc: 根据部门id获取部门信息
     * @description:
     * @param: [dept_id]
     * @return com.hryj.common.Result<com.hryj.entity.bo.staff.dept.DeptGroup>
     * @create 2018-07-07 16:19
     **/
    @PostMapping("/getDeptGroupByDeptId")
    public Result<DeptGroup> getDeptGroupByDeptId(@RequestParam("dept_id") Long dept_id){
        DeptGroup deptGroup = deptService.selectById(dept_id);
        return new Result<>(CodeEnum.SUCCESS,deptGroup);
    }

    /**
     * @author 代廷波
     * @description: 根据部门id查询向上的部门列表
     * @param: dept_id 部门id
     * @return com.hryj.common.Result
     * @create 2018/06/27 19:24
     **/
    @PostMapping("/getDeptIdByUpDeptList")
    public Result<ListResponseVO<DeptGroupListResponseVO>> getDeptIdByUpDeptList(@RequestBody DeptIdRequestVO vo) throws GlobalException {

        return deptService.getDeptIdByUpDeptList(vo);

    }

    /**
     * @author 代廷波
     * @description: 批量设置部门分润
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/07/07 17:49
     **/
    @PostMapping("/savaBatchDeptShare")
    public Result savaBatchDeptShare(@RequestBody DeptShareBatchReqestVO vo) throws GlobalException {


        return deptService.savaBatchDeptShare(vo);

    }
    /**
     * @author 代廷波
     * @description: 根据当前组织id,判断是否可以创建区域公司
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/07/09 16:13
     **/
    @PostMapping("/getDeptCreateCompanyFlag")
    public Result<DeptCreateCompanyFlag> getDeptCreateCompanyFlag(@RequestBody DeptIdRequestVO vo) throws GlobalException {


        return deptService.getDeptCreateCompanyFlag(vo);

    }
    /**
     * @author 代廷波
     * @description:根据当前登陆人查询所有门店或者仓库列表
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptStoreOrWarehouseResponseVO>>
     * @create 2018/07/14 14:34
     **/

    @PostMapping("/getDeptStoreOrWarehouseList")
    public Result<ListResponseVO<DeptStoreOrWarehouseResponseVO>> getDeptStoreOrWarehouseList(@RequestBody DeptStoreOrWarehouseRequestVO vo) throws GlobalException {

        return deptService.getDeptStoreOrWarehouseList(vo);

    }

    /**
     * @author 代廷波
     * @description: 根据部门id或者员工或者登录token查询下面所有组织
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupTreeResponseVO>>
     * @create 2018/07/23 14:37
     **/
    @PostMapping("/common/getDeptIdByDownDeptTree")
    public Result<ListResponseVO<DeptGroupTreeResponseVO>> getDeptIdByDownDeptTree(@RequestBody DeptDownTreeRequestVO vo) throws GlobalException {

        return deptService.getDeptIdByDownDeptTree(vo);

    }

    /**
     * @author 代廷波
     * @description: 根据组织id获取组织的基本信息与配送范围VO
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO>
     * @create 2018/07/09 19:17
     **/

   /* @PostMapping("/getDeptDetAndDistributionArea")
    public Result<DeptDetAndDistributionAreaResponseVO> getDeptDetAndDistributionArea(@RequestBody DeptIdRequestVO vo) throws GlobalException {

        return deptService.getDeptDetAndDistributionArea(vo);

    }*/
    /**
     * @author 代廷波
     * @description: 根据部门id集合查询详情
     * @param: basicInfoRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @create 2018/07/10 21:06
     **/
    /*@PostMapping("/getDeptGroupBasicInfo")
    public Result<ListResponseVO<DeptGroupResponseVO>> getDeptGroupBasicInfo(@RequestBody DeptGroupBasicInfoRequestVO basicInfoRequestVO) throws GlobalException {


        return deptService.getDeptGroupBasicInfo(basicInfoRequestVO);

    }*/



}
