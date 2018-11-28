package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动范围条目
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("pt_activity_scope_item")
public class ActivityScopeItem extends Model<ActivityScopeItem> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 活动id
     */
    private Long activity_id;
    /**
     * 门店仓库id
     */
    private Long party_id;
    /**
     * 活动开始时间
     */
    private Date start_date;
    /**
     * 活动结束时间
     */
    private Date end_date;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
