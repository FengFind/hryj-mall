package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * 广告跳转配置
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("pt_advertising_jump_config")
public class AdvertisingJumpConfig extends Model<AdvertisingJumpConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 广告id
     */
    private Long advertising_id;
    /**
     * 跳转类型:01-跳转url,02-跳转商品,03-跳转活动
     */
    private String jump_type;
    /**
     * 跳转目标值:该字段值根据跳转类型存储对应的值
     */
    private String jump_value;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
