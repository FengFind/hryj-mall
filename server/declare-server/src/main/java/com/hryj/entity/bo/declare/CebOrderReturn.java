package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebOrderReturn
 * @description:
 * @create 2018/9/26 10:37
 **/
@Data
@TableName("dec_ceb_order_return")
public class CebOrderReturn extends BaseEntity{

    /** 订单ID */
    @TableField(value = "order_id")
    private Long orderId;

    /** 系统唯一编号 */
    @TableField(value = "guid")
    private String guid;

    /** 电商平台代码 */
    @TableField(value = "ebp_code")
    private String ebpCode;

    /** 电商企业代码 */
    @TableField(value = "ebc_code")
    private String ebcCode;

    /** 订单编号 */
    @TableField(value = "order_no")
    private String orderNo;

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
