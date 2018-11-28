package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.storewarehouse.request.SearchPartyRequestVO;
import com.hryj.entity.vo.staff.storewarehouse.response.PartySearchItemResponseVO;
import com.hryj.exception.BizException;
import com.hryj.service.StoreWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 王光银
 * @className: StoreWarehouseController
 * @description:
 * @create 2018/7/4 0004 14:55
 **/
@RestController
@RequestMapping("/storeWarehouse")
public class StoreWarehouseController {

    @Autowired
    private StoreWarehouseService storeWarehouseService;

    /**
     * @author 王光银
     * @methodName: searchPartyPage
     * @methodDesc: 返回当前请求用户能够看到的所有仓库或门店数据
     * @description: 数据权限处理依据当前请求用户在组织结构上的节点位置
     * @param: [partySearchRequestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO<com.hryj.entity.vo.product.partyprod.response.PartySearchItemResponseVO>>
     * @create 2018-06-30 9:20
     **/
    @PostMapping("/searchVisiblePage")
    public Result<PageResponseVO<PartySearchItemResponseVO>> searchVisiblePartyPage(@RequestBody SearchPartyRequestVO partySearchRequestVO) throws BizException {
        return storeWarehouseService.searchVisiblePartyPage(partySearchRequestVO);
    }
}
