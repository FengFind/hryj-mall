package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: CebInvt
 * @description: 电子清单
 * @create 2018/9/26 10:47
 **/
@Data
@TableName("dec_ceb_invt")
public class CebInvt extends BaseEntity{

    /** UUID */
    @TableField(value = "guid")
    private String guid;

    /** 报文类型 */
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

    /** 电商平台代码 */
    @TableField(value = "ebp_code")
    private String ebpCode;

    /** 电商平台名称 */
    @TableField(value = "ebp_name")
    private String ebpName;

    /** 电商企业名称 */
    @TableField(value = "ebc_code")
    private String ebcCode;

    /** 电商企业名称 */
    @TableField(value = "ebc_name")
    private String ebcName;

    /** 物流运单编号 */
    @TableField(value = "logistics_no")
    private String logisticsNo;

    /** 物流企业代码 */
    @TableField(value = "logistics_code")
    private String logisticsCode;

    /** 物流企业名称 */
    @TableField(value = "logistics_name")
    private String logisticsName;

    /** 企业内部编号 */
    @TableField(value = "cop_no")
    private String copNo;

    /** 预录入编号 */
    @TableField(value = "pre_no")
    private String preNo;

    /** 担保企业编号 */
    @TableField(value = "assure_code")
    private String assureCode;

    /** 账册编号 */
    @TableField(value = "ems_no")
    private String emsNo;

    /** 清单编号 */
    @TableField(value = "invt_no")
    private String invtNo;

    /** 进出口标记 */
    @TableField(value = "ie_flag")
    private String ieFlag;

    /** 申报日期 */
    @TableField(value = "decl_time")
    private String declTime;

    /** 申报海关代码 */
    @TableField(value = "customs_code")
    private String customsCode;

    /** 口岸海关代码 */
    @TableField(value = "port_code")
    private String portCode;

    /** 进口日期 */
    @TableField(value = "ie_date")
    private String ieDate;

    /** 订购人证件类型 */
    @TableField(value = "buyer_id_type")
    private String buyerIdType;

    /** 订购人证件号码 */
    @TableField(value = "buyer_id_number")
    private String buyerIdNumber;

    /** 订购人姓名 */
    @TableField(value = "buyer_name")
    private String buyerName;

    /** 订购人电话 */
    @TableField(value = "buyer_telephone")
    private String buyerTelephone;

    /** 收件地址 */
    @TableField(value = "consignee_address")
    private String consigneeAddress;

    /** 申报企业代码 */
    @TableField(value = "agent_code")
    private String agentCode;

    /** 申报企业名称 */
    @TableField(value = "agent_name")
    private String agentName;

    /** 区内企业代码 */
    @TableField(value = "area_code")
    private String areaCode;

    /** 区内企业名称 */
    @TableField(value = "area_name")
    private String areaName;

    /** 贸易方式 */
    @TableField(value = "trade_mode")
    private String tradeMode;

    /** 运输方式 */
    @TableField(value = "traf_mode")
    private String trafMode;

    /** 运输工具编号 */
    @TableField(value = "traf_no")
    private String trafNo;

    /** 航班航次号 */
    @TableField(value = "voyage_no")
    private String voyageNo;

    /** 提运单号 */
    @TableField(value = "bill_no")
    private String billNo;

    /** 监管场所代码 */
    @TableField(value = "loct_no")
    private String loctNo;

    /** 许可证件号 */
    @TableField(value = "license_no")
    private String licenseNo;

    /** 起运国(地区) */
    @TableField(value = "country")
    private String country;

    /** 运费 */
    @TableField(value = "freight")
    private BigDecimal freight;

    /** 保费 */
    @TableField(value = "insured_fee")
    private BigDecimal insuredFee;

    /** 币制 */
    @TableField(value = "currency")
    private String currency;

    /** 包装种类代码 */
    @TableField(value = "wrap_type")
    private String wrapType;

    /** 件数 */
    @TableField(value = "pack_no")
    private Integer packNo;

    /** 毛重(KG) */
    @TableField(value = "gross_weight")
    private BigDecimal grossWeight;

    /** 净重(KG) */
    @TableField(value = "net_weight")
    private BigDecimal netWeight;

    /** 分拣线ID */
    @TableField(value = "sortline_id")
    private String sortlineId;

    /** 时间机构代码 */
    @TableField(value = "org_code")
    private String orgCode;

    /** 备注 */
    @TableField(value = "note")
    private String note;

    /** 传输企业代码 */
    @TableField(value = "cop_code")
    private String copCode;

    /** 传输企业名称 */
    @TableField(value = "cop_name")
    private String copName;

    /** 报文传输模式 */
    @TableField(value = "dxp_mode")
    private String dxpMode;

    /** 报文传输编号 */
    @TableField(value = "dxp_id")
    private String dxpId;

    /** 备注 */
    @TableField(value = "bt_note")
    private String btNote;

    /** 清单状态 */
    @TableField(value = "invt_status")
    private Integer invtStatus;

}
