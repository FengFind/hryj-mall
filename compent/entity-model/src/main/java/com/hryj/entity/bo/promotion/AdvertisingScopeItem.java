package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 广告范围条目
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("pt_advertising_scope_item")
public class AdvertisingScopeItem extends Model<AdvertisingScopeItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 广告位id
     */
    private Long advertising_id;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 生效时间
     */
    private Date start_date;
    /**
     * 失效时间
     */
    private Date end_date;
    /**
     * 置顶标识:1-置顶,0-不置顶
     */
    private Integer top_flag;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
