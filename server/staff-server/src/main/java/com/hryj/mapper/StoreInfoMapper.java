package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.staff.store.StoreInfo;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.entity.vo.staff.store.request.StoreIdRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreListParamRequestVO;
import com.hryj.entity.vo.staff.store.request.StoreStaffRequestVO;
import com.hryj.entity.vo.staff.store.response.StoreInfoResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreListResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreStaffResponseVO;
import com.hryj.entity.vo.staff.store.response.StoreWhDistributionAreaResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 门店信息表 Mapper 接口
 *
 * @author daitingbo
 * @since 2018-07-02
 */
@Component
public interface StoreInfoMapper extends BaseMapper<StoreInfo> {

    /**
     * @return
     * @author 代廷波
     * @description: 查询门店列表
     * @param:
     * @create 2018/07/03 11:39
     **/
    List<StoreListResponseVO> getStoreList(StoreListParamRequestVO vo, Page page);

    /**
     * 查询指定的门店
     *
     * @param params_map
     * @param page
     * @return
     */
    List<StoreInfo> selectStorePageList(Map<String, Object> params_map, Page<StoreInfo> page);

    /**
     * @return
     * @author 代廷波
     * @description: 根据门店id查询详情
     * @param: null
     * @create 2018/07/05 22:10
     **/
    StoreInfoResponseVO getStoreIdByDet(StoreIdRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 根据门店id查询覆盖的仓库
     * @param: null
     * @create 2018/07/09 21:17
     **/
    List<StoreWhDistributionAreaResponseVO> getWhListByStoreId(StoreIdRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 获取门店员工列表
     * @param: null
     * @create 2018/07/10 19:30
     **/
    List<StoreStaffResponseVO> getStoreStaffList(StoreStaffRequestVO staffRequestVO);

    /**
     * @return
     * @author 代廷波
     * @description: app 根据门店id 获取门店详情
     * @param: null
     * @create 2018/07/12 22:13
     **/
    DeptIdByStoreOrWarehouseResponseVO getAppStoreIdByDet(DeptIdRequestVO vo);

    /**
     * @return
     * @author 代廷波
     * @description: 根据仓库配送区域查询匹配的门店
     * @param: null
     * @create 2018/08/07 16:18
     **/
    int whDistributionAreamatchingStore(@Param("city_ids") List<Long> city_ids);

    /**
     * @return
     * @author 代廷波
     * @description: 根据仓库id查询匹配的门店
     * @param: null
     * @create 2018/08/07 16:18
     **/
    int stopWhDistributionAreaStore(@Param("warehouse_id") Long warehouse_id);
}
