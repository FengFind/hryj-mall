package com.hryj.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.entity.bo.staff.warehouse.WarehouseInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.product.partyprod.request.PartyProductStatisticsRequestVO;
import com.hryj.entity.vo.product.partyprod.response.PartyProductStatisticsItem;
import com.hryj.entity.vo.staff.store.request.StoreStaffRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO;
import com.hryj.entity.vo.staff.storewarehouse.request.SearchPartyRequestVO;
import com.hryj.entity.vo.staff.storewarehouse.response.PartySearchItemResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.exception.BizException;
import com.hryj.feign.PartyProductFeign;
import com.hryj.mapper.StoreInfoMapper;
import com.hryj.mapper.WarehouseInfoMapper;
import com.hryj.utils.UtilMisc;
import com.hryj.utils.UtilValidate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author 王光银
 * @className: StoreWarehouseService
 * @description:
 * @create 2018/7/4 0004 15:00
 **/
@Slf4j
@Service
public class StoreWarehouseService extends ServiceImpl<StoreInfoMapper, StoreInfo> {

    protected static final String PARTY_TYPE_STORE = "store";

    protected static final String PARTY_TYPE_WAREHOUSE = "warehouse";

    @Autowired
    private WarehouseInfoMapper warehouseInfoMapper;

    @Autowired
    private PartyProductFeign partyProductFeign;

    @Autowired
    private AppStoreService appStoreServices;

    @Autowired
    private StaffService staffService;

    /**
     * @author 王光银
     * @methodName: searchVisiblePartyPage
     * @methodDesc: 根据当前请求的用户在组织结构树上的位置，返回可见的门店或仓库
     * @description:
     * @param: [partySearchRequestVO]
     * @return Result<PageResponseVO<PartySearchItemResponseVO>>
     * @create 2018-07-12 9:06
     **/
    public Result<PageResponseVO<PartySearchItemResponseVO>> searchVisiblePartyPage(SearchPartyRequestVO partySearchRequestVO) throws BizException {
        //参数处理和验证
        if (UtilValidate.isEmpty(partySearchRequestVO.getParty_type())) {
            partySearchRequestVO.setParty_type(PARTY_TYPE_STORE);
        } else if (!PARTY_TYPE_STORE.equals(partySearchRequestVO.getParty_type())
                && !PARTY_TYPE_WAREHOUSE.equals(partySearchRequestVO.getParty_type())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "无法识别的参数值: party_type=" + partySearchRequestVO.getParty_type());
        }

        List<Long> party_id_list = null;

        //获取当前请求用户在组织结构树中能够看到的所有门店或仓库
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(partySearchRequestVO.getLogin_token());

        if (staffAdminLoginVO == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "获取当前请求用户数据失败, token=" + partySearchRequestVO.getLogin_token());
        }

        String party_id_str;
        try {
            Result<StaffStoreWhVO> staff_visble_party_result = staffService.findStaffStoreWhVO(staffAdminLoginVO.getStaff_id());
            if (staff_visble_party_result == null || staff_visble_party_result.isFailed()) {
                //实时 获取可见门店与仓库失败时，从缓存中获取
                if (PARTY_TYPE_STORE.equals(partySearchRequestVO.getParty_type())) {
                    party_id_str = staffAdminLoginVO.getStoreIdList();
                } else {
                    party_id_str = staffAdminLoginVO.getWhIdList();
                }
            } else {
                if (PARTY_TYPE_STORE.equals(partySearchRequestVO.getParty_type())) {
                    party_id_str = staff_visble_party_result.getData().getStoreIdList();
                } else {
                    party_id_str = staff_visble_party_result.getData().getWhIdList();
                }
            }
        } catch (Exception e) {
            log.error("加载用户所在部门能够看到的门店、仓库数据失败:", e);
            return new Result<>(CodeEnum.FAIL_BUSINESS, "加载用户所在部门能够看到的门店、仓库数据失败");
        }

        if (UtilValidate.isNotEmpty(party_id_str)) {
            String[] arr = party_id_str.split(",");
            party_id_list = new ArrayList<>(arr.length);
            for (String s : arr) {
                try {
                    party_id_list.add(Long.valueOf(s));
                } catch (NumberFormatException e) {
                    log.error("门店或仓库ID出现非数字的错误数据,发生在查询操作用户可见门店或仓库数据时, 该ID值是从缓存中通过用户 token获取,获取到的原始数据为:[" + party_id_str + "]", e);
                }
            }
        }

        //没有可见门店或仓库时直接返回
        if (UtilValidate.isEmpty(party_id_list)) {
            return new Result(CodeEnum.SUCCESS, new PageResponseVO());
        }

        //组装查询条件
        Map<String, Object> params_map = new HashMap<>(6);
        //01表示店长
        params_map.put("party_leader_type", "01");
        if (UtilValidate.isNotEmpty(partySearchRequestVO.getParty_name())) {
            params_map.put("party_name", partySearchRequestVO.getParty_name());
        }
        if (UtilValidate.isNotEmpty(partySearchRequestVO.getParty_leader())) {
            params_map.put("party_leader", partySearchRequestVO.getParty_leader());
        }
        if (UtilValidate.isNotEmpty(partySearchRequestVO.getProvince_code())) {
            params_map.put("province_code", partySearchRequestVO.getProvince_code());
        }
        if (UtilValidate.isNotEmpty(partySearchRequestVO.getCity_code())) {
            params_map.put("city_code", partySearchRequestVO.getCity_code());
        }
        if (UtilValidate.isNotEmpty(partySearchRequestVO.getArea_code())) {
            params_map.put("area_code", partySearchRequestVO.getArea_code());
        }

        //组装查询需要的 in 参数格式
        List<String> id_in_params = generateStoreWarehouseIdsParam(party_id_list);
        params_map.put("party_id_list", id_in_params);

        Result<PageResponseVO<PartySearchItemResponseVO>> result = new Result(CodeEnum.SUCCESS, new PageResponseVO());

        //查询可见仓库处理
        if (PARTY_TYPE_WAREHOUSE.equals(partySearchRequestVO.getParty_type())) {
            try {
                loadVisibleWarehouse(result, params_map, partySearchRequestVO.getPage_num(), partySearchRequestVO.getPage_size());
            } catch (Exception e) {
                log.error("查询可见仓库数据失败", e);
                return new Result<>(CodeEnum.FAIL_BUSINESS, "查询可见仓库数据失败");
            }
        } else {
            //查询可见门店处理
            try {
                loadVisibleStore(result, params_map, partySearchRequestVO.getPage_num(), partySearchRequestVO.getPage_size());
            } catch (Exception e) {
                log.error("查询可见门店数据失败", e);
                return new Result<>(CodeEnum.FAIL_BUSINESS, "查询可见门店数据失败");
            }
        }
        if (UtilValidate.isEmpty(result.getData().getRecords())) {
            return result;
        }

        Set<Long> party_id_set = new HashSet<>(result.getData().getRecords().size());
        for (PartySearchItemResponseVO vo : result.getData().getRecords()) {
            party_id_set.add(vo.getParty_id());
        }
        //处理商品统计
        if (partySearchRequestVO.getStatistics_party_product()) {
            //(invoke-api) 需要调用接口获取
            PartyProductStatisticsRequestVO partyProductStatisticsRequestVO = new PartyProductStatisticsRequestVO();
            partyProductStatisticsRequestVO.setParty_id_list(UtilMisc.toList(party_id_list));
            partyProductStatisticsRequestVO.setReturn_all(true);
            try {
                Result<ListResponseVO<PartyProductStatisticsItem>> partyProdStatisticsResult = partyProductFeign.partyProdSimpleStatistics(partyProductStatisticsRequestVO);
                if (partyProdStatisticsResult.isSuccess() && partyProdStatisticsResult.getData() != null && UtilValidate.isNotEmpty(partyProdStatisticsResult.getData().getRecords())) {
                    Map<Long, PartyProductStatisticsItem> map = new HashMap(partyProdStatisticsResult.getData().getRecords().size());
                    for (PartyProductStatisticsItem item : partyProdStatisticsResult.getData().getRecords()) {
                        map.put(item.getParty_id(), item);
                    }
                    for (PartySearchItemResponseVO vo : result.getData().getRecords()) {
                        vo.setProduct_total_num(map.get(vo.getParty_id()).getAll_num());
                    }
                }
            } catch (Exception e) {
                //接口调用异常时，只记录日志，不影响其他数据的正常返回
                log.error("返回请求用户可见的门店或仓库-调用接口获取门店和仓库的商品数量异常", e);
            }
        }

        //门店时处理店长
        if (PARTY_TYPE_STORE.equals(partySearchRequestVO.getParty_type())) {
            //查询门店的店长
            StoreStaffRequestVO staffRequestVO = new StoreStaffRequestVO();
            staffRequestVO.setStore_ids(party_id_list.toArray(new Long[party_id_list.size()]));
            staffRequestVO.setStaff_job(SysCodeEnmu.STAFFJOB_01.getCodeValue());
            try {
                Result<ListResponseVO<StoreStaffResponseVO>> storeLeaderResult = appStoreServices.getStoreStaffList(staffRequestVO);
                if (storeLeaderResult.isSuccess() && storeLeaderResult.getData() != null && UtilValidate.isNotEmpty(storeLeaderResult.getData().getRecords())) {
                    Map<Long, String> map = new HashMap<>(result.getData().getRecords().size());
                    for (StoreStaffResponseVO vo : storeLeaderResult.getData().getRecords()) {
                        map.put(vo.getStore_id(), vo.getStaff_name());
                    }
                    for (PartySearchItemResponseVO vo : result.getData().getRecords()) {
                        vo.setParty_leader(map.get(vo.getParty_id()));
                    }
                }
            } catch (Exception e) {
                //异常时只记日志，不影响其他数据正常返回
                log.error("根据当前请求用户在组织结构树上的位置，返回可见的门店或仓库--调用服务获取门店店长时异常", e);
            }
        }
        return result;
    }

    private void loadVisibleWarehouse(Result<PageResponseVO<PartySearchItemResponseVO>> result, Map<String, Object> params_map, int page_num, int page_size) {
        Page pageCond = new Page<>(page_num, page_size);
        List<WarehouseInfo> pageList = warehouseInfoMapper.selectWarehousePageList(params_map, pageCond);
        if (UtilValidate.isEmpty(pageList)) {
            result.getData().setTotal_count(0L);
            result.getData().setTotal_page(0L);
            return;
        }
        result.getData().setTotal_count(pageCond.getTotal());
        result.getData().setTotal_page(pageCond.getPages());
        if (UtilValidate.isNotEmpty(pageList)) {
            List<PartySearchItemResponseVO> vo_list = new ArrayList<>(pageList.size());
            result.getData().setRecords(vo_list);
            for (WarehouseInfo info : pageList) {
                PartySearchItemResponseVO vo = new PartySearchItemResponseVO();
                vo_list.add(vo);
                vo.setArea(info.getArea_name());
                vo.setCity(info.getCity_name());
                vo.setProvince(info.getProvince_name());
                vo.setParty_id(info.getId());
                //仓库名字是关联组织表取的，查询时存在 province_code字段中
                vo.setParty_name(info.getProvince_code());
            }
        }
    }

    private void loadVisibleStore(Result<PageResponseVO<PartySearchItemResponseVO>> result, Map<String, Object> params_map, int page_num, int page_size) {
        Page pageCond = new Page<>(page_num, page_size);
        List<StoreInfo> storePageList = super.baseMapper.selectStorePageList(params_map, pageCond);
        if (UtilValidate.isEmpty(storePageList)) {
            result.getData().setTotal_count(0L);
            result.getData().setTotal_page(0L);
            return;
        }
        result.getData().setTotal_count(pageCond.getTotal());
        result.getData().setTotal_page(pageCond.getPages());
        List<PartySearchItemResponseVO> vo_list = new ArrayList<>(storePageList.size());
        result.getData().setRecords(vo_list);
        for (StoreInfo info : storePageList) {
            PartySearchItemResponseVO vo = new PartySearchItemResponseVO();
            vo.setArea(info.getArea_name());
            vo.setCity(info.getCity_name());
            vo.setProvince(info.getProvince_name());
            vo.setParty_id(info.getId());
            //仓库名字是关联组织表取的，查询时存在 province_code字段中
            vo.setParty_name(info.getProvince_code());
            vo_list.add(vo);
        }
    }

    protected static List<String> generateStoreWarehouseIdsParam(List<Long> party_id_list) {
        if (UtilValidate.isEmpty(party_id_list)) {
            return null;
        }
        List<String> in_params_list = new ArrayList<>(party_id_list.size() / 1000 + 1);
        StringBuilder params = new StringBuilder();
        for (int i = 0; i < party_id_list.size(); i++) {
            params.append("\'").append(party_id_list.get(i)).append("\'").append(",");
            if ((i + 1) % 1000 == 0) {
                params.deleteCharAt(params.length() - 1);
                in_params_list.add(params.toString());
                params.delete(0, params.length());
            }
        }
        if (params.length() > 0) {
            params.deleteCharAt(params.length() - 1);
            in_params_list.add(params.toString());
        }
        return in_params_list;
    }

}
