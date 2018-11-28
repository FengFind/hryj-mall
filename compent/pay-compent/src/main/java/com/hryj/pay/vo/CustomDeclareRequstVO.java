package com.hryj.pay.vo;

import lombok.Data;

/**
 * @author 李道云
 * @className: CustomDeclareRequstVO
 * @description: 海关申报请求VO
 * @create 2018/9/10
 **/
@Data
public class CustomDeclareRequstVO {

    /*****************************************************申报海关信息******************************************************/

    /**
     * 支付订单id,对应支付组id
     */
    private String pay_order_id;
    /**
     * 支付交易流水号
     */
    private String pay_trade_num;
    /**
     * 报关金额(支付宝)
     */
    private String declare_amt;
    /**
     * 报关类型:不传，默认是新增;ADD 新增报关申请,MODIFY 修改报关信息
     * (微信)
     */
    private String action_type;

    /*****************************************************子订单信息******************************************************/

    /**
     * 子订单id
     */
    private String sub_order_id;
    /**
     * 应付金额(微信)，单位为分
     */
    private Integer order_fee;
    /**
     * 物流费用(微信)，单位为分
     */
    private Integer transport_fee;
    /**
     * 商品价格(微信)，单位为分
     */
    private Integer product_fee;

    /*****************************************************订购人信息*****************************************************/

    /**
     * 订购人身份证号码
     */
    private String buyer_cert_id;
    /**
     * 订购人姓名
     */
    private String buyer_name;
}
