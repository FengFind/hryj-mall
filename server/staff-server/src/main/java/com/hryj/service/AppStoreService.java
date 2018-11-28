package com.hryj.service;

import com.alibaba.fastjson.JSON;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.store.request.StoreIdRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreStaffRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreWhDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.mapper.StoreInfoMapper;
import com.hryj.mapper.WarehouseInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author 代廷波
 * @className: AppStoreService
 * @description: 门店app服务
 * @create 2018/7/12 0012-21:13
 **/

@Service
@Slf4j
public class AppStoreService {

    @Autowired
    private StoreInfoMapper storeInfoMapper;

    @Autowired
    private WarehouseInfoMapper warehouseInfoMapper;

    /**
     * @author 代廷波
     * @description: 获取门店员工列表
     * @param: staffRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO>>
     * @create 2018/07/10 19:27
     **/
    public Result<ListResponseVO<StoreStaffResponseVO>> getStoreStaffList(StoreStaffRequestVO staffRequestVO) {
        List<StoreStaffResponseVO> staffList = storeInfoMapper.getStoreStaffList(staffRequestVO);
        ListResponseVO listResponseVO = new ListResponseVO();
        listResponseVO.setRecords(staffList);
        log.info("根据门店id查询覆盖的仓库,listResponseVO=" + JSON.toJSONString(listResponseVO));
        return  new Result(CodeEnum.SUCCESS, listResponseVO);
    }

    /**
     * @author 代廷波
     * @description: 根据门店id查询覆盖的仓库
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO>
     * @create 2018/07/09 19:17
     **/
    public Result<ListResponseVO<StoreWhDistributionAreaResponseVO>> getWhListByStoreId(StoreIdRequestVO storeIdRequestVO) {
        List<StoreWhDistributionAreaResponseVO> whList = storeInfoMapper.getWhListByStoreId(storeIdRequestVO);
        ListResponseVO listResponseVO = new ListResponseVO();
        listResponseVO.setRecords(whList);
        log.info("根据门店id查询覆盖的仓库,listResponseVO=" + JSON.toJSONString(listResponseVO));
        return new Result(CodeEnum.SUCCESS, listResponseVO);
    }

    /**
     * @author 李道云
     * @methodName: getStoreIdListByWhId
     * @methodDesc: 根据仓库id查询门店id列表
     * @description:
     * @param: [wh_id]
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.team.StaffStoreWhVO>
     * @create 2018-07-25 20:51
     **/
    public Result<StaffStoreWhVO> getStoreIdListByWhId(Long wh_id){
        if(wh_id ==null){
            return new Result(CodeEnum.SUCCESS, "仓库id不能为空");
        }
        StaffStoreWhVO staffStoreWhVO = new StaffStoreWhVO();
        Map store_map = warehouseInfoMapper.getStoreIdListByWhId(wh_id);
        if(store_map !=null){
            String storeIdList = (String) store_map.get("storeIdList");
            staffStoreWhVO.setStoreIdList(storeIdList);
            staffStoreWhVO.setWhIdList(wh_id.toString());
        }
        log.info("根据仓库id查询门店id列表,staffStoreWhVO=" + JSON.toJSONString(staffStoreWhVO));
        return new Result(CodeEnum.SUCCESS, staffStoreWhVO);
    }
}
