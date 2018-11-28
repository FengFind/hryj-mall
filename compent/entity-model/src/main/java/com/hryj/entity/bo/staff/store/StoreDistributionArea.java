package com.hryj.entity.bo.staff.store;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 门店配送区域表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_store_distribution_area")
public class StoreDistributionArea extends Model<StoreDistributionArea> {


    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 门店id
     */
    private Long dept_id;
    /**
     * poi位置id
     */
    private String poi_id;
    /**
     * 位置类型:地图获取
     */
    private String location_type;
    /**
     * 位置名称:地图获取
     */
    private String location_name;
    /**
     * 位置地址:地图获取
     */
    private String address;
    /**
     * 区域坐标,经纬度","分隔
     */
    private String locations;
    /**
     * 距离(米)
     */
    private BigDecimal distance;
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
