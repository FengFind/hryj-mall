package com.hryj.entity.bo.product;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.hryj.entity.vo.product.audit.response.ProductAuditResponseVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品审核记录表
 *
 * @author daitingbo
 * @since 2018-06-28
 */
@Data
@TableName("p_product_audit_record")
public class ProductAuditRecord extends Model<ProductAuditRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private Long id;
    /**
     * 商品id
     */
    private Long product_id;
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
     * 商品调整类别:01-新增,02-修改
     */
    private String submit_type;
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
    /**
     * 商品快照数据备份id
     */
    private Long prod_backup_data_id;

    @Override
    protected Serializable pkVal() {
          return this.id;
    }

    public ProductAuditResponseVO convertTo() {
        ProductAuditResponseVO vo_audit_rec = new ProductAuditResponseVO();
        vo_audit_rec.setId(this.getId());
        vo_audit_rec.setAudit_remark(this.getAudit_remark());
        vo_audit_rec.setHandle_staff(this.getHandle_staff_name());
        vo_audit_rec.setHandle_time(DateUtil.formatDateTime(this.getHandle_time()));
        vo_audit_rec.setSubmit_staff(this.getSubmit_staff_name());
        vo_audit_rec.setSubmit_time(DateUtil.formatDateTime(this.getSubmit_time()));
        vo_audit_rec.setHandle_result(this.getHandle_result() == null || this.getHandle_result().intValue() == 0 ? false : true);
        vo_audit_rec.setSubmit_type(this.getSubmit_type());
        return vo_audit_rec;
    }
}
