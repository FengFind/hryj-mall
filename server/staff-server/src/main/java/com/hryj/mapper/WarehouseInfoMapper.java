package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.entity.bo.staff.warehouse.WarehouseInfo;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.entity.vo.staff.warehouse.request.WareHouseCityIdsRequestVO;
import com.hryj.entity.vo.staff.warehouse.request.WarehouseIdRequestVO;
import com.hryj.entity.vo.staff.warehouse.request.WarehuoseParamRequestVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseCityAreaResponseVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseInfoResponseVO;
import com.hryj.entity.vo.staff.warehouse.response.WarehouseListResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 仓库信息表 Mapper 接口
 *
 * @author daitingbo
 * @since 2018-07-02
 */
@Component
public interface WarehouseInfoMapper extends BaseMapper<WarehouseInfo> {
    /**
     * @return
     * @author 代廷波
     * @description: 查询仓库列表
     * @param: null
     * @create 2018/07/03 11:47
     **/
    List<WarehouseListResponseVO> getWarehoseList(WarehuoseParamRequestVO vo, Page page);

    /**
     * @return Page
     * @author 王光银
     * @description: 查询指定的门店
     * @param: warehouse_id_list
     * @create 2018/07/03 11:47
     **/
    List<WarehouseInfo> selectWarehousePageList(Map<String, Object> params_map, Page<StoreInfo> page);

    /**
     * @return
     * @author 代廷波
     * @description: 根据仓库id获取仓库详情
     * @param: null
     * @create 2018/07/06 20:15
     **/
    WarehouseInfoResponseVO getWarehoseByIdDet(WarehouseIdRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: app - 根据组织id获取仓库详情 getAppWarehouseIdByDet
     * @param: null
     * @create 2018/07/13 10:23
     **/
    DeptIdByStoreOrWarehouseResponseVO getAppWarehouseIdByDet(DeptIdRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 根据省ia获取仓库的配送区域
     * @param: ids:省ids
     * @param: wareHouse_id:仓库id
     * @create 2018/07/17 11:58
     *
     * @param
     * */
    List<WarehouseCityAreaResponseVO> getWarehoseCityAreaList(WareHouseCityIdsRequestVO vo);

    /**
     * @author 李道云
     * @methodName: getStoreIdListByWhId
     * @methodDesc: 根据仓库id查询门店id列表
     * @description:
     * @param: [wh_id]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-07-25 21:10
     **/
    Map<String,Object> getStoreIdListByWhId(@Param("wh_id") Long wh_id);

}
