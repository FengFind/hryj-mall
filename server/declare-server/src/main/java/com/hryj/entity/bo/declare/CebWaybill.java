package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CebWaybill
 * @description:
 * @create 2018/9/26 11:26
 **/
@Data
@TableName("dec_ceb_waybill")
public class CebWaybill extends BaseEntity{

    /** 系统唯一编号 */
    @TableField(value = "guid")
    private String guid;

    /** 报送类型 */
    @TableField(value = "app_type")
    private String appType;

    /** 报送时间 */
    @TableField(value = "app_time")
    private String appTime;

    /** 业务状态 */
    @TableField(value = "app_status")
    private String appStatus;

    /** 订单编号 */
    @TableField(value = "order_no")
    private String orderNo;

    /** 电商备案编号 */
    @TableField(value = "ebc_code")
    private String ebcCode;

    /** 物流企业代码K-物流公司简称 */
    @TableField(value = "logistics_key")
    private String logisticsKey;
    /** 物流企业代码 */
    @TableField(value = "logistics_code")
    private String logisticsCode;

    /** 物流企业名称 */
    @TableField(value = "logistics_name")
    private String logisticsName;

    /** 物流运单编号 */
    @TableField(value = "logistics_no")
    private String logisticsNo;

    /** 提运单号 */
    @TableField(value = "bill_no")
    private String billNo;

    /** 运费 */
    @TableField(value = "freight")
    private BigDecimal freight;

    /** 保价费 */
    @TableField(value = "insured_fee")
    private BigDecimal insuredFee;

    /** 币值 */
    @TableField(value = "currency")
    private String currency;

    /** 毛重 */
    @TableField(value = "weight")
    private BigDecimal weight;

    /** 件数 */
    @TableField(value = "pack_no")
    private Integer packNo;

    /** 主要货物信息 */
    @TableField(value = "goods_info")
    private String goodsInfo;

    /** 收货人姓名 */
    @TableField(value = "consingee")
    private String consingee;

    /** 收货地址 */
    @TableField(value = "consignee_address")
    private String consigneeAddress;

    /** 收货人电话 */
    @TableField(value = "consignee_telephone")
    private String consigneeTelephone;

    /** 传输企业代码 */
    @TableField(value = "cop_code")
    private String copCode;

    /** 传输企业名称 */
    @TableField(value = "cop_name")
    private String copName;

    /** 报文传输模式 */
    @TableField(value = "dxp_model")
    private String dxpModel;

    /** 报文传输编号 */
    @TableField(value = "dxp_id")
    private String dxpId;

    /** 备注 */
    @TableField(value = "note")
    private String note;

    /** 运单申报状态：0-未申报，1-成功，2-失败 */
    @TableField(value = "waybill_status")
    private Integer waybillStatus;


}
