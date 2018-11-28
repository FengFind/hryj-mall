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
 * @className: OrderDistribution
 * @description: 订单配送信息
 * @create 2018-07-02 11:01
 **/
@Data
@TableName("o_order_distribution")
public class OrderDistribution extends Model<OrderDistribution> {

    private static final long serialVersionUID = 1L;



    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 配送类别:01-送货,02-取货
     */
    private String distribution_type;
    /**
     * 配送状态:01-待配送,02-配送中,03-已配送,04-取消配送
     */
    private String distribution_status;
    /**
     * 退货单编号
     */
    private Long return_id;
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
     * 用户地址经纬度
     */
    private String address_locations;
    /**
     * 期望送达开始时间
     */
    private Date hope_delivery_start_time;
    /**
     * 期望送达截止时间
     */
    private Date hope_delivery_end_time;
    /**
     * 实际送达截止时间
     */
    private Date actual_delivery_end_time;

    /**
     * 配送完成时间
     */
    private Date complete_time;
    /**
     * 配送员id
     */
    private Long distribution_staff_id;
    /**
     * 配送员姓名
     */
    private String distribution_staff_name;
    /**
     * 配送员电话
     */
    private String distribution_staff_phone;
    /**
     * 配送费用
     */
    private BigDecimal distribution_amt;
    /**
     * 分配人id
     */
    private Long assign_staff_id;
    /**
     * 分配人姓名
     */
    private String assign_staff_name;
    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;



    @Override
    protected Serializable pkVal() {
          return this.id;
    }





}
