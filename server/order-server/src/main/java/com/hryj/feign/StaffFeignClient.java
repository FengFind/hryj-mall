package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.store.request.StoreStaffRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李道云
 * @className: StaffFeignClient
 * @description: 员工feign接口
 * @create 2018/7/10 20:46
 **/
@FeignClient(name = "staff-server")
public interface StaffFeignClient {

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

    /**
     * @author 罗秋涵
     * @description: 查询门店下用户列表
     * @param: [staffRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO>>
     * @create 2018-07-18 22:01
     **/
    @RequestMapping(value = "/app/store/getStoreStaffList", method = RequestMethod.POST)
    Result<ListResponseVO<StoreStaffResponseVO>> getStoreStaffList(@RequestBody StoreStaffRequestVO staffRequestVO);

    /**
     * @author 罗秋涵
     * @methodName: findStaffDeptVO
     * @methodDesc: 查询员工信息
     * @description:
     * @param: [referral_code, phone_num, staff_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.user.StaffDeptVO>
     * @create 2018-07-06 14:01
     **/
    @RequestMapping(value = "/staff/findStaffDeptVO", method = RequestMethod.POST)
    Result<StaffDeptVO> findStaffDeptVO(@RequestParam(name="referral_code",required = false) String referral_code,
                                        @RequestParam(name="phone_num",required = false) String phone_num,
                                        @RequestParam(name="staff_id",required = false) Long staff_id);


}
