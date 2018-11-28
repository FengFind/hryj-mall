package com.hryj.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.sys.AppStartupRecord;
import com.hryj.entity.vo.RequestVO;
import com.hryj.mapper.AppStartupRecordMapper;
import org.springframework.stereotype.Service;

/**
 * @author 李道云
 * @className: AppStartupRecordService
 * @description: 应用启动记录service
 * @create 2018/7/2 14:08
 **/
@Service
public class AppStartupRecordService extends ServiceImpl<AppStartupRecordMapper,AppStartupRecord> {

    /**
     * @author 李道云
     * @methodName: saverAppStartupRecord
     * @methodDesc: 保存应用启动记录
     * @description:
     * @param: [requestVO]
     * @return void
     * @create 2018-07-02 14:13
     **/
    public void saverAppStartupRecord(RequestVO requestVO){
        AppStartupRecord appStartupRecord = new AppStartupRecord();
        BeanUtil.copyProperties(requestVO,appStartupRecord);
        appStartupRecord.setStartup_time(DateUtil.date());
        super.insert(appStartupRecord);
    }
}
