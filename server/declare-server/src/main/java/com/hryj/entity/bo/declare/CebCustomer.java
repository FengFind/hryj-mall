package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebCustomer
 * @description: 关区信息
 * @create 2018/9/26 14:06
 **/
@Data
@TableName("dec_ceb_customer")
public class CebCustomer extends BaseEntity{

    /**  分拣线 */
    @TableField(value = "sortline_id")
    private String sortlineId;

    /** 分拣线名称 */
    @TableField(value = "sortline_name")
    private String sortlineName;

    /** 关区代码 */
    @TableField(value = "customer_code")
    private String customerCode;

    /** 关区名称 */
    @TableField(value = "customer_name")
    private String customerName;

    /** 是否启用 */
    @TableField(value = "is_enable")
    private Boolean isEnable;

    /** 推送地址 */
    @TableField(value = "hs_post_url")
    private Boolean hsPostUrl;
}
