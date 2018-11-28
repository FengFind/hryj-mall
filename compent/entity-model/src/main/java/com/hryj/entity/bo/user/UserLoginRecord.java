package com.hryj.entity.bo.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李道云
 * @className: UserLoginRecord
 * @description: 用户登陆记录
 * @create 2018-06-26 9:02
 **/
@Data
@TableName("u_user_login_record")
public class UserLoginRecord extends Model<UserLoginRecord> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 应用标识
     */
    private String app_key;
    /**
     * 用户id
     */
    private Long user_id;
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