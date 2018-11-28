package com.hryj.feign;

import com.hryj.common.Result;
import com.hryj.entity.bo.staff.dept.DeptGroup;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 李道云
 * @className: StaffFeignClient
 * @description: 员工模块feign接口
 * @create 2018/7/7 16:07
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

}
