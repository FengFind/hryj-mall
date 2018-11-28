package com.hryj.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CacheGroup;
import com.hryj.cache.RedisService;
import com.hryj.entity.bo.sys.CityArea;
import com.hryj.entity.bo.sys.Code;
import com.hryj.entity.bo.sys.CodeType;
import com.hryj.entity.bo.sys.PaymentConfig;
import com.hryj.entity.vo.sys.response.CodeInfoVO;
import com.hryj.mapper.CityAreaMapper;
import com.hryj.mapper.CodeMapper;
import com.hryj.mapper.CodeTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李道云
 * @className: CodeService
 * @description:
 * @create 2018/6/23 8:53
 **/
@Service
public class CodeService extends ServiceImpl<CodeMapper,Code> {

    @Autowired
    private CodeTypeMapper codeTypeMapper;

    @Autowired
    private CityAreaMapper cityAreaMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private PaymentConfigService paymentConfigService;

    /**
     * @author 李道云
     * @description: 刷新code的缓存
     * @param: []
     * @return void
     * @create 2018-06-23 8:59
     **/
    public void flushCodeCache(){
        //将字典表数据加载到code缓存
        List<CodeType> codeTypeList = codeTypeMapper.selectList(null);
        if(CollectionUtil.isNotEmpty(codeTypeList)){
            for (CodeType ct : codeTypeList){
                String code_type = ct.getCode_type();
                List<CodeInfoVO> codeList = baseMapper.findCodeListByType(code_type);
                if(CollectionUtil.isNotEmpty(codeList)){
                    redisService.put(CacheGroup.CODE.getValue(),code_type,JSON.toJSONString(codeList),null);
                }
            }
        }
        //将所有城市区域加载到code缓存
        List<CodeInfoVO> cityAreaList = new ArrayList<>();
        List<CityArea> allCityAreaList = cityAreaMapper.selectList(null);
        if(CollectionUtil.isNotEmpty(allCityAreaList)){
            for (CityArea cityArea: allCityAreaList){
                if(StrUtil.isNotEmpty(cityArea.getShort_name())){
                    CodeInfoVO codeInfoVO = new CodeInfoVO();
                    codeInfoVO.setCode_key("S"+String.valueOf(cityArea.getId()));
                    codeInfoVO.setCode_value(String.valueOf(cityArea.getId()));
                    codeInfoVO.setCode_name(cityArea.getCity_name());
                    cityAreaList.add(codeInfoVO);
                }
            }
            redisService.put(CacheGroup.CODE.getValue(),"CityArea",JSON.toJSONString(cityAreaList),null);
        }
        //将支付配置信息加载到code缓存
        List<CodeInfoVO> payConfigList = new ArrayList<>();
        List<PaymentConfig> paymentConfigList = paymentConfigService.findPaymentConfigList();
        if(CollectionUtil.isNotEmpty(paymentConfigList)){
            for (PaymentConfig paymentConfig: paymentConfigList){
                CodeInfoVO codeInfoVO = new CodeInfoVO();
                codeInfoVO.setCode_key("S"+paymentConfig.getApp_key()+paymentConfig.getParty_id()+paymentConfig.getPayment_method());
                codeInfoVO.setCode_value(paymentConfig.getPayment_config());
                codeInfoVO.setCode_name(paymentConfig.getDescription());
                payConfigList.add(codeInfoVO);
            }
            redisService.put(CacheGroup.CODE.getValue(),"PaymentConfig",JSON.toJSONString(payConfigList),null);
        }
    }
}
