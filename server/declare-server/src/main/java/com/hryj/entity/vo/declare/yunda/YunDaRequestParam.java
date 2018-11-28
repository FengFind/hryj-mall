package com.hryj.entity.vo.declare.yunda;

import lombok.Data;

/**
 * @author 白飞
 * @className: YunDaRequestParam
 * @description:
 * @create 2018/9/28 11:00
 **/
@Data
public class YunDaRequestParam {

    /** 应用编码，由oms给出 */
    private String app_key = "fjldt";
    /** 对接账号，由oms给出 */
    private String tradeId = "1612140001,360588EE1A5F2720B58C50DF9B3AAE58";
     /** 业务类型，默认partner （落地配） */
    private String buz_type = "partner";
     /** 接口名称，示例：global_order_create */
    private String method = "global_order_create";
     /** 版本号，示例：1.0 */
    private String version = "1.0";
     /** 请求数据格式（json,xml），默认xml */
    private String format = "xml";
     /** 业务数据 */
    private String data;
     /** 签名 */
    private String sign;
    /** app_secret */
    private String app_secret = "123456";
}
