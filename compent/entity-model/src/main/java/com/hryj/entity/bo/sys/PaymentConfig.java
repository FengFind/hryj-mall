package com.hryj.entity.bo.sys;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李道云
 * @className: PaymentConfig
 * @description: 支付配置
 * @create 2018/6/27 19:44
 **/
@Data
@TableName("sys_payment_config")
public class PaymentConfig extends Model<PaymentConfig> {

    @TableId(value="id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 应用唯一标识
     */
    private String app_key;
    /**
     * 支付方式:01-微信,02-支付宝,03-银联
     */
    private String payment_method;
    /**
     * 支付配置详情,json格式
     */
    private String payment_config;
    /**
     * 所属组织:表明当前支付配置是属于哪个企业或组织的
     */
    private Long party_id;
    /**
     * 组织名称
     */
    private String party_name;
    /**
     * 支付配置说明
     */
    private String description;
    /**
     * 配置状态:1-正常,0-无效
     */
    private Boolean config_status;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
