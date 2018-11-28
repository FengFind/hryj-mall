package com.hryj.entity.vo.staff.role.request;

import com.hryj.entity.vo.RequestVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author 代廷波
 * @className: RoleAddResourceRequestVO
 * @description:
 * @create 2018/7/5 0005-10:32
 **/
@Data
@ApiModel(description = "添加角色vo")
public class RoleAddResourceRequestVO extends RequestVO {
    @ApiModelProperty(value = "角色id", required = true)

    private Long role_id;

    @ApiModelProperty(value = "资源列表", required = true)
    private List<ResourceRequestVO> resourceList;


}
