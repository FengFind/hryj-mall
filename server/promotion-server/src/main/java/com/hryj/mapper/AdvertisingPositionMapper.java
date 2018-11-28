package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.promotion.AdvertisingPosition;
import com.hryj.entity.vo.promotion.advertisingposition.request.SearchAdvertisingPositionRequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AdvertisingPositionItemResponseVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AppAdvertisingPositionResponseVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.JoinPartyAdvertisingItemResponseVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 汪豪
 * @className: AdvertisingPositionMapper
 * @description: 广告位信息mapper
 * @create 2018/7/9 0009 19:08
 **/
@Component
public interface AdvertisingPositionMapper extends BaseMapper<AdvertisingPosition> {

    /**
     * @author 汪豪
     * @methodName: getAdvertisingPositionPageByCondition
     * @methodDesc: 根据条件分页查询广告位信息
     * @description:
     * @param: [searchAdvertisingPositionRequestVO,page]
     * @return List<AdvertisingPositionItemResponseVO>
     * @create 2018-07-09 19:20
     **/
    List<AdvertisingPositionItemResponseVO> getAdvertisingPositionPageByCondition(SearchAdvertisingPositionRequestVO searchAdvertisingPositionRequestVO, Page page);
    
    /**
     * @author 汪豪
     * @methodName: 根据门店或仓库id查找展示时间内的广告位跳转信息
     * @methodDesc: getAdvertisingPositionJumpDataForApp
     * @description: 
     * @param: 
     * @return 
     * @create 2018-07-10 20:30
     **/
    List<AppAdvertisingPositionResponseVO> getAdvertisingPositionJumpDataForApp(@Param("party_id") Long party_id, @Param("currTime") String currTime);

    List<AppAdvertisingPositionResponseVO> getAdvertisingPositionJumpDataForAppPlus(Long party_id);
    /**
     * @author 汪豪
     * @methodName: getJoinPartyAdvertisingByAdvertisingId
     * @methodDesc: 根据广告位id查找参与门店信息
     * @description:
     * @param: [advertising_id]
     * @return List<JoinPartyAdvertisingItemResponseVO>
     * @create 2018-07-16 21:26
     **/
    List<JoinPartyAdvertisingItemResponseVO> getPartyAdvertisingStoreByAdvertisingId(Long advertising_id,Page page);

    /**
     * @author 汪豪
     * @methodName: getJoinPartyAdvertisingByAdvertisingId
     * @methodDesc: 根据广告位id查找参与仓库信息
     * @description:
     * @param: [advertising_id]
     * @return List<JoinPartyAdvertisingItemResponseVO>
     * @create 2018-07-16 21:26
     **/
    List<JoinPartyAdvertisingItemResponseVO> getPartyAdvertisingWarehouseByAdvertisingId(Long advertising_id,Page page);
}
