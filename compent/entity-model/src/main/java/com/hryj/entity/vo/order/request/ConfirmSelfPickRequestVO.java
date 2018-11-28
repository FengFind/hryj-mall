package com.hryj.entity.vo.order.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 罗秋涵
 * @className: ConfirmSelfPickRequestVO
 * @description: 确认自提
 * @create 2018/7/5 0005 11:55
 **/
@Data
@ApiModel(value = "确认自提VO")
public class ConfirmSelfPickRequestVO extends RequestVO {

        @ApiModelProperty(value = "自提单Id")
        private Long self_pick_id;
}
