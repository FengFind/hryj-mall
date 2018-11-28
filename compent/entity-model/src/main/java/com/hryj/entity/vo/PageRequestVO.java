package com.hryj.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 李道云
 * @className: PageRequestVO
 * @description: 分页查询请求VO
 * @create 2018/7/9 16:54
 **/
@ApiModel(value = "分页查询请求VO")
@Data
public class PageRequestVO extends RequestVO {

    @ApiModelProperty(value = "页码", required = true)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = true)
    private Integer page_size = 10;
}
