package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.staff.store.request.StoreIdRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreStaffRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreWhDistributionAreaResponseVO;
import com.hryj.entity.vo.staff.team.StaffStoreWhVO;
import com.hryj.exception.GlobalException;
import com.hryj.service.AppStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 代廷波
 * @className: AppStoreController
 * @description:
 * @create 2018/7/12 0012-21:10
 **/
@RestController
@RequestMapping("/app/store")
public class AppStoreController {

    @Autowired
    private AppStoreService appStoreService;

    /**
     * @author 代廷波
     * @description: 获取门店员工列表
     * @param: staffRequestVO
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO>>
     * @create 2018/07/10 19:27
     **/
    @PostMapping("/getStoreStaffList")
    public Result<ListResponseVO<StoreStaffResponseVO>> getStoreStaffList(@RequestBody StoreStaffRequestVO staffRequestVO){
        return appStoreService.getStoreStaffList(staffRequestVO);
    }

    /**
     * @author 代廷波
     * @description: 根据门店id返回覆盖仓库id
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.staff.dept.response.DeptDetAndDistributionAreaResponseVO>
     * @create 2018/07/09 19:17
     **/
    @PostMapping("/getWhListByStoreId")
    public Result<ListResponseVO<StoreWhDistributionAreaResponseVO>> getWhListByStoreId(@RequestBody StoreIdRequestVO storeIdRequestVO) throws GlobalException {
        return appStoreService.getWhListByStoreId(storeIdRequestVO);
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
    @PostMapping("/getStoreIdListByWhId")
    public Result<StaffStoreWhVO> getStoreIdListByWhId(@RequestParam("wh_id") Long wh_id){
        return appStoreService.getStoreIdListByWhId(wh_id);
    }
}
