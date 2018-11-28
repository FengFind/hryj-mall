package com.hryj.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.declare.CebInvt;
import com.hryj.entity.bo.declare.CebOrder;
import com.hryj.entity.bo.declare.CebOrderDetail;
import com.hryj.entity.vo.declare.ceb.CEB512Message;
import com.hryj.entity.vo.declare.ceb.LogisticsReturn;
import com.hryj.entity.vo.declare.order.DeclareConstant;
import com.hryj.entity.vo.declare.yunda.*;
import com.hryj.service.util.HttpUtil;
import com.hryj.service.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * @author 白飞
 * @className: WaybillService
 * @description:
 * @create 2018/9/28 10:20
 **/
@Slf4j
@Service
public class WaybillService {

    @Autowired
    private CebInvtService cebInvtService;
    @Autowired
    private CebNotifyService cebNotifyService;

    /**
     * 韵达快递组装报文
     *
     * @return
     */
    public Result yunda(String app_key, String app_secret, String partner, String logisticsNo, CebOrder cebOrder, List<CebOrderDetail> orderList){
        try {
            log.info("韵达获取运单即申报=========================" + JSON.toJSONString(cebOrder) + "======" + JSON.toJSONString(orderList));
            //发件人
            String senderTelphone = CodeCache.getValueByKey("CustomerServicePhone","S02");
            YunDaSender sender = new YunDaSender(cebOrder.getEbcName(), cebOrder.getEbcName(), senderTelphone, cebOrder.getEbcName());
            //收货人
            YunDaReceiver receiver = new YunDaReceiver(cebOrder.getConsigneeAddress(), cebOrder.getConsignee(), cebOrder.getConsigneeTelephone());

            List<YunDaGoodsList> goodsList = Lists.newArrayList();
            for(CebOrderDetail cebOrderDetail : orderList){
                YunDaGoodsList yunDaGoodsList = new YunDaGoodsList();
                BigDecimal price = NumberUtil.roundHalfEven(cebOrderDetail.getPrice(), 2);
                yunDaGoodsList.setGoods(new YunDaGoods(cebOrderDetail.getItemName(), "", price,  BigDecimal.ZERO,  BigDecimal.ZERO, cebOrderDetail.getQty()));
                goodsList.add(yunDaGoodsList);
            }
            YunDaHawb hawb = new YunDaHawb();
            hawb.setMail_no(logisticsNo);
            hawb.setHawbno(cebOrder.getOrderNo());
            hawb.setReceiver(receiver);
            hawb.setSender(sender);
            hawb.setGoods_list(goodsList);
            YunDaHawbs hawbs = new YunDaHawbs();
            hawbs.setHawb(hawb);
            YunDaBeans beans = new YunDaBeans();
            beans.setHawbs(hawbs);
            String xml = XmlUtil.objectToXml(beans);
            log.info("韵达获取运单即申报XML报文=========================" +xml);
            YunDaRequestParam yunDaRequestParam = new YunDaRequestParam();
            yunDaRequestParam.setData(xml);
            SortedMap<String, String > paramMap = Maps.newTreeMap();
            paramMap.put("app_key", app_key.trim());
            paramMap.put("buz_type", yunDaRequestParam.getBuz_type());
            paramMap.put("data", yunDaRequestParam.getData());
            paramMap.put("format", yunDaRequestParam.getFormat());
            paramMap.put("method", yunDaRequestParam.getMethod());
            paramMap.put("tradeId", partner.trim());
            paramMap.put("version", yunDaRequestParam.getVersion());
            String sign = "";
            for(Map.Entry<String, String> entry : paramMap.entrySet()){
                sign += entry.getKey() + entry.getValue();
            }
            paramMap.put("sign", Base64.encode(DigestUtils.md5Hex(sign + app_secret.trim())));
            log.info("韵达获取运单即申报参数=========================" + JSON.toJSONString(paramMap));
            //String testUrl = "http://116.228.72.130:8080/oms/interface.php";
            String produceUrl = "http://ydoms.yundasys.com:10582/oms/interface.php";
            String result = HttpUtil.httppost(paramMap, produceUrl, "UTF-8");
            log.info("韵达获取运单即申报返回结果=========================" + result);
            YunDaResponses yunDaResponses = XmlUtil.xmlToObject(result, YunDaResponses.class);
            log.info("韵达获取运单即申报返回结果=========================" + JSON.toJSONString(yunDaResponses));
            if(yunDaResponses != null && yunDaResponses.getResponse()!= null && yunDaResponses.getResponse().get(0) != null){
                if(!"E99".equals(yunDaResponses.getResponse().get(0).getCode())){
                    return new Result(CodeEnum.FAIL_BUSINESS, yunDaResponses.getResponse().get(0).getMsg());
                }
                return new Result(CodeEnum.SUCCESS, yunDaResponses.getResponse().get(0).getMail_no());
            }
            return new Result(CodeEnum.FAIL_BUSINESS, "韵达运单拉取及申报错误");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(CodeEnum.FAIL_BUSINESS, "运单拉取申报出异常");
        }
    }

    /**
     * 回执消息
     *
     * @param ebcCode
     *      电商企业编号
     * @param orderNo
     *          订单号
     * @param logisticsNo
     *           运单号
     */
    public void waybillReturn(String ebcCode, String orderNo,String logisticsCode, String logisticsNo, String guid, Integer waybillStatus, String returnInfo){
        CebInvt cebInvt = cebInvtService.findInvt(orderNo, ebcCode);
        log.info("运单报文回执cebInvt=========================" + JSON.toJSONString(cebInvt));
        if(cebInvt.getInvtStatus() == DeclareConstant.DeclareStatus.Status_800.getIndex()){
            return;
        }
        LogisticsReturn logisticsReturn = new LogisticsReturn();
        logisticsReturn.setGuid(guid);
        logisticsReturn.setLogisticsCode(logisticsCode);
        logisticsReturn.setLogisticsNo(logisticsNo);
        logisticsReturn.setReturnInfo(returnInfo);
        logisticsReturn.setReturnStatus(waybillStatus + "");
        logisticsReturn.setReturnTime(DateUtil.format(new Date(),"yyyyMMddHHmmss"));
        CEB512Message ceb512Message = new CEB512Message();
        ceb512Message.setLogisticsReturn(logisticsReturn);
        String xml = XmlUtil.objectToXml(ceb512Message);
        log.info("运单报文回执=========================" + xml);
        cebNotifyService.shippingNotify(xml);
    }
}
