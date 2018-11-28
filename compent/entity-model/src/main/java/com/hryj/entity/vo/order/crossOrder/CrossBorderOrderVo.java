package com.hryj.entity.vo.order.crossOrder;

import com.hryj.entity.bo.order.OrderProduct;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author 罗秋涵
 * @className: CrossBorderOrderVo
 * @description:
 * @create 2018/9/14 0014 15:53
 **/
@Data
public class CrossBorderOrderVo {
    /**
     * 订单Id
     */
    private Long order_id;
    /**
     * 订单编号
     */
    private String order_num;
    /**
     * 订单类型
     */
    private String order_type;
    /**
     * 订单状态
     */
    private String order_status;

    /**
     * 支付方式
     */
    private String pay_method;

    /**
     * 代下单人Id
     */
    private String help_staff_id;

    /**
     * 支付金额
     */
    private BigDecimal pay_amt;

    /**
     * 支付时间
     */
    private Date pay_time;

    /**
     * 用户id
     */
    private Long user_id;

    /**
     * 用户姓名
     */
    private String user_name;

    /**
     * 用户电话
     */
    private String user_phone;
    /**
     * 用户地址
     */
    private String user_address;

    /**
     *省代码
     */
    private String province_id;

    /**
     * 市代码
     */
    private String city_id;

    /**
     * 区代码
     */
    private String area_id;
    /**
     * 省、市、区
     */
    private String area_info;
    /**
     * 第三方订单状态
     */
    private String third_order_code;

    /**
     * 第三方订单状态说明
     */
    private String  third_order_status_desc;

    /**
     * 订购人姓名
     */
    private String subscriber_name;

    /**
     * 订购人身份证
     */
    private String subscriber_id_card;

    /**
     * 支付总交易流水
     */
    private String pay_trade_num;

    /**
     * 支付子流水号
     */
    private String sub_pay_trade_num;

    /**
     * 支付组对应订单数量
     */
    private Integer  pay_order_count;

    /**
     * appkey
     */
    private String app_key;

    /**
     * 支付渠道
     */
    private Long payment_channel;

    /**
     * 支付组Id
     */
    private Long pay_group_id;

    /**
     * 支付组编号
     */
    private String pay_group_sn;

    /**
     * 支付订单明细Id
     */
    private Long group_item_id;

    /**
     * 订单税金
     */
    private String order_tax;

    /**
     * 订单备注
     */
    private String order_remark;

    /**
     * 商品明细
     */
    private List<OrderProduct> productList;


}
