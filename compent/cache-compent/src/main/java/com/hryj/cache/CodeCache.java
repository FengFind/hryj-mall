package com.hryj.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.hryj.entity.vo.sys.response.CodeInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author 李道云
 * @className: CacheTable
 * @description: 字典代码缓存
 * @create 2018/6/22 21:20
 **/
@Component
public class CodeCache {

    private static CodeCache codeCache;

    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        codeCache = this;
        codeCache.redisService = this.redisService;
    }

/***************************************************系统代码表缓存*******************************************************/

    /**
     * @author 李道云
     * @methodName: getCodeList
     * @methodDesc: 根据type获取code列表
     * @description:
     * @param: [codeType]
     * @return java.util.List<java.util.Map>
     * @create 2018-07-02 16:51
     **/
    public static List<CodeInfoVO> getCodeList(String codeType){
        String value = codeCache.redisService.get(CacheGroup.CODE.getValue(),codeType);
        if(StrUtil.isEmpty(value)){
            return null;
        }
        return JSON.parseArray(value,CodeInfoVO.class);
    }

    /**
     * @author 李道云
     * @methodName: getValueByKey
     * @methodDesc: 根据key获取code值
     * @description:
     * @param: [codeType, codeKey]
     * @return java.lang.String
     * @create 2018-07-02 16:51
     **/
    public static String getValueByKey(String codeType, String codeKey){
        List<CodeInfoVO> codeList = getCodeList(codeType);
        if(CollectionUtil.isNotEmpty(codeList)){
            for (CodeInfoVO codeInfoVO: codeList){
                if(codeKey.equals(codeInfoVO.getCode_key())){
                    return codeInfoVO.getCode_value();
                }
            }
        }
        return "";
    }

    /**
     * @author 李道云
     * @methodName: getNameByKey
     * @methodDesc: 根据key获取code名称
     * @description:
     * @param: [codeType, codeKey]
     * @return java.lang.String
     * @create 2018-07-02 16:51
     **/
    public static String getNameByKey(String codeType, String codeKey){
        List<CodeInfoVO> codeList = getCodeList(codeType);
        if(CollectionUtil.isNotEmpty(codeList)){
            for (CodeInfoVO codeInfoVO: codeList){
                if(codeKey.equals(codeInfoVO.getCode_key())){
                    return codeInfoVO.getCode_name();
                }
            }
        }
        return "";
    }

    /**
     * @author 李道云
     * @methodName: getNameByValue
     * @methodDesc: 根据value获取code名称
     * @description:
     * @param: [codeType, codeValue]
     * @return java.lang.String
     * @create 2018-07-02 16:51
     **/
    public static String getNameByValue(String codeType, String codeValue){
        List<CodeInfoVO> codeList = getCodeList(codeType);
        if(CollectionUtil.isNotEmpty(codeList)){
            for (CodeInfoVO codeInfoVO: codeList){
                if(codeValue.equals(codeInfoVO.getCode_value())){
                    return codeInfoVO.getCode_name();
                }
            }
        }
        return "";
    }

}
