package com.hryj.entity.bo.staff.store;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 门店信息表
 *
 * @author daitingbo
 * @since 2018-07-03
 */
@Data
@TableName("sf_store_info")
public class StoreInfo extends Model<StoreInfo> {


    /**
     * 为门店在部门组织的id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    /**
     * 门店类别
     */
    private String store_type;
    /**
     * 所在省代码
     */
    private String province_code;
    /**
     * 所在省
     */
    private String province_name;
    /**
     * 所在市代码
     */
    private String city_code;
    /**
     * 所在市
     */
    private String city_name;
    /**
     * 所在区代码
     */
    private String area_code;
    /**
     * 所在区
     */
    private String area_name;
    /**
     * 所在街道代码
     */
    private String street_code;
    /**
     * 所在街道
     */
    private String street_name;
    /**
     * 详细地址
     */
    private String detail_address;
    /**
     * 门店坐标,经纬度","分隔
     */
    private String locations;
    /**
     * 联系人
     */
    private String contact_name;
    /**
     * 联系电话
     */
    private String telephone;
    /**
     * 营业时间起
     */
    private String business_time_start;
    /**
     * 营业时间止
     */
    private String business_time_end;
    /**
     * 门店编号
     */
    private String store_num;
    /**
     * 服务提成规则:01-自定义,02-平均分配
     */
    private String service_rule;
    /**
     * 开业时间：yyyy-MM-dd
     */
    private String open_date;
    /**
     * 分摊成本标识:1-分摊,0-不分摊
     */
    private Integer share_cost_flag;

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
