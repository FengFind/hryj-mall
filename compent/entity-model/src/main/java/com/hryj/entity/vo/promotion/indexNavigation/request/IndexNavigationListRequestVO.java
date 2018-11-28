package com.hryj.entity.vo.promotion.indexNavigation.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 汪豪
 * @className: IndexNavigationListRequestVO
 * @description:
 * @create 2018/8/23 0023 14:24
 **/
@ApiModel(value = "新增或修改首页导航请求VO", description = "新增或修改首页导航请求VO")
@Data
public class IndexNavigationListRequestVO extends RequestVO {

    @ApiModelProperty(value = "用户端首页导航信息集合", required = true)
    private List<IndexNavigationRequestVO> navigation_list;
}
