package com.hryj.pay;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.egzosn.pay.common.api.PayConfigStorage;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.http.HttpConfigStorage;
import com.egzosn.pay.common.http.HttpRequestTemplate;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.egzosn.pay.wx.api.WxPayConfigStorage;
import com.egzosn.pay.wx.api.WxPayService;
import com.egzosn.pay.wx.bean.WxPayError;
import com.egzosn.pay.wx.bean.WxTransactionType;
import com.hryj.pay.vo.CustomDeclareRequstVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author 李道云
 * @className: WxPay
 * @description: 微信支付
 * @create 2018/6/29 21:16
 **/
@Slf4j
public class WxPay {

    private static final String DECLARE_CUSTOMS_URL = "https://api.mch.weixin.qq.com/cgi-bin/mch/customs/customdeclareorder";

    /**
     * 获取WxPayService
     * @param payment_config
     * @return
     */
    public static WxPayService getWxPayService(JSONObject payment_config){
        WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
        wxPayConfigStorage.setAppid(payment_config.getString("appId"));
        wxPayConfigStorage.setMchId(payment_config.getString("mchId"));
        wxPayConfigStorage.setKeyPublic(payment_config.getString("keyPublic"));
        wxPayConfigStorage.setKeyPrivate(payment_config.getString("keyPrivate"));
        wxPayConfigStorage.setNotifyUrl(payment_config.getString("notifyUrl"));
        wxPayConfigStorage.setReturnUrl(payment_config.getString("returnUrl"));
        wxPayConfigStorage.setSignType(payment_config.getString("signType"));
        wxPayConfigStorage.setInputCharset(payment_config.getString("inputCharset"));
        wxPayConfigStorage.setTest(false);//是否为沙箱测试环境
        WxPayService service = new WxPayService(wxPayConfigStorage);
        return service;
    }

    /**
     * 获取WxPayService
     * @param payment_config
     * @return
     */
    public static WxPayService getWxPayServiceForRefund(JSONObject payment_config){
        WxPayConfigStorage wxPayConfigStorage = new WxPayConfigStorage();
        wxPayConfigStorage.setAppid(payment_config.getString("appId"));
        wxPayConfigStorage.setMchId(payment_config.getString("mchId"));
        wxPayConfigStorage.setKeyPublic(payment_config.getString("keyPublic"));
        wxPayConfigStorage.setKeyPrivate(payment_config.getString("keyPrivate"));
        wxPayConfigStorage.setNotifyUrl(payment_config.getString("notifyUrl"));
        wxPayConfigStorage.setReturnUrl(payment_config.getString("returnUrl"));
        wxPayConfigStorage.setSignType(payment_config.getString("signType"));
        wxPayConfigStorage.setInputCharset(payment_config.getString("inputCharset"));
        wxPayConfigStorage.setTest(false);//是否为沙箱测试环境
        HttpConfigStorage httpConfigStorage = new HttpConfigStorage();
        httpConfigStorage.setKeystore(payment_config.getString("keystore"));
        httpConfigStorage.setStorePassword(payment_config.getString("storePassword"));
        httpConfigStorage.setPath(true);//是否为证书地址
        WxPayService service = new WxPayService(wxPayConfigStorage, httpConfigStorage);
        return service;
    }

    /**
     * app支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static Map<String,Object> appPay(JSONObject payment_config, PayOrder payOrder){
        WxPayService service = getWxPayService(payment_config);
        payOrder.setTransactionType(WxTransactionType.APP);
        Map<String,Object> params = service.orderInfo(payOrder);
        Object packages = params.get("package");
        params.put("packages",packages);
        params.remove("package");
        log.info("微信APP支付签名：" + JSON.toJSONString(params));
        return params;
    }


    /**
     * 公众号支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static Map<String,Object> gzhPay(JSONObject payment_config, PayOrder payOrder){
        WxPayService service = getWxPayService(payment_config);
        payOrder.setTransactionType(WxTransactionType.JSAPI);
        Map<String,Object> params = service.orderInfo(payOrder);
        params.put("code", 0);
        log.info("微信公众号支付支付签名：" + JSON.toJSONString(params));
        return params;
    }

    /**
     * WAP网页支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static String wapPay(JSONObject payment_config,PayOrder payOrder){
        WxPayService service = getWxPayService(payment_config);
        payOrder.setTransactionType(WxTransactionType.MWEB);
        Map<String,Object> wapOrderInfo = service.orderInfo(payOrder);
        String directHtml = service.buildRequest(wapOrderInfo, MethodType.POST);
        return directHtml;
    }

    /**
     * 二维码支付
     * @param payment_config
     * @param payOrder
     * @return
     */
    public static byte[] genQrPay(JSONObject payment_config, PayOrder payOrder){
        WxPayService service = getWxPayService(payment_config);
        payOrder.setTransactionType(WxTransactionType.NATIVE);
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
        WxPayService service = getWxPayService(payment_config);
        payOrder.setTransactionType(WxTransactionType.MICROPAY);
        Map<String, Object> params = service.microPay(payOrder);
        return params;
    }

    /**
     * 订单交易查询
     * @param payment_config
     * @param tradeNo
     * @param outTradeNo
     * @return
     */
    public static Map<String, Object> query(JSONObject payment_config,String tradeNo,String outTradeNo){
        WxPayService service = getWxPayService(payment_config);
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
        WxPayService service = getWxPayService(payment_config);
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
        WxPayService service = getWxPayServiceForRefund(payment_config);
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
        WxPayService service = getWxPayService(payment_config);
        Map<String,Object> result = service.refundquery(tradeNo, out_trade_no);
        return result;
    }

    /**
     * 下载对账单：目前只支持日账单
     * @param payment_config
     * @param billDate 账单时间：日账单格式为yyyy-MM-dd
     * @param billType 账单类型：trade-指商户基于微信交易收单的业务账单;signcustomer-是指基于商户微信余额收入及支出等资金变动的帐务账单
     * @return
     */
    public static Map<String, Object> downloadBill(JSONObject payment_config, Date billDate, String billType){
        WxPayService service = getWxPayService(payment_config);
        Map<String,Object> result = service.downloadbill(billDate, billType);
        return result;
    }

    /**
     * 申报海关
     * @param payment_config
     * @param customDeclareRequstVO
     * @return
     */
    public static Map<String, Object> declareCustom(JSONObject payment_config, CustomDeclareRequstVO customDeclareRequstVO){
        WxPayService service = getWxPayService(payment_config);
        PayConfigStorage payConfigStorage = service.getPayConfigStorage();
        Map<String, Object> parameters = new TreeMap<>();
        parameters.put("appid", payConfigStorage.getAppid());
        parameters.put("mch_id", payConfigStorage.getPid());
        //parameters.put("nonce_str", SignUtils.randomStr());

        parameters.put("out_trade_no",customDeclareRequstVO.getPay_order_id());//支付订单id
        parameters.put("transaction_id",customDeclareRequstVO.getPay_trade_num());//支付交易流水号
        parameters.put("customs","ZHENGZHOU_ZH_ZS");//海关编号
        parameters.put("mch_customs_no",payment_config.getString("mch_customs_no"));//商户海关备案号
        if(StrUtil.isNotEmpty(customDeclareRequstVO.getAction_type())){
            parameters.put("action_type",customDeclareRequstVO.getAction_type());//报关类型：ADD 新增报关申请,MODIFY 修改报关信息
        }
        if(StrUtil.isNotEmpty(customDeclareRequstVO.getSub_order_id())){
            parameters.put("sub_order_no", customDeclareRequstVO.getSub_order_id());//子订单id
            parameters.put("fee_type", "CNY");//币种
            parameters.put("order_fee", customDeclareRequstVO.getOrder_fee());//应付金额
            parameters.put("transport_fee", customDeclareRequstVO.getTransport_fee() == null ? "0":customDeclareRequstVO.getTransport_fee());//物流费用
            parameters.put("product_fee", customDeclareRequstVO.getProduct_fee());//商品价格
        }
        if(StrUtil.isNotEmpty(customDeclareRequstVO.getBuyer_cert_id())){
            parameters.put("cert_type", "IDCARD");//证件类型
            parameters.put("cert_id", customDeclareRequstVO.getBuyer_cert_id());//订购人身份证号码
            parameters.put("name", customDeclareRequstVO.getBuyer_name());//订购人姓名
        }
        String sign_str = SignUtils.parameterText(parameters, "&",null);
        log.info("sign_str：" + sign_str);
        String sign = service.createSign(sign_str, payConfigStorage.getInputCharset());
        parameters.put("sign", sign);
        String requestXML = customsXml(parameters);
        log.info("requestXML：" + requestXML);
        HttpRequestTemplate requestTemplate = service.getHttpRequestTemplate();
        Map<String, Object> result = requestTemplate.postForObject(DECLARE_CUSTOMS_URL, requestXML, JSONObject.class);
        if (!"SUCCESS".equals(result.get("return_code"))) {
            throw new PayErrorException(new WxPayError(result.get("return_code").toString(), result.get("return_msg").toString(), JSON.toJSONString(result)));
        }
        log.info("微信支付申报海关请求响应：result= " + JSON.toJSONString(result));
        return result;
    }

    /**
     * 组装微信申报xml报文
     * @param sortMap
     * @return xml报文
     */
    private static String customsXml(Map<String, Object> sortMap){
        String xml = "<xml>";
        String signXml = "";
        for(Map.Entry<String,Object> entry : sortMap.entrySet()){
            String value = null == entry.getValue() ? "" : entry.getValue()+"";
            if(StringUtils.isEmpty(value)){
                continue;
            }
            value = NumberUtil.isNumber(value) ? value : "<![CDATA["+value+"]]>";
            if(!entry.getKey().equals("sign")){
                xml += "<"+ entry.getKey() +">" + value + "</"+ entry.getKey() +">";
            }
            if(entry.getKey().equals("sign")){
                signXml = "<sign>" +  value + "</sign>";
            }
        }
        xml += signXml + "</xml>";
        return xml;
    }
}
