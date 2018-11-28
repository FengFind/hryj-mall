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
 * @className: ReturnProduct
 * @description: 退货单商品信息
 * @create 2018-07-02 11:03
 **/
@Data
@TableName("o_return_product")
public class ReturnProduct extends Model<ReturnProduct> {

    private static final long serialVersionUID = 1L;



    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    private Long return_id;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 订单商品id
     */
    private Long order_product_id;
    /**
     * 退货数量
     */
    private Integer return_quantity;
    /**
     * 退货单价
     */
    private BigDecimal return_price;
    /**
     * 退货金额
     */
    private BigDecimal return_amt;
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
