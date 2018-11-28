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
 * @className: UserInfo
 * @description: 用户信息
 * @create 2018-06-26 9:02
 **/
@Data
@TableName("u_user_info")
public class UserInfo extends Model<UserInfo> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 手机号码
     */
    private String phone_num;
    /**
     * 推荐码
     */
    private String referral_code;
    /**
     * 推荐员工id
     */
    private Long staff_id;
    /**
     * 推荐员工姓名
     */
    private String staff_name;
    /**
     * 推荐员工手机号
     */
    private String staff_phone;
    /**
     * 推荐员工岗位名称
     */
    private String staff_job_name;
    /**
     * 推荐门店id
     */
    private Long store_id;
    /**
     * 推荐门店名称
     */
    private String store_name;
    /**
     * 推荐部门的路径
     */
    private String dept_path;
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