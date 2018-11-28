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
 * @className: AppVersionConfig
 * @description: 应用版本控制
 * @create 2018/6/27 19:53
 **/
@Data
@TableName("sys_app_version_config")
public class AppVersionConfig extends Model<AppVersionConfig> {

    @TableId(value="id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 应用标识
     */
    private String app_key;
    /**
     * 客户端类型
     */
    private String client_type;
    /**
     * 版本号:1.0.1,统一为3位
     */
    private String app_version;
    /**
     * 版本序号:用于比较版本号大小
     */
    private Integer version_sort;
    /**
     * 版本号集合:表示需要强制升级的版本号,如果配置为最新版本号,表示所有版本都要强制升级,如果为空表示无需强制升级
     */
    private String version_list;
    /**
     * 版本更新说明
     */
    private String version_info;
    /**
     * 生效时间
     */
    private Date effect_time;
    /**
     * 下载地址
     */
    private String download_url;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
