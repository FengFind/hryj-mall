package com.hryj.entity.bo.staff.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 员工用户信息表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_staff_user_info")
public class StaffUserInfo extends Model<StaffUserInfo> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 员工账号
     */
    private String staff_account;
    /**
     * 员工类型;01-普通员工,02-内置员工,03-超级管理员
     */
    private String staff_type;
    /**
     * 手机号码
     */
    private String phone_num;
    /**
     * 登录密码
     */
    private String login_pwd;
    /**
     * 登录失败次数
     */
    private Integer login_fail_num;
    /**
     * 员工姓名
     */
    private String staff_name;
    /**
     * 身份证号码
     */
    private String id_card;
    /**
     * 性别
     */
    private String sex;
    /**
     * 学历
     */
    private String education;
    /**
     * 员工照片
     */
    private String staff_pic;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 家庭地址
     */
    private String home_address;
    /**
     * 紧急联系人
     */
    private String contact_name;
    /**
     * 紧急联系电话
     */
    private String contact_tel;
    /**
     * 推荐码:6位的数字
     */
    private String referral_code;
    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    @TableField(exist = false)
    private String role_name;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
