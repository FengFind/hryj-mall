package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.store.request.StoreIdRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreInfoRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreListParamRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreUpdateStatusRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreInfoResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreListResponseVO;
import com.hryj.exception.GlobalException;
import com.hryj.service.StoreService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 代廷波
 * @className: Storecontroller
 * @description:门店管理
 * @create 2018/6/27 0027-17:08
 **/
@RestController
@RequestMapping("/store")
public class StoreController {


    @Resource
    private StoreService storeService;

    
    /**
     * @author 代廷波
     * @description: 添加门店
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/27 18:44
     **/

    @PostMapping("/saveStore")
    public Result saveStore(@RequestBody StoreInfoRequestVO vo) throws GlobalException {

        return storeService.saveStore(vo);

    }

    /**
     * @author 代廷波
     * @description: 修改门店
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/27 18:44
     **/

    @PostMapping("/updateStore")
    public Result updateStore(@RequestBody StoreInfoRequestVO vo) throws GlobalException {

        return storeService.updateStore(vo);

    }


    /**
     * @author 代廷波
     * @description: 根据门店id查询详情
     * @param: Store_id 门店id
     * @return com.hryj.common.Result
     * @create 2018/06/27 19:24
     **/
    @PostMapping("/getStoreIdByDet")
    public Result<StoreInfoResponseVO> getStoreIdByDet(@RequestBody StoreIdRequestVO vo) throws GlobalException {


        return storeService.getStoreIdByDet(vo);

    }


    /**
     * @author 代廷波
     * @description: 修改门店状态
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/27 19:53
     **/
    @PostMapping("/updateStoreStatus")
    public Result updateStoreStatus(@RequestBody StoreUpdateStatusRequestVO vo) throws GlobalException {

        return storeService.updateStoreStatus(vo);

    }


    /**
    * @author 代廷波
    * @description: 门店列表查询
    * @param: vo
    * @return
    * @create 2018/06/27 19:39
    **/
    @PostMapping("/getStoreList")
    public Result<PageResponseVO<StoreListResponseVO>> getStoreList(@RequestBody StoreListParamRequestVO vo)throws GlobalException{
        return  storeService.getStoreList(vo);

    }




}
