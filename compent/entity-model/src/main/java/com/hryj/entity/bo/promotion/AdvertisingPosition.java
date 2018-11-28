package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 广告位信息
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("pt_advertising_position")
public class AdvertisingPosition extends Model<AdvertisingPosition> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 广告位名称
     */
    private String advertising_name;
    /**
     * 展示开始时间
     */
    private Date start_date;
    /**
     * 展示结束时间
     */
    private Date end_date;
    /**
     * 广告图片url地址
     */
    private String advertising_image;
    /**
     * 广告位状态:1-启用,0-禁用
     */
    private Integer advertising_status;
    /**
     * 广告位范围:01-仓库,02-门店
     */
    private String advertising_scope;

    /**
     * 广告位类型, 01 banner广告
     */
    private String advertising_type;

    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 提交人姓名
     */
    private String operator_name;
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
