package com.hryj.entity.vo.user;

import lombok.Data;

/**
 * @author 罗秋涵
 * @className: UserIdentityCardVO
 * @description: 用户身份证VO类
 * @create 2018/9/10 0010 10:39
 **/
@Data
public class UserIdentityCardVO {

    private Long id;
    /**
     * 用户id
     */
    private Long user_id;
    /**
     * 身份证号
     */
    private String identity_card;
    /**
     * 身份证真实姓名
     */
    private String true_name;
    /**
     * 是否默认
     */
    private Boolean is_default;
}
