package com.hryj.entity.bo.profit;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author 李道云
 * @className: RegionProfitDetail
 * @description: 区域公司分润明细
 * @create 2018/7/11 18:25
 **/
@Data
@TableName("pr_region_profit_detail")
public class RegionProfitDetail extends Model<RegionProfitDetail> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 分润月份
     */
    private String profit_month;
    /**
     * 所属区域公司
     */
    private Long region_id;
    /**
     * 门店id
     */
    private Long store_id;
    /**
     * 门店名称
     */
    private String store_name;
    /**
     * 门店员工id
     */
    private Long store_staff_id;
    /**
     * 门店员工姓名
     */
    private String store_staff_name;
    /**
     * 部门id
     */
    private Long dept_id;
    /**
     * 员工id
     */
    private Long dept_staff_id;
    /**
     * 分润金额
     */
    private BigDecimal profit_amt;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
