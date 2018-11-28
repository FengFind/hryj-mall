package com.hryj.entity.vo.promotion.indexNavigation.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 汪豪
 * @className: IndexNavigationRequestVO
 * @description:
 * @create 2018/8/23 0023 14:08
 **/
@ApiModel(value = "新增或修改用户端首页导航请求VO", description = "新增或修改用户端首页导航请求VO")
@Data
public class IndexNavigationRequestVO extends RequestVO {

    @ApiModelProperty(value = "导航ID，新增时不需要，修改时必须", required = false)
    private Long index_navigation_id;

    @ApiModelProperty(value = "导航名称", required = true)
    private String navigation_name;

    @ApiModelProperty(value = "导航图标", required = true)
    private String navigation_icon;

    @ApiModelProperty(value = "导航链接", required = true)
    private String navigation_link;

    @ApiModelProperty(value = "位置序号", required = true)
    private Integer sort_num;

}
