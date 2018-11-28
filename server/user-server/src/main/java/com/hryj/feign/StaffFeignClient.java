package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李道云
 * @className: StaffFeignClient
 * @description: 员工模块feign接口
 * @create 2018/6/26 10:50
 **/
@FeignClient(name = "staff-server")
public interface StaffFeignClient {

    /**
     * @author 李道云
     * @methodName: findStaffDeptVO
     * @methodDesc: 查询员工部门信息
     * @description:
     * @param: [referral_code, phone_num, staff_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.StaffDeptVO>
     * @create 2018-07-06 14:01
     **/
    @RequestMapping(value = "/staff/findStaffDeptVO", method = RequestMethod.POST)
    Result<StaffDeptVO> findStaffDeptVO(@RequestParam(name="referral_code",required = false) String referral_code,
                                        @RequestParam(name="phone_num",required = false) String phone_num,
                                        @RequestParam(name="staff_id",required = false) Long staff_id);

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
     * @author 李道云
     * @methodName: getDeptGroupByDeptId
     * @methodDesc: 根据部门id获取部门信息
     * @description:
     * @param: [dept_id]
     * @return com.hryj.common.Result<com.hryj.entity.bo.staff.dept.DeptGroup>
     * @create 2018-07-07 16:09
     **/
    @RequestMapping(value = "/dept/getDeptGroupByDeptId", method = RequestMethod.POST)
    Result<DeptGroup> getDeptGroupByDeptId(@RequestParam("dept_id") Long dept_id);
}
