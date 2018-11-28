package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebLogistics
 * @description:
 * @create 2018/9/26 15:13
 **/
@Data
@TableName("dec_ceb_logistics")
public class CebLogistics extends BaseEntity{

    /** 物流企业代码 */
    @TableField(value = "logistics_code")
    private String logisticsCode;

    /** 物流企业名称 */
    @TableField(value = "logistics_name")
    private String logisticsName;

    /** 物流编号 */
    @TableField(value = "logistics_no")
    private String logisticsNo;

    /** appKey */
    @TableField(value = "app_key")
    private String appKey;

    /** appSecret */
    @TableField(value = "app_secret")
    private String appSecret;

    /** 合作者账户 */
    @TableField(value = "partner")
    private String partner;

    /** 电商企业备案编码 */
    @TableField(value = "ebc_code")
    private String ebc_code;


}
