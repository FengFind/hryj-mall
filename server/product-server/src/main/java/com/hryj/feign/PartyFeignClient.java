package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptGroupBasicInfoRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.store.request.StoreIdRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreStaffRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreWhDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author 王光银
 * @className: PartyFeignClient
 * @description: 当事组织服务调用客户端
 * @create 2018/7/9 0009 22:40
 **/
@FeignClient(name = "staff-server")
public interface PartyFeignClient {

    /**
     * @author 王光银
     * @methodName: getPartySimpleInfo
     * @methodDesc: 查询当事组织的简单基本信息与配送范围
     * @description:
     * @param: [vo]
     * @return
     * @create 2018-07-11 12:01
     **/
    @RequestMapping(value = "/app/dept/getAppDeptDetAndDistributionArea", method = RequestMethod.POST)
    Result<DeptDetAndDistributionAreaResponseVO> getPartySimpleInfo(@RequestBody DeptIdRequestVO vo);

    /**
     * @author 王光银
     * @methodName: getPartyCoveredBy
     * @methodDesc: 查询覆盖当事组织门店的仓库
     * @description:
     * @param: [vo]
     * @return
     * @create 2018-07-11 12:00
     **/
    @RequestMapping(value = "/app/store/getWhListByStoreId", method = RequestMethod.POST)
    Result<ListResponseVO<StoreWhDistributionAreaResponseVO>> getWhListByStoreId(@RequestBody StoreIdRequestVO vo);

    /**
     * @author 王光银
     * @description: 获取多个当事组织的基本信息
     * @param: basicInfoRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @create 2018/07/10 21:06
     **/
    @RequestMapping(value = "/app/dept/getAppDeptGroupBasicInfo", method = RequestMethod.POST)
    Result<ListResponseVO<DeptGroupResponseVO>> getManyPartyBaseInfo(@RequestBody DeptGroupBasicInfoRequestVO basicInfoRequestVO);

    /**
     * @author 王光银
     * @methodName: getStoreStaffList
     * @methodDesc: 获取指定门店的员工
     * @description:
     * @param: [staffRequestVO]
     * @return 
     * @create 2018-07-11 22:17
     **/
    @RequestMapping(value = "/app/store/getStoreStaffList", method = RequestMethod.POST)
    Result<ListResponseVO<StoreStaffResponseVO>> getStoreStaffList(@RequestBody StoreStaffRequestVO staffRequestVO);

    /**
     * @author 李道云
     * @methodName: getUserServiceRange
     * @methodDesc: 根据位置获取服务于用户的门店和仓库
     * @description:
     * @param: [poi_id, city_code]
     * @return com.hryj.common.Result
     * @create 2018-07-02 20:55
     **/
    @RequestMapping(value = "/staff/getUserServiceRange", method = RequestMethod.POST)
    Result<UserServiceRangeVO> getUserServiceRange(@RequestParam("poi_id") String poi_id, @RequestParam("city_code") String city_code);

    /**
     * @author 代廷波
     * @description: 查询员工部门下所有门店和仓库
     * @param: staffIdRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.response.AppTeamDataResponseVO>
     * @create 2018/07/18 10:45
     **/
    @PostMapping("/staff/findStaffStoreWhVO")
    Result<StaffStoreWhVO> findStaffStoreWhVO(@RequestParam("staff_id") Long staff_id);

    /**
     * @author 代廷波
     * @description: 查询部门下所有门店和仓库
     * @param: staffIdRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.response.AppTeamDataResponseVO>
     * @create 2018/07/18 10:45
     **/
    @PostMapping("/staff/findStoreWhVOByDeptId")
    Result<StaffStoreWhVO> findStoreWhVOByDeptId(@RequestParam("dept_id") Long dept_id);

    /**
     * @author 李道云
     * @methodName: getStoreIdListByWhId
     * @methodDesc: 根据仓库id查询门店id列表
     * @description:
     * @param: [wh_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.StaffStoreWhVO>
     * @create 2018-07-25 20:51
     **/
    @PostMapping("/app/store/getStoreIdListByWhId")
    Result<StaffStoreWhVO> getStoreIdListByWhId(@RequestParam("wh_id") Long wh_id);
}
