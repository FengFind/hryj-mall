package com.hryj.entity.vo.staff.team;

import lombok.Data;

/**
 * @author 李道云
 * @className: StaffStoreWhVO
 * @description: 员工能看到的门店和仓库
 * @create 2018/7/25 9:14
 **/
@Data
public class StaffStoreWhVO {

    /**
     * 门店id集合:","分隔
     */
    private String storeIdList;
    /**
     * 仓库id集合:","分隔
     */
    private String whIdList;
}
