package com.hryj.entity.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author 罗秋涵
 * @className: OrderSyncVO
 * @description: 订单同步参数VO
 * @create 2018/9/14 0014 11:45
 **/
@Data
public class OrderSyncVO {

    /**
     * 订购人姓名
     */
    private String buy_name;
    /**
     * 订购人手机号
     */
    private String buy_mobile;
    /**
     * 身份证号码
     */
    private String idnum;
    /**
     * 	收货人姓名
     */
    private String take_name;
    /**
     * 收货人手机号
     */
    private String take_mobile;
    /**
     * 省代码
     */
    private String province_id;
    /**
     * 市代码
     */
    private String city_id;
    /**
     * 区县代码
     */
    private String area_id;
    /**
     * 省市县区域
     */
    private String area_info;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 商品明细
     */
    private List<OrderGoodsFroThirdVO> goodsList;
    /**
     * 三方平台的订单号（提交支付的单号）
     */
    private String out_trade_no;
    /**
     * 支付返回的交易流水号
     */
    private String trade_no;
    /**
     * 支三方平台的订单金额（保留两位小数）
     */
    private String order_amount;
    /**
     * 三方平台的订单金额（保留两位小数）
     */
    private String order_tax;
    /**
     * 支付方式的名称代码
     */
    private String payment_code;
    /**
     * 支付时间时间戳
     */
    private String payment_time;
    /**
     * 三方平台下单用户的用户名
     */
    private String name;
    /**
     * 订单备注信息
     */
    private String note;




}
