package com.hryj.entity.vo.promotion.indexNavigation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author 汪豪
 * @className: IndexNavigationResponseVO
 * @description:
 * @create 2018/8/23 0023 14:32
 **/
@ApiModel(value = "用户端和后台管理首页导航配置条目响应VO", description = "用户端和后台管理首页导航配置条目响应VO")
@Data
public class IndexNavigationResponseVO {

    @ApiModelProperty(value = "导航ID，新增时不需要，修改时必须")
    private Long index_navigation_id;

    @ApiModelProperty(value = "导航名称")
    private String navigation_name;

    @ApiModelProperty(value = "导航图标")
    private String navigation_icon;

    @ApiModelProperty(value = "导航链接")
    private String navigation_link;

    @ApiModelProperty(value = "位置序号")
    private Integer sort_num;
}
