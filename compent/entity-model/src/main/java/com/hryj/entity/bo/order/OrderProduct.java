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
 * @className: OrderProduct
 * @description: 订单商品信息
 * @create 2018-07-02 11:02
 **/
@Data
@TableName("o_order_product")
public class OrderProduct extends Model<OrderProduct> {

    private static final long serialVersionUID = 1L;


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 订单id
     */
    private Long order_id;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 商品分类id
     */
    private Long product_category_id;
    /**
     * 商品id
     */
    private Long product_id;
    /**
     * 第三方SKUID
     */
    private String third_sku_id;

    /**
     * HS_code
     */
    private String hs_code;

    /**
     * 商品名称
     */
    private String product_name;
    /**
     * 商品图片
     */
    private String list_image_url;
    /**
     * 商品类型
     */
    private String product_type_id;
    /**
     * 成本价格
     */
    private BigDecimal cost_price;
    /**
     * 原售价
     */
    private BigDecimal org_price;
    /**
     * 实际价格
     */
    private BigDecimal actual_price;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 活动id
     */
    private Long activity_id;
    /**
     * 价格修改标识:1-修改过,0-未修改
     */
    private Integer price_modify_flag;
    /**
     * 修改前价格
     */
    private BigDecimal before_price;
    /**
     * 库存释放标识:1-已释放,0-未释放
     */
    private Integer stock_release_flag;
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
