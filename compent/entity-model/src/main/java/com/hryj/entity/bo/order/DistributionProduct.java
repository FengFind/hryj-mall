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
 * @className: DistributionProduct
 * @description: 配送单商品信息
 * @create 2018-07-02 11:00
 **/
@Data
@TableName("o_distribution_product")
public class DistributionProduct extends Model<DistributionProduct> {

    private static final long serialVersionUID = 1L;



    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 配送单id
     */
    private Long distribution_id;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 订单商品id
     */
    private Long order_product_id;
    /**
     * 配送数量
     */
    private Integer distribution_quantity;
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
