package com.hryj.entity.bo.order;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 罗秋涵
 * @className: OrderStatusRecord
 * @description: 订单状态记录
 * @create 2018-07-02 11:03
 **/
@Data
@TableName("o_order_status_record")
public class OrderStatusRecord extends Model<OrderStatusRecord> {

    private static final long serialVersionUID = 1L;



    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 订单状态
     */
    private String order_status;
    /**
     * 状态说明
     */
    private String status_remark;
    /**
     * 操作人类别:01-用户,02-公司员工
     */
    private String operator_user_type;
    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 操作人姓名
     */
    private String operator_name;
    /**
     * 变更原因
     */
    private String change_reason;
    /**
     * 耗时
     */
    private Integer consume_time;
    /**
     * 记录时间
     */
    private Date record_time;



    @Override
    protected Serializable pkVal() {
          return this.id;
    }





}
