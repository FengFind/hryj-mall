package com.hryj.service.util;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptGroupBasicInfoRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.entity.vo.userparty.request.UserPartyRequestVO;
import com.hryj.exception.ServerException;
import com.hryj.feign.DeptFeignClient;
import com.hryj.feign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 汪豪
 * @className: ServiceInvoker
 * @description:
 * @create 2018/7/12 0012 9:15
 **/
@Slf4j
public class ServiceInvoker {

    public static UserServiceRangeVO getUserServiceRange(DeptFeignClient deptFeignClient, String user_poi_id, String user_city_code) {
        try {
            Result<UserServiceRangeVO> result = deptFeignClient.getUserServiceRange(user_poi_id, user_city_code);
            if (result.isFailed()) {
                throw new ServerException(result.getMsg());
            }
            return result.getData();
        } catch (Exception e) {
            log.error("com.hryj.service.util.ServiceInvoker.getUserServiceRange()", e);
            throw new ServerException("", e);
        }
    }

    public static ListResponseVO<DeptGroupResponseVO> getPartyBaseInfo(DeptFeignClient deptFeignClient, List<Long> party_id_list) {
        try {
            DeptGroupBasicInfoRequestVO deptGroupBasicInfoRequestVO = new DeptGroupBasicInfoRequestVO();
            deptGroupBasicInfoRequestVO.setDept_ids(party_id_list.toArray(new Long[party_id_list.size()]));
            Result<ListResponseVO<DeptGroupResponseVO>> result = deptFeignClient.getManyPartyBaseInfo(deptGroupBasicInfoRequestVO);
            if (result.isFailed()) {
                throw new ServerException(result.getMsg());
            }
            return result.getData();
        } catch (Exception e) {
            log.error("com.hryj.service.util.ServiceInvoker.getPartyBaseInfo()", e);
            throw new ServerException("", e);
        }
    }

    public static StaffStoreWhVO findStaffStoreWhVO(DeptFeignClient deptFeignClient,Long staff_id){
        try {
            Result<StaffStoreWhVO> result = deptFeignClient.findStaffStoreWhVO(staff_id);
            if (result.isFailed()) {
                throw new ServerException(result.getMsg());
            }
            return result.getData();
        }catch (Exception e){
            log.error("com.hryj.service.util.ServiceInvoker.findStaffStoreWhVO()", e);
            throw new ServerException("", e);
        }
    }

    public static Long getUserUniqueParty(ProductFeignClient productFeignClient,UserPartyRequestVO userPartyRequestVO){
        try {
            Result<Long> result = productFeignClient.getUserUniqueParty(userPartyRequestVO);
            if (result.isFailed()) {
                return null;
            }
            return result.getData();
        }catch (Exception e){
            log.error("com.hryj.service.util.ServiceInvoker.getUserUniqueParty()", e);
            throw new ServerException("", e);
        }
    }
}
