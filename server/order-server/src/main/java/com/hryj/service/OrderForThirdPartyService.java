package com.hryj.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.cache.CodeCache;
import com.hryj.constant.OrderConstants;
import com.hryj.entity.bo.order.OrderCrossBorder;
import com.hryj.entity.bo.order.OrderProduct;
import com.hryj.entity.bo.payment.PaymentGroupOrder;
import com.hryj.entity.vo.order.crossOrder.CrossBorderOrderVo;
import com.hryj.mapper.OrderProductMapper;
import com.hryj.pay.vo.CustomDeclareRequstVO;
import com.hryj.utils.SendEmail;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author 罗秋涵
 * @className: OrderForThirdPartyService
 * @description: 第三方订单管理
 * @create 2018/9/13 0013 15:25
 **/
@Slf4j
@Service
@Component
public class OrderForThirdPartyService {

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private  PaymentGroupOrderService paymentGroupOrderService;

    @Autowired
    private  OrderProductMapper orderProductMapper;

    @Autowired
    private  OrderCrossBorderService orderCrossBorderService;
    /**
     * 光彩全球Api接口信息
     */
    private static final String GCQQ_API_URL = "https://www.qqbsmall.com/gcshop/index.php?gct=partner";
    private static final String PARTNER_ID = "89";
    private static final String APP_KEY = "3de4c6f9db20ce981231eeba403ae1f2";

    /**
     * 订单同步获取待同步订单列表
     */
    public void synchronizationOrder() {
        log.info("订单同步开始执行：{}", new Date());
        //1、获取待同步的订单信息(1、支付状态为已支付，2、订单类型为跨境订单，3、第三方订单ID为空且订单状态为待申报)
        List<CrossBorderOrderVo> orderList = orderService.findAwaitSyncOrderList(CodeCache.getValueByKey("PaymentStatus", "S02"),
                OrderConstants.CROSS_BORDER_BONDED_ORDER, OrderConstants.ThirdOrderStatus.waitDeclare.ordinal(), OrderConstants.CROSS_BORDER_PROD_TAX);
        if (orderList == null || orderList.size() == 0) {
            return;
        }
        //开5个线程
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(5);
        final List<Future> threadList = new ArrayList<>();
        for (CrossBorderOrderVo crossBorderOrderVo : orderList) {
            Future future = cachedThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    syncOrder(crossBorderOrderVo);
                }
            });
            threadList.add(future);
        }

        for (Future future : threadList) {
            try {
                //30秒响应失败则线程结束
                future.get(30000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                future.cancel(true);
            } catch (ExecutionException e) {
                future.cancel(true);
            } catch (TimeoutException e) {
                future.cancel(true);
            }
        }
        log.info("订单同步执行结束：{}", new Date());
    }



    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 订单同步
     * @param: []
     * @create 2018-09-13 15:30
     **/
    public void syncOrder(CrossBorderOrderVo crossBorderOrderVo) {
        try {
                PaymentGroupOrder paymentGroupOrder = new PaymentGroupOrder();
                paymentGroupOrder.setId(crossBorderOrderVo.getGroup_item_id());
                //如果子流水号为空时发起报关获取子流水号
                    //创建报关参数
                    CustomDeclareRequstVO declareRequstVO = createDeclareCustomParams(crossBorderOrderVo, false);
                    //发起报关申请
                    Map<String, Object> result = paymentService.declareCustom(crossBorderOrderVo.getApp_key(), crossBorderOrderVo.getPay_method(), crossBorderOrderVo.getPayment_channel(), declareRequstVO);
                    log.info("报关返回数据：{}", JSON.toJSONString(result));
                    if (result != null) {
                        //01-微信
                        if (crossBorderOrderVo.getPay_method().equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
                            if ("SUCCESS".equals(result.get("return_code")) && "SUCCESS".equals(result.get("result_code"))) {
                                if (result.get("sub_order_id") != null) {
                                    //子流水号不为空时支付平台订单号为微信子订单号
                                    crossBorderOrderVo.setPay_trade_num(result.get("sub_order_id").toString());
                                    paymentGroupOrder.setSub_pay_trade_num(result.get("sub_order_id").toString());
                                    paymentGroupOrderService.updateById(paymentGroupOrder);
                                } else {
                                    //子流水号为空时支付平台订单号为微信支付订单号
                                    crossBorderOrderVo.setPay_trade_num(result.get("transaction_id").toString());
                                    //回填子流水号
                                    paymentGroupOrder.setSub_pay_trade_num(result.get("transaction_id").toString());
                                    paymentGroupOrderService.updateById(paymentGroupOrder);

                                }
                            } else {
                                log.info("订单编号：{}，微信报关失败,原因：{}", crossBorderOrderVo.getOrder_num(), JSON.toJSONString(result));
                                return;
                            }

                        } else if (crossBorderOrderVo.getPay_method().equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
                            Map<String, Object> alipay = result;
                            //02 支付宝
                            if (alipay != null && "T".equals(alipay.get("is_success"))) {
                                //合并支付
                                if(crossBorderOrderVo.getPay_order_count()>1){

                                    //crossBorderOrderVo.setSub_pay_trade_num("");
                                }
                            } else {
                                log.info("订单编号：{}，支付宝报关失败,原因：{}", crossBorderOrderVo.getOrder_num(), JSON.toJSONString(result));
                                return;
                            }
                        }
                    } else {
                        log.info("订单编号：{}，报关失败,原因：{}", crossBorderOrderVo.getOrder_num(), JSON.toJSONString(result));
                        return;
                    }
                //获取订单商品信息
                EntityWrapper wrapper = new EntityWrapper();
                wrapper.eq("order_id", crossBorderOrderVo.getOrder_id());
                List<OrderProduct> productList = orderProductMapper.selectList(wrapper);
                if (productList != null && productList.size() > 0) {
                    crossBorderOrderVo.setProductList(productList);
                } else {
                    log.info("订单编号：{}，同步失败,原因：{}", crossBorderOrderVo.getOrder_num(), "订单商品信息异常");
                    return;
                }
                //创建同步参数
                String params = this.createParams(crossBorderOrderVo);
                //发起同步请求
                String data = restTemplate.postForObject(GCQQ_API_URL, params, String.class);
                if(StringUtil.isEmpty(data)){
                    log.info("订单编号：{}，同步失败,原因：{}", crossBorderOrderVo.getOrder_id(),"第三方同步订单接口返回数据异常");
                    SendEmail.sendEmail("订单："+crossBorderOrderVo.getOrder_id()+"同步失败", "第三方同步订单接口返回数据异常");
                    return;
                }
                JSONObject jsonData=JSONObject.parseObject(Base64.decodeStr(data));
                log.info("光彩响应----{}", jsonData);
                //JSONObject gc=(JSONObject)jsonData.get("gc");
                //成功
                if(jsonData != null && jsonData.get("head") != null && jsonData.get("body") != null){
                    JSONObject body=(JSONObject)jsonData.get("body");
                    if(body==null){
                        log.error("订单编号:{},同步失败，原因：{}",crossBorderOrderVo.getOrder_num(),"第三方同步订单接口返回数据异常"+jsonData);
                        return;
                    }
                    JSONObject order=(JSONObject)body.get("order");
                    if(order==null){
                        log.error("订单编号:{},同步失败，原因：{}",crossBorderOrderVo.getOrder_num(),"第三方同步订单接口返回数据异常"+body);
                        return;
                    }
                    //回填订单状态及第三方订单号
                    OrderCrossBorder orderCrossBorder=new OrderCrossBorder();
                    orderCrossBorder.setOrder_id(crossBorderOrderVo.getOrder_id());
                    //状态为待申中
                    orderCrossBorder.setThird_order_status(OrderConstants.ThirdOrderStatus.declareing.ordinal());
                    //第三方订单编号
                    orderCrossBorder.setThird_order_code(order.get("order_sn").toString());
                    orderCrossBorderService.updateById(orderCrossBorder);
                }else{
                    log.error("订单编号:{},同步失败，原因：{}",crossBorderOrderVo.getOrder_num(), jsonData.get("errorMessage"));
                }
        } catch (Exception e) {
            log.error("订单同步,失败：" + e.getMessage());
            log.error("程序异常：" + ExceptionUtil.stacktraceToString(e));
            SendEmail.sendEmail("订单同步,失败"+ExceptionUtil.getSimpleMessage(e), ExceptionUtil.stacktraceToString(e));
            e.printStackTrace();
        }
    }

    /**
     * @author 罗秋涵
     * @description: 组装支付报关参数
     * @param: [groupOrderMap, crossBorderOrderVo, isSplit]
     * @return com.hryj.pay.vo.CustomDeclareRequstVO
     * @create 2018-09-17 17:14
     **/
    public CustomDeclareRequstVO createDeclareCustomParams(CrossBorderOrderVo crossBorderOrderVo,Boolean isSplit){
        //合并支付和单订单支付分别报关情况
        //参数
        CustomDeclareRequstVO declareRequstVO=new CustomDeclareRequstVO();
        declareRequstVO.setPay_trade_num(crossBorderOrderVo.getPay_trade_num());
        declareRequstVO.setBuyer_cert_id(crossBorderOrderVo.getSubscriber_id_card());
        declareRequstVO.setBuyer_name(crossBorderOrderVo.getSubscriber_name());
        //报关金额支付宝用
        declareRequstVO.setDeclare_amt(crossBorderOrderVo.getPay_amt().toString());
        //单笔和合并支付都根据拆单
        if(isSplit){
            declareRequstVO.setPay_order_id(crossBorderOrderVo.getPay_group_sn());
            declareRequstVO.setSub_order_id(crossBorderOrderVo.getOrder_num());
            //金额转换成分（支付金额*100）
            declareRequstVO.setOrder_fee(crossBorderOrderVo.getPay_amt().multiply(new BigDecimal(100)).intValue());
            declareRequstVO.setProduct_fee(crossBorderOrderVo.getPay_amt().multiply(new BigDecimal(100)).intValue());
        }else {
                if(crossBorderOrderVo.getPay_order_count()>1){
                    declareRequstVO.setPay_order_id(crossBorderOrderVo.getPay_group_sn());
                    declareRequstVO.setSub_order_id(crossBorderOrderVo.getOrder_num());
                    //金额转换成分（支付金额*100）
                    declareRequstVO.setOrder_fee(crossBorderOrderVo.getPay_amt().multiply(new BigDecimal(100)).intValue());
                    declareRequstVO.setProduct_fee(crossBorderOrderVo.getPay_amt().multiply(new BigDecimal(100)).intValue());
                }else{
                    //02-支付宝支付
                    if (crossBorderOrderVo.getPay_method().equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
                        declareRequstVO.setPay_order_id(crossBorderOrderVo.getPay_group_sn());
                        declareRequstVO.setSub_order_id(crossBorderOrderVo.getOrder_num());
                    }else{
                        declareRequstVO.setPay_order_id(crossBorderOrderVo.getPay_group_sn());
                    }

                }
            }
        return declareRequstVO;
    }


    /**
     * @return java.lang.String
     * @author 罗秋涵
     * @description: 订单同步组装请求参数
     * @param: [orderSyncVO]
     * @create 2018-09-14 14:18
     **/
    public String createParams(CrossBorderOrderVo crossBorderOrderVo) throws UnsupportedEncodingException {
        String xmlbegin = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<xml>\n";
        String gcbegin = "<gc>\n";
        String head = "<head>\n" +
                "        <action>create_order</action>\n" +
                "        <return_type>json</return_type>\n" +
                "        <partner_id>" + PARTNER_ID + "</partner_id>\n" +
                "</head>\n";
        String bodybegin = "<body>\n";

        String reciver = "<reciver>\n" +
                "            <buy_name>" + crossBorderOrderVo.getSubscriber_name() + "</buy_name>\n" +
                "            <buy_mobile>" + crossBorderOrderVo.getUser_phone() + "</buy_mobile>\n" +
                "            <idnum>" + crossBorderOrderVo.getSubscriber_id_card() + "</idnum>\n" +
                "            <take_name>" + crossBorderOrderVo.getUser_name() + "</take_name>\n" +
                "            <take_mobile>" + crossBorderOrderVo.getUser_phone() + "</take_mobile>\n" +
                "            <province_id>" + crossBorderOrderVo.getProvince_id() + "</province_id>\n" +
                "            <city_id>" + crossBorderOrderVo.getCity_id() + "</city_id>\n" +
                "            <area_id>" + crossBorderOrderVo.getArea_id() + "</area_id>\n" +
                "            <area_info>" + crossBorderOrderVo.getArea_info() + "</area_info>\n" +
                "            <address>" + crossBorderOrderVo.getUser_address() + "</address>\n" +
                "        </reciver>\n";
        String goodsbegin = "<goods>\n";

        /**
         * 遍历组装商品信息
         */
        String detail = "";
        for (OrderProduct goods : crossBorderOrderVo.getProductList()) {
            detail = detail + "<detail>\n" +
                    "                <serial>" + goods.getThird_sku_id() + "</serial>\n" +
                    "                <price>" + goods.getActual_price() + "</price>\n" +
                    "                <num>" + goods.getQuantity() + "</num>\n" +
                    "</detail>\n";
        }
        String goodsend = "</goods>\n";
        /**
         * 支付方式默认设置为支付宝
         */
        String payment_code=null;
        if(crossBorderOrderVo.getPay_method().equals(CodeCache.getValueByKey("PaymentMethod", "S01"))){
            payment_code="wxpay_hr";

        }else if(crossBorderOrderVo.getPay_method().equals(CodeCache.getValueByKey("PaymentMethod", "S02"))){
            payment_code="alipay";
        }
        String is_split=null;
        if(crossBorderOrderVo.getPay_order_count()>1){
            is_split="1";
        }
        String other = "<other>\n" +
                "            <out_trade_no>" +crossBorderOrderVo.getOrder_num()+"</out_trade_no>\n" +
                "            <trade_no>" +crossBorderOrderVo.getPay_trade_num() + "</trade_no>\n" +
                "            <is_split>" +is_split+ "</is_split>\n" +
                "            <out_request_no>" +crossBorderOrderVo.getOrder_num() + "</out_request_no>\n" +
                "            <order_amount>" + crossBorderOrderVo.getPay_amt() + "</order_amount>\n" +
                "            <order_tax>" + crossBorderOrderVo.getOrder_tax() + "</order_tax>\n" +
                "            <payment_code>" + payment_code + "</payment_code>\n" +
                "            <payment_time>" + crossBorderOrderVo.getPay_time().getTime()/1000 + "</payment_time>\n" +
                "            <name>" + crossBorderOrderVo.getUser_name() + "</name>\n" +
                "            <note>" + crossBorderOrderVo.getOrder_remark() + "</note>\n" +
                "        </other>\n";
        String bodyend = "</body>\n";
        String gcend = "</gc>";
        String xmlend = "</xml>";
        String appkey = "<appkey>" + APP_KEY + "</appkey>";
        //生成签名参数
        String gcStr = gcbegin + head + bodybegin + reciver + goodsbegin + detail + goodsend + other + bodyend + gcend;
        String sign = "<sign>" + DigestUtils.md5Hex(gcStr + appkey) + "</sign>\n";
        //生成最终参数
        String params = URLEncoder.encode(Base64.encode(xmlbegin + gcStr + sign + xmlend), "UTF-8");
        log.info("签名参数：{}", gcStr + appkey);
        log.info("加密参数：{}", xmlbegin + gcStr + sign + xmlend);
        log.info("加密结果：{}", params);
        return params;
    }


}
