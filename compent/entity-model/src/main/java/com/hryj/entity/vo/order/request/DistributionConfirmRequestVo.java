package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.order.DistributionConfirmVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 叶方宇
 * @className: DistributionConfirmRequestVo
 * @description:
 * @create 2018/7/20 0020 10:45
 **/
@Data
@ApiModel(value = "配送,取货完成参数VO")
public class DistributionConfirmRequestVo extends RequestVO {

    @ApiModelProperty(value = "确认配送,取货操作完成参数", required = true)
    private List<DistributionConfirmVo> confirmRequestVoList;
}
