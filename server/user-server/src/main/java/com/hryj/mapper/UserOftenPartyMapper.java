package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.user.UserOftenParty;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * @author 李道云
 * @className: UserOftenPartyMapper
 * @description: 用户常用门店或仓库
 * @create 2018/8/16 10:01
 **/
@Component
public interface UserOftenPartyMapper extends BaseMapper<UserOftenParty> {

    /**
     * @author 李道云
     * @methodName: updateOtherUserOftenParty
     * @methodDesc: 更新用户其他常用门店或仓库为非默认
     * @description:
     * @param: [id,user_id]
     * @return void
     * @create 2018-08-16 10:21
     **/
    void updateOtherUserOftenParty(@Param("id") Long id, @Param("user_id") Long user_id);

}
