package com.hryj.scheduled;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.XmlUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.common.ThirdOrderStatusEnum;
import com.hryj.entity.bo.order.OrderCrossBorder;
import com.hryj.entity.bo.order.OrderInfo;
import com.hryj.entity.vo.order.OderForGCRequestVO;
import com.hryj.entity.vo.order.OrderStatusVO;
import com.hryj.entity.vo.order.request.OrderExpressVO;
import com.hryj.entity.vo.order.request.OrderIdVO;
import com.hryj.service.OrderAdminService;
import com.hryj.service.OrderForGCService;
import com.hryj.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author 叶方宇
 * @className: OrderScheduled
 * @description:
 * @create 2018/9/11 0011 10:07
 **/
@Slf4j
@Component
public class OrderScheduled {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderForGCService orderForGCService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAdminService orderAdminService;




    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description:  定时任务,订单状态更新
     * @param:
     * @return
     * @create 2018-09-17 11:45
     **/
    public void findOrderForGCStatus(){
        List<OderForGCRequestVO> borderOrderList = orderForGCService.findCrossBorderOrderList();
        //开5个线程
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(5);
        final List<Future> threadList = new ArrayList<>();
        borderOrderList.forEach((OderForGCRequestVO cross) ->{
            if(cross.getThird_order_code()!=null&&cross.getOrder_num()!=null){
                Future future = cachedThreadPool.submit(new Runnable() {
                    @Override
                    public void run() {
                        if(cross.getThird_order_code()!=null&&cross.getOrder_num()!=null){
                            Map<String, Object> order = new LinkedHashMap<>();
                            order.put("order_sn", cross.getThird_order_code());
                            order.put("trade_no", cross.getOrder_num());
                            log.info("请求数据：" + JSON.toJSONString(order));
                            getOrderForGC(order);
                        }
                    }
                });
                threadList.add(future);
            }
        });

        for(Future future:threadList){
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
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 调用光彩接口查询订单信息
     * 根据该返回数据更新订单状态
     * @param:
     * @return
     * @create 2018-09-14 11:37
     **/
    @Transactional
    public void getOrderForGC(Map<String, Object> order) {
        Map<String, Object> returnMap = send(requestMap(ThirdOrderStatusEnum.SELECT_ORDER, order), ThirdOrderStatusEnum.APPKEY);
        log.info("光彩返回map：" + JSON.toJSONString(returnMap));
        if (ThirdOrderStatusEnum.SUCCESS_F.equals(returnMap.get("is_success"))) {
            //失败处理
        } else {
            Map<String, Object> body = (Map) returnMap.get("body");
            //获取返回订单信息
            Map<String, Object> rorder = (Map) body.get("order");
            //光彩ID
            String order_sn = rorder.get("order_sn").toString();
            //红瑞num
            String trade_no = rorder.get("trade_no").toString();
            OrderInfo orderInfo = orderService.getOrderInfoByNum(trade_no);
            OrderCrossBorder crossBorderOrder = orderForGCService.findCrossBorderOrder(orderInfo.getId(), order_sn);
            OrderCrossBorder upOrderCrossBorder = new OrderCrossBorder();
            if (crossBorderOrder != null) {
                //光彩订单状态
                String order_state = rorder.get("order_state").toString();
                //获取状态对应值
                if (ThirdOrderStatusEnum.GCmap_status.containsKey(order_state)) {
                    OrderStatusVO vo = new OrderStatusVO();
                    //如果状态相同则不更新
                    if (ThirdOrderStatusEnum.GCmap_status.get(order_state).equals(crossBorderOrder.getThird_order_status())) {
                        return;
                    } else if (ThirdOrderStatusEnum.ALREADY_SHIPPED.getGcStatus().equals(order_state)  || ThirdOrderStatusEnum.OUT_OF_THE_TREASURY.getGcStatus().equals(order_state)) {
                        //如果有物流插入物流信息
                        log.info("物流信息：" + JSON.toJSONString(rorder));
                        addOrderExpress(rorder, orderInfo.getId());
                        //更新订单状态为已发货
                        vo.setOrder_status(SysCodeEnmu.ORDERSTATUS_04.getCodeValue());
                        vo.setOrder_id(crossBorderOrder.getOrder_id());
                        vo.setLogin_token(null);
                        vo.setChange_reason(ThirdOrderStatusEnum.HRmap_status_desc.get(ThirdOrderStatusEnum.GCmap_status.get(order_state)));
                        vo.setStatus_remark(ThirdOrderStatusEnum.HRmap_status_desc.get(ThirdOrderStatusEnum.GCmap_status.get(order_state)));
                        orderService.updateStatus(vo);

                    } else if (ThirdOrderStatusEnum.CONFIRM_FAIL.getGcStatus().equals(order_state)) {
                        //最终状态下取消订单-退款-存原因-发短信
                        OrderIdVO orderIdVO = new OrderIdVO();
                        orderIdVO.setOrder_id(orderInfo.getId());
                        Result result = orderService.cancelOrder(orderIdVO);
                        if(!result.isSuccess()){
                            return;
                        }
                    } else if (ThirdOrderStatusEnum.CUSTOMS_REFUND.getGcStatus().equals(order_state)) {
                        //报关失败
                        upOrderCrossBorder.setFailed_reason(rorder.get("message").toString());
                        upOrderCrossBorder.setThird_order_status(Integer.valueOf(ThirdOrderStatusEnum.GCmap_status.get(order_state)));
                        upOrderCrossBorder.setThird_order_status_desc(ThirdOrderStatusEnum.HRmap_status_desc.get(ThirdOrderStatusEnum.GCmap_status.get(order_state)));
                        ////将次数重置为0
                        //upOrderCrossBorder.setCancel_count(0);
                    }
                    //更新三方订单表状态
                    EntityWrapper wrapper = new EntityWrapper();
                    wrapper.eq("order_id", crossBorderOrder.getOrder_id());
                    wrapper.eq("third_order_code", order_sn);
                    upOrderCrossBorder.setThird_order_status(Integer.valueOf(ThirdOrderStatusEnum.GCmap_status.get(order_state)));
                    upOrderCrossBorder.setThird_order_status_desc(ThirdOrderStatusEnum.HRmap_status_desc.get(ThirdOrderStatusEnum.GCmap_status.get(order_state)));
                    orderForGCService.updateCrossBorderOrder(upOrderCrossBorder, wrapper);
                }
            }
        }
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 取消订单调用
     * @param: order_sn -gc;trade_no -hr
     * @return
     * @create 2018-09-14 11:44
     **/
    public Map<String,String> cancelOrderForGC(String order_sn,String trade_no){
        log.info("order_sn：{},trade_no:{}",order_sn,trade_no );
        Map<String,String> map = new HashMap<>(2);
        Map<String, Object> order = new LinkedHashMap<>();
        order.put("order_sn", order_sn);
        order.put("trade_no", trade_no);
        Map<String,Object> returnMap = send(requestMap(ThirdOrderStatusEnum.CANCEL_ORDER,order),ThirdOrderStatusEnum.APPKEY);
        log.info("取消订单光彩返回map：" + JSON.toJSONString(returnMap));
        if(returnMap==null){
            map.put("message","接口响应异常");
            map.put("success",ThirdOrderStatusEnum.SUCCESS_F);
        }else if(ThirdOrderStatusEnum.SUCCESS_T.equals(returnMap.get("is_success"))){
            map.put("message",returnMap.get("successMessage")+"");
            map.put("success",ThirdOrderStatusEnum.SUCCESS_T);
        }else if(ThirdOrderStatusEnum.SUCCESS_F.equals(returnMap.get("is_success"))){
            map.put("message",returnMap.get("errorMessage")+"");
            map.put("success",ThirdOrderStatusEnum.SUCCESS_F);
        }
        return map;
    }

    public static final String GGC="<gc>";
    public static final String EGC="</gc>";
    public static final String BSIGN="<sign>";
    public static final String ESIGN="</sign>";

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 接口请求
     * @param:
     * @return
     * @create 2018-09-19 10:29
     **/
    public Map send(Map<String,Object> xml,String appKey){
        String params = XmlUtil.mapToXmlStr(xml, "xml");
        String appKeyAppend = "<appkey>"+appKey+"</appkey>";
        String findGC = params.substring(params.indexOf(GGC), params.indexOf(EGC)+EGC.length());
        String signString = params.substring(0, params.indexOf(EGC)+EGC.length())
                +BSIGN + DigestUtils.md5Hex(findGC + appKeyAppend)
                + ESIGN + params.substring(params.indexOf(EGC)+EGC.length(), params.length());
        Map<String,Object> returnMap = new LinkedHashMap<>();
        try {
            String data = URLEncoder.encode(Base64.encode(signString), "UTF-8");
            log.info("请求报文打印："+data);
            String returnData = restTemplate.postForObject(ThirdOrderStatusEnum.URL, data, String.class);
            returnMap = JSONObject.parseObject(Base64.decodeStr(returnData));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return returnMap;
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 创建xml
     * @param:
     * @return
     * @create 2018-09-18 15:29
     **/
    public Map<String,Object> requestMap(String action,Map<String, Object> order){
        Map<String, Object> xml = new LinkedHashMap<>();
        Map<String, Object> gc = new LinkedHashMap<>();
        Map<String, Object> head = createHead(action);
        Map<String, Object> body = new LinkedHashMap<>();
        gc.put("head", head);
        gc.put("body", body);
        body.put("order", order);
        xml.put("gc", gc);
        return xml;
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 创建head
     * @param:
     * @return
     * @create 2018-09-18 15:29
     **/
    public Map<String,Object> createHead(String action){
        Map<String, Object> head = new LinkedHashMap<>();
        head.put("action", action);
        head.put("return_type", "json");
        head.put("partner_id", ThirdOrderStatusEnum.PARTNER_ID);
        return head;
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 插入物流信息
     * @param:
     * @return
     * @create 2018-09-18 17:16
     **/
    public void addOrderExpress(Map<String,Object> order,Long orderId){
        OrderExpressVO orderExpressVO = new OrderExpressVO();
        if(order.get("freight_code")!=null){
            orderExpressVO.setExpress_code(order.get("freight_no").toString());
        }
        if(order.get("freight_name")!=null){
            orderExpressVO.setExpress_name(order.get("freight_name").toString());
        }
        if(order.get("freight_no")!=null){
            orderExpressVO.setExpress_id(order.get("freight_code").toString());
        }
        orderExpressVO.setOrder_id(orderId);
        orderAdminService.insertDeliveryForExpress(orderExpressVO);
    }


    public static void main(String[] args) {
        String s = "<?xml version='1.0' encoding='utf-8'?>\n" +
                "<gc>\n" +
                "    <head>\n" +
                "        <action>select_order</action>\n" +
                "        <time>20180919171255</time>\n" +
                "        <result_code>success</result_code>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <order>\n" +
                "            <order_sn>gc1000000003623001</order_sn>\n" +
                "            <trade_no>20180919152518839525418</trade_no>\n" +
                "            <pay_sn>20180919152518839525418</pay_sn>\n" +
                "            <freight_code>yunda</freight_code>\n" +
                "            <freight_name>韵达快递</freight_name>\n" +
                "            <freight_no>7700068993357</freight_no>\n" +
                "            <add_time>20180919162026</add_time>\n" +
                "            <payment_time>19700101075959</payment_time>\n" +
                "            <finished_time></finished_time>\n" +
                "            <goods_amount>6.00</goods_amount>\n" +
                "            <shipping_fee>0.00</shipping_fee>\n" +
                "            <order_tax>0.67</order_tax>\n" +
                "            <order_amount>6.67</order_amount>\n" +
                "            <order_state>50</order_state>\n" +
                "            <message>订单申报海关成功</message>\n" +
                "        </order>\n" +
                "        <consignee>\n" +
                "            <name>白飞</name>\n" +
                "            <address>重庆 重庆市 南岸区 重庆重庆市南岸区茶园工业园区东本企业上谷B8栋A8</address>\n" +
                "        </consignee>\n" +
                "    </body>\n" +
                "</gc>";
        Map<String,Object> map = XmlUtil.xmlToMap(s);
        map.get("gc");
    }

}
