package com.hryj.sms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author 李道云
 * @className: AliYunSms
 * @description: 阿里云短信组件
 * @create 2018/7/3 19:22
 **/
@Slf4j
public class AliYunSms {

    /**
     * 商品名称:云通信短信API商品,开发者无需替换
     */
    private static final String product = "Dysmsapi";
    /**
     * 商品域名,开发者无需替换
     */
    private static final String domain = "dysmsapi.aliyuncs.com";

    private static final String accessKeyId = "LTAIDc46OwyyuMtf";

    private static final String accessKeySecret = "q5xSgpYVX5ymKKi8smKG03rrMudKw8";

    private static final String signName1 = "红瑞颐家";
    private static final String signName2 = "红瑞乐邦";

    /**
     * @author 李道云
     * @methodName: sendSingleSms
     * @methodDesc: 发送单条短信
     * @description:
     * @param: [phone_num, template_code, template_param]
     * @return Result
     * @create 2018-07-03 20:31
     **/
    public static Result sendSingleSms(String phone_num, String template_code, HashMap template_param){
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            //初始化acsClient,暂不支持region化（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            request.setPhoneNumbers(phone_num);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName2);
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(template_code);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            if (template_param != null) {
                request.setTemplateParam(JSON.toJSONString(template_param));
            }
            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            //request.setOutId("yourOutId");

            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            log.info("阿里云短信发送：sendSmsResponse={}",JSON.toJSONString(sendSmsResponse));
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                return new Result(CodeEnum.SUCCESS);
            }else if("isv.BUSINESS_LIMIT_CONTROL".equals(sendSmsResponse.getCode())){
                log.error("阿里云短信发送失败：sendSmsResponse={}",JSON.toJSONString(sendSmsResponse));
                return new Result(CodeEnum.FAIL_BUSINESS,"发送太频繁，请稍后再试");
            }else{
                log.error("阿里云短信发送失败：sendSmsResponse={}",JSON.toJSONString(sendSmsResponse));
                return new Result(CodeEnum.FAIL_BUSINESS,sendSmsResponse.getMessage());
            }
        }catch (ClientException e){
            log.error("阿里云短信发送异常：ClientException={}",JSON.toJSONString(e));
            return new Result(CodeEnum.FAIL_SERVER,e.getErrMsg());
        }
    }

    /**
     * @author 李道云
     * @methodName: sendBatchSms
     * @methodDesc: 发送批量短信
     * @description: phone_nums为多个手机号码,逗号分隔
     * @param: [phone_nums, template_code, template_param_list]
     * @return Result
     * @create 2018-07-03 20:48
     **/
    public static Result sendBatchSms(String phone_nums, String template_code, List<HashMap> template_param_list){
        try {
            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendBatchSmsRequest request = new SendBatchSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持JSON格式的批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumberJson(phone_nums);
            //必填:短信签名-支持不同的号码发送不同的短信签名
            request.setSignNameJson(JSONArray.toJSONString(new ArrayList<String>().add(signName2)));
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(template_code);
            //必填:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            if (template_param_list != null && template_param_list.size()>0) {
                request.setTemplateParamJson(JSONArray.toJSONString(template_param_list));
            }
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCodeJson("[\"90997\",\"90998\"]");

            SendBatchSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                return new Result(CodeEnum.SUCCESS);
            }else{
                log.error("阿里云短信发送失败：SendSmsResponse={}",JSON.toJSONString(sendSmsResponse));
                return new Result(CodeEnum.FAIL_BUSINESS,sendSmsResponse.getMessage());
            }
        }catch (ClientException e){
            log.error("阿里云短信发送异常：ClientException={}",JSON.toJSONString(e));
            return new Result(CodeEnum.FAIL_SERVER,e.getErrMsg());
        }
    }

}
