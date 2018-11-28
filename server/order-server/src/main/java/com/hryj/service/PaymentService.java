package com.hryj.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.bean.RefundOrder;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.order.OrderInfo;
import com.hryj.entity.bo.payment.PaymentGroup;
import com.hryj.entity.bo.payment.PaymentGroupOrder;
import com.hryj.entity.vo.order.request.ConfirmPayOrderRequestVO;
import com.hryj.entity.vo.payment.PaymentGroupInfo;
import com.hryj.mapper.OrderProductMapper;
import com.hryj.mapper.PaymentGroupMapper;
import com.hryj.mapper.PaymentGroupOrderMapper;
import com.hryj.pay.AliPay;
import com.hryj.pay.WxPay;
import com.hryj.pay.vo.Ali;
import com.hryj.pay.vo.CustomDeclareRequstVO;
import com.hryj.utils.CommonUtil;
import com.hryj.utils.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 罗秋涵
 * @className: PaymentService
 * @description: 支付服务service
 * @create 2018/7/10 15:24
 **/
@Slf4j
@Service
public class PaymentService extends ServiceImpl<PaymentGroupMapper, PaymentGroup>{

        @Autowired
        private PaymentGroupMapper paymentGroupMapper;

        @Autowired
        private PaymentGroupOrderMapper paymentGroupOrderMapper;

        @Autowired
        private OrderService orderService;

        @Autowired
        private PaymentGroupOrderService paymentGroupOrderService;

        @Autowired
        private OrderProductMapper orderProductMapper;

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @methodName: confirmPayOrder
     * @methodDesc: 订单支付签名 v1.2 修改，内容：根据订单类型获取对应支付渠道
     * @description:
     * @param: [confirmPayOrderRequestVO]
     * @create 2018-07-10 19:56
     **/
    @Transactional
    public Result<Map> confirmPayOrder(ConfirmPayOrderRequestVO confirmPayOrderRequestVO) {
        log.info("订单支付签名:confirmPayOrderRequestVO===" + JSON.toJSONString(confirmPayOrderRequestVO));
        if (confirmPayOrderRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        String orderNumStr = confirmPayOrderRequestVO.getOrderNumStr();
        String payment_method = confirmPayOrderRequestVO.getPay_method();
        if (StrUtil.isEmpty(orderNumStr)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        if (StrUtil.isEmpty(payment_method)) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "支付方式不能为空");
        }
        List<String> numlist = CommonUtil.stringToList(confirmPayOrderRequestVO.getOrderNumStr());
        if (numlist == null || numlist.size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        //1、获取订单基本信息
        List<OrderInfo> orderList = orderService.findOrderInfoList(numlist);
        log.info("查询订单信息:orderInfo===" + JSON.toJSONString(orderList));
        if (orderList == null || orderList.size() == 0) {
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应订单信息");
        }
        //2、判断订单是否存在支付中的支付组，如果存在需要向支付平台查询支付结果，防止重复支付
        //获取字符信息参数 默认为： 100000：红瑞集团，100001：光彩国际
        Long party_id=100000L;
        //记录同一批第一个订单的所属渠道，用于防止不同渠道合并支付
        Long party_id_no1=100000L;
        StringBuffer order_num = new StringBuffer();
        BigDecimal pay_amt = BigDecimal.ZERO;
        List<PaymentGroupOrder> groupOrderList = new ArrayList<>();
        for (int i=0;i<orderList.size();i++) {
            OrderInfo info =orderList.get(i);
            // v1.2  luoqh add  根据订单类型获取对应支付渠道 begin
            //更具订单类型获取支付渠道
            party_id=orderService.getOrderPayChannel(info.getOrder_type());
            if(party_id==null){
                log.info("订单类型异常，无法根据订单类型判断支付渠道，订单编号{}。",info.getId());
                return new Result(CodeEnum.FAIL_BUSINESS, "支付失败，请联系"+CodeCache.getValueByKey("CustomerServicePhone","S01"));
            }
            //获取第一个订单的渠道
            if(i==0){
                party_id_no1=party_id;
            }
            //如果渠道不同不能支付
            if(!party_id.equals(party_id_no1)){
                return new Result(CodeEnum.FAIL_BUSINESS, "跨境订单和新零售订单不能合并支付");
            }
            // v1.2  luoqh add  根据订单类型获取对应支付渠道 end
            //如果订单状态不是待支付不能发起支付
            if (!CodeCache.getValueByKey("OrderStatus", "S01").equals(info.getOrder_status())) {
                order_num.append(info.getOrder_num() + ",");
            }
            PaymentGroupInfo groupInfo = paymentGroupMapper.getPaymentGroupInfoByOrderId(info.getId(), CodeCache.getValueByKey("PaymentStatus", "S01"));
            if (groupInfo != null) {
                //根据支付类型查询支付结果
                Map<String, Object> result = queryDealStatus(groupInfo.getApp_key(), groupInfo.getPayment_method(), groupInfo.getPay_trade_num(),groupInfo.getPay_group_sn(),groupInfo.getPayment_channel());
                log.info("支付订单时查询订单支付结果：" + JSON.toJSONString(result));
                //微信
                if (result != null && groupInfo.getPayment_method().equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
                    //先处理异常情况
                    if ("FAIL".equals(result.get("result_code"))) {
                        //微信交易订单号不存在
                        if ("ORDERNOTEXIST".equals(result.get("err_code"))) {
                            log.info("微信交易订单号不存在");
                            return new Result(CodeEnum.FAIL_BUSINESS, "微信交易订单号不存在");
                        } else if ("SYSTEMERROR".equals(result.get("err_code"))) {
                            log.info("微信支付服务异常");
                            return new Result(CodeEnum.FAIL_BUSINESS, "微信支付服务异常");
                        }
                    }
                    //判断是否有返回支付业务状态
                    if (result.get("trade_state") == null) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "支付异常");
                    }
                    //支付成功
                    if ("SUCCESS".equals(result.get("trade_state"))) {
                        orderService.callbackAfterPay(result, groupInfo.getPayment_method(), result.get("out_trade_no").toString(), result.get("transaction_id").toString());
                        order_num.append(info.getOrder_num() + ",");
                    } else if ("PAYERROR".equals(result.get("trade_state"))) {
                        //更改支付状态
                        PaymentGroup paymentGroup = new PaymentGroup();
                        paymentGroup.setId(groupInfo.getPay_group_id());
                        paymentGroup.setPay_status(CodeCache.getValueByKey("PaymentStatus", "S03"));
                        paymentGroupMapper.updateById(paymentGroup);
                    } else if ("NOTPAY".equals(result.get("trade_state"))) {
                        //关闭微信交易
                        Map<String, Object> closeresult = closeTrader(groupInfo.getApp_key(), groupInfo.getPayment_method(), groupInfo.getPay_trade_num(), groupInfo.getPay_group_id().toString(),party_id);
                        log.info("订单未支付关闭交易（微信）:" + closeresult);
                        //更改支付组状态
                        PaymentGroup paymentGroup = new PaymentGroup();
                        paymentGroup.setId(groupInfo.getPay_group_id());
                        paymentGroup.setPay_status(CodeCache.getValueByKey("PaymentStatus", "S04"));
                        paymentGroupMapper.updateById(paymentGroup);
                    }
                    //支付宝
                } else if (result != null && groupInfo.getPayment_method().equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
                    //获得查询结果
                    Map<String, Object> aliPayresult = (Map<String, Object>) result.get("alipay_trade_query_response");
                    //先处理异常情况
                    if ("20000".equals(aliPayresult.get("code"))) {
                        //微信交易订单号不存在
                        if ("ACQ.SYSTEM_ERROR".equals(result.get("sub_code"))) {
                            log.info("支付宝系统错误");
                            return new Result(CodeEnum.FAIL_BUSINESS, "支付宝系统错误");
                        } else if ("ACQ.INVALID_PARAMETER".equals(result.get("sub_code"))) {
                            log.info("支付参数异常，检查请求参数");
                            return new Result(CodeEnum.FAIL_BUSINESS, "支付参数异常，检查请求参数");
                        } else if ("ACQ.TRADE_NOT_EXIST".equals(result.get("sub_code"))) {
                            log.info("查询的交易不存在");
                            return new Result(CodeEnum.FAIL_BUSINESS, "查询的交易不存在");
                        }
                    }
                    //检测业务状态是否有值
                    if (aliPayresult.get("trade_status") == null) {
                        //return new Result(CodeEnum.FAIL_BUSINESS, "支付异常");
                        aliPayresult.put("trade_status","WAIT_BUYER_PAY");
                    }
                    //结果为支付成功处理
                    if ("10000".equals(aliPayresult.get("code").toString()) && "TRADE_SUCCESS".equals(aliPayresult.get("trade_status").toString())) {
                        log.info("支付宝查询结果为支付成功，手动执行订单回调接口" + result);
                        orderService.callbackAfterPay(aliPayresult, groupInfo.getPayment_method(), aliPayresult.get("out_trade_no").toString(), aliPayresult.get("trade_no").toString());
                        order_num.append(info.getOrder_num() + ",");
                    } else {
                        //关闭支付宝交易
                        Map<String, Object> closeresult = closeTrader(groupInfo.getApp_key(), groupInfo.getPayment_method(), groupInfo.getPay_trade_num(), groupInfo.getPay_group_id().toString(),party_id);
                        log.info("订单未支付关闭交易（支付宝）:" + closeresult);
                        //更改支付组状态
                        PaymentGroup paymentGroup = new PaymentGroup();
                        paymentGroup.setId(groupInfo.getPay_group_id());
                        paymentGroup.setPay_status(CodeCache.getValueByKey("PaymentStatus", "S04"));
                        paymentGroupMapper.updateById(paymentGroup);
                    }
                }
            }
            //创建支付组订单记录
            PaymentGroupOrder paymentGroupOrder = new PaymentGroupOrder();
            paymentGroupOrder.setOrder_id(info.getId());
            paymentGroupOrder.setOrder_num(info.getOrder_num());
            paymentGroupOrder.setPay_amt(info.getPay_amt());
            groupOrderList.add(paymentGroupOrder);
            //叠加支付金额
            pay_amt = NumberUtil.add(pay_amt, info.getPay_amt());
        }
        if (order_num.length() > 0) {
            return new Result(CodeEnum.FAIL_BUSINESS, "订单态变更，请刷新列表");
        }
        //3、创建支付组
        PaymentGroup paymentGroup = new PaymentGroup();
        paymentGroup.setApp_key(confirmPayOrderRequestVO.getApp_key());
        paymentGroup.setPayment_type(CodeCache.getValueByKey("PaymentType", "S01"));
        paymentGroup.setPayment_method(confirmPayOrderRequestVO.getPay_method());
        paymentGroup.setPay_total_amt(pay_amt);
        paymentGroup.setPay_status(CodeCache.getValueByKey("PaymentStatus", "S01"));
        // 支付渠道，v1.2新增
        paymentGroup.setPayment_channel(party_id);
        //支付组编号，v1.2新增
        paymentGroup.setPay_group_sn(CommonUtil.getDateRandom());
        paymentGroup.setId(IdWorker.getId());
        //支付请求信息
        PayOrder payOrder = new PayOrder();
        payOrder.setSubject("红瑞颐家");
        payOrder.setBody("订单支付摘要");
        payOrder.setPrice(pay_amt);
        payOrder.setOutTradeNo(paymentGroup.getPay_group_sn());
        //循环设置组订单记录中组ID
        for (int i = 0; i < groupOrderList.size(); i++) {
            groupOrderList.get(i).setPay_group_id(paymentGroup.getId());
        }
        //批量插入记录
        paymentGroupOrderService.insertBatch(groupOrderList);
        //4、生成支付签名
        Map<String, Object> pay_map = getPaySign(payOrder, payment_method, confirmPayOrderRequestVO.getApp_key(),party_id);
        pay_map.put("out_trade_no", payOrder.getOutTradeNo());
        paymentGroup.setRequest_data(JSON.toJSONString(pay_map));
        //插入支付组
        paymentGroupMapper.insert(paymentGroup);
        log.info("支付签名：" + pay_map);
        return new Result(CodeEnum.SUCCESS, pay_map);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @methodName: notifyForWxPay
     * @methodDesc: 微信支付回调
     * @description:
     * @param: [param_map]
     * @create 2018-07-10 20:01
     **/
    @Transactional
    public Result notifyForWxPay(Map<String, Object> param_map) {
        log.info("微信回调开始："+param_map);
        Result result = new Result();
        if ("SUCCESS".equals(param_map.get("result_code"))) {
            // 系统订单编号
            String out_trade_no = param_map.get("out_trade_no").toString();
            // 微信支付订单号
            String transaction_id = param_map.get("transaction_id").toString();
            result = orderService.callbackAfterPay(param_map, CodeCache.getValueByKey("PaymentMethod", "S01"), out_trade_no, transaction_id);
            log.info("回调执行结果："+result);
        }
        return result;
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @methodName: notifyForAliPay
     * @methodDesc: 支付宝支付回调
     * @description:
     * @param: [param_map]
     * @create 2018-07-10 20:01
     **/
    @Transactional
    public Result notifyForAliPay(Map<String, Object> param_map) {
        log.info("支付宝回调开始："+param_map);
        Result result = new Result();
        if ("TRADE_SUCCESS".equals(param_map.get("trade_status"))) {
            // 系统订单编号
            String out_trade_no = param_map.get("out_trade_no").toString();
            // 支付订单号
            String trade_no = param_map.get("trade_no").toString();
            result = orderService.callbackAfterPay(param_map, CodeCache.getValueByKey("PaymentMethod", "S02"), out_trade_no, trade_no);
        } else if ("TRADE_FINISHED".equals(param_map.get("trade_status"))) {
            // 注意：
            // 该种交易状态只在两种情况下出现
            // 1、开通了普通即时到账，买家付款成功后。
            // 2、开通了高级即时到账，从该笔交易成功时间算起，过了签约时的可退款时限（如：三个月以内可退款、一年以内可退款等）后。\
            // 系统订单编号
            String out_trade_no = param_map.get("out_trade_no").toString();
            // 订单号
            String trade_no = param_map.get("trade_no").toString();
            result = orderService.callbackAfterPay(param_map, CodeCache.getValueByKey("PaymentMethod", "S02"), out_trade_no, trade_no);
        }
        log.info("回调结果："+result);
        return result;

    }


    /**
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author 罗秋涵
     * @description: 获取支付秘钥
     * @param: [payOrder, payment_type, payment_method]
     * @create 2018-07-11 11:29
     **/
    public Map<String, Object> getPaySign(PayOrder payOrder, String payment_method, String app_key,Long party_id) {
        String paymentConfig = CodeCache.getValueByKey("PaymentConfig", "S" + app_key + party_id + payment_method);
        log.info("获取支付秘钥，支付配置信息：paymentConfig===" + paymentConfig);
        log.info("获取支付秘钥：payOrder===" + JSON.toJSONString(payOrder));
        JSONObject payment_config = JSON.parseObject(paymentConfig);
        Map<String, Object> pay_map = new HashMap<>();
        //01-微信支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
            pay_map = WxPay.appPay(payment_config, payOrder);
        }
        //02-支付宝支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
            pay_map = AliPay.appPay(payment_config, payOrder);
        }
        return pay_map;
    }

    /**
     * @return java.util.Map<java.lang.String                                                                                                                               ,                                                                                                                               java.lang.Object>
     * @author 罗秋涵
     * @description: 订单交易查询
     * @param: [payment_type, payment_method, tradeNo, outTradeNo]
     * @create 2018-07-11 11:44
     **/
    public Map<String, Object> queryDealStatus(String app_key, String payment_method, String tradeNo, String outTradeNo,Long party_id) {
        String paymentConfig = CodeCache.getValueByKey("PaymentConfig", "S" + app_key + party_id + payment_method);
        log.info("订单交易查询，支付配置信息：paymentConfig===" + paymentConfig);
        JSONObject payment_config = JSON.parseObject(paymentConfig);
        Map<String, Object> result = new HashMap<>();
        //01-微信支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
            result = WxPay.query(payment_config, tradeNo, outTradeNo);
        }
        //02-支付宝支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
            result = AliPay.query(payment_config, tradeNo == null ? " " : tradeNo, outTradeNo);
        }
        return result;
    }


    /**
     * @return java.util.Map<java.lang.String                                                                                                                               ,                                                                                                                               java.lang.Object>
     * @author 罗秋涵
     * @description: 关闭交易
     * @param: [payment_type, payment_method, tradeNo, outTradeNo]
     * @create 2018-07-11 11:44
     **/
    public Map<String, Object> closeTrader(String app_key, String payment_method, String tradeNo, String outTradeNo,Long party_id) {
        String paymentConfig = CodeCache.getValueByKey("PaymentConfig", "S" + app_key + party_id  + payment_method);
        log.info("关闭交易，支付配置信息：paymentConfig===" + paymentConfig);
        JSONObject payment_config = JSON.parseObject(paymentConfig);
        Map<String, Object> result = new HashMap<>();
        //01-微信支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
            result = WxPay.close(payment_config, tradeNo, outTradeNo);
        }
        //02-支付宝支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
            result = AliPay.close(payment_config, tradeNo == null ? " " : tradeNo, outTradeNo);
        }
        return result;
    }


    /**
     * @author 罗秋涵
     * @description: 申报海关
     * @param: [app_key, payment_method, party_id, customDeclareRequstVO]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-09-17 13:51
     **/
    public Map<String, Object> declareCustom(String app_key, String payment_method,Long party_id,CustomDeclareRequstVO customDeclareRequstVO) {
        String paymentConfig = CodeCache.getValueByKey("PaymentConfig", "S" + app_key + party_id  + payment_method);
        log.info("申报海关 ，支付配置信息：paymentConfig===" + paymentConfig);
        JSONObject payment_config = JSON.parseObject(paymentConfig);
        Map<String, Object> result = new HashMap<>();
        //01-微信支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
            result = WxPay.declareCustom(payment_config, customDeclareRequstVO);
        }
        //02-支付宝支付
        if (payment_method.equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
            Ali ali = AliPay.declareCustom(payment_config, customDeclareRequstVO);
            result = XmlUtil.objectToMap(ali);
        }
        return result;
    }


    /**
     * @return java.util.Map<java.lang.String                                                                                                                               ,                                                                                                                               java.lang.Object>
     * @author 罗秋涵
     * @description: 发起退款
     * @param: [payment_type, payment_method, refundOrder]
     * @create 2018-07-12 10:00
     **/
    public Result refund(Long order_id) {
        log.info("发起退款，退款订单号："+order_id);
        try {
            if(order_id==null){
                return new Result(CodeEnum.FAIL_BUSINESS, "参数为空");
            }
            //获取订单信息
            OrderInfo orderInfo = orderService.selectById(order_id);

            //获取支付组信息
            PaymentGroupInfo groupInfo = paymentGroupMapper.getPaymentGroupInfoByOrderId(orderInfo.getId(),null);
            if (groupInfo == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "没有订单对应的支付信息");
            }
            if(!CodeCache.getValueByKey("PaymentStatus","S02").equals(groupInfo.getPay_status())){
                return new Result(CodeEnum.FAIL_BUSINESS, "该订单不能发起退款");
            }
            RefundOrder refundOrder = new RefundOrder();
            //支付方编号
            refundOrder.setTradeNo(groupInfo.getPay_trade_num());
            //系统内部编号
            refundOrder.setOutTradeNo(groupInfo.getPay_group_sn());
            //退款金额
            refundOrder.setRefundAmount(orderInfo.getPay_amt());
            //支付订单总金额
            refundOrder.setTotalAmount(groupInfo.getPay_amt());
            //退款订单编号
            refundOrder.setRefundNo(orderInfo.getId().toString());
            String paymentConfig = CodeCache.getValueByKey("PaymentConfig", "S" + groupInfo.getApp_key() + groupInfo.getPayment_channel() + groupInfo.getPayment_method());
            log.info("发起退款，支付配置信息：paymentConfig===" + paymentConfig);
            JSONObject payment_config = JSON.parseObject(paymentConfig);
            log.info("退款支付参数：" + payment_config);
            log.info("退款订单参数：" + JSON.toJSONString(refundOrder));
            Map<String, Object> result = new HashMap<>();
            //01-微信退款
            if (groupInfo.getPayment_method().equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
                result = WxPay.refund(payment_config, refundOrder);
                log.info("微信退款结果："+result);
                if(result==null){
                    return new Result(CodeEnum.FAIL_BUSINESS, "退货失败，请联系"+CodeCache.getValueByKey("CustomerServicePhone","S01"));
                }
                if (!"SUCCESS".equals(result.get("result_code").toString())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "退货失败，请联系"+CodeCache.getValueByKey("CustomerServicePhone","S01"));
                }
            }
            //02-支付宝退款
            if (groupInfo.getPayment_method().equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
                result = AliPay.refund(payment_config, refundOrder);
                log.info("支付宝退款结果："+result);
                if(result==null||result.get("alipay_trade_refund_response")==null){
                    return new Result(CodeEnum.FAIL_BUSINESS, "退货失败，请联系"+CodeCache.getValueByKey("CustomerServicePhone","S01"));
                }
                Map<String, Object> refundResult= (Map<String, Object>) result.get("alipay_trade_refund_response");
                if (!"10000".equals(refundResult.get("code"))) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "退货失败，请联系"+CodeCache.getValueByKey("CustomerServicePhone","S01"));
                }
            }
            return new Result(CodeEnum.SUCCESS);
        } catch (Exception e) {
            return new Result(CodeEnum.FAIL_BUSINESS,"退货失败，请联系"+CodeCache.getValueByKey("CustomerServicePhone","S01"));
        }
    }


}
