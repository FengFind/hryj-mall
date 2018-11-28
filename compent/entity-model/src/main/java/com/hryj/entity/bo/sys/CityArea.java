package com.hryj.entity.bo.sys;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: CityArea
 * @description: 城市区域
 * @create 2018/6/25 16:54
 **/
@Data
@TableName("sys_city_area")
public class CityArea extends Model<CityArea> {

    @TableId(value="id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 城市名称
     */
    private String city_name;
    /**
     * 上级id
     */
    private Long pid;
    /**
     * 城市短名称
     */
    private String short_name;
    /**
     * 城市级别
     */
    private Integer clevel;
    /**
     * 邮政编码
     */
    private String zip_code;
    /**
     * 城市路径
     */
    private String path_name;
    /**
     * 经度
     */
    private String lng;
    /**
     * 纬度
     */
    private String lat;
    /**
     * 拼音
     */
    private String pinyin;
    /**
     * 字母
     */
    private String letter;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
