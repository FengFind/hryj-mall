package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hryj.utils.UtilValidate;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动信息表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("pt_activity_info")
public class ActivityInfo extends Model<ActivityInfo> {

    public static final Integer AUDIT_STATUS_WAIT = 0;
    public static final Integer AUDIT_STATUS_PASS = 1;
    public static final Integer AUDIT_STATUS_REJECT = 2;

    public static final Integer ACTIVITY_STATUS_NORMAL = 1;
    public static final Integer ACTIVITY_STATUS_CLOSE = 0;

    public static final String ACTIVITY_TYPE_BK = "01";
    public static final String ACTIVITY_TYPE_TG = "02";

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 活动名称
     */
    private String activity_name;
    /**
     * 活动开始时间
     */
    private Date start_date;
    /**
     * 活动结束时间
     */
    private Date end_date;
    /**
     * 活动banner图
     */
    private String activity_image;
    /**
     * 活动样式:01-活动模板,02-指定url
     */
    private String activity_style;
    /**
     * 活动模板内容
     */
    private String templete_data;
    /**
     * 活动背景图
     */
    private String background_image;

    /**
     * 活动角标图
     */
    private String activity_mark_image;

    /**
     * 活动详情
     */
    private String activity_detail;
    /**
     * 活动审核状态:0-待审核,1-通过,2-不通过
     */
    private Integer audit_status;
    /**
     * 活动状态:1-正常,0-关闭
     */
    private Integer activity_status;
    /**
     * 活动范围:01-仓库,02-门店
     */
    private String activity_scope;
    /**
     * 活动类型:01-爆款,02-团购
     */
    private String activity_type;
    /**
     * 操作人id
     */
    private Long operator_id;
    /**
     * 操作人姓名
     */
    private String operator_name;
    /**
     * 本次活动参与门店或仓库是指定或为当前操作员工数据权限上能看到的全部门店或仓库  0-指定 1-全部
     */
    private Integer all_party;
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

    public static String getActivityTypeName(String activity_type) {
        if (UtilValidate.isEmpty(activity_type)) {
            return "默认(爆款)";
        }
        if (ACTIVITY_TYPE_BK.equals(activity_type.trim())) {
            return "爆款";
        }
        if (ACTIVITY_TYPE_TG.equals(activity_type.trim())) {
            return "团购";
        }
        return "--";
    }
}
