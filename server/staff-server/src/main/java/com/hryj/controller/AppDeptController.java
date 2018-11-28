package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptGroupBasicInfoRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdsRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.entity.vo.staff.team.response.AppTeamDataResponseVO;
import com.hryj.exception.GlobalException;
import com.hryj.service.AppDeptService;
import com.hryj.service.StaffAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 代廷波
 * @className: Deptcontroller
 * @description: 部门管理
 * @create 2018/6/27 0027-17:08
 **/
@RestController
@RequestMapping("/app/dept")
public class AppDeptController {

    @Autowired
    private AppDeptService appdeptService;

    @Autowired
    private StaffAppService staffAppService;

    /**
     * @author 代廷波
     * @description: 获取团队数据
     * @param: teamDataRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.response.AppTeamDataResponseVO>
     * @create 2018/07/10 9:31
     **/
    @PostMapping("/getTeamData")
    public Result<AppTeamDataResponseVO> getTeamData(@RequestBody RequestVO requestVO){
        return staffAppService.getTeamData(requestVO);
    }

    /**
     * @author 代廷波
     * @description: 根据组织id获取组织的基本信息与配送范围VO
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO>
     * @create 2018/07/09 19:17
     **/

    @PostMapping("/getAppDeptDetAndDistributionArea")
    public Result<DeptDetAndDistributionAreaResponseVO> getAppDeptDetAndDistributionArea(@RequestBody DeptIdRequestVO vo) throws GlobalException {

        return appdeptService.getAppDeptDetAndDistributionArea(vo);

    }
    /**
     * @author 代廷波
     * @description: 根据部门id集合查询详情
     * @param: basicInfoRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @create 2018/07/10 21:06
     **/
    @PostMapping("/getAppDeptGroupBasicInfo")
    public Result<ListResponseVO<DeptGroupResponseVO>> getAppDeptGroupBasicInfo(@RequestBody DeptGroupBasicInfoRequestVO basicInfoRequestVO) throws GlobalException {


        return appdeptService.getAppDeptGroupBasicInfo(basicInfoRequestVO);

    }

    /**
     * @author 代廷波
     * @description: app 根据组织id获取门店或者仓库查询详情
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO>
     * @create 2018/07/12 21:56
     **/
    @PostMapping("/getAppDeptIdByStoreOrWarehouseDet")
    public Result<DeptIdByStoreOrWarehouseResponseVO> getAppDeptIdByStoreOrWarehouseDet(@RequestBody DeptIdRequestVO vo) throws GlobalException {

        return appdeptService.getAppDeptIdByStoreOrWarehouseDet(vo);
    }

    /**
     * @author 代廷波
     * @description: app 根据组织ids获取门店或者仓库查询详情
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO>
     * @create 2018/07/12 21:56
     **/
    @PostMapping("/getAppDeptIdsByStoreOrWarehouseDet")
    public Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> getAppDeptIdsByStoreOrWarehouseDet(@RequestBody DeptIdsRequestVO vo) throws GlobalException {

        return appdeptService.getAppDeptIdsByStoreOrWarehouseDet(vo);
    }

}
