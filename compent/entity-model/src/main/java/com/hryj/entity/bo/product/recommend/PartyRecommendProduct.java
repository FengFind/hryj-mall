package com.hryj.entity.bo.product.recommend;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 门店仓库推荐商品
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_party_recommend_product")
public class PartyRecommendProduct extends Model<PartyRecommendProduct> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 商品id
     */
    private Long product_id;

    /**
     * 是否置顶， 1置顶 0未置顶
     */
    private Integer top_flag;

    /**
     * 推荐开始日期
     */
    private Date start_date;
    /**
     * 推荐结束日期
     */
    private Date end_date;
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
