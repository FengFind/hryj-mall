package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebInvtReturn
 * @description:
 * @create 2018/9/26 11:06
 **/
@Data
@TableName("dec_ceb_invt_return")
public class CebInvtReturn extends BaseEntity{

    /** 清单ID */
    @TableField(value = "invt_id")
    private Long invtId;

    /** 系统唯一编号 */
    @TableField(value = "guid")
    private String guid;

    /** 申报海关代码 */
    @TableField(value = "customs_code")
    private String customsCode;

    /** 电商平台代码 */
    @TableField(value = "ebp_code")
    private String ebpCode;

    /** 电商企业代码 */
    @TableField(value = "ebc_code")
    private String ebcCode;

    /** 申报企业代码 */
    @TableField(value = "agent_code")
    private String agentCode;

    /** 企业内部编号 */
    @TableField(value = "cop_no")
    private String copNo;

    /** 预录入编号 */
    @TableField(value = "pre_no")
    private String preNo;

    /** 清单编号 */
    @TableField(value = "invt_no")
    private String invtNo;

    /** 回执状态 */
    @TableField(value = "return_status")
    private String returnStatus;

    /** 回执时间 */
    @TableField(value = "return_time")
    private String returnTime;

    /** 回执信息 */
    @TableField(value = "return_info")
    private String returnInfo;
}
