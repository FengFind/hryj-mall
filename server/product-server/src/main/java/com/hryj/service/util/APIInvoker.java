package com.hryj.service.util;

import cn.hutool.core.util.StrUtil;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptGroupBasicInfoRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.user.UserServiceRangeVO;
import com.hryj.exception.ServerException;
import com.hryj.feign.PartyFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;

/**
 * @author 王光银
 * @className: APIInvoker
 * @description: 其他服务组件的调用类
 * @create 2018/7/11 0011 18:17
 **/
@Slf4j
public class APIInvoker {

    public static UserServiceRangeVO getUserServiceRange(PartyFeignClient partyFeignClient, String user_poi_id, String user_city_code) {
        try {
            Result<UserServiceRangeVO> result = partyFeignClient.getUserServiceRange(user_poi_id, user_city_code);
            if (result == null) {
                return null;
            }
            if (result.isFailed()) {
                throw new ServerException(result.getMsg());
            }
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            return null;
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            log.error("com.hryj.service.util.APIInvoker.getUserServiceRange():", e);
            throw new ServerException("", e);
        }
    }

    public static UserServiceRangeVO getUserServiceRange(UserFeignClient userFeignClient, Long user_id) {
        try {
            Result<UserServiceRangeVO> result = userFeignClient.getUserServiceRangeByUserId(user_id);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            if (result.isFailed()) {
                throw new ServerException(result.getMsg());
            }
            return null;
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            log.error("com.hryj.service.util.APIInvoker.getUserServiceRange():", e);
            throw new ServerException("", e);
        }
    }

    public static ListResponseVO<DeptGroupResponseVO> getManyPartyBaseInfo(PartyFeignClient partyFeignClient, List<Long> party_id_list) {
        try {
            DeptGroupBasicInfoRequestVO deptGroupBasicInfoRequestVO = new DeptGroupBasicInfoRequestVO();
            deptGroupBasicInfoRequestVO.setDept_ids(party_id_list.toArray(new Long[party_id_list.size()]));
            Result<ListResponseVO<DeptGroupResponseVO>> result = partyFeignClient.getManyPartyBaseInfo(deptGroupBasicInfoRequestVO);
            if (result.isSuccess() && result.getData() != null) {
                return result.getData();
            }
            if (result.isFailed()) {
                throw new ServerException(result.getMsg());
            }
            return null;
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            log.error("com.hryj.service.util.APIInvoker.getPartyBaseInfo():", e);
            throw new ServerException("", e);
        }
    }


    public static IdsHandler getStaffUserCoveredParty(Long staff_id, PartyFeignClient partyFeignClient) {
        if (staff_id == null || staff_id <= 0L) {
            return new IdsHandler();
        }
        try {
            Result<StaffStoreWhVO> party_result = partyFeignClient.findStaffStoreWhVO(staff_id);
            if (party_result.isFailed()) {
                throw new ServerException(party_result.getMsg());
            }
            return generateIdsHandlerBy(party_result);
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            log.error("com.hryj.service.util.APIInvoker.getStaffUserCoveredParty():", e);
            throw new ServerException("", e);
        }
    }

    public static IdsHandler getPartySubParty(Long dept_id, PartyFeignClient partyFeignClient) {
        if (dept_id == null || dept_id <= 0L) {
            return new IdsHandler();
        }
        try {
            Result<StaffStoreWhVO> party_result = partyFeignClient.findStoreWhVOByDeptId(dept_id);
            if (party_result.isFailed()) {
                throw new ServerException(party_result.getMsg());
            }
            return generateIdsHandlerBy(party_result);
        } catch (ServerException e) {
            throw e;
        } catch (Exception e) {
            log.error("com.hryj.service.util.APIInvoker.getStaffUserCoveredParty():", e);
            throw new ServerException("", e);
        }
    }

    private static IdsHandler generateIdsHandlerBy(Result<StaffStoreWhVO> party_result) {
        IdsHandler idsHandler = new IdsHandler();
        if (party_result.getData() != null) {
            if (UtilValidate.isNotEmpty(party_result.getData().getStoreIdList())) {
                String[] store_list = StrUtil.split(party_result.getData().getStoreIdList(), ",");
                idsHandler.id_set_one = new HashSet<>(store_list.length);
                for (String id : store_list) {
                    try {
                        idsHandler.id_set_one.add(Long.valueOf(id));
                    } catch (NumberFormatException e) {
                        log.error("com.hryj.service.util.APIInvoker.getStaffUserCoveredParty(): 调用接口获取用户可见组织，组织ID格式错误:" + id, e);
                    }
                }
            }

            if (UtilValidate.isNotEmpty(party_result.getData().getWhIdList())) {
                String[] wh_list = StrUtil.split(party_result.getData().getWhIdList(), ",");
                idsHandler.id_set_two = new HashSet<>(wh_list.length);
                for (String id : wh_list) {
                    try {
                        idsHandler.id_set_two.add(Long.valueOf(id));
                    } catch (NumberFormatException e) {
                        log.error("com.hryj.service.util.APIInvoker.getStaffUserCoveredParty(): 调用接口获取用户可见组织，组织ID格式错误:" + id, e);
                    }
                }
            }
        }
        return idsHandler;
    }
}
