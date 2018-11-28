package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 汪豪
 * @className: IndexNavigationConfig
 * @description: 首页导航配置实体
 * @create 2018/8/23 0023 13:52
 **/
@Data
@TableName("pt_index_navigation_config")
public class IndexNavigationConfig extends Model<IndexNavigationConfig> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 导航名称
     */
    private String navigation_name;
    /**
     * 导航图标
     */
    private String navigation_icon;
    /**
     * 导航链接
     */
    private String navigation_link;
    /**
     * 位置序号
     */
    private Integer sort_num;
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
