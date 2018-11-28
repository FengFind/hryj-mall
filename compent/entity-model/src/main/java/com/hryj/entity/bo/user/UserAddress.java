package com.hryj.entity.bo.user;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 李道云
 * @className: UserAddress
 * @description: 用户地址
 * @create 2018-06-26 9:01
 **/
@Data
@TableName("u_user_address")
public class UserAddress extends Model<UserAddress> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 用户id
     */
    private Long user_id;
    /**
     * poi位置id：地图获取
     */
    private String poi_id;
    /**
     * 位置类型code:地图获取
     */
    private String location_type_code;
    /**
     * 位置类型名称:地图获取
     */
    private String location_type_name;
    /**
     * 位置名称:地图获取
     */
    private String location_name;
    /**
     * 位置地址:地图获取
     */
    private String location_address;
    /**
     * 位置坐标,经纬度","分隔
     */
    private String locations;
    /**
     * 所在省代码
     */
    private String province_code;
    /**
     * 所在省名称
     */
    private String province_name;
    /**
     * 所在城市代码
     */
    private String city_code;
    /**
     * 所在城市名称
     */
    private String city_name;
    /**
     * 所在区代码
     */
    private String area_code;
    /**
     * 所在区名称
     */
    private String area_name;
    /**
     * 收货人
     */
    private String receive_name;
    /**
     * 收货手机号
     */
    private String receive_phone;
    /**
     * 详细地址
     */
    private String detail_address;
    /**
     * 默认标识:1-是,0-否
     */
    private Boolean default_flag;
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