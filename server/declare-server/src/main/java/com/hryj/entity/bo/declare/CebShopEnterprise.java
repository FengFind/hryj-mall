package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebShopEnterprise
 * @description:
 * @create 2018/9/26 14:11
 **/
@Data
@TableName("dec_ceb_shop_enterprise")
public class CebShopEnterprise extends BaseEntity{

    /** 电商平台代码 */
    @TableField(value = "ebp_code")
    private String ebpCode;

    /** 电商平台名称 */
    @TableField("ebp_name")
    private String ebpName;

    /** 电商企业代码 */
    @TableField(value = "ebc_code")
    private String ebcCode;

    /** 电商企业名称 */
    @TableField(value = "ebc_name")
    private String ebcName;

    /** 担保企业编号 */
    @TableField(value = "assure_code")
    private String assureCode;

    /** 申报企业代码 */
    @TableField("agent_code")
    private String agentCode;

    /** 申报企业名称 */
    @TableField(value = "agent_name")
    private String agentName;

    /** 默认关区 */
    @TableField(value = "customer_id")
    private Long customerId;

    /** 默认区内账册企业 */
    @TableField(value = "area_company_id")
    private Long areaCompanyId;

    /** 默认企业传输模式ID */
    @TableField(value = "base_transfer_id")
    private Long baseTransferId;

    /** 是否启用 */
    @TableField(value = "is_enable")
    private Boolean isEnable;

    /** 通知地址 */
    @TableField(value = "notify_url")
    private String notifyUrl;
}
