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
 * @className: OrderExpress
 * @description: 订单快递信息表
 * @create 2018-07-02 11:01
 **/
@Data
@TableName("o_order_express")
public class OrderExpress extends Model<OrderExpress> {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    private Long order_id;
    /**
     * 快递公司
     */
    private String express_name;

    /**
     * 快递公司编号
     */
    private String express_id;

    /**
     * 快递单号
     */
    private String express_code;
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
     * 创建时间
     */
    private Date create_time;
    /**
     * 更新时间
     */
    private Date update_time;



    @Override
    protected Serializable pkVal() {
          return null;
    }





}
