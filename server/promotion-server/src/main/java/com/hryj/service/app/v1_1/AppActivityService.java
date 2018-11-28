package com.hryj.service.app.v1_1;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.product.response.app.AppProdListItemResponseVO;
import com.hryj.entity.vo.promotion.activity.request.OnlyActivityIdRequestVO;
import com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityDataResponseVO;
import com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityResponseVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.userparty.request.UserPartyRequestVO;
import com.hryj.exception.ServerException;
import com.hryj.feign.ProductFeignClient;
import com.hryj.mapper.ActivityMapper;
import com.hryj.service.util.ServiceInvoker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 汪豪
 * @className: AppActivityService
 * @description:
 * @create 2018/8/15 0015 10:57
 **/
@Slf4j
@Service("v1.1-AppActivityService")
public class AppActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityResponseVO>
     * @author 汪豪
     * @methodName: findPromotionActivity
     * @methodDesc: APP端加载促销活动
     * @description: 不分页
     * @param: [requestVO]
     * @create 2018-06-28 19:44
     **/
    public Result<PageResponseVO<AppPromotionActivityResponseVO>> findPromotionActivity(RequestVO requestVO) {
        log.info("APP端加载促销活动-v1_1");
        Long party_id;
        UserPartyRequestVO userPartyRequestVO = new UserPartyRequestVO();
        userPartyRequestVO.setLogin_token(requestVO.getLogin_token());
        try {
            party_id = ServiceInvoker.getUserUniqueParty(productFeignClient, userPartyRequestVO);
        }catch (ServerException e){
            log.error("APP端加载促销活动-调用接口获取用户默认门店id异常", e);
            return new Result<>(CodeEnum.FAIL_SERVER, "调用接口获取用户默认门店id失败");
        }
        if (party_id == null || party_id == 0){
            new Result<>(CodeEnum.FAIL_BUSINESS, "您周围还没有咱们的小店");
        }
        log.info("用户默认门店id:"+party_id);
        String currTime = DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        List<AppPromotionActivityResponseVO> activityItems = activityMapper.getActivityListToApp(party_id, currTime);

        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(activityItems);
        log.info("APP端加载活动信息:result=====：" + JSON.toJSONString(activityItems));
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.promotion.activity.response.AppPromotionActivityDataResponseVO>
     * @author 汪豪
     * @methodName: getPromotionActivityData
     * @methodDesc: APP端加载活动的详细信息
     * @description:
     * @param: [onlyActivityIdRequestVO]
     * @create 2018-06-29 11:52
     **/
    public Result<AppPromotionActivityDataResponseVO> getPromotionActivityData(OnlyActivityIdRequestVO onlyActivityIdRequestVO) {
        log.info("APP端加载活动的详细信息-v1_1");
        log.info("party_id:"+onlyActivityIdRequestVO.getParty_id());
        Long party_id;
        UserPartyRequestVO userPartyRequestVO = new UserPartyRequestVO();
        if(onlyActivityIdRequestVO.getParty_id() != null && onlyActivityIdRequestVO.getParty_id() != 0){
            party_id = onlyActivityIdRequestVO.getParty_id();
        }else{
            userPartyRequestVO.setLogin_token(onlyActivityIdRequestVO.getLogin_token());
            try {
                party_id = ServiceInvoker.getUserUniqueParty(productFeignClient, userPartyRequestVO);
            }catch (ServerException e){
                log.error("APP端加载促销活动-调用接口获取用户默认门店id异常", e);
                return new Result<>(CodeEnum.FAIL_SERVER, "调用接口获取用户默认门店id失败");
            }
        }

        if (party_id == null || party_id == 0){
            new Result<>(CodeEnum.FAIL_BUSINESS, "您周围还没有咱们的小店");
        }
        log.info("用户默认门店id:"+party_id);
        AppPromotionActivityDataResponseVO promotionActivityData = activityMapper.getActivityDataByActivityIdToApp(onlyActivityIdRequestVO.getActivity_id());
        if (promotionActivityData == null) {
            return new Result<>(CodeEnum.FAIL_BUSINESS, "id为" + onlyActivityIdRequestVO.getActivity_id() + "的活动不存在");
        }
        Long startDate = DateUtil.parse(promotionActivityData.getStart_date()).getTime();
        Long endDate = DateUtil.parse(promotionActivityData.getEnd_date()).getTime();
        Long currDate = new Date().getTime();
        if ("0".equals(promotionActivityData.getActivity_status()) || currDate < startDate || currDate > endDate) {
            promotionActivityData.setActivity_status("0");
            log.info("APP端加载活动的详细信息：result=====" + JSON.toJSONString(promotionActivityData));
            return new Result<>(CodeEnum.SUCCESS, promotionActivityData);
        }
        if ("01".equals(promotionActivityData.getActivity_style())) {
            List<AppProdListItemResponseVO> productDataList = activityMapper.getActivityProductDataListByPartyIdToApp(onlyActivityIdRequestVO.getActivity_id(),party_id);
            for (int i = 0; i < productDataList.size(); i++){
                if(NumberUtil.equals(new BigDecimal(productDataList.get(i).getInventory_quantity()),BigDecimal.ZERO)){
                    productDataList.remove(i);
                    i--;
                }
            }
            promotionActivityData.setProduct_list(productDataList);
        }
        log.info("APP端加载活动的详细信息：result=====" + JSON.toJSONString(promotionActivityData));
        return new Result<>(CodeEnum.SUCCESS, promotionActivityData);
    }
}
