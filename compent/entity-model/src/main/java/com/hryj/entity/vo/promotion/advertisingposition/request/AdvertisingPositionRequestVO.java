package com.hryj.entity.vo.promotion.advertisingposition.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: AdvertisingPositionRequestVO
 * @description:
 * @create 2018/6/27 0027 22:43
 **/
@ApiModel(value = "新增或修改广告位请求VO", description = "新增或修改广告位请求VO")
@Data
public class AdvertisingPositionRequestVO extends RequestVO {

    @ApiModelProperty(value = "广告位ID，新增时不需要，修改时必须", required = false)
    private Long advertising_position_id;

    @ApiModelProperty(value = "广告位名称", required = true)
    private String advertising_name;

    @ApiModelProperty(value = "展示开始时间", required = true)
    private String start_date;

    @ApiModelProperty(value = "展示结束时间", required = true)
    private String end_date;

    @ApiModelProperty(value = "广告位图片URL", required = true)
    private String advertising_image;

    @ApiModelProperty(value = "广告位应用范围，01-仓库,02-门店", required = true)
    private String advertising_scope;

    @ApiModelProperty(value = "跳转类型:01-跳转url,02-跳转商品,03-跳转活动", required = true)
    private String jump_type;

    @ApiModelProperty(value = "跳转目标值:该字段值根据跳转类型存储对应的值", required = true)
    private String jump_value;

    @ApiModelProperty(value = "广告位类型, 01 banner广告, 缺省 01")
    private String advertising_type;

    @ApiModelProperty(value = "广告位的应用范围，门店或仓库集合数据", required = true)
    private List<AdvertisingPositionScopeRequestVO> party_list;
}
