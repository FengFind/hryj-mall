package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptGroupBasicInfoRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author 汪豪
 * @className: DeptFeignClient
 * @description: 部门组织服务调用客户端
 * @create 2018/7/12 0012 9:05
 **/
@FeignClient(name = "staff-server")
public interface DeptFeignClient {

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
     * @author 王光银
     * @description: 获取多个当事组织的基本信息
     * @param: basicInfoRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO>>
     * @create 2018/07/10 21:06
     **/
    @RequestMapping(value = "/app/dept/getAppDeptGroupBasicInfo", method = RequestMethod.POST)
    Result<ListResponseVO<DeptGroupResponseVO>> getManyPartyBaseInfo(@RequestBody DeptGroupBasicInfoRequestVO basicInfoRequestVO);

    /**
     * @author 李道云
     * @methodName: findStaffStoreWhVO
     * @methodDesc: 查询员工部门下所有门店和仓库
     * @description:
     * @param: [staff_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.StaffStoreWhVO>
     * @create 2018-07-25 9:29
     **/
    @RequestMapping(value = "/staff/findStaffStoreWhVO", method = RequestMethod.POST)
    Result<StaffStoreWhVO> findStaffStoreWhVO(@RequestParam("staff_id") Long staff_id);
}
