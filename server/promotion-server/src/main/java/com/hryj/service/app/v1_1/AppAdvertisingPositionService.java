package com.hryj.service.app.v1_1;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.promotion.ActivityInfo;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.promotion.advertisingposition.response.AppAdvertisingPositionResponseVO;
import com.hryj.entity.vo.userparty.request.UserPartyRequestVO;
import com.hryj.exception.ServerException;
import com.hryj.feign.ProductFeignClient;
import com.hryj.mapper.ActivityMapper;
import com.hryj.mapper.AdvertisingPositionMapper;
import com.hryj.service.util.ServiceInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 汪豪
 * @className: AdvertisingPositionService
 * @description: 广告相关Service
 * @create 2018/7/9 0009 19:07
 **/
@Slf4j
@Service("v1.1-AppAdvertisingPositionService")
public class AppAdvertisingPositionService {

    @Autowired
    private AdvertisingPositionMapper advertisingPositionMapper;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ProductFeignClient productFeignClient;
    /**
     * @author 汪豪
     * @methodName: findAdvertisingPosition
     * @methodDesc: APP端加载广告位
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<java.util.List<com.hryj.entity.vo.promotion.advertisingposition.response.AppAdvertisingPositionResponseVO>>
     * @create 2018-06-29 11:13
     **/
    public Result<PageResponseVO<AppAdvertisingPositionResponseVO>> findAdvertisingPosition(RequestVO requestVO) {
        log.info("APP端加载广告位-v1_1");
        Long party_id;
        UserPartyRequestVO userPartyRequestVO = new UserPartyRequestVO();
        userPartyRequestVO.setLogin_token(requestVO.getLogin_token());
        try {
            party_id = ServiceInvoker.getUserUniqueParty(productFeignClient, userPartyRequestVO);
        }catch (ServerException e){
            log.error("APP端加载广告位-调用接口获取用户默认门店id异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "调用接口获取用户默认门店id失败");
        }

        if (party_id == null || party_id == 0){
            new Result<>(CodeEnum.FAIL_BUSINESS, "您周围还没有咱们的小店");
        }
        log.info("用户默认门店id:"+party_id);
        String currTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        List<AppAdvertisingPositionResponseVO> appResponseVO = advertisingPositionMapper.getAdvertisingPositionJumpDataForApp(party_id, currTime);
        if(CollectionUtil.isNotEmpty(appResponseVO)){
            for (AppAdvertisingPositionResponseVO vo : appResponseVO) {
                if("03".equals(vo.getJump_type())){
                    ActivityInfo activityInfo = activityMapper.selectById(Long.parseLong(vo.getJump_value()));
                    vo.setJump_value(activityInfo.getTemplete_data()+"?activity_id="+vo.getJump_value()+"&login_token="+requestVO.getLogin_token());
                }
            }
        }

        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(appResponseVO);
        log.info("APP端加载广告位-result=====" + JSON.toJSONString(appResponseVO));
        return new Result<>(CodeEnum.SUCCESS,pageResponseVO);
    }
}
