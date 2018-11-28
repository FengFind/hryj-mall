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
 * @className: OrderReturn
 * @description: 订单退货信息
 * @create 2018-07-02 11:02
 **/
@Data
@TableName("o_order_return")
public class OrderReturn extends Model<OrderReturn> {

    private static final long serialVersionUID = 1L;



    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 退货单类别:01-客户自主退货,02-店员代退货
     */
    private String return_type;
    /**
     * 退货单状态:01-申请中,02-同意退货,03-取消退货,04-拒绝退货
     */
    private String return_status;
    /**
     * 退货申请人id
     */
    private Long return_apply_id;
    /**
     * 退货申请人姓名
     */
    private String return_apply_name;
    /**
     * 退货申请时间
     */
    private Date return_apply_time;
    /**
     * 退货申请原因:01-无理由退货,02-质量问题退货,03-其他
     */
    private String return_reason;
    /**
     * 退货图片
     */
    private String return_image;

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
     * 退货说明
     */
    private String return_remark;
    /**
     * 退货金额
     */
    private BigDecimal return_total;
    /**
     * 调整退货金额标识:1-是,0-否
     */
    private Integer adjust_return_amt_flag;
    /**
     * 调整前退货金额
     */
    private BigDecimal bafore_return_total;
    /**
     * 退货处理人id
     */
    private Long return_handel_id;
    /**
     * 退货处理人姓名
     */
    private String return_handel_name;
    /**
     * 退货处理说明
     */
    private String return_handel_remark;
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
