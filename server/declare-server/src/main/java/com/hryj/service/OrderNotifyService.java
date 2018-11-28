package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.entity.bo.declare.*;
import com.hryj.entity.vo.declare.order.*;
import com.hryj.exception.BizException;
import com.hryj.mapper.OrderNotifyMapper;
import com.hryj.service.util.HttpUtil;
import com.hryj.service.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 白飞
 * @className: OrderNotifyService
 * @description:
 * @create 2018/10/10 9:58
 **/
@Slf4j
@Service
public class OrderNotifyService extends ServiceImpl<OrderNotifyMapper, OrderNotify> {

    private final static Integer NOTITY_COUNT = 3;

    @Autowired
    private CebWaybillService cebWaybillService;
    @Autowired
    private CebInvtService cebInvtService;
    @Autowired
    private CebOrderService cebOrderService;
    @Autowired
    private CebShopEnterpriseService cebShopEnterpriseService;

    /**
     * 根据订单编号和备案10位编码查询
     *
     * @param orderNo
     *          订单编号
     * @param ebcCode
     *          备案10位编码
     * @return 对象
     */
    public OrderNotify findOrderNotify(String orderNo, String ebcCode){

        if(StringUtils.isEmpty(orderNo)){
            return null;
        }
        EntityWrapper<OrderNotify> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("order_no", orderNo);
        entityWrapper.eq("ebc_code", ebcCode);
        return super.selectOne(entityWrapper);
    }

    /**
     * 根据订单编号和备案10位编码查询-通知
     *
     * @param orderNo
     *          订单编号
     * @param ebcCode
     *          备案10位编码
     */
    public void notify(String orderNo, String ebcCode){
        try {
            CebWaybill cebWaybill = this.cebWaybillService.findByOrderNo(orderNo, ebcCode);
            CebInvt cebInvt = this.cebInvtService.findInvt(orderNo, ebcCode);
            OrderNotify orderNotify =  this.findOrderNotify(orderNo, ebcCode);
            EshopOrderNotify eshopOrderNotify = new EshopOrderNotify();
            EshopOrder order = new EshopOrder();
            order.setEbcCode(orderNotify.getEbcCode());
            order.setEbcName(orderNotify.getEbcName());
            order.setOrderNo(orderNo);
            order.setCopNo(orderNotify.getCopNo());
            order.setOrderAmount(orderNotify.getOrderAmount());
            order.setTaxAmount(orderNotify.getTaxAmount());
            eshopOrderNotify.setOrder(order);
            EshopWaybill waybill = new EshopWaybill();
            waybill.setWaybill(cebWaybill.getLogisticsNo());
            waybill.setWaybillKey(cebWaybill.getLogisticsKey());
            waybill.setWaybillName(cebWaybill.getLogisticsName());
            EshopDeclareReturn declareReturn = new EshopDeclareReturn();
            declareReturn.setDeclareStatus(cebInvt.getInvtStatus());
            declareReturn.setReturnInfo(DeclareConstant.getDeclareName(cebInvt.getInvtStatus()));
            declareReturn.setReturnTime(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            eshopOrderNotify.setOrder(order);
            eshopOrderNotify.setWaybill(waybill);
            eshopOrderNotify.setDeclareReturn(declareReturn);
            String xml = XmlUtil.objectToXml(eshopOrderNotify);
            log.info("通知商家EshopOrderNotify===================" + xml);
            String result = HttpUtil.postNotify(orderNotify.getNotifyUrl(), xml);
            if(StringUtils.isEmpty(result)){
                log.info("通知商家EshopOrderNotify失败===================" + orderNo + "---" + ebcCode + "----result:" + result);
                for(int i = 0; i < NOTITY_COUNT; i++){
                    HttpUtil.postNotify(orderNotify.getNotifyUrl(), xml);
                }
            }
        }catch (BizException e){
            e.printStackTrace();
            log.info("通知商家EshopOrderNotify异常===================" + orderNo + "---" + ebcCode);
        }
    }

    /**
     * 根据订单编号和备案10位编码查询-错误通知
     *
     * @param orderNo
     *          订单编号
     * @param ebcCode
     *          备案10位编码
     * @param errorInfo
     *          错误信息
     */
    public void notifyError(String orderNo, String ebcCode, String errorInfo){
        try {
            CebShopEnterprise cebShopEnterprise = this.cebShopEnterpriseService.findByEbcCode(ebcCode);
            if(null == cebShopEnterprise){
                return;
            }
            EshopOrderNotify eshopOrderNotify = new EshopOrderNotify();
            EshopDeclareReturn declareReturn = new EshopDeclareReturn();
            declareReturn.setDeclareStatus(DeclareConstant.DeclareStatus.Status_805.getIndex());
            declareReturn.setReturnInfo(DeclareConstant.getDeclareName(declareReturn.getDeclareStatus()));
            declareReturn.setReturnCode("ERROR");
            declareReturn.setErrorInfo(errorInfo);
            declareReturn.setReturnTime(DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
            eshopOrderNotify.setDeclareReturn(declareReturn);
            String xml = XmlUtil.objectToXml(eshopOrderNotify);
            log.info("通知商家notifyError===================" + xml);
            String result = HttpUtil.postNotify(cebShopEnterprise.getNotifyUrl(), xml);
            if(StringUtils.isEmpty(result)){
                log.info("通知商家notifyError失败===================" + orderNo + "---" + ebcCode + "----result:" + result);
                for(int i = 0; i < NOTITY_COUNT; i++){
                    HttpUtil.postNotify(cebShopEnterprise.getNotifyUrl(), xml);
                }
            }
        }catch (BizException e){
            e.printStackTrace();
            log.info("通知商家notifyError异常===================" + orderNo + "---" + ebcCode);
        }
    }
}
