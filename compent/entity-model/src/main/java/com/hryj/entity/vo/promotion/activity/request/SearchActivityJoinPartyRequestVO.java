package com.hryj.entity.vo.promotion.activity.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: SearchActivityJoinPartyRequestVO
 * @description:
 * @create 2018/6/28 0028 13:57
 **/

@ApiModel(value = "查询促销活动的参与门店请求VO", description = "查询促销活动的参与门店请求VO")
@Data
public class SearchActivityJoinPartyRequestVO extends RequestVO {

    @ApiModelProperty(value = "页码", required = false)
    private Integer page_num = 1;

    @ApiModelProperty(value = "每页大小", required = false)
    private Integer page_size = 10;

    @ApiModelProperty(value = "促销活动ID, 参数为空时直接返回，不做任何处理", required = true)
    private Long activity_id;

    @ApiModelProperty(value = "门店或仓库名称", required = false)
    private String party_name;

    @ApiModelProperty(value = "店长姓名", required = false)
    private String party_leader;
}
