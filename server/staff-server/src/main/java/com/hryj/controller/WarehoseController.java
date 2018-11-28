package com.hryj.controller;

import com.hryj.common.Result;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.staff.warehouse.request.*;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseCityAreaResponseVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseInfoResponseVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseListResponseVO;
import com.hryj.exception.GlobalException;
import com.hryj.service.WarehoseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 代廷波
 * @className: Warehosecontroller
 * @description: 仓库管理
 * @create 2018/6/27 0027-17:08
 **/
@RestController
@RequestMapping("/warehouse")
public class WarehoseController {


    @Resource
    private WarehoseService warehoseService;

    
    /**
     * @author 代廷波
     * @description: 添加仓库
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/27 18:44
     **/

    @PostMapping("/saveWarehose")
    public Result saveWarehose(@RequestBody WarehouseInfoRequestVO vo) throws GlobalException {

        return warehoseService.saveWarehose(vo);

    }

    /**
     * @author 代廷波
     * @description: 修改仓库
     * @param: vo
     * @return com.hryj.common.Result
     * @create 2018/06/27 18:44
     **/

    @PostMapping("/updateWarehose")
    public Result updateWarehose(@RequestBody WarehouseInfoRequestVO vo) throws GlobalException {

        return warehoseService.updateWarehose(vo);

    }


    /**
     * @author 代廷波
     * @description: 根据仓库id查询详情
     * @param: warehose_id 仓库id
     * @return com.hryj.common.Result
     * @create 2018/06/27 19:24
     **/
    @PostMapping("/getWarehoseByIdDet")
    public Result<WarehouseInfoResponseVO> getWarehoseByIdDet(@RequestBody WarehouseIdRequestVO vo) throws GlobalException {


        return warehoseService.getWarehoseByIdDet(vo);

    }


    /**
     * @author 代廷波
     * @description: 修改仓库状态
    * @param: warehose_id 仓库id
     * @param: status_value 仓库状态:1-正常,0-关闭
     * @return com.hryj.common.Result
     * @create 2018/07/04 10:55
     **/
    @PostMapping("/updateWarehoseStatus")
    public Result updateWarehoseStatus(@RequestBody WarehouseUpdateStatusRequestVO vo) throws GlobalException {

        return warehoseService.updateWarehoseStatus(vo);

    }


    /**
    * @author 代廷波
    * @description: 仓库列表查询
    * @param: vo
    * @return
    * @create 2018/06/27 19:39
    **/
    @PostMapping("/getWarehoseList")
    public Result<PageResponseVO<WarehouseListResponseVO>> getWarehoseList(@RequestBody WarehuoseParamRequestVO vo)throws GlobalException{

        return  warehoseService.getWarehoseList(vo);

    }
    /**
     * @author 代廷波
     * @description: 根据省ia获取仓库的配送区域
     * @param: vo
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.staff.warehouse.response.WarehouseCityAreaResponseVO>>
     * @create 2018/07/17 11:52
     **/
    @PostMapping("/getWarehoseCityAreaList")
    public Result<ListResponseVO<WarehouseCityAreaResponseVO>> getWarehoseCityAreaList(@RequestBody WareHouseCityIdsRequestVO vo)throws GlobalException{

        return  warehoseService.getWarehoseCityAreaList(vo);

    }






}
