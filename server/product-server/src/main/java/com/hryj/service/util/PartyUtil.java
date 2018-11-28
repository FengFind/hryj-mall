package com.hryj.service.util;

import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.product.partyprod.response.PartySimpleInfoResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.dept.response.DeptGroupResponseVO;
import com.hryj.entity.vo.staff.store.request.StoreIdRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreWhDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.staff.team.request.AppTeamDataRequestVO;
import com.hryj.feign.PartyFeignClient;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @author 王光银
 * @className: PartyUtil
 * @description:
 * @create 2018/7/18 0018 12:01
 **/
@Slf4j
public class PartyUtil {

    /**
     * 获取仓库覆盖的所有门店的ID集合
     * @param party_id
     * @param partyFeignClient
     * @return
     */
    public static Result<List<Long>> getCoveredParty(Long party_id, PartyFeignClient partyFeignClient) {
        if (party_id == null || party_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "仓库ID不能是空值");
        }
        try {
            Result<StaffStoreWhVO> result = partyFeignClient.getStoreIdListByWhId(party_id);
            if (result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_SERVER, "获取仓库覆盖的门店失败:" + result.getMsg());
            }
            if (result.getData() != null && UtilValidate.isNotEmpty(result.getData().getStoreIdList())) {
                String[] arr = result.getData().getStoreIdList().split(",");
                List<Long> ids = new ArrayList<>(arr.length);
                for (String str : arr) {
                    try {
                        ids.add(Long.valueOf(str));
                    } catch (NumberFormatException e) {
                        log.error("获取仓库覆盖的门店 - 门店ID格式错误:" + str, e);
                    }
                }
                return new Result<>(CodeEnum.SUCCESS, ids);
            }
            return new Result<>(CodeEnum.SUCCESS);
        } catch (Exception e) {
            log.error("com.hryj.service.util.PartyUtil.getCoveredParty: 调用接口获取仓库覆盖的门店集合异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "调用接口获取仓库覆盖的门店集合异常");
        }
    }

    /**
     * 获取覆盖指定门店的仓库ID集合（一般覆盖门店的仓库只有一个）
     * @param party_id
     * @param partyFeignClient
     * @return
     */
    public static Result<List<Long>> getCoveredBy(Long party_id, PartyFeignClient partyFeignClient) {
        if (party_id == null || party_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "当事组织(门店)ID不能是空值");
        }
        StoreIdRequestVO storeIdRequestVO = new StoreIdRequestVO();
        storeIdRequestVO.setStore_id(party_id);
        try {
            Result<ListResponseVO<StoreWhDistributionAreaResponseVO>> covered_warehouse_result = partyFeignClient.getWhListByStoreId(storeIdRequestVO);
            if (covered_warehouse_result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_SERVER, "获取当事组织门店的覆盖仓库失败:" + covered_warehouse_result.getMsg());
            }
            if (covered_warehouse_result.getData() == null || UtilValidate.isEmpty(covered_warehouse_result.getData().getRecords())) {
                return new Result<>(CodeEnum.FAIL_SERVER, "获取当事组织门店的覆盖仓库失败: 没有覆盖这家门店的仓库");
            }
            return new Result<>(CodeEnum.SUCCESS, UtilMisc.toList(covered_warehouse_result.getData().getRecords().get(0).getId()));
        } catch (Exception e) {
            log.error("com.hryj.service.util.PartyUtil: 调用接口获取覆盖门店的仓库异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "获取覆盖门店的仓库异常");
        }
    }

    /**
     * 获取一个组织（门店和仓库）的基本信息和配送范围
     * @param party_id
     * @param partyFeignClient
     * @return
     */
    public static Result<PartySimpleInfoResponseVO> getPartySimpleInfo(Long party_id, PartyFeignClient partyFeignClient) {
        if (party_id == null || party_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "当事组织ID是空值");
        }
        DeptIdRequestVO deptIdRequestVO = new DeptIdRequestVO();
        deptIdRequestVO.setDept_id(party_id);
        try {
            Result<DeptDetAndDistributionAreaResponseVO> party_get_result = partyFeignClient.getPartySimpleInfo(deptIdRequestVO);
            if (party_get_result.isFailed()) {
                return new Result<>(CodeEnum.FAIL_SERVER, party_get_result.getMsg());
            }

            if (party_get_result.getData() == null) {
                log.error("com.hryj.service.util.PartyUtil: 调用接口获取当事组织基础信息，接口未返回数据");
                return new Result<>(CodeEnum.FAIL_SERVER, "接口未返回数据");
            }

            DeptDetAndDistributionAreaResponseVO dept_vo = party_get_result.getData();
            PartySimpleInfoResponseVO party_simple_info = new PartySimpleInfoResponseVO();
            party_simple_info.setParty_name(dept_vo.getDept_name());
            party_simple_info.setProvince(dept_vo.getProvince_name());
            party_simple_info.setCity(dept_vo.getCity_name());
            party_simple_info.setArea(dept_vo.getArea_name());
            party_simple_info.setAddress(dept_vo.getDetail_address());
            party_simple_info.setParty_type(dept_vo.getDept_type());

            if (UtilValidate.isNotEmpty(dept_vo.getDeptDistributionAreaResponseVOList())) {
                List<String> delivery_area = new ArrayList<>(dept_vo.getDeptDistributionAreaResponseVOList().size());
                party_simple_info.setDelivery_area(delivery_area);
                for (DeptDistributionAreaResponseVO areaResponseVO : dept_vo.getDeptDistributionAreaResponseVOList()) {
                    delivery_area.add(areaResponseVO.getAddress());
                }
            }
            return new Result<>(CodeEnum.SUCCESS, party_simple_info);
        } catch (Exception e) {
            log.error("com.hryj.service.util.PartyUtil: 调用接口获取当事组织基础信息异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "获取当事组织基础信息异常");
        }
    }

    /**
     * 获取一个组织下的所有门店与仓库的ID集合
     * @param party_id
     * @param party_type
     * @param client
     * @return
     */
    public static Result<Set<Long>> getSubPartyGroup(Long party_id, String party_type, PartyFeignClient client) {
        if (party_id == null || party_id <= 0L) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "当事组织ID是空值");
        }
        AppTeamDataRequestVO appTeamDataRequestVO = new AppTeamDataRequestVO();
        appTeamDataRequestVO.setDept_id(party_id);
        try {
            IdsHandler idsHandler = APIInvoker.getPartySubParty(party_id, client);
            if (UtilValidate.isEmpty(idsHandler.id_set_one) && UtilValidate.isEmpty(idsHandler.id_set_two)) {
                return new Result<>(CodeEnum.SUCCESS);
            }
            Set<Long> party_id_set = new HashSet<>();
            if (UtilValidate.isEmpty(party_type)) {
                if (UtilValidate.isNotEmpty(idsHandler.id_set_one)) {
                    party_id_set.addAll(idsHandler.id_set_one);
                }
                if (UtilValidate.isNotEmpty(idsHandler.id_set_two)) {
                    party_id_set.addAll(idsHandler.id_set_two);
                }
            } else if (SysCodeEnmu.DEPTETYPE_01.getCodeValue().equals(party_type) && UtilValidate.isNotEmpty(idsHandler.id_set_one)) {
                party_id_set.addAll(idsHandler.id_set_one);
            } else if (SysCodeEnmu.DEPTETYPE_02.getCodeValue().equals(party_type) && UtilValidate.isNotEmpty(idsHandler.id_set_two)) {
                party_id_set.addAll(idsHandler.id_set_two);
            }
            return new Result<>(CodeEnum.SUCCESS, party_id_set);
        } catch (Exception e) {
            log.error("com.hryj.service.util.PartyUtil: 调用接口获取组织下的所有门店与仓库异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "获取组织下所有门店与仓库异常");
        }
    }

    /**
     * 根据组织ID批量获取这些组织的基本信息
     * @param party_id_list
     * @param partyFeignClient
     * @return
     */
    public static Result<Map<Long, DeptGroupResponseVO>> getManyPartySimpleInfo(List<Long> party_id_list, PartyFeignClient partyFeignClient) {
        if (UtilValidate.isEmpty(party_id_list)) {
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "当事组织ID集合不能是空值");
        }
        for (Long party_id : party_id_list) {
            if (party_id == null || party_id <= 0L) {
                return new Result<>(CodeEnum.FAIL_PARAMCHECK, "集合中存在当事组织ID是空值的错误数据");
            }
        }
        try {
            ListResponseVO<DeptGroupResponseVO> party_info_list = APIInvoker.getManyPartyBaseInfo(partyFeignClient, party_id_list);
            if (party_info_list == null || UtilValidate.isEmpty(party_info_list.getRecords())) {
                log.error("com.hryj.service.util.PartyUtil: 调用接口获取多个当事组织的基本信息 - 接口未返回数据");
                return new Result<>(CodeEnum.SUCCESS);
            }
            Map<Long, DeptGroupResponseVO> party_map = new HashMap<>(party_info_list.getRecords().size());
            for (DeptGroupResponseVO vo : party_info_list.getRecords()) {
                party_map.put(vo.getDept_id(), vo);
            }
            return new Result<>(CodeEnum.SUCCESS, party_map);
        } catch (Exception e) {
            log.error("com.hryj.service.util.PartyUtil: 调用接口获取多个当事组织的基本信息异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "获取多个当事组织的基本信息异常");
        }
    }

}
