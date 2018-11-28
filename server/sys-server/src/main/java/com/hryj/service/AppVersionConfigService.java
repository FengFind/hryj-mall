package com.hryj.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.sys.AppVersionConfig;
import com.hryj.entity.vo.sys.response.AppVersionResponseVO;
import com.hryj.mapper.AppVersionConfigMapper;
import org.springframework.stereotype.Service;

/**
 * @author 李道云
 * @className: AppVersionConfigService
 * @description: 应用版本配置service
 * @create 2018/7/2 12:17
 **/
@Service
public class AppVersionConfigService extends ServiceImpl<AppVersionConfigMapper,AppVersionConfig> {

    /**
     * @author 李道云
     * @methodName: getAppVersionConfig
     * @methodDesc: 获取应用版本配置信息
     * @description:
     * @param: [app_key, client_type, app_version]
     * @return com.hryj.entity.bo.sys.AppVersionConfig
     * @create 2018-07-02 12:20
     **/
    private AppVersionConfig getAppVersionConfig(String app_key, String client_type, String app_version){
        EntityWrapper<AppVersionConfig> wrapper = new EntityWrapper<>();
        wrapper.eq("app_key",app_key);
        wrapper.eq("client_type",client_type);
        wrapper.eq("app_version",app_version);
        return super.selectOne(wrapper);
    }

    /**
     * @author 李道云
     * @methodName: getAppVersionInfo
     * @methodDesc: 获取应用版本配置信息
     * @description:
     * @param: [app_key, client_type, app_version]
     * @return com.hryj.common.Result<com.hryj.entity.vo.sys.response.AppVersionResponseVO>
     * @create 2018-07-02 13:59
     **/
    public Result<AppVersionResponseVO> getAppVersionInfo(String app_key, String client_type, String app_version){
        //1、查询客户端的版本配置信息
        AppVersionConfig appVersionConfig = this.getAppVersionConfig(app_key, client_type, app_version);
        //2、查询数据库配置的最新版本
        AppVersionConfig latestAppVersion = baseMapper.findLatestAppVersion(app_key, client_type);
        //3、判断是否需要更新
        Boolean update_flag = false;
        Boolean force_update_flag = false;
        if(appVersionConfig !=null && latestAppVersion!=null
                && latestAppVersion.getVersion_sort()>appVersionConfig.getVersion_sort()){
            update_flag = true;
            //4、判断是否需要强制更新
            String latest_version = latestAppVersion.getApp_version();
            //版本号集合:表示需要强制升级的版本号;如果配置为最新版本号;表示所有版本都要强制升级;如果为空表示无需强制升级
            String version_list = latestAppVersion.getVersion_list();
            if(StrUtil.isEmpty(version_list)){
                force_update_flag = false;
            }else if(version_list.equals(latest_version)){
                force_update_flag = true;
            }else if(version_list.contains(app_version)){
                force_update_flag = true;
            }else{
                force_update_flag = false;
            }
        }
        //5、返回应用版本配置信息
        AppVersionResponseVO appVersionResponseVO = new AppVersionResponseVO();
        if(latestAppVersion !=null){
            appVersionResponseVO.setApp_version(latestAppVersion.getApp_version());
            appVersionResponseVO.setDownload_url(latestAppVersion.getDownload_url());
            appVersionResponseVO.setVersion_info(latestAppVersion.getVersion_info());
        }
        appVersionResponseVO.setUpdate_flag(update_flag);
        appVersionResponseVO.setForce_update_flag(force_update_flag);
        return new Result<>(CodeEnum.SUCCESS, appVersionResponseVO);
    }
}
