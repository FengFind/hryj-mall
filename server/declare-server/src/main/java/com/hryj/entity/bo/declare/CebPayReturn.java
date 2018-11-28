package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebPayReturn
 * @description:
 * @create 2018/9/26 11:21
 **/
@Data
@TableName("dec_ceb_pay_return")
public class CebPayReturn extends  BaseEntity{

    /** 支付单ID */
    @TableField(value = "pay_id")
    private Long payId;

    /** 系统唯一编号 */
    @TableField(value = "guid")
    private String guid;

    /** 电商平台代码 */
    @TableField(value = "pay_code")
    private String payCode;

    /** 支付交易编号 */
    @TableField(value = "pay_transaction_id")
    private String payTransactionId;

    /** 回执状态 操作结果（2电子口岸申报中/3发送海关成功/4发送海关失败/100海关退单/120海关入库）,若小于0数字表示处理异常回执*/
    @TableField(value = "return_status")
    private String returnStatus;

    /** 回执时间 */
    @TableField(value = "return_time")
    private String returnTime;

    /** 回执信息 */
    @TableField(value = "return_info")
    private String returnInfo;
}
