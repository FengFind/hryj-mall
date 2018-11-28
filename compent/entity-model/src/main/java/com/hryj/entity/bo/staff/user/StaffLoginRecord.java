package com.hryj.entity.bo.staff.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工登陆记录表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_staff_login_record")
public class StaffLoginRecord extends Model<StaffLoginRecord> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 员工id
     */
    private Long staff_id;
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
     * 登录ip
     */
    private String login_ip;
    /**
     * 登录时间
     */
    private Date login_time;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
