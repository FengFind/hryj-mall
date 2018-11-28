package com.hryj.entity.vo.userparty.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: UserPartyRequestVO
 * @description:
 * @create 2018/8/15 0015 16:47
 **/
@ApiModel(value = "用户可选门店请求VO", description = "用户可选门店请求VO")
@Data
public class UserPartyRequestVO extends RequestVO {

    @ApiModelProperty(value = "用户ID， 门店端需要传此参数， 用户端请忽略此参数")
    private Long user_id;
}
