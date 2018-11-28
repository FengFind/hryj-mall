package com.hryj.entity.bo.staff.warehouse;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 仓库配送区域
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_wh_distribution_area")
public class WhDistributionArea extends Model<WhDistributionArea> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 仓库id
     */
    private Long dept_id;
    /**
     * 城市名称
     */
    private String city_name;
    /**
     * 城市id
     */
    private Long city_id;
    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 创建时间
     */
    private Date create_time;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


}
