package com.hryj.entity.vo.product.common.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: PartyProductInventoryAdjustRequestVO
 * @description:
 * @create 2018/7/9 0009 16:35
 **/
@ApiModel(value = "当事组织（门店或仓库）库存调整请求VO", description = "当事组织（门店或仓库）库存调整请求VO")
@Data
public class PartyProductInventoryAdjustRequestVO extends RequestVO {

    @ApiModelProperty(value = "商品调整条目集合", required = true)
    private List<PartyProductInventoryAdjustItem> adjust_list;
}
