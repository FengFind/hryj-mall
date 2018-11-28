package com.hryj.entity.bo.declare;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

/**
 * @author 白飞
 * @className: CebAreaCompany
 * @description: 区内企业信息
 * @create 2018/9/26 14:01
 **/
@Data
@TableName("dec_ceb_area_company")
public class CebAreaCompany extends BaseEntity{

    /** 系统编号，对外使用 */
    @TableField(value = "sys_code")
    private String sysCode;

    /** 账册编号 */
    @TableField(value = "ems_no")
    private String emsNo;

    /** 区内企业名称 */
    @TableField(value = "area_name")
    private String areaName;

    /** 区内企业代码 */
    @TableField(value = "area_code")
    private String areaCode;

    /** 区内公司名称 */
    @TableField(value = "company_name")
    private String companyName;

    /** 仓库编码，外部提供。预留字段 */
    @TableField(value = "warehouse_code")
    private String warehouseCode;

    /** POST地址 */
    @TableField(value = "wms_post_url")
    private String wmsPostUrl;



}
