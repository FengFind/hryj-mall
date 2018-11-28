package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.hryj.entity.bo.declare.*;
import com.hryj.entity.vo.declare.ceb.*;
import com.hryj.entity.vo.declare.order.DeclareConstant;
import com.hryj.service.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author 白飞
 * @className: CebNotifyService
 * @description:
 * @create 2018/9/26 16:05
 **/
@Slf4j
@Service
public class CebNotifyService {

    @Autowired
    private CebInvtReturnService cebInvtReturnService;
    @Autowired
    private CebInvtService cebInvtService;
    @Autowired
    private CebOrderReturnService cebOrderReturnService;
    @Autowired
    private CebOrderService cebOrderService;
    @Autowired
    private CebPayReturnService cebPayReturnService;
    @Autowired
    private CebPayService cebPayService;
    @Autowired
    private CebWaybillReturnService cebWaybillReturnService;
    @Autowired
    private CebWaybillService cebWaybillService;
    @Autowired
    private OrderNotifyService orderNotifyService;
    @Autowired
    private CebTaxService cebTaxService;
    @Autowired
    private CebTaxDetailService cebTaxDetailService;

    /**
     * 通知入口函数
     *
     * @param xml
     *      报文
     */
    public void notify(String xml) {
        try {
            log.info("海关通知=====================" + xml);
            //入库通知
            if(xml.contains("MES_ASK_INFO")){
                log.info("本地海关通知=====================" + xml);
                this.messageAskInfo(xml);
            }
            //商品消息回执
            else if(xml.contains("CEB300Message")){
                this.goodsNotify(xml);
            }
            //电子订单回执
            else if(xml.contains("CEB312Message")){
                log.info("海关电子订单通知=====================" + xml);
                this.orderNotify(xml);
            }
            //支付凭证回执
            else if(xml.contains("CEB412Message")){
                this.paymentNotify(xml);
            }
            //物流运单回执
            else if(xml.contains("CEB512Message")){
                this.shippingNotify(xml);
            }
            //物流运单状态回执
            else if(xml.contains("CEB514Message")){
                this.shippingStatusNotify(xml);
            }
            //进口清单回执
            else if(xml.contains("CEB622Message")){
                log.info("海关电子清单通知=====================" + xml);
                this.invtNotify(xml);
            }
            //撤销申请单回执
            else if(xml.contains("CEB624Message")){
                this.revokeListNotify(xml);
            }
            //退货申请单回执
            else if(xml.contains("CEB626Message")){
                this.returnNotify(xml);
            }
            //进口国检清单回执
            else if(xml.contains("CEB700Message")){
                this.nationalNotify(xml);
            }
            //入库单明细回执
            else if(xml.contains("CEB712Message")){
                this.storageNotify(xml);
            }
            //扣税报文
            else if(xml.contains("CEB816Message")){
                this.taxNotify(xml);
            }
        } catch (Exception e) {

        }
    }


    /**
     * 本地海关入库回执
     *
     * @param xml
     *          报文
     */
    public void messageAskInfo(String xml) {
        if(StringUtils.isNotEmpty(xml)){
            DTCMessage dtcMessage = XmlUtil.xmlToObject(xml, DTCMessage.class);
            if(dtcMessage != null){
                DTCMessageBody messageBody = dtcMessage.getMessageBody();
                DTCFlow dtcFlow = messageBody.getDTCFlow();
                DTCFlowMESASKINFO dTCMesAskInfo = dtcFlow.getMES_ASK_INFO();
                //1-成功，0-失败
                String success = dTCMesAskInfo.getSUCCESS();
                //订单编号
                String workNo = dTCMesAskInfo.getWORK_NO();
                //回执类型
                String type = dTCMesAskInfo.getMESSAGE_TYPE();
                //回执备注
                String memo = dTCMesAskInfo.getMEMO();
                //商品备案回执
                if(type.equals("CEB299Message")){

                }
                //订单入库回执
                else if(type.equals("CEB311Message")){
                    CebOrder cebOrder = this.cebOrderService.findByOrderNo(workNo);
                    if(null != cebOrder && StringUtils.isNotEmpty(success)){
                        Integer orderStatus = Integer.valueOf(success);
                        if(cebOrder.getOrderStatus() == null || !orderStatus.equals(cebOrder.getOrderStatus())){
                            cebOrder.setOrderStatus(orderStatus);
                            this.cebOrderService.updateById(cebOrder);
                        }
                        CebOrderReturn cebOrderReturn = new CebOrderReturn();
                        cebOrderReturn.setOrderId(cebOrder.getId());
                        cebOrderReturn.setOrderNo(workNo);
                        cebOrderReturn.setEbpCode(cebOrder.getCopCode());
                        cebOrderReturn.setEbcCode(cebOrder.getEbcCode());
                        cebOrderReturn.setReturnStatus(success);
                        cebOrderReturn.setReturnInfo("1".equalsIgnoreCase(success) ? "本地口岸入库成功" : memo);
                        cebOrderReturn.setReturnTime(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
                        this.cebOrderReturnService.insert(cebOrderReturn);
                    }
                }
                //支付单入库回执
                else if(type.equals("CEB411Message")){

                }
                //运单入库回执
                else if(type.equals("CEB511Message")){

                }
                //运单状态入库回执
                else if (type.equals("CEB513Message")){

                }
                //清单入库回执
                else if(type.equals("CEB621Message")){
                    CebInvt cebInvt = this.cebInvtService.findByCopNo(workNo, null);
                    if(null != cebInvt && StringUtils.isNotEmpty(success) && !cebInvt.getInvtStatus().equals(800)){
                        Integer invtStatus = Integer.valueOf(success);
                        if(cebInvt.getInvtStatus() == null || !cebInvt.getInvtStatus().equals(invtStatus)){
                            cebInvt.setInvtStatus(invtStatus);
                            this.cebInvtService.updateById(cebInvt);
                        }
                        CebInvtReturn cebInvtReturn = new CebInvtReturn();
                        cebInvtReturn.setInvtId(cebInvt.getId());
                        cebInvtReturn.setGuid(cebInvt.getGuid());
                        cebInvtReturn.setAgentCode(cebInvt.getAgentCode());
                        cebInvtReturn.setCustomsCode(cebInvt.getCustomsCode());
                        cebInvtReturn.setEbpCode(cebInvt.getEbpCode());
                        cebInvtReturn.setEbcCode(cebInvt.getEbcCode());
                        cebInvtReturn.setCopNo(cebInvt.getCopNo());
                        cebInvtReturn.setReturnStatus(success);
                        cebInvtReturn.setReturnInfo("1".equalsIgnoreCase(success) ? "本地口岸入库成功" : memo);
                        cebInvtReturn.setReturnTime(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
                        this.cebInvtReturnService.insert(cebInvtReturn);
                    }
                }
                //进口国检清单回执
                else if(type.equals("CEB700Message")){

                }
                //撤销申请单回执
                else if(type.equals("CEB624Message")){

                }
                //退货申请单回执
                else if(type.equals("CEB626Message")){

                }
                //入库明细单回执
                else if(type.equals("CEB712Message")){

                }
            }
        }
    }


    /**
     * 订单总署回执
     *
     * @param xml
     */
    public void orderNotify(String xml) {
        CEB312Message ceb312Message = XmlUtil.xmlToObject(xml, CEB312Message.class);
        if(ceb312Message == null) {
            log.info("订单回执，转换有问题=============================" + xml);
        }
        Ceb312OrderReturn orderReturn = ceb312Message.getOrderReturn();
        String orderNo = orderReturn.getOrderNo();
        if(orderReturn != null){
            CebInvt cebInvt = this.cebInvtService.findByOrderNo(orderNo, orderReturn.getEbcCode());
            if(cebInvt == null){
                log.info("订单回执，清单无数据=============================" + xml);
            }
            if(cebInvt.getInvtStatus() == DeclareConstant.DeclareStatus.Status_800.getIndex()){
                return;
            }
            Integer orderStatus = Integer.valueOf(orderReturn.getReturnStatus());
            CebOrder cebOrder = this.cebOrderService.findByOrderNo(orderNo);
            if(null != cebOrder){
                cebOrder.setOrderStatus(orderStatus);
                this.cebOrderService.updateById(cebOrder);
                CebOrderReturn cebOrderReturn = new CebOrderReturn();
                cebOrderReturn.setOrderId(cebOrder.getId());
                cebOrderReturn.setOrderNo(orderNo);
                cebOrderReturn.setEbcCode(orderReturn.getEbcCode());
                cebOrderReturn.setEbpCode(orderReturn.getEbpCode());
                cebOrderReturn.setReturnStatus(orderReturn.getReturnStatus());
                cebOrderReturn.setReturnInfo(orderReturn.getReturnInfo());
                cebOrderReturn.setReturnTime(orderReturn.getReturnTime());
                this.cebOrderReturnService.insert(cebOrderReturn);
           }
        }
    }

    /**
     * 进口清单回执
     *
     * @param xml
     */
    public void invtNotify(String xml) {
        CEB622Message ceb622Message = XmlUtil.xmlToObject(xml, CEB622Message.class);
        if(ceb622Message == null){
            log.info("清单回执，转换有问题=============================" + xml);
        }
        InventoryReturn inventoryReturn = ceb622Message.getInventoryReturn();
        if(inventoryReturn != null){
            // 企业内部编号传送给海关,尽量与订单号保持一致
            String copNo = inventoryReturn.getCopNo();
            CebInvt cebInvt = this.cebInvtService.findByCopNo(copNo, inventoryReturn.getEbcCode());
            CebOrder cebOrder = this.cebOrderService.findCebOrder(cebInvt.getOrderNo(), cebInvt.getEbcCode());
            CebWaybill cebWaybill = this.cebWaybillService.findByOrderNo(cebInvt.getOrderNo(), cebInvt.getEbcCode());
            if(cebInvt == null || null == cebOrder || null == cebWaybill){
                log.info("清单回执，清单无数据=============================" + xml);
                return;
            }
            log.info((cebInvt.getInvtStatus() == DeclareConstant.DeclareStatus.Status_800.getIndex()) ? "清单回执，清单放行=============================" + inventoryReturn.getReturnInfo() : cebInvt.getInvtStatus() + "");
            log.info("清单回执==================清单内部编号：" + ceb622Message.getInventoryReturn().getCopNo() + "==========================" + JSON.toJSONString(ceb622Message));
            if(cebInvt.getInvtStatus() == DeclareConstant.DeclareStatus.Status_800.getIndex()) {
                return;
            }
            //修改清单和预录入编号
            if(StringUtils.isNotEmpty(inventoryReturn.getPreNo())){
                cebInvt.setPreNo(inventoryReturn.getPreNo());
            }
            if(StringUtils.isNotEmpty(inventoryReturn.getInvtNo())){
                cebInvt.setInvtNo(inventoryReturn.getInvtNo());
            }
            //设置状态
            int status = Integer.valueOf(inventoryReturn.getReturnStatus());
            cebInvt.setInvtStatus(status);
            if(status == DeclareConstant.DeclareStatus.Status_2.getIndex()){
                if(inventoryReturn.getReturnInfo().indexOf("运单信息不存在") >= 0){
                    status = DeclareConstant.DeclareStatus.Status_45.getIndex();
                }else if(inventoryReturn.getReturnInfo().indexOf("支付信息不存在") >= 0){
                    status = DeclareConstant.DeclareStatus.Status_46.getIndex();
                }else{
                    //订单不存在
                    status = DeclareConstant.DeclareStatus.Status_49.getIndex();
                }
                cebInvt.setInvtStatus(status);
            }else if(status == DeclareConstant.DeclareStatus.Status_100.getIndex()){
                if(inventoryReturn.getReturnInfo().indexOf("超过年度限额") >= 0){
                    status = DeclareConstant.DeclareStatus.Status_44.getIndex();
                }
                cebInvt.setInvtStatus(status);
            }else if(status == DeclareConstant.DeclareStatus.Status_0.getIndex()){
                if(inventoryReturn.getReturnInfo().indexOf("身份验证未通过") >= 0){
                    cebInvt.setInvtStatus(DeclareConstant.DeclareStatus.Status_44.getIndex());
                }
            }else if(status == DeclareConstant.DeclareStatus.Status_621049.getIndex() || status == DeclareConstant.DeclareStatus.Status_621053.getIndex()){
                cebInvt.setInvtStatus(DeclareConstant.DeclareStatus.Status_44.getIndex());
            }else if(status == DeclareConstant.DeclareStatus.Status_800.getIndex()){
                cebInvt.setInvtStatus(DeclareConstant.DeclareStatus.Status_800.getIndex());
            }
            this.cebInvtService.updateById(cebInvt);
            //存储回执信息
            CebInvtReturn cebInvtReturn = new CebInvtReturn();
            BeanUtils.copyProperties(inventoryReturn, cebInvtReturn);
            cebInvtReturn.setInvtId(cebInvt.getId());
            this.cebInvtReturnService.insert(cebInvtReturn);
            //异步通知
            OrderNotify orderNotify = this.orderNotifyService.findOrderNotify(cebInvt.getOrderNo(), cebInvt.getEbcCode());
            log.info("通知商家OrderNotify===================" + JSON.toJSONString(orderNotify));
            if(null != orderNotify && status > 0){
                this.orderNotifyService.notify(cebInvt.getOrderNo(), cebInvt.getEbcCode());
            }
        }
    }


    /**
     * 商品消息回执
     *
     * @param xml
     */
    public void goodsNotify(String xml) {

    }

    /**
     * 支付单总署回执-本地模拟
     *
     * @param xml
     */
    public void paymentNotify(String xml) {
        log.info("支付单总署回执-本地模拟=====================" + xml);
        CEB412Message ceb412Message = XmlUtil.xmlToObject(xml, CEB412Message.class);
        if(null != ceb412Message){
            PaymentReturn paymentReturn = ceb412Message.getPaymentReturn();
            if(null != paymentReturn){
                Integer payStatus = Integer.valueOf(paymentReturn.getReturnStatus());
                CebPay cebPay = this.cebPayService.findByOrderNo(paymentReturn.getOrderNo(), paymentReturn.getPayCode());
                if(null != cebPay){
                    cebPay.setPayStatus(payStatus);
                    cebPay.setPayDeclareNo(paymentReturn.getPayDeclareNo());
                    this.cebPayService.updateStatus(cebPay.getId(),cebPay.getPayStatus(), cebPay.getPayDeclareNo());
                    CebPayReturn cebPayReturn = new CebPayReturn();
                    BeanUtils.copyProperties(paymentReturn, cebPayReturn);
                    cebPayReturn.setPayId(cebPay.getId());
                    this.cebPayReturnService.insert(cebPayReturn);
                }
            }
        }
    }

    /**
     * 运单总署回执-本地模拟
     *
     * @param xml
     */
    public void shippingNotify(String xml) {
        log.info("运单总署回执-本地模拟=====================" + xml);
        CEB512Message ceb512Message = XmlUtil.xmlToObject(xml, CEB512Message.class);
        if(null != ceb512Message){
            LogisticsReturn logisticsReturn = ceb512Message.getLogisticsReturn();
            if(null != logisticsReturn){
                Integer waybillStatus = Integer.valueOf(logisticsReturn.getReturnStatus());
                CebWaybill cebWaybill = this.cebWaybillService.findCebWaybill(logisticsReturn.getLogisticsNo(), logisticsReturn.getLogisticsCode());
                if(null != cebWaybill){
                    cebWaybill.setWaybillStatus(waybillStatus);
                    this.cebWaybillService.updateById(cebWaybill);
                    CebWaybillReturn cebWaybillReturn = new CebWaybillReturn();
                    BeanUtils.copyProperties(logisticsReturn, cebWaybillReturn);
                    cebWaybillReturn.setWaybillId(cebWaybill.getId());
                    this.cebWaybillReturnService.insert(cebWaybillReturn);
                }

            }
        }
    }

    /**
     * 物流运单状态回执
     *
     * @param xml
     */
    public void shippingStatusNotify(String xml) {

    }


    /**
     * 撤销申请单回执
     *
     * @param xml
     */
    public void revokeListNotify(String xml) {

    }


    /**
     * 退货申请单回执
     *
     * @param xml
     */
    public void returnNotify(String xml) {

    }


    /**
     * 进口国检清单回执
     *
     * @param xml
     */
    public void nationalNotify(String xml) {

    }

    /**
     * 入库单明细回执
     *
     * @param xml
     */
    public void storageNotify(String xml) {

    }

    /**
     * 扣税回执
     *
     * @param xml
     */
    public void taxNotify(String xml) {
        log.info("\n扣税回执==================\n" + xml);
        CEB816Message ceb816Message = XmlUtil.xmlToObject(xml, CEB816Message.class);
        if(null == ceb816Message || ceb816Message.getTax() == null || ceb816Message.getTax().getTaxHeadRd() == null || ceb816Message.getTax().getTaxListRd() == null){
            return;
        }
        if(ceb816Message.getTax().getTaxListRd().size() == 0){
            return;
        }
        CebTax cebTax = new CebTax();
        BeanUtils.copyProperties(ceb816Message.getTax().getTaxHeadRd(), cebTax);
        cebTax.setId(IdWorker.getId());
        List<CebTaxDetail> cebTaxDetails = Lists.newArrayList();
        for(CEB816TaxListRd ceb816TaxListRd : ceb816Message.getTax().getTaxListRd()){
            CebTaxDetail cebTaxDetail = new CebTaxDetail();
            BeanUtils.copyProperties(ceb816TaxListRd, cebTaxDetail);
            cebTaxDetail.setId(IdWorker.getId());
            cebTaxDetail.setTaxId(cebTax.getId());
            cebTaxDetails.add(cebTaxDetail);
        }
        CebTax pCebTax = this.cebTaxService.findByInvtNo(cebTax.getInvtNo(), cebTax.getEbcCode());
        if(pCebTax == null){
            this.cebTaxService.insert(cebTax);
            this.cebTaxDetailService.insertBatch(cebTaxDetails);
        }
        log.info("\n扣税回执==================\n" + JSON.toJSONString(ceb816Message));
    }


}
