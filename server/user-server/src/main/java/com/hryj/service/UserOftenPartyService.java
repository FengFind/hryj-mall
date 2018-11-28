package com.hryj.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.user.UserLoginToken;
import com.hryj.entity.bo.user.UserOftenParty;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.entity.vo.user.UserPartyVO;
import com.hryj.entity.vo.user.request.DefaultPartyRequestVO;
import com.hryj.mapper.UserLoginTokenMapper;
import com.hryj.mapper.UserOftenPartyMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 李道云
 * @className: UserOftenPartyService
 * @description: 用户常用门店或仓库
 * @create 2018/8/16 10:02
 **/
@Slf4j
@Service
public class UserOftenPartyService extends ServiceImpl<UserOftenPartyMapper, UserOftenParty> {

    @Autowired
    private UserLoginTokenMapper userLoginTokenMapper;

    @Autowired
    private UserService userService;

    /**
     * 获取用户默认门店
     * @param user_id
     * @return
     */
    public UserOftenParty getDefaultParty(Long user_id){
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("user_id",user_id);
        wrapper.eq("default_flag",1);
        return super.selectOne(wrapper);
    }


    /**
     * @author 李道云
     * @methodName: setDefaultParty
     * @methodDesc: 设置默认门店或者仓库
     * @description:
     * @param: [defaultPartyRequestVO]
     * @return com.hryj.common.Result
     * @create 2018-08-15 15:48
     **/
    @Transactional
    public Result setDefaultParty(DefaultPartyRequestVO defaultPartyRequestVO){
        if(defaultPartyRequestVO.getParty_id() ==null || defaultPartyRequestVO.getParty_id() <=0L){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "没有选择默认门店或仓库");
        }
        if(StrUtil.isEmpty(defaultPartyRequestVO.getDept_type())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "部门类型不能为空");
        }
        if(StrUtil.isEmpty(defaultPartyRequestVO.getParty_name())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "默认门店或仓库名称不能为空");
        }
        if(StrUtil.isEmpty(defaultPartyRequestVO.getParty_address())){
            return new Result<>(CodeEnum.FAIL_PARAMCHECK, "默认门店或仓库地址不能为空");
        }
        String login_token = defaultPartyRequestVO.getLogin_token();
        UserLoginVO userLoginVO = LoginCache.getUserLoginVO(login_token);
        UserOftenParty userOftenParty = this.saveUserOftenParty(userLoginVO.getUser_id(),defaultPartyRequestVO);
        UserPartyVO defaultParty = new UserPartyVO();
        BeanUtil.copyProperties(userOftenParty,defaultParty);
        //刷新缓存里的默认门店信息
        userLoginVO.setDefaultParty(defaultParty);
        LoginCache.setUserLoginVO(login_token,userLoginVO);
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * 保存用户选择的默认门店或仓库
     * @param user_id
     * @param defaultPartyRequestVO
     */
    public UserOftenParty saveUserOftenParty(Long user_id, DefaultPartyRequestVO defaultPartyRequestVO){
        EntityWrapper<UserOftenParty> wrapper = new EntityWrapper();
        wrapper.eq("user_id",user_id);
        wrapper.eq("party_id",defaultPartyRequestVO.getParty_id());
        UserOftenParty userOftenParty = super.selectOne(wrapper);
        if(userOftenParty ==null){
            userOftenParty = new UserOftenParty();
            userOftenParty.setUser_id(user_id);
            userOftenParty.setDept_type(defaultPartyRequestVO.getDept_type());
            userOftenParty.setParty_id(defaultPartyRequestVO.getParty_id());
            userOftenParty.setParty_name(defaultPartyRequestVO.getParty_name());
            userOftenParty.setParty_address(defaultPartyRequestVO.getParty_address());
            userOftenParty.setDistance(defaultPartyRequestVO.getDistance());
        }
        userOftenParty.setDefault_flag(1);
        userOftenParty.setCreate_time(null);
        userOftenParty.setUpdate_time(null);
        if(userOftenParty.getId() !=null){
            baseMapper.updateById(userOftenParty);
        }else{
            baseMapper.insert(userOftenParty);
        }
        //更新用户其他门店或仓库为非默认
        baseMapper.updateOtherUserOftenParty(userOftenParty.getId(),user_id);
        return userOftenParty;
    }

    /**
     * @author 李道云
     * @methodName: updateUserOftenPartyInfo
     * @methodDesc: 更新用户常用门店或仓库的基本信息
     * @description:
     * @param: [party_id, party_name, party_address]
     * @return com.hryj.common.Result
     * @create 2018-08-17 17:31
     **/
    @Transactional
    public Result updateUserOftenPartyInfo(Long party_id, String party_name, String party_address){
        if(party_id ==null || party_id <=0L){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"门店或仓库id不能为空");
        }
        if(StrUtil.isEmpty(party_name) && StrUtil.isEmpty(party_address)){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"门店或仓库名称和地址不能都为空");
        }
        EntityWrapper<UserOftenParty> wrapper = new EntityWrapper<>();
        wrapper.eq("party_id",party_id);
        List<UserOftenParty> userOftenPartyList = baseMapper.selectList(wrapper);
        List<String> tokenList = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(userOftenPartyList)){
            for (UserOftenParty userOftenParty : userOftenPartyList){
                userOftenParty.setParty_name(party_name);
                userOftenParty.setParty_address(party_address);
                userOftenParty.setCreate_time(null);
                userOftenParty.setUpdate_time(null);
                if(userOftenParty.getDefault_flag() ==1){//如果为默认门店,刷新缓存
                    EntityWrapper<UserLoginToken> wrapper1 = new EntityWrapper<>();
                    wrapper1.eq("user_id",userOftenParty.getUser_id());
                    List<UserLoginToken> userLoginTokenList = userLoginTokenMapper.selectList(wrapper1);
                    if(CollectionUtil.isNotEmpty(userLoginTokenList)){
                        for (UserLoginToken userLoginToken : userLoginTokenList){
                            String login_token = userLoginToken.getLogin_token();
                            if(StrUtil.isNotEmpty(login_token)){
                                tokenList.add(login_token);
                            }
                        }
                    }
                }
            }
            super.updateBatchById(userOftenPartyList);
            log.info("更新用户常用门店或仓库的基本信息:" + JSON.toJSONString(userOftenPartyList));
        }
        if(CollectionUtil.isNotEmpty(tokenList)){
            for (String login_token : tokenList){
                userService.flushUserLoginVO(login_token);
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }

}
