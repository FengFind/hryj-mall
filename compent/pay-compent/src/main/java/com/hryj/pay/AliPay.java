package com.hryj.pay;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.ali.api.AliPayConfigStorage;
import com.egzosn.pay.ali.api.AliPayService;
import com.egzosn.pay.ali.bean.AliTransactionType;
import com.egzosn.pay.common.api.PayConfigStorage;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.result.PayException;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.http.HttpRequestTemplate;
import com.egzosn.pay.common.http.UriVariables;
import com.google.common.collect.Maps;
import com.hryj.pay.vo.Ali;
import com.hryj.pay.vo.CustomDeclareRequstVO;
import com.hryj.utils.XmlUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author 李道云
 * @className: AliPay
 * @description: 支付宝支付
 * @create 2018/6/29 21:24
 **/
@Slf4j
public class AliPay {

    private final static String httpsReqUrl = "https://mapi.alipay.com/gateway.do";

    /**
     * 获取AliPayService
     * @param payment_config
     * @return
     */
    public static AliPayService getAliPayService(JSONObject payment_config){
        AliPayConfigStorage aliPayConfigStorage = new AliPayConfigStorage();
        aliPayConfigStorage.setPid(payment_config.getString("pid"));
        aliPayConfigStorage.setAppId(payment_config.getString("appId"));
        aliPayConfigStorage.setKeyPublic(payment_config.getString("keyPublic"));
        aliPayConfigStorage.setKeyPrivate(payment_config.getString("keyPrivate"));
        aliPayConfigStorage.setNotifyUrl(payment_config.getString("notifyUrl"));
        aliPayConfigStorage.setReturnUrl(payment_config.getString("returnUrl"));
        aliPayConfigStorage.setSignType(payment_config.getString("signType"));
        aliPayConfigStorage.setSeller(payment_config.getString("seller"));
        aliPayConfigStorage.setInputCharset(payment_config.getString("inputCharset"));
        aliPayConfigStorage.setTest(false);//是否为沙箱测试环境
        AliPayService service = new AliPayService(aliPayConfigStorage);
        return service;
    }

    /**
     * app支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static Map<String,Object> appPay(JSONObject payment_config,PayOrder payOrder){
        AliPayService service = getAliPayService(payment_config);
        payOrder.setTransactionType(AliTransactionType.APP);
        Map<String,Object> params = service.orderInfo(payOrder);
        log.info("支付宝支付签名：" + JSON.toJSONString(params));
        return params;
    }

    /**
     * WAP网页支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static Map<String,Object> wapPay(JSONObject payment_config,PayOrder payOrder){
        AliPayService service = getAliPayService(payment_config);
        payOrder.setTransactionType(AliTransactionType.WAP);
        Map<String,Object> params = service.orderInfo(payOrder);
        return params;
    }

    /**
     * PC网页支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static String pcPay(JSONObject payment_config,PayOrder payOrder){
        AliPayService service = getAliPayService(payment_config);
        payOrder.setTransactionType(AliTransactionType.DIRECT);
        Map<String,Object> pcOrderInfo = service.orderInfo(payOrder);
        String directHtml = service.buildRequest(pcOrderInfo, MethodType.POST);
        return directHtml;
    }

    /**
     * 二维码支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static byte[] genQrPay(JSONObject payment_config, PayOrder payOrder){
        AliPayService service = getAliPayService(payment_config);
        payOrder.setTransactionType(AliTransactionType.SWEEPPAY);
        BufferedImage image = service.genQrPay(payOrder);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "JPEG", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * 被动扫码付款(条码付)，刷卡付
     * @param payment_config
     * @param payOrder: 要传authCode条码信息
     * @return
     */
    public static Map<String,Object> microPay(JSONObject payment_config,PayOrder payOrder){
        AliPayService service = getAliPayService(payment_config);
        payOrder.setTransactionType(AliTransactionType.BAR_CODE);
        Map<String, Object> params = service.microPay(payOrder);
        return params;
    }

    /**
     * 声波支付
     * @param payment_config
     * @param payOrder: 要传authCode条码信息
     * @return
     */
    public static Map<String,Object> waveCodePay(JSONObject payment_config,PayOrder payOrder){
        AliPayService service = getAliPayService(payment_config);
        payOrder.setTransactionType(AliTransactionType.WAVE_CODE);
        Map<String, Object> result = service.microPay(payOrder);
        return result;
    }

    /**
     * 订单交易查询
     * @param payment_config
     * @param tradeNo
     * @param outTradeNo
     * @return
     */
    public static Map<String, Object> query(JSONObject payment_config,String tradeNo,String outTradeNo){
        AliPayService service = getAliPayService(payment_config);
        Map<String,Object> result = service.query(tradeNo, outTradeNo);
        return result;
    }

    /**
     * 订单交易关闭
     * @param payment_config
     * @param tradeNo 支付交易流水号
     * @param outTradeNo 我方支付订单号
     * @return
     */
    public static Map<String, Object> close(JSONObject payment_config,String tradeNo,String outTradeNo){
        AliPayService service = getAliPayService(payment_config);
        Map<String,Object> result = service.close(tradeNo, outTradeNo);
        return result;
    }

    /**
     * 订单申请退款
     * @param payment_config
     * @param refundOrder
     * @return
     */
    public static Map<String, Object> refund(JSONObject payment_config,RefundOrder refundOrder){
        AliPayService service = getAliPayService(payment_config);
        Map<String, Object> result = service.refund(refundOrder);
        return result;
    }

    /**
     * 订单退款查询
     * @param payment_config
     * @param tradeNo 支付交易流水号
     * @param out_trade_no 我方支付订单号
     * @return
     */
    public static Map<String, Object> refundQuery(JSONObject payment_config,String tradeNo,String out_trade_no){
        AliPayService service = getAliPayService(payment_config);
        Map<String,Object> result = service.refundquery(tradeNo, out_trade_no);
        return result;
    }

    /**
     * 下载对账单：目前只支持日账单
     * @param payment_config
     * @param billDate 账单时间：日账单格式为yyyy-MM-dd
     * @param billType 账单类型：trade-指商户基于支付宝交易收单的业务账单;signcustomer-是指基于商户支付宝余额收入及支出等资金变动的帐务账单
     * @return
     */
    public static Map<String, Object> downloadBill(JSONObject payment_config,Date billDate, String billType){
        AliPayService service = getAliPayService(payment_config);
        Map<String,Object> result = service.downloadbill(billDate, billType);
        return result;
    }

    /**
     * 申报海关
     * @param payment_config
     * @param customDeclareRequstVO
     * @return
     */
    public static Ali declareCustom(JSONObject payment_config, CustomDeclareRequstVO customDeclareRequstVO){
        AliPayService service = getAliPayService(payment_config);
        PayConfigStorage payConfigStorage = service.getPayConfigStorage();
        Map<String, Object> parameters = new TreeMap<>();
        parameters.put("service", "alipay.acquire.customs");
        parameters.put("partner", payConfigStorage.getPid());
        parameters.put("_input_charset", payConfigStorage.getInputCharset());
        parameters.put("sign_type", "MD5");

        parameters.put("out_request_no", customDeclareRequstVO.getPay_order_id());//支付订单id
        parameters.put("trade_no", customDeclareRequstVO.getPay_trade_num());//支付交易流水号
        parameters.put("merchant_customs_code", payment_config.getString("mch_customs_no"));//商户海关备案号
        parameters.put("amount", customDeclareRequstVO.getDeclare_amt());//报关金额
        parameters.put("customs_place", "ZONGSHU");//海关编号：ZONGSHU-重庆海关
        parameters.put("merchant_customs_name", "光彩国际重庆电子商务有限公司");//商户海关备案名称
        if(StrUtil.isNotEmpty(customDeclareRequstVO.getSub_order_id())){
            parameters.put("is_split", "T");
            parameters.put("sub_out_biz_no", customDeclareRequstVO.getSub_order_id());
        }
        if(StrUtil.isNotEmpty(customDeclareRequstVO.getBuyer_cert_id())){
            parameters.put("buyer_id_no", customDeclareRequstVO.getBuyer_cert_id());//订购人身份证号码
            parameters.put("buyer_name", customDeclareRequstVO.getBuyer_name());//订购人姓名
        }
        HttpRequestTemplate requestTemplate = service.getHttpRequestTemplate();

        //待请求参数数组
        Map<String, Object> sPara = buildRequestPara(parameters , "MD5", payment_config.getString("securityCheckCode"), payConfigStorage.getInputCharset());
        log.info("支付宝支付申报海关参数:" + JSON.toJSONString(sPara));
        String url = httpsReqUrl  + "?" + UriVariables.getMapToParameters(sPara);
        log.info("url:" + url);
        String resultStr = requestTemplate.getForObject(url, String.class);
        Ali alipay = XmlUtil.xmlToObject(resultStr, Ali.class);
        Map<String, Object> result = Maps.newHashMap();
        if (null == alipay || !"T".equals(alipay.getIs_success())) {
            throw new PayErrorException(new PayException(result.get("error").toString(), JSON.toJSONString(result)));
        }
        log.info("支付宝支付申报海关响应：result= " + JSON.toJSONString(result));
        return alipay;
    }


    /**
     * 支付单申报查询
     *
     * @param outRequestNo
     *          请求流水号
     * @param payment_config
     *
     * @return
     */
    public static  Map<String, Object> declareQuery(String outRequestNo, JSONObject payment_config) {
        AliPayService service = getAliPayService(payment_config);
        PayConfigStorage payConfigStorage = service.getPayConfigStorage();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("service", "alipay.overseas.acquire.customs.query");
        parameters.put("partner", "2088911644936980");
        parameters.put("_input_charset", "UTF-8");
        parameters.put("sign_type", "MD5");
        parameters.put("out_request_nos", outRequestNo);
        //待请求参数数组
        Map<String, Object> sPara = buildRequestPara(parameters , "MD5", payment_config.getString("securityCheckCode"), payConfigStorage.getInputCharset());
        log.info("支付宝支付申报海关参数:" + JSON.toJSONString(sPara));
        String url = httpsReqUrl  + "?" + UriVariables.getMapToParameters(sPara);
        log.info("url:" + url);
        HttpRequestTemplate requestTemplate = service.getHttpRequestTemplate();
        Map<String, Object> result = requestTemplate.getForObject(url, JSONObject.class);
        log.info("支付宝支付申报海关查询回执:" + JSON.toJSONString(sPara));
        if (!"T".equals(result.get("is_success"))) {
            throw new PayErrorException(new PayException(result.get("error").toString(), JSON.toJSONString(result)));
        }
        return result;
    }

    /**
     * 生成要请求给支付宝的参数数组
     * @param sParaTemp 请求前的参数数组
     * @return 要请求的参数数组
     */
    private static Map<String, Object> buildRequestPara(Map<String, Object> sParaTemp, String signType, String key, String inputCharset) {
        //除去数组中的空值和签名参数
        SortedMap<String, Object> sPara = new TreeMap<>();
        for(Map.Entry<String, Object> entry : sParaTemp.entrySet()){
            if(StringUtil.isNotBlank(entry.getKey()) && null != entry.getValue()){
                sPara.put(entry.getKey(), entry.getValue());
            }
        }
        //生成签名结果
        String mysign = "";
        String prestr = "";
        for(Map.Entry<String,Object> entry : sPara.entrySet()){
            if(!entry.getKey().equals("sign") && !entry.getKey().equals("sign_type")){
                prestr += prestr.equals("") ? entry.getKey() + "=" + entry.getValue() : "&" + entry.getKey() + "=" + entry.getValue();
            }
        }
        if(signType.equals("MD5") ) {
            mysign = DigestUtils.md5Hex(prestr + key);
        }
        //签名结果与签名方式加入请求提交参数组中
        sPara.put("sign", mysign);
        sPara.put("sign_type", signType);
        return sPara;
    }

}
