package com.hryj.entity.vo.staff.user.request;

import lombok.Data;

/**
 * 员工用户信息表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
public class StaffUserExcelVO{


    /**
     * 手机号码
     */
    private String phone_num;

    private Boolean phone_num_validation = false;

    private Boolean phone_num_only=false;


    /**
     * 员工姓名
     */
    private String staff_name;
    private Boolean staff_name_validation = false;
    /**
     * 身份证号码
     */
    private String id_card;

    private Boolean id_card_validation = false;

    private Boolean id_card_only = false;
    /**
     * 性别
     */
    private String sex;
    private Boolean sex_validation=false;

    /**
     * 性别保存数据库的值
     */
    private String sex_val;
    /**
     * 学历
     */
    private String education;
    private Boolean education_validation = false;

    /**
     * 学历保存数据库的值
     */
    private String education_val;

    /**
     * 邮箱
     */
    private String email;
    private Boolean email_validation = false;
    /**
     * 家庭地址
     */
    private String home_address;
    private Boolean home_address_validation = false;
    /**
     * 紧急联系人
     */
    private String contact_name;
    private Boolean contact_name_validation = false;
    /**
     * 紧急联系电话
     */
    private String contact_tel;
    private Boolean contact_tel_validation = false;
    /**
     * 角色
     */
    private String role_name;
    private Boolean role_name_validation = false;
    private Boolean role_name_db_flag = false;


}
