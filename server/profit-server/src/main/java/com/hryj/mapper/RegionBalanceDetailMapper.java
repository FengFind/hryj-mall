package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.hryj.entity.bo.profit.RegionBalanceDetail;
import com.hryj.entity.vo.profit.request.RegionProfitDetailRequestVO;
import com.hryj.entity.vo.profit.response.RegionBalanceDetailVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李道云
 * @className: RegionBalanceDetailMapper
 * @description: 区域分润明细mapper
 * @create 2018/7/13 17:04
 **/
@Component
public interface RegionBalanceDetailMapper extends BaseMapper<RegionBalanceDetail> {

    /**
     * @author 李道云
     * @methodName: searchRegionProfitDetailList
     * @methodDesc: 分页查询区域分润明细列表
     * @description:
     * @param: [regionProfitDetailRequestVO, page]
     * @return java.util.List<com.hryj.entity.vo.profit.response.RegionBalanceDetailVO>
     * @create 2018-07-13 17:13
     **/
    List<RegionBalanceDetailVO> searchRegionProfitDetailList(RegionProfitDetailRequestVO regionProfitDetailRequestVO, Page page);

}
