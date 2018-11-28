package com.hryj.entity.vo.declare.yunda;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author 白飞
 * @className: YunDaHawb
 * @description:
 * @create 2018/9/28 9:51
 **/
@Data
@XmlRootElement(name = "Hawb")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaHawb {

    /** 运单号 */
    private String mail_no = "";
    /** 订单号 */
    private String hawbno = "";
    /** 校验重复单 Y*/
    private String order_id = "";
    /** 主单号 申报时当主运单号使用 Y*/
    private String mawb = "";
    /** 包裹数量 */
    private BigDecimal piece = new BigDecimal(1);
    /** 货物总重量 */
    private BigDecimal weight = BigDecimal.ZERO;
    /** 运费 */
    private BigDecimal freight = BigDecimal.ZERO;
    /** 上游快递单号 */
    private String pre_express = "";
    /** 下游快递单号 */
    private String next_express = "";
    /** 出发国 */
    private String fcountry = "CN";
    /** 到达国 */
    private String tcountry = "CN";
    /** 信息来源 */
    private String infor_origin = "";
    /** 保险费 */
    private BigDecimal insurance_fee = BigDecimal.ZERO;
    /** 货物价值 */
    private BigDecimal goods_money = BigDecimal.ZERO;
    /** certificate_type */
    private String certificate_type = "zj01";
    /** 报关证号 (可用于买家人身份证号)*/
    private String certificate_id = "";
    /** 货币 */
    private String currency="142";
    /** 要求 */
    private String request = "";
    /** 备注说明 */
    private String remark = "";
    /** 增值服务代码 (多项用；隔开) */
    private String vat_service = "";
    /** 收货人信息 */
    @XmlElement(name = "receiver")
    private YunDaReceiver receiver = new YunDaReceiver();
    /** 发货人信息 */
    @XmlElement(name = "sender")
    private YunDaSender sender = new YunDaSender();
    /** 货品信息 */
    @XmlElement(name = "goods_list")
    private List<YunDaGoodsList> goods_list = Lists.newArrayList();
}
