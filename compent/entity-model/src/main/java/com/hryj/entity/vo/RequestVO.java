package com.hryj.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: RequestVO
 * @description: 请求基础VO
 * @create 2018/6/25 18:21
 **/
@Data
@ApiModel(value="请求基础VO")
public class RequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * app唯一标识
     */
    @ApiModelProperty(value="app唯一标识",name="app_key",hidden=true)
    private String app_key;

    /**
     * app版本号
     */
    @ApiModelProperty(value="app版本号",name="app_version",hidden=true)
    private String app_version;

    /**
     * 调用来源
     */
    @ApiModelProperty(value="调用来源",name="call_source",hidden=true)
    private String call_source;

    /**
     * 应用渠道
     */
    @ApiModelProperty(value="应用渠道",name="app_channel",hidden=true)
    private String app_channel;

    /**
     * 设备唯一标识
     */
    @ApiModelProperty(value="设备唯一标识",name="device_id",hidden=true)
    private String device_id;

    /**
     * 设备型号
     */
    @ApiModelProperty(value="设备型号",name="device_model",hidden=true)
    private String device_model;

    /**
     * 设备操作系统版本号
     */
    @ApiModelProperty(value="设备操作系统版本号",name="os_version",hidden=true)
    private String os_version;

    /**
     * IP地址
     */
    @ApiModelProperty(value="IP地址",name="device_ip",hidden=true)
    private String device_ip;

    /**
     * 时间戳
     */
    @ApiModelProperty(value="时间戳",name="time_stamp",hidden=true)
    private String time_stamp;

    /**
     * 登录token
     */
    @ApiModelProperty(value="登录token",name="login_token",hidden=true)
    private String login_token;

    public RequestVO() {
    }
}
