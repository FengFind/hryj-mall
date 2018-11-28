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
 * @className: PartyGrossProfitStatis
 * @description: 门店或仓库毛利统计表
 * @create 2018/8/16 11:13
 **/
@Data
@TableName("pr_party_gross_profit_statis")
public class PartyGrossProfitStatis extends Model<PartyGrossProfitStatis> {

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 统计月份
     */
    private String statis_month;
    /**
     * 门店或仓库id
     */
    private Long party_id;
    /**
     * 门店或仓库名称
     */
    private String party_name;
    /**
     * 部门路径
     */
    private String dept_path;
    /**
     * 毛利总和
     */
    private BigDecimal total_gross_profit;
    /**
     * 创建时间
     */
    private Date create_time;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
