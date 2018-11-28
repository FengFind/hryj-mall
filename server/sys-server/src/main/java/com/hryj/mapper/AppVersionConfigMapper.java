package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.sys.AppVersionConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: AppVersionConfigMapper
 * @description: 应用版本配置mapper
 * @create 2018/7/2 11:45
 **/
@Component
public interface AppVersionConfigMapper extends BaseMapper<AppVersionConfig> {

    /**
     * @author 李道云
     * @methodName: findLatestAppVersion
     * @methodDesc: 查询最新的应用版本配置
     * @description:
     * @param: [app_key, client_type]
     * @return com.hryj.entity.bo.sys.AppVersionConfig
     * @create 2018-07-02 13:48
     **/
    AppVersionConfig findLatestAppVersion(@Param("app_key") String app_key, @Param("client_type") String client_type);

}
