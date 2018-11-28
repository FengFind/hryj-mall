package com.hryj.entity.bo.sys;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李道云
 * @className: AppStartupRecord
 * @description: 应用启动记录
 * @create 2018/6/27 19:56
 **/
@Data
@TableName("sys_app_startup_record")
public class AppStartupRecord extends Model<AppStartupRecord> {


    @TableId(value="id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 应用标识
     */
    private String app_key;
    /**
     * 请求来源
     */
    private String call_source;
    /**
     * 应用渠道
     */
    private String app_channel;
    /**
     * 应用版本号
     */
    private String app_version;
    /**
     * 设备id
     */
    private String device_id;
    /**
     * 设备型号
     */
    private String device_model;
    /**
     * 操作系统版本号
     */
    private String os_version;
    /**
     * 设备ip
     */
    private String device_ip;
    /**
     * 启动时间
     */
    private Date startup_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
