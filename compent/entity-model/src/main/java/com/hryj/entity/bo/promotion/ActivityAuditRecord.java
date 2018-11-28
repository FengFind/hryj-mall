package com.hryj.entity.bo.promotion;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动审核记录表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("pt_activity_audit_record")
public class ActivityAuditRecord extends Model<ActivityAuditRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 活动id
     */
    private Long activity_id;
    /**
     * 提交人id
     */
    private Long submit_staff_id;
    /**
     * 提交人姓名
     */
    private String submit_staff_name;
    /**
     * 提交时间
     */
    private Date submit_time;
    /**
     * 处理人id
     */
    private Long handle_staff_id;
    /**
     * 处理人姓名
     */
    private String handle_staff_name;
    /**
     * 处理时间
     */
    private Date handle_time;
    /**
     * 处理结果:1-通过,0-驳回
     */
    private Integer handle_result;
    /**
     * 审核说明
     */
    private String audit_remark;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

}
