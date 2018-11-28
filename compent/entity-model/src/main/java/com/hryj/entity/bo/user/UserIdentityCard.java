package com.hryj.entity.bo.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 罗秋涵
 * @className: UserCardsInfo
 * @description: 用户身份证信息
 * @create 2018/9/10 0010 10:38
 **/
@Data
@TableName("u_user_identity_card")
public class UserIdentityCard extends Model<UserIdentityCard> {

    @TableId(value = "id", type = IdType.ID_WORKER)
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


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
