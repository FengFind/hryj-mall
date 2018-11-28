package com.hryj.entity.bo.staff.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李道云
 * @className: StaffLoginToken
 * @description: 员工登录令牌
 * @create 2018/7/20 18:09
 **/
@Data
@TableName("sf_staff_login_token")
public class StaffLoginToken extends Model<StaffLoginToken> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 员工id
     */
    private Long staff_id;
    /**
     * app登录令牌
     */
    private String app_login_token;
    /**
     * app登录时间
     */
    private Date app_login_time;
    /**
     * app登录设备id
     */
    private String device_id;
    /**
     * 后台登录令牌
     */
    private String admin_login_token;
    /**
     * 后台登录时间
     */
    private Date admin_login_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
