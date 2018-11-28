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
 * @className: UserLoginToken
 * @description:
 * @create 2018/7/20 18:16
 **/
@Data
@TableName("u_user_login_token")
public class UserLoginToken extends Model<UserLoginToken> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 应用唯一标识
     */
    private String app_key;
    /**
     * 用户id
     */
    private Long user_id;
    /**
     * 登录令牌
     */
    private String login_token;
    /**
     * 登录时间
     */
    private Date login_time;
    /**
     * 登录设备id
     */
    private String device_id;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
