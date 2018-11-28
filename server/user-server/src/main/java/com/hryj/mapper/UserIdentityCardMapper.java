package com.hryj.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.hryj.entity.bo.user.UserIdentityCard;
import org.springframework.stereotype.Component;

/**
 * @author 罗秋涵
 * @className: UserIdentityCardMapper
 * @description:
 * @create 2018/9/12 0012 14:35
 **/
@Component
public interface UserIdentityCardMapper extends BaseMapper<UserIdentityCard> {

    UserIdentityCard getCardInfoByUserIdAndCardId(Long user_id,String identityCard);
}
