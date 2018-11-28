package com.hryj.entity.vo.staff.store.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 代廷波
 * @className: RoleUpdateStatusRequestVO
 * @description:
 * @create 2018/7/5 0005-10:26
 **/
@Data
@ApiModel(description = "修改门店状态VO")
public class StoreUpdateStatusRequestVO extends RequestVO {

    @ApiModelProperty(value = "门店id", required = true)
    private Long store_id;

    @ApiModelProperty(value = "门店状态:1-正常,0-停用", required = true)
    private Integer store_status;

    @Override
    public String toString() {
        return "StoreUpdateStatusRequestVO{" +
                "store_id=" + store_id +
                ", store_status=" + store_status +
                '}';
    }
}
