package com.hryj.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.hryj.cache.CodeCache;
import com.hryj.common.Result;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.sys.response.AppVersionResponseVO;
import com.hryj.feign.StaffFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.service.AppStartupRecordService;
import com.hryj.service.AppVersionConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 李道云
 * @className: SysController
 * @description:
 * @create 2018/7/2 11:42
 **/
@Slf4j
@RestController
@RequestMapping(value = "/sys")
public class SysController {

    @Autowired
    private AppVersionConfigService appVersionConfigService;

    @Autowired
    private AppStartupRecordService appStartupRecordService;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private StaffFeignClient staffFeignClient;

    /**
     * @author 李道云
     * @methodName: getAppVersionInfo
     * @methodDesc: 获取应用版本配置信息
     * @description:
     * @param: [requestVO]
     * @return com.hryj.common.Result<com.hryj.entity.vo.sys.response.AppVersionResponseVO>
     * @create 2018-07-02 14:14
     **/
    @PostMapping("/getAppVersionInfo")
    public Result<AppVersionResponseVO> getAppVersionInfo(@RequestBody final RequestVO requestVO){
        final String app_key = requestVO.getApp_key();
        String app_version = requestVO.getApp_version();
        String call_source = requestVO.getCall_source();
        String client_type = "";
        if(CodeCache.getValueByKey("CallSource","S01").equals(call_source)){
            client_type = CodeCache.getValueByKey("ClientType","S01");//01-安卓端
        }
        if(CodeCache.getValueByKey("CallSource","S02").equals(call_source)){
            client_type = CodeCache.getValueByKey("ClientType","S02");//02-IOS端
        }
        //1、获取应用版本配置信息
        Result<AppVersionResponseVO> result = appVersionConfigService.getAppVersionInfo(app_key,client_type,app_version);
        ThreadUtil.excAsync(() -> {
            //2、保存应用启动记录
            appStartupRecordService.saverAppStartupRecord(requestVO);
            //3、刷新登录缓存信息
            String login_token = requestVO.getLogin_token();
            if(StrUtil.isNotEmpty(login_token)){
                //01：HRYJ-USER-OLD-红瑞颐家用户端APP-老年版
                if(app_key.equals(CodeCache.getValueByKey("AppKey","S01"))){
                    userFeignClient.flushUserLoginVO(requestVO);
                }
                //02：HRYJ-USER-YOUNG-红瑞颐家用户端APP-年轻版
                if(app_key.equals(CodeCache.getValueByKey("AppKey","S02"))){
                    userFeignClient.flushUserLoginVO(requestVO);
                }
                //03：HRYJ-STORE-红瑞颐家门店端APP
                if(app_key.equals(CodeCache.getValueByKey("AppKey","S03"))){
                    staffFeignClient.flushStaffApploginVO(requestVO);
                }
            }
        },false);
        return result;
    }

}
