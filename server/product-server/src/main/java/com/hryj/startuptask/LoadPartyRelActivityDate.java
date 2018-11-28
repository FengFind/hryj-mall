package com.hryj.startuptask;

import com.hryj.service.util.cacheutil.PartyActivityMonitorCacheUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * @author 王光银
 * @className: LoadPartyRelActivityDate
 * @description: 服务启动时加载所有门店相关的活动日期数据
 * @create 2018/8/31 0031 11:20
 **/
@Component
public class LoadPartyRelActivityDate implements ApplicationRunner, Ordered {

    @Override
    public int getOrder() {
        return 1;
    }

    @Override
    public void run(ApplicationArguments applicationArguments) {
        PartyActivityMonitorCacheUtil.checkLoadFromDB();
    }
}
