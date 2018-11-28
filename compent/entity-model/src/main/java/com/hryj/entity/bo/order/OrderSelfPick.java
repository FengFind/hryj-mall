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
 * @className: OrderSelfPick
 * @description: 订单自提信息表
 * @create 2018-07-02 11:02
 **/
@Data
@TableName("o_order_self_pick")
public class OrderSelfPick extends Model<OrderSelfPick> {

    private static final long serialVersionUID = 1L;



    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;

    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 自提码
     */
    private String self_pick_code;
    /**
     * 自提单状态:01-待自提,02-已自提
     */
    private String self_pick_status;
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
     * 自提地址:门店地址
     */
    private String self_pick_address;
    /**
     * 自提联系人:门店联系人
     */
    private String self_pick_contact;
    /**
     * 自提联系电话:门店联系电话
     */
    private String self_pick_phone;
    /**
     * 自提处理人id
     */
    private Long self_pick_handel_id;
    /**
     * 自提处理人姓名
     */
    private String self_pick_handel_name;

    /**
     * 配送完成时间
     */
    private Date complete_time;

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
