package com.hryj.entity.bo.cart;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车商品记录表
 *
 * @author daitingbo
 * @since 2018-07-02
 */
@Data
@TableName("sc_shopping_cart_record")
public class ShoppingCartRecord extends Model<ShoppingCartRecord> {

    private static final long serialVersionUID = 1L;



    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 购物车类型:01-默认购物车,02-促销购物车
     */
    private String cart_type;
    /**
     * 应用标识
     */
    private String app_key;
    /**
     * 用户id
     */
    private Long user_id;
    /**
     * 代下单员工编号
     */
    private Long help_staff_id;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 活动id
     */
    private Long activity_id;
    /**
     * 加入购物车时的价格
     */
    private BigDecimal into_cart_price;
    /**
     * 商品数量
     */
    private Integer quantity;
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



    @Override
    protected Serializable pkVal() {
          return this.id;
    }





}
