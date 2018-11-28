package com.hryj.entity.vo.sys.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: AppVersionResponseVO
 * @description: 应用版本响应VO
 * @create 2018/6/27 20:01
 **/
@Data
@ApiModel(value="应用版本响应VO")
public class AppVersionResponseVO {

    @ApiModelProperty(value = "后台最新版本号", required = true)
    private String app_version;

    @ApiModelProperty(value = "是否升级", notes = "是否升级", required = true)
    private Boolean update_flag;

    @ApiModelProperty(value = "是否强制升级", notes = "是否强制升级", required = true)
    private Boolean force_update_flag;

    @ApiModelProperty(value = "应用下载地址", notes = "应用下载地址", required = true)
    private String download_url;

    @ApiModelProperty(value = "版本更新说明", notes = "版本更新说明", required = true)
    private String version_info;
}
