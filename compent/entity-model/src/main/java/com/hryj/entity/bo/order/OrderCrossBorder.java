package com.hryj.entity.bo.order;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 罗秋涵
 * @className: OrderCrossBorder
 * @description: 订单扩展-跨境保税订单
 * @create 2018/9/10 0010 9:54
 **/
@Data
@TableName("o_cross_border_order")
public class OrderCrossBorder extends Model<OrderCrossBorder> {
    /**
     * 订单ID
     */
    @TableId(value ="order_id")
    private Long order_id;

    /**
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;

    /**
     * 第三方订单ID
     */
    private String third_order_code;
    /**
     * 第三方订单状态说明
     */
    private Integer third_order_status;
    /**
     * 第三方订单状态说明
     */
    private String third_order_status_desc;
    /**
     * 订购人姓名
     */
    private String subscriber_name;
    /**
     * 订购人身份证号
     */
    private String subscriber_id_card;
    /**
     * 跨境订单取消次数
     */
    private Integer cancel_count;
    /**
     * 取消失败原因
     */
    private String cancel_failed_reason;
    /**
     * 失败原因
     */
    private String failed_reason;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
