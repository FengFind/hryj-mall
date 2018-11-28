package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebBaseTransfer
 * @description:
 * @create 2018/9/27 9:17
 **/
@Data
@TableName("dec_ceb_base_transfer")
public class CebBaseTransfer extends BaseEntity{

    /** 传输企业代码 */
    @TableField(value = "cop_code")
    private String copCode;
    /** 传输企业名称 */
    @TableField(value = "cop_name")
    private String copName;
    /** 报文传输模式 */
    @TableField(value = "dxp_mode")
    private String dxpMode;
    /** 报文传输编号 */
    @TableField(value = "dxp_id")
    private String dxpId;
    /** 备注 */
    @TableField(value = "note")
    private String note;
}
