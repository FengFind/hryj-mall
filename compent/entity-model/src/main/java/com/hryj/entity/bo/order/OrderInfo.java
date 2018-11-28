package com.hryj.entity.bo.order;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @author 罗秋涵
 * @className: OrderInfo
 * @description: 订单信息
 * @create 2018-07-02 11:02
 **/
@Data
@TableName("o_order_info")
public class OrderInfo extends Model<OrderInfo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 订单编号
     */
    private String order_num;
    /**
     * 订单类型 订单类别:01-普通订单,02-预付订单
     */
    private String order_type;
    /**
     * 应用标识
     */
    private String app_key;
    /**
     * 订单日期
     */
    private Date order_date;
    /**
     * 销售渠道
     */
    private String sales_channel;
    /**
     * 订单状态:01-待支付,02-待发货,03-待自提,04-已发货,05-退货申请中,06-退货成功,07-已取消,08-已完成
     */
    private String order_status;
    /**
     * 支付方式:01-微信,02-支付宝,03-银联
     */
    private String pay_method;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 门店仓库名
     */
    private String party_name;

    /**
     * 部门路径
     */
    private String party_path;

    /**
     * 门店类别
     */
    private String party_type;

    /**
     * 代下单员工id
     */
    private Long help_staff_id;

    /**
     * 代下单门店ID
     */
    private Long help_store_id;
    /**
     * 订单金额
     */
    private BigDecimal order_amt;
    /**
     * 优惠金额
     */
    private BigDecimal discount_amt;
    /**
     * 实付金额
     */
    private BigDecimal pay_amt;
    /**
     * 付款时间
     */
    private Date pay_time;
    /**
     * 订单商品成本总计
     */
    private BigDecimal total_cost;
    /**
     * 订单毛利
     */
    private BigDecimal order_profit;
    /**
     * 配送方式:01-自提,02-送货上门,03-快递
     */
    private String delivery_type;
    /**
     * 期望送达开始时间
     */
    private Date hope_delivery_start_time;
    /**
     * 期望送达截止时间
     */
    private Date hope_delivery_end_time;
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
     * 收货手机号
     */
    private String receive_phone;
    /**
     * 用户地址
     */
    private String user_address;
    /**
     * 订单备注
     */
    private String order_remark;

    /**
     * 分享者ID
     */
    private Long share_user_id;

    /**
     * 分享来源
     */
    private String share_source;

    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 新交易标识
     */
    private Integer new_trade_flag;

    /**
     * 订单完成时间
     */
    private Date complete_time;

    /**
     * 省份ID
     */
    private String province_id;

    /**
     * 城市ID
     */
    private String city_id;

    /**
     * 地区ID
     */
    private String area_id;

    /**
     * 区域信息
     */
    private String area_info;

    /**
     * 发货仓库编码
     */
    private String delivery_warehouse;

    /**
     * 发货仓库名称
     */
    private String delivery_warehouse_name;


    @Override
    protected Serializable pkVal() {
          return this.id;
    }





}
