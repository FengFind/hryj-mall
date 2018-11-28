package com.hryj.cache;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.hryj.common.Result;
import com.hryj.sms.AliYunSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * @author 李道云
 * @className: SmsCache
 * @description: 短信验证码缓存
 * @create 2018/7/4 15:07
 **/
@Slf4j
@Component
public class SmsCache {

    private static SmsCache smsCache;

    @Autowired
    private RedisService redisService;

    private static final int SMS_EXPIRE_TIME_SECOND = 60*10;//10分钟

    //短信模板：您的验证码为${verify_code}，该验证码5分钟内有效，请勿泄露于他人。
    private static final String SMS_TEMPLATE_01 = "SMS_139235817";
    //短信模板：您的验证码为${verify_code}，该验证码10分钟内有效，请勿泄露于他人。
    private static final String SMS_TEMPLATE_02 = "SMS_140720060";

    @PostConstruct
    public void init() {
        smsCache = this;
        smsCache.redisService = this.redisService;
    }

    /**
     * @author 李道云
     * @methodName: sendVerifyCode
     * @methodDesc: 发送验证码
     * @description:
     * @param: [phone_num]
     * @return boolean
     * @create 2018-07-04 15:26
     **/
    public static Result sendVerifyCode(String phone_num){
        String verify_code = RandomUtil.randomNumbers(6);
        HashMap template_param = new HashMap();
        template_param.put("verify_code",verify_code);
        Result result = AliYunSms.sendSingleSms(phone_num, SMS_TEMPLATE_02, template_param);
        //短信发送成功，将验证码缓存起来
        if(result.isSuccess()){
            smsCache.redisService.put1(CacheGroup.SMS.getValue(), phone_num, verify_code, SMS_EXPIRE_TIME_SECOND);
        }
        return result;
    }

    /**
     * @author 李道云
     * @methodName: verifySmsCode
     * @methodDesc: 校验短信验证码
     * @description:
     * @param: [phone_num, verify_code]
     * @return boolean
     * @create 2018-07-04 15:34
     **/
    public static boolean verifySmsCode(String phone_num, String verify_code){
        String cache_code = smsCache.redisService.get1(CacheGroup.SMS.getValue(), phone_num);
        if(StrUtil.isEmpty(cache_code)){
            log.error("验证码错误或已过期,phone_num={},verify_code={}", phone_num, verify_code);
            return false;
        }
        if(!cache_code.equals(verify_code)){
            log.error("验证码不正确,phone_num={},cache_code={},verify_code={}", phone_num, cache_code, verify_code);
            return false;
        }
        //验证成功,删除验证码
        smsCache.redisService.delete1(CacheGroup.SMS.getValue(), phone_num);
        return true;
    }

    /**
     * @author 李道云
     * @methodName: onlyVerifySmsCode
     * @methodDesc: 只是校验短信验证码，不删除验证码
     * @description:
     * @param: [phone_num, verify_code]
     * @return boolean
     * @create 2018-07-04 15:34
     **/
    public static boolean onlyVerifySmsCode(String phone_num, String verify_code){
        String cache_code = smsCache.redisService.get1(CacheGroup.SMS.getValue(), phone_num);
        if(StrUtil.isEmpty(cache_code)){
            log.error("验证码错误或已过期,phone_num={},verify_code={}", phone_num, verify_code);
            return false;
        }
        if(!cache_code.equals(verify_code)){
            log.error("验证码不正确,phone_num={},cache_code={},verify_code={}", phone_num, cache_code, verify_code);
            return false;
        }
        return true;
    }

}
