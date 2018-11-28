package com.hryj.entity.bo.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李道云
 * @className: UserOftenParty
 * @description: 用户常用门店或仓库
 * @create 2018/8/16 9:57
 **/
@Data
@TableName("u_user_often_party")
public class UserOftenParty extends Model<UserOftenParty> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 用户id
     */
    private Long user_id;
    /**
     * 默认门店或仓库id
     */
    private Long party_id;
    /**
     * 部门类型
     */
    private String dept_type;
    /**
     * 默认门店或仓库名称
     */
    private String party_name;
    /**
     * 默认门店或仓库地址
     */
    private String party_address;
    /**
     * 距离
     */
    private BigDecimal distance;
    /**
     * 默认标识:1-是,0-否
     */
    private Integer default_flag;
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
