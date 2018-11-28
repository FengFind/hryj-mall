package com.hryj.entity.bo.product;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品分类表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_category")
public class ProductCategory extends Model<ProductCategory> {

    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分类名称
     */
    private String category_name;
    /**
     * 分类上级id
     */
    private Long category_pid;
    /**
     * 分类图片
     */
    private String category_url;
    /**
     * 序号
     */
    private Integer sort_num;
    /**
     * 操作人id
     */
    private Long operator_id;
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
