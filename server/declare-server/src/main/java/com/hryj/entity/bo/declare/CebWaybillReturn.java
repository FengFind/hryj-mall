package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebWaybillReturn
 * @description:
 * @create 2018/9/26 11:29
 **/
@Data
@TableName("dec_ceb_waybill_return")
public class CebWaybillReturn extends BaseEntity{

    /** 运单号ID */
    @TableField(value = "waybill_id")
    private Long waybillId;

    /** 系统唯一编号 */
    @TableField(value = "guid")
    private String guid;

    /** 物流企业代码 */
    @TableField(value = "logistics_code")
    private String logisticsCode;

    /** 物流运单编号 */
    @TableField(value = "logistics_no")
    private String logisticsNo;

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
