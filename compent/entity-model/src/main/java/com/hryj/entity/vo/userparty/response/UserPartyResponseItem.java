package com.hryj.entity.vo.userparty.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 王光银
 * @className: UserPartyResponseItem
 * @description:
 * @create 2018/8/15 0015 16:48
 **/
@ApiModel(value = "用户可选门店响应条目", description = "用户可选门店响应条目")
@Data
public class UserPartyResponseItem {

    @ApiModelProperty(value = "门店或仓库id", required = true)
    private Long party_id;

    @ApiModelProperty(value = "组织类型, 01门店， 02仓库", required = true)
    private String dept_type;

    @ApiModelProperty(value = "门店或仓库名称", required = true)
    private String party_name;

    @ApiModelProperty(value = "门店或仓库地址", required = true)
    private String party_address;

    @ApiModelProperty(value = "用户位置到门店的距离, 单位：米")
    private Integer distance;

    @Override
    public int hashCode() {
        return party_id == null ? 0 : party_id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof UserPartyResponseItem) {
            UserPartyResponseItem item = (UserPartyResponseItem) obj;
            if (item.getParty_id() == null) {
                return false;
            }
            return item.getParty_id().equals(this.party_id);
        }
        return false;
    }
}
