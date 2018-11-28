package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hryj.cache.CodeCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.entity.bo.declare.*;
import com.hryj.entity.vo.declare.ceb.*;
import com.hryj.entity.vo.declare.order.*;
import com.hryj.exception.BizException;
import com.hryj.pay.AliPay;
import com.hryj.pay.WxPay;
import com.hryj.pay.vo.Ali;
import com.hryj.pay.vo.CustomDeclareRequstVO;
import com.hryj.service.util.HttpUtil;
import com.hryj.service.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author 白飞
 * @className: OrderDeclareService
 * @description:
 * @create 2018/9/26 16:02
 **/
@Slf4j
@Service
public class OrderDeclareService {

    @Autowired
    private CebAreaCompanyService cebAreaCompanyService;
    @Autowired
    private CebCustomerService cebCustomerService;
    @Autowired
    private CebInvtDetailService cebInvtDetailService;
    @Autowired
    private CebInvtService cebInvtService;
    @Autowired
    private CebLogisticsService cebLogisticsService;
    @Autowired
    private CebOrderDetailService cebOrderDetailService;
    @Autowired
    private CebOrderService cebOrderService;
    @Autowired
    private CebPaymentService cebPaymentService;
    @Autowired
    private CebPayService cebPayService;
    @Autowired
    private CebShopEnterpriseService cebShopEnterpriseService;
    @Autowired
    private CebWaybillService cebWaybillService;
    @Autowired
    private  CebBaseTransferService cebBaseTransferService;
    @Autowired
    private CebNotifyService cebNotifyService;
    @Autowired
    private WaybillService waybillService;
    @Autowired
    private CebProductService cebProductService;
    @Autowired
    private OrderNotifyService orderNotifyService;
    @Autowired
    private CebTaxRateService cebTaxRateService;

    /**
     * 申报订单处理入口
     *
     * @param base64Data
     *              Base64加密
     * @return Result
     */
    public Result declare(String base64Data){
        try {
            byte[] bytes = base64Data.getBytes("utf-8");
            byte[] convertBytes = Base64.decodeBase64(bytes);
            String xml = new String(convertBytes, "utf-8");
            log.info("xml请求报文===" + xml);
            if(StringUtils.isEmpty(xml)){
                return new Result(CodeEnum.FAIL_BUSINESS,"报文解析换错误");
            }
            final  CebOrderRequest cebOrderRequest = XmlUtil.xmlToObject(xml, CebOrderRequest.class);
            if(cebOrderRequest == null || cebOrderRequest.getOrderHead() == null || cebOrderRequest.getOrderList() == null){
                return new Result(CodeEnum.FAIL_BUSINESS,"报文转换换错误");
            }
            if(StringUtils.isEmpty(cebOrderRequest.getOrderHead().getEbcCode())){
                return new Result(CodeEnum.FAIL_BUSINESS,"电商企业编号不能为空");
            }
            ThreadUtil.execAsync(new Runnable() {
                @Override
                public void run() {
                    CebOrder pCebOrder = cebOrderService.findCebOrder(cebOrderRequest.getOrderHead().getOrderNo(), cebOrderRequest.getOrderHead().getEbcCode());
                    if(null != pCebOrder){
                        orderNotifyService.notify(cebOrderRequest.getOrderHead().getOrderNo(), cebOrderRequest.getOrderHead().getEbcCode());
                    }else{
                        Result result = createOrder(cebOrderRequest);
                        if(!result.isSuccess()){
                            orderNotifyService.notifyError(cebOrderRequest.getOrderHead().getOrderNo(), cebOrderRequest.getOrderHead().getEbcCode(), result.getMsg());
                            return;
                        }
                        //异步通知
                        orderNotifyService.notify(cebOrderRequest.getOrderHead().getOrderNo(), cebOrderRequest.getOrderHead().getEbcCode());
                        //调用报关
                        declareCustom(cebOrderRequest.getOrderHead().getOrderNo(), cebOrderRequest.getOrderHead().getEbcCode(), cebOrderRequest.getOrderHead().getEbcCode());
                    }
                }
            });
        } catch (Exception e) {
            log.info("xml请求报文解析Base64异常===" + base64Data);
           throw new BizException("数据异常");
        }
        return new Result(CodeEnum.SUCCESS,"接收成功");
    }

    /**
     * 订单处理
     *
     * @param cebOrderReq
     *          请求的对象
     * @return
     */
    @Transactional
    public Result createOrder(CebOrderRequest cebOrderReq){
        if(null == cebOrderReq || cebOrderReq.getOrderHead() == null || cebOrderReq.getOrderList() == null || cebOrderReq.getOrderList().size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品信息不存在");
        }
        CebOrderHeadRequest cebOrderRequest = cebOrderReq.getOrderHead();
        cebOrderRequest.setTaxTotal(NumberUtil.roundHalfEven(cebOrderRequest.getTaxTotal(), 2));
        cebOrderRequest.setActuralPaid(NumberUtil.roundHalfEven(cebOrderRequest.getActuralPaid(),2));
        cebOrderRequest.setGoodsValue(NumberUtil.roundHalfEven(cebOrderRequest.getGoodsValue(),2));
        cebOrderRequest.setGrossWeight(NumberUtil.roundHalfEven(cebOrderRequest.getGrossWeight(),4));
        cebOrderRequest.setNetWeight(NumberUtil.roundHalfEven(cebOrderRequest.getNetWeight(),4));
        cebOrderRequest.setCurrency(StringUtils.isEmpty(cebOrderRequest.getCurrency()) ? "142" : cebOrderRequest.getCurrency());
        if(!cebOrderRequest.getBuyerIdType().equalsIgnoreCase("1") && !cebOrderRequest.getBuyerIdType().equalsIgnoreCase("2")){
            cebOrderRequest.setBuyerIdType("1");
        }
        if(StringUtils.isEmpty(cebOrderRequest.getPayKey())){
            return new Result(CodeEnum.FAIL_BUSINESS,"PayKey不能为空");
        }
        if(StringUtils.isEmpty(cebOrderRequest.getLogisticsKey())){
            return new Result(CodeEnum.FAIL_BUSINESS,"LogisticsKey不能为空");
        }
        if(StringUtils.isEmpty(cebOrderRequest.getBizTypeCode())){
            return new Result(CodeEnum.FAIL_BUSINESS,"BizTypeCode不存在");
        }
        if(!cebOrderRequest.getBizTypeCode().equals(DeclareConstant.BizTypeCode.I10.name()) && !cebOrderRequest.getBizTypeCode().equals(DeclareConstant.BizTypeCode.I20.name())){
            return new Result(CodeEnum.FAIL_BUSINESS,"BizTypeCode类型错误只能为I10-直邮或I20-保税");
        }
        if (StringUtils.isEmpty(cebOrderRequest.getOrderNo())){
            return new Result(CodeEnum.FAIL_BUSINESS,"OrderNo不能为空");
        }
        String ebcOrderNo = cebOrderRequest.getOrderNo();
        cebOrderRequest.setLogisticsKey(cebOrderRequest.getLogisticsKey().toLowerCase());
        if(StringUtils.isEmpty(cebOrderRequest.getSplit()) || !cebOrderRequest.getSplit().equalsIgnoreCase(DeclareConstant.SplitCode.T.name())){
            cebOrderRequest.setSplit(DeclareConstant.SplitCode.F.name());
            if (cebOrderRequest.getPayKey().equals(DeclareConstant.PayCode.wxpay.name())){
                cebOrderRequest.setOrderNo(cebOrderRequest.getPayOrderNo());
            }
        }
        CebShopEnterprise cebShopEnterprise = this.cebShopEnterpriseService.findByEbcCode(cebOrderRequest.getEbcCode());
        if(cebShopEnterprise == null){
            return new Result(CodeEnum.FAIL_BUSINESS,"电商企业不存在");
        }
        CebBaseTransfer cebBaseTransfer = this.cebBaseTransferService.selectById(cebShopEnterprise.getBaseTransferId());
        if(cebBaseTransfer == null){
            return new Result(CodeEnum.FAIL_BUSINESS,"电商企业DXP不存在");
        }
        if(StringUtils.isEmpty(cebOrderRequest.getCustomsCode())){
            CebCustomer cebCustomer = this.cebCustomerService.selectById(cebShopEnterprise.getCustomerId());
            if(null == cebCustomer){
                return new Result(CodeEnum.FAIL_BUSINESS,"电商企业对应的关区不存在");
            }
            cebOrderRequest.setCustomsCode(cebCustomer.getCustomerCode());
            cebOrderRequest.setSortlineId(cebCustomer.getSortlineId());
        }
        CebPayment cebPayment = this.cebPaymentService.findByPayNo(cebOrderRequest.getPayKey(), cebOrderRequest.getEbcCode());
        if(null == cebPayment){
            return new Result(CodeEnum.FAIL_BUSINESS,"支付编码对应的支付公司不存在");
        }
        String waybill = cebOrderRequest.getWaybill();
        CebLogistics cebLogistics = this.cebLogisticsService.findByLogisticsNo(cebOrderRequest.getLogisticsKey(), cebOrderRequest.getEbcCode());
        if(cebLogistics == null){
            return new Result(CodeEnum.FAIL_BUSINESS,"物流编码对应的物流公司不存在");
        }

        if(StringUtils.isEmpty(cebOrderRequest.getSysAreaCode()) && StringUtils.isEmpty(cebOrderRequest.getEmsNo())){
            return new Result(CodeEnum.FAIL_BUSINESS,"区内系统编码或区内编码不存在");
        }
        CebAreaCompany cebAreaCompany = this.cebAreaCompanyService.findCebAreaCompany(cebOrderRequest.getSysAreaCode(), cebOrderRequest.getEmsNo());
        if(null == cebAreaCompany){
            return new Result(CodeEnum.FAIL_BUSINESS,"区内企业不存在");
        }
        Map<String, CebProduct> productMap = cebProductService.findListToMap(cebOrderRequest.getEbcCode(), cebOrderReq.getItemNos());
        if(productMap == null || productMap.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品备案基础信息不存在");
        }
        if(productMap.size() != cebOrderReq.getOrderList().size()){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品备案基础信息不匹配");
        }
        BigDecimal totalTaxAmount = new BigDecimal(0);
        Map<String, CebOrderDetailRequest> orderDetailMap = Maps.newHashMap();
        List<String> hsCodes = Lists.newArrayList();
        for (CebOrderDetailRequest orderDetail : cebOrderReq.getOrderList()){
            orderDetailMap.put(orderDetail.getItemNo(), orderDetail);
        }
        for(Map.Entry<String, CebProduct> entry : productMap.entrySet()){
            CebProduct cebProduct = entry.getValue();
            hsCodes.add(cebProduct.getGcode());
        }
        Map<String, CebTaxRate> cebTaxRateMap = this.cebTaxRateService.findByHsCodeToMap(hsCodes);
        if(null == cebTaxRateMap){
            return new Result(CodeEnum.FAIL_BUSINESS,"入库失败，HSCODE错误");
        }
        for(Map.Entry<String, CebProduct> entry : productMap.entrySet()){
            CebProduct cebProduct = entry.getValue();
            CebTaxRate cebTaxRate = cebTaxRateMap.get(cebProduct.getGcode());
            CebOrderDetailRequest orderDetail = orderDetailMap.get(entry.getKey());
            BigDecimal shopPrice = orderDetail.getPrice();
            Integer quantity = orderDetail.getQty();
            BigDecimal netContents = cebProduct.getNetContents();
            String gunit = null == cebProduct.getGunit() ? null : CebProduct.Gunit.values()[cebProduct.getGunit()].name();
            BigDecimal incrementTax = cebTaxRate.getIncrementTax();
            BigDecimal consumeTax = cebTaxRate.getConsumeTax();
            BigDecimal taxAmount = this.cebProductService.calculateTax(shopPrice, quantity, netContents, gunit, incrementTax, consumeTax);
            totalTaxAmount = NumberUtil.add(totalTaxAmount, NumberUtil.roundHalfEven(taxAmount,2));
        }
        if(totalTaxAmount.compareTo(cebOrderRequest.getTaxTotal()) != 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"入库失败，税金核对失败");
        }
        /*****************************电子订单开始***********************************/
        CebOrder cebOrder = new CebOrder();
        cebOrder.setId(IdWorker.getId());
        /** UUID */
        cebOrder.setGuid(cebOrderRequest.getGuid());
        /** 企业报送类型。1-新增 2-变更 3-删除。默认为1。 */
        cebOrder.setAppType("1");
        /** 企业报送时间。格式:YYYYMMDDhhmmss。 */
        cebOrder.setAppTime(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        /** 业务状态:1-暂存,2-申报,默认为2。 */
        cebOrder.setAppStatus("2");
        /** 电子订单类型：I进口 */
        cebOrder.setOrderType("I");
        /** 交易平台的订单编号，同一交易平台的订单编号应唯一。订单编号长度不能超过60位。 */
        cebOrder.setOrderNo(cebOrderRequest.getOrderNo());
        /** 电商平台支付号 */
        cebOrder.setPayOrderNo(cebOrderRequest.getPayOrderNo());
        /** 电商平台原始单号 */
        cebOrder.setEbcOrderNo(ebcOrderNo);
        /** 业务类型：I10直邮，I20保税 */
        cebOrder.setBizTypeCode(cebOrderRequest.getBizTypeCode());
        /** 电商平台的海关注册登记编号；电商平台未在海关注册登记，由电商企业发送订单的，以中国电子口岸发布的电商平台标识编号为准。 */
        cebOrder.setEbpCode(cebShopEnterprise.getEbpCode());
        /** 电商平台名称 */
        cebOrder.setEbpName(cebShopEnterprise.getEbpName());
        /** 电商企业代码 */
        cebOrder.setEbcCode(cebShopEnterprise.getEbcCode());
        /** 电商企业名称 */
        cebOrder.setEbcName(cebShopEnterprise.getEbcName());
        /** 商品价格 */
        cebOrder.setGoodsValue(cebOrderRequest.getGoodsValue());
        /** 运杂费 */
        cebOrder.setFreight(cebOrderRequest.getFreight());
        /** 非现金抵扣金额 */
        cebOrder.setDiscount(cebOrderRequest.getDiscount());
        /** 代扣税金 */
        cebOrder.setTaxTotal(cebOrderRequest.getTaxTotal());
        /** 实际支付金额 */
        cebOrder.setActuralPaid(cebOrderRequest.getActuralPaid());
        /** 币制 */
        cebOrder.setCurrency("142");
        /** 订购人注册号 */
        cebOrder.setBuyerRegNo(cebOrderRequest.getBuyerRegNo());
        /** 订购人姓名 */
        cebOrder.setBuyerName(cebOrderRequest.getBuyerName());
        /** 订购人证件类型 */
        cebOrder.setBuyerIdType(cebOrderRequest.getBuyerIdType());
        /** 订购人证件号码 */
        cebOrder.setBuyerIdNumber(cebOrderRequest.getBuyerIdNumber());
        /** 订购人电话号码 */
        cebOrder.setBuyerTelephone(cebOrderRequest.getBuyerTelephone());
        /** 支付企业代码 */
        cebOrder.setPayCode(cebPayment.getPayCode());
        /** 支付企业名称 */
        cebOrder.setPayName(cebPayment.getPayName());
        /** 支付交易编号 */
        cebOrder.setPayTransactionId(cebOrderRequest.getPayTransactionId());
        /** 支付交易子流水号 */
        if (cebOrderRequest.getPayKey().equals(DeclareConstant.PayCode.alipay.name())){
            /** 支付宝-拆单与未拆单都是一样，以支付流水号为中心，报关请求流水号自定义，子订单号就是电商系统创建需要报关的订单号。 */
            cebOrder.setPayDeclareNo(cebOrderRequest.getPayOrderNo());
        }
        /** 微信 */
        if (cebOrderRequest.getPayKey().equals(DeclareConstant.PayCode.wxpay.name())){
            /** 未拆单：默认报关的订单号是支付时候的商户号。*/
            if(!DeclareConstant.SplitCode.T.name().equalsIgnoreCase(cebOrderRequest.getSplit())){
                cebOrder.setPayDeclareNo(cebOrderRequest.getPayTransactionId());
            }
            /** 拆单：根据流水号和订单号调用微信接口获取 报关流水号。暂时为空 */
            else{
                cebOrder.setPayDeclareNo("");
            }
        }
        /** 商品批次号 */
        cebOrder.setBatchNumbers("");
        /** 收货人 */
        cebOrder.setConsignee(cebOrderRequest.getConsignee());
        /** 收货人电话 */
        cebOrder.setConsigneeTelephone(cebOrderRequest.getConsigneeTelephone());
        /** 收货地址 */
        cebOrder.setConsigneeAddress(cebOrderRequest.getConsigneeAddress());
        /** 收货地址行政区划代码 */
        cebOrder.setConsigneeDitrict(cebOrderRequest.getConsigneeDitrict());
        /** 传输企业代码 */
        cebOrder.setCopCode(cebBaseTransfer.getCopCode());
        /** 传输企业名称 */
        cebOrder.setCopName(cebBaseTransfer.getCopName());
        /** 报文传输模式 */
        cebOrder.setDxpMode(cebBaseTransfer.getDxpMode());
        /** 报文传输编号 */
        cebOrder.setDxpId(cebBaseTransfer.getDxpId());
        /** 备注 */
        cebOrder.setNote("");
        /** 订单状态 */
        cebOrder.setOrderStatus(0);
        /** 拆单标记 */
        cebOrder.setSplit(cebOrderRequest.getSplit());
        Integer gnum = 1;
        List<CebOrderDetail> cebOrderDetails = Lists.newArrayList();
        for(CebOrderDetailRequest cebOrderDetailRequest : cebOrderReq.getOrderList()){
            CebOrderDetail cebOrderDetail = new CebOrderDetail();
            CebProduct cebProduct = productMap.get(cebOrderDetailRequest.getItemNo());
            /** 订单ID */
            cebOrderDetail.setOrderId(cebOrder.getId());
            /** 商品序号  */
            cebOrderDetail.setGnum(gnum);
            /** 企业商品货号 */
            cebOrderDetail.setItemNo(cebProduct.getItemNo());
            /** 企业商品名称 */
            cebOrderDetail.setItemName(cebProduct.getItemName());
            /** 单位 */
            cebOrderDetail.setUnit(cebProduct.getUnit());
            /** 企业商品描述 */
            cebOrderDetail.setItemDescribe(cebProduct.getItemDescribe());
            /** 商品数量 */
            cebOrderDetail.setQty(cebOrderDetailRequest.getQty());
            /** 单价 */
            cebOrderDetail.setPrice(cebOrderDetailRequest.getPrice());
            /** 总价 */
            cebOrderDetail.setTotalPrice(NumberUtil.mul(cebOrderDetailRequest.getQty(), cebOrderDetailRequest.getPrice()));
            /** 币制  */
            cebOrderDetail.setCurrency("142");
            /** 原产国 */
            cebOrderDetail.setCountry(cebProduct.getCountry());
            /** 条形码  */
            cebOrderDetail.setBarCode(cebProduct.getBarCode());
            /** 备注 */
            cebOrderDetail.setNote("");
            /** 规格型号 */
            cebOrderDetail.setGmodel(cebProduct.getGmodel());
            gnum++;
            cebOrderDetails.add(cebOrderDetail);
        }
        this.cebOrderService.insert(cebOrder);
        this.cebOrderDetailService.insertBatch(cebOrderDetails);
        /*****************************电子订单结束***********************************/

        /*****************************电子清单开始***********************************/
        CebInvt cebInvt = new CebInvt();
        cebInvt.setId(IdWorker.getId());
        /** UUID */
        cebInvt.setGuid(cebOrder.getGuid());
        /** 报文类型 */
        cebInvt.setAppType(cebOrder.getAppType());
        /** 报送时间 */
        cebInvt.setAppTime(cebOrder.getAppTime());
        /** 业务状态 */
        cebInvt.setAppStatus(cebOrder.getAppStatus());
        /** 订单编号 */
        cebInvt.setOrderNo(cebOrder.getOrderNo());
        /** 电商平台代码 */
        cebInvt.setEbpCode(cebOrder.getEbpCode());
        /** 电商平台名称 */
        cebInvt.setEbpName(cebOrder.getEbpName());
        /** 电商企业名称 */
        cebInvt.setEbcCode(cebOrder.getEbcCode());
        /** 电商企业名称 */
        cebInvt.setEbcName(cebOrder.getEbcName());
        /** 物流运单编号 */
        cebInvt.setLogisticsNo(waybill);
        /** 物流企业代码 */
        cebInvt.setLogisticsCode(cebLogistics.getLogisticsCode());
        /** 物流企业名称 */
        cebInvt.setLogisticsName(cebLogistics.getLogisticsName());
        /** 企业内部编号 */
        cebInvt.setCopNo(IdWorker.getId() + "");
        /** 预录入编号 */
        cebInvt.setPreNo("");
        /** 担保企业编号 */
        cebInvt.setAssureCode(cebShopEnterprise.getAssureCode());
        /** 账册编号 */
        cebInvt.setEmsNo(cebAreaCompany.getEmsNo());
        /** 清单编号 */
        cebInvt.setInvtNo("");
        /** 进出口标记 */
        cebInvt.setIeFlag("I");
        /** 申报日期 */
        cebInvt.setDeclTime(DateUtil.format(new Date(), "yyyyMMdd"));
        /** 申报海关代码 */
        cebInvt.setCustomsCode(cebOrderRequest.getCustomsCode());
        /** 口岸海关代码 */
        cebInvt.setPortCode(cebOrderRequest.getCustomsCode());
        /** 进口日期 */
        cebInvt.setIeDate(DateUtil.format(new Date(), "yyyyMMdd"));
        /** 订购人证件类型 */
        cebInvt.setBuyerIdType("1");
        /** 订购人证件号码 */
        cebInvt.setBuyerIdNumber(cebOrder.getBuyerIdNumber());
        /** 订购人姓名 */
        cebInvt.setBuyerName(cebOrder.getBuyerName());
        /** 订购人电话 */
        cebInvt.setBuyerTelephone(cebOrder.getBuyerTelephone());
        /** 收件地址 */
        cebInvt.setConsigneeAddress(cebOrder.getConsigneeAddress());
        /** 申报企业代码 */
        cebInvt.setAgentCode(cebShopEnterprise.getAgentCode());
        /** 申报企业名称 */
        cebInvt.setAgentName(cebShopEnterprise.getAgentName());
        /** 区内企业代码 */
        cebInvt.setAreaCode(cebAreaCompany.getAreaCode());
        /** 区内企业名称 */
        cebInvt.setAreaName(cebAreaCompany.getAreaName());
        /** 贸易方式 */
        cebInvt.setTradeMode("I10".equals(cebOrderRequest.getBizTypeCode()) ? "9610" : "1210");
        /** 运输方式 */
        cebInvt.setTrafMode("I20".equals(cebOrderRequest.getBizTypeCode()) ? "Y" : cebOrderRequest.getTrafModel());
        /** 运输工具编号 */
        cebInvt.setTrafNo("");
        /** 航班航次号 */
        cebInvt.setVoyageNo("");
        /** 提运单号 */
        cebInvt.setBillNo("");
        /** 监管场所代码 */
        cebInvt.setLoctNo("");
        /** 许可证件号 */
        cebInvt.setLicenseNo("");
        /** 起运国(地区) */
        cebInvt.setCountry("I20".equals(cebOrderRequest.getBizTypeCode()) ? "142" : null);
        /** 运费 */
        cebInvt.setFreight(cebOrder.getFreight());
        /** 保费 */
        cebInvt.setInsuredFee(new BigDecimal(0));
        /** 币制 */
        cebInvt.setCurrency("142");
        /** 包装种类代码 */
        cebInvt.setWrapType("2");
        /** 件数 */
        cebInvt.setPackNo(1);
        /** 毛重(KG) */
        cebInvt.setGrossWeight(cebOrderRequest.getGrossWeight());
        /** 净重(KG) */
        cebInvt.setNetWeight(cebOrderRequest.getNetWeight());
        /** 分拣线ID */
        cebInvt.setSortlineId(cebOrderRequest.getSortlineId());
        /** 时间机构代码 */
        cebInvt.setOrgCode("");
        /** 备注 */
        cebInvt.setNote("");
        /** 传输企业代码 */
        cebInvt.setCopCode(cebBaseTransfer.getCopCode());
        /** 传输企业名称 */
        cebInvt.setCopName(cebBaseTransfer.getCopName());
        /** 报文传输模式 */
        cebInvt.setDxpMode(cebBaseTransfer.getDxpMode());
        /** 报文传输编号 */
        cebInvt.setDxpId(cebBaseTransfer.getDxpId());
        /** 备注 */
        cebInvt.setBtNote("");
        /** 清单状态 */
        cebInvt.setInvtStatus(0);
        gnum = 1;
        List<CebInvtDetail> cebInvtDetails = Lists.newArrayList();
        for (CebOrderDetailRequest cebOrderDetailRequest : cebOrderReq.getOrderList()){
            CebProduct cebProduct = productMap.get(cebOrderDetailRequest.getItemNo());
            CebInvtDetail cebInvtDetail = new CebInvtDetail();
            cebInvtDetail.setId(IdWorker.getId());
            /** 清单ID */
            cebInvtDetail.setInvtId(cebInvt.getId());
            /** 商品序号 */
            cebInvtDetail.setGnum(gnum);
            gnum++;
            /** 账册备案号 */
            cebInvtDetail.setItemRecordNo(cebProduct.getItemRecordNno());
            /** 企业商品货号 */
            cebInvtDetail.setItemNo(cebProduct.getItemNo());
            /** 企业商品品名 */
            cebInvtDetail.setItemName(cebProduct.getItemName());
            /** 商品编码-海关10位商品编码 */
            cebInvtDetail.setGcode(cebProduct.getGcode());
            /** 商品名称 */
            cebInvtDetail.setGname(cebProduct.getItemName());
            /** 商品规格型号 */
            cebInvtDetail.setGmodel(cebProduct.getGmodel());
            /** 条码 */
            cebInvtDetail.setBarCode(cebProduct.getBarCode());
            /** 原产国(地区) */
            cebInvtDetail.setCountry(cebProduct.getCountry());
            /** 贸易国 */
            cebInvtDetail.setTradeCountry(cebProduct.getTradeCountry());
            /** 币制 */
            cebInvtDetail.setCurrency("142");
            /** 数量 */
            cebInvtDetail.setQty(cebOrderDetailRequest.getQty());
            /** 法定数量 */
            cebInvtDetail.setQty1(cebProduct.getQty1());
            /** 第二数量 */
            cebInvtDetail.setQty2(cebProduct.getQty2());
            /** 计量单位 */
            cebInvtDetail.setUnit(cebProduct.getUnit());
            /** 法定计量单位 */
            cebInvtDetail.setUnit1(cebProduct.getUnit1());
            /** 第二计量单位 */
            cebInvtDetail.setUnit2(cebProduct.getUnit2());
            /** 单价 */
            cebInvtDetail.setPrice(cebOrderDetailRequest.getPrice());
            /** 总价 */
            cebInvtDetail.setTotalPrice(NumberUtil.mul(cebOrderDetailRequest.getPrice(), cebOrderDetailRequest.getQty()));
            /** 备注 */
            cebInvtDetail.setNote("");

            cebInvtDetails.add(cebInvtDetail);
        }
        this.cebInvtService.insert(cebInvt);
        this.cebInvtDetailService.insertBatch(cebInvtDetails);
        /*****************************电子清单结束***********************************/


        /*****************************支付单开始***********************************/

        CebPay cebPay = new CebPay();
        /** 系统唯一序号 */
        cebPay.setGuid(cebOrder.getGuid());
        /** 报送类型 */
        cebPay.setAppType(cebOrder.getAppType());
        /** 报送时间 */
        cebPay.setAppTime(cebOrder.getAppTime());
        /** 业务状态 */
        cebPay.setAppStatus(cebOrder.getAppStatus());
        /** 支付企业代码KEY */
        cebPay.setPayKey(cebPayment.getPayNo());
        /** 支付企业代码 */
        cebPay.setPayCode(cebPayment.getPayCode());
        /** 支付企业名称 */
        cebPay.setPayName(cebPayment.getPayName());
        /** 支付标识-区分用户端和门店端 */
        cebPay.setPayFlag(cebOrderRequest.getPayFlag());
        /** 支付交易编号 */
        cebPay.setPayTransactionId(cebOrder.getPayTransactionId());
        /** 子支付交易编号 */
        cebPay.setPayDeclareNo(cebOrder.getPayDeclareNo());
        /** 订单编号 */
        cebPay.setOrderNo(cebOrder.getOrderNo());
        /** 平台支付号 */
        cebPay.setPayOrderNo(cebOrder.getPayOrderNo());
        /** 电商平台代码 */
        cebPay.setEbpCode(cebOrder.getEbpCode());
        /** 电商平台名称 */
        cebPay.setEbpName(cebOrder.getEbpName());
        /** 支付人证件类型 */
        cebPay.setPayerIdType(cebOrder.getBuyerIdType());
        /** 支付人 */
        cebPay.setPayerName(cebOrderRequest.getBuyerName());
        /** 支付人证件号码 */
        cebPay.setPayerIdNumber(cebOrder.getBuyerIdNumber());
        /** 支付人电话 */
        cebPay.setTelephone(cebOrder.getBuyerTelephone());
        /** 支付金额 */
        cebPay.setAmountPaid(cebOrder.getActuralPaid());
        /** 支付币值 */
        cebPay.setCurrency("142");
        /** 支付时间 */
        cebPay.setPayTime(cebOrderRequest.getPayTime());
        /** 传输企业代码 */
        cebPay.setCopCode(cebOrder.getCopCode());
        /** 传输企业名称 */
        cebPay.setCopName(cebOrder.getCopName());
        /** 报文传输模式 */
        cebPay.setDxpModel(cebOrder.getDxpMode());
        /** 报文传输编号 */
        cebPay.setDxpId(cebOrder.getDxpId());
        /** 备注 */
        cebPay.setNote("");
        /** 支付申报状态：0-未申报，1-成功，2-失败 */
        cebPay.setPayStatus(0);
        /** 拆单标志-T拆单，F为不拆单 */
        cebPay.setSplit(cebOrderRequest.getSplit());
        this.cebPayService.insert(cebPay);
        /*****************************支付单结束***********************************/

        /*****************************运单开始***********************************/
        CebWaybill cebWaybill = new CebWaybill();
        /** 系统唯一编号 */
        cebWaybill.setGuid(cebOrder.getGuid());
        /** 报送类型 */
        cebWaybill.setAppType(cebOrder.getAppType());
        /** 报送时间 */
        cebWaybill.setAppTime(cebOrder.getAppTime());
        /** 业务状态 */
        cebWaybill.setAppStatus(cebOrder.getAppStatus());
        /** 订单编号 */
        cebWaybill.setOrderNo(cebOrder.getOrderNo());
        /** 物流企业代码KEY */
        cebWaybill.setLogisticsKey(cebOrderRequest.getLogisticsKey());
        /** 物流企业代码 */
        cebWaybill.setLogisticsCode(cebLogistics.getLogisticsCode());
        /** 物流企业名称 */
        cebWaybill.setLogisticsName(cebLogistics.getLogisticsName());
        /** 物流运单编号 */
        cebWaybill.setLogisticsNo(waybill);
        /** 提运单号 */
        cebWaybill.setBillNo("");
        /** 电商海关备案编码 */
        cebWaybill.setEbcCode(cebOrder.getEbcCode());
        /** 运费 */
        cebWaybill.setFreight(cebOrderRequest.getFreight());
        /** 保价费 */
        cebWaybill.setInsuredFee(new BigDecimal(0));
        /** 币值 */
        cebWaybill.setCurrency("142");
        /** 毛重 */
        cebWaybill.setWeight(cebOrderRequest.getGrossWeight());
        /** 件数 */
        cebWaybill.setPackNo(1);
        /** 主要货物信息 */
        cebWaybill.setGoodsInfo("");
        /** 收货人姓名 */
        cebWaybill.setConsingee(cebOrderRequest.getConsignee());
        /** 收货地址 */
        cebWaybill.setConsigneeAddress(cebOrderRequest.getConsigneeAddress());
        /** 收货人电话 */
        cebWaybill.setConsigneeTelephone(cebOrderRequest.getConsigneeTelephone());
        /** 传输企业代码 */
        cebWaybill.setCopCode(cebOrder.getCopCode());
        /** 传输企业名称 */
        cebWaybill.setCopName(cebOrder.getCopName());
        /** 报文传输模式 */
        cebWaybill.setDxpModel(cebOrder.getDxpMode());
        /** 报文传输编号 */
        cebWaybill.setDxpId(cebOrder.getDxpId());
        /** 备注 */
        cebWaybill.setNote("");
        /** 运单申报状态：0-未申报，1-成功，2-失败 */
        cebWaybill.setWaybillStatus(0);
        this.cebWaybillService.insert(cebWaybill);
        /*****************************运单结束***********************************/
        return new Result(CodeEnum.SUCCESS, "等待申报");
    }

    /**
     * 订单申报组装报文
     *
     * @return Result
     */
    public Result declareOrderCustom(String orderNo, String ebcCode){
        CebOrder cebOrder = this.cebOrderService.findCebOrder(orderNo, ebcCode);
        List<CebOrderDetail> orderDetailList = this.cebOrderDetailService.findListByOrderId(cebOrder.getId());
        if(cebOrder == null ||orderDetailList == null || orderDetailList.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS, "参数不能为空");
        }
        OrderHead orderHead = new OrderHead();
        BeanUtils.copyProperties(cebOrder, orderHead);
        List<OrderList> orderList = Lists.newArrayList();
        for(CebOrderDetail cebOrderDetail : orderDetailList){
            OrderList orderDetail = new OrderList();
            BeanUtils.copyProperties(cebOrderDetail, orderDetail);
            orderDetail.setPrice(NumberUtil.roundHalfEven(orderDetail.getPrice(), 2));
            orderDetail.setTotalPrice(NumberUtil.roundHalfEven(orderDetail.getTotalPrice(), 2));
            orderList.add(orderDetail);
        }
        CEB311Order ceb311Order = new CEB311Order();
        //设置流水号为空
        orderHead.setPayTransactionId("");
        orderHead.setTaxTotal(NumberUtil.roundHalfEven(orderHead.getTaxTotal(), 2));
        orderHead.setActuralPaid(NumberUtil.roundHalfEven(orderHead.getActuralPaid(), 2));
        orderHead.setGoodsValue(NumberUtil.roundHalfEven(orderHead.getGoodsValue(), 2));
        orderHead.setDiscount(NumberUtil.roundHalfEven(orderHead.getDiscount(), 2));
        orderHead.setFreight(NumberUtil.roundHalfEven(orderHead.getFreight(), 2));
        orderHead.setAppTime(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        ceb311Order.setOrderHead(orderHead);
        ceb311Order.setOrderLists(orderList);
        BaseTransfer baseTransfer = new BaseTransfer();
        baseTransfer.setCopCode(cebOrder.getCopCode());
        baseTransfer.setCopName(cebOrder.getCopName());
        baseTransfer.setDxpMode(cebOrder.getDxpMode());
        baseTransfer.setDxpId(cebOrder.getDxpId());
        baseTransfer.setNote("");
        CEB311Message ceb311Message = new CEB311Message();
        ceb311Message.setGuid(cebOrder.getGuid());
        ceb311Message.setVersion("1.0");
        ceb311Message.setOrder(ceb311Order);
        ceb311Message.setBaseTransfer(baseTransfer);
        String xml = XmlUtil.objectToXml(ceb311Message);
        log.info("订单申报组装报文========================================" + xml);
        //推送口岸
        Map<String, String> result = postCustom("","",xml);
        //回执信息
        Ceb312OrderReturn ceb312OrderReturn = new Ceb312OrderReturn();
        ceb312OrderReturn.setEbcCode(cebOrder.getEbcCode());
        ceb312OrderReturn.setEbpCode(cebOrder.getCopCode());
        ceb312OrderReturn.setGuid(cebOrder.getGuid());
        ceb312OrderReturn.setOrderNo(cebOrder.getOrderNo());
        ceb312OrderReturn.setReturnInfo(result.get("msg"));
        ceb312OrderReturn.setReturnStatus(result.get("status"));
        ceb312OrderReturn.setReturnTime(DateUtil.format(new Date(),"yyyyMMddHHmmss"));
        CEB312Message ceb312Message = new CEB312Message();
        ceb312Message.setOrderReturn(ceb312OrderReturn);
        this.cebNotifyService.orderNotify(XmlUtil.objectToXml(ceb312Message));
        return new Result(result.get("status").equals("1") ? CodeEnum.SUCCESS : CodeEnum.FAIL_BUSINESS);
    }

    /**
     * 清单申报组装报文
     *
     * @return
     */
    public Result declareInvtCustom(String orderNo, String ebcCode){
        CebInvt cebInvt = this.cebInvtService.findInvt(orderNo, ebcCode);
        List<CebInvtDetail> cebInvtDetails = this.cebInvtDetailService.findListByInvtId(cebInvt.getId());
        if(cebInvt == null ||cebInvtDetails == null || cebInvtDetails.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS, "参数不能为空");
        }
        if(cebInvt.getInvtStatus() == DeclareConstant.DeclareStatus.Status_44.getIndex()){
            log.info("已经终止申报不允许继续申报==============================" + JSON.toJSONString(cebInvt));
            return new Result(CodeEnum.FAIL_BUSINESS, "已经终止申报不允许继续申报");
        }
        InventoryHead inventoryHead = new InventoryHead();
        BeanUtils.copyProperties(cebInvt, inventoryHead);
        List<InventoryList> inventoryLists = Lists.newArrayList();
        for(CebInvtDetail cebInvtDetail : cebInvtDetails){
            InventoryList inventoryList = new InventoryList();
            BeanUtils.copyProperties(cebInvtDetail, inventoryList);
            inventoryList.setPrice(NumberUtil.roundHalfEven(inventoryList.getPrice(),2));
            inventoryList.setTotalPrice(NumberUtil.roundHalfEven(inventoryList.getTotalPrice(),2));
            inventoryLists.add(inventoryList);
        }

        BaseTransfer baseTransfer = new  BaseTransfer();
        baseTransfer.setCopCode(cebInvt.getCopCode());
        baseTransfer.setCopName(cebInvt.getCopName());
        baseTransfer.setDxpMode(cebInvt.getDxpMode());
        baseTransfer.setDxpId(cebInvt.getDxpId());
        baseTransfer.setNote("");
        Inventory inventory = new Inventory();
        inventoryHead.setAppTime(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        inventoryHead.setPreNo("");
        inventoryHead.setAppType(cebInvt.getInvtStatus() != 0 && StringUtils.isNotEmpty(cebInvt.getInvtNo()) ? "2" : "1");
        inventoryHead.setGrossWeight(NumberUtil.roundHalfEven(inventoryHead.getGrossWeight(), 2));
        inventoryHead.setNetWeight(NumberUtil.roundHalfEven(inventoryHead.getNetWeight(), 2));
        inventoryHead.setFreight(NumberUtil.roundHalfEven(inventoryHead.getFreight(), 2));
        inventoryHead.setInsuredFee(NumberUtil.roundHalfEven(inventoryHead.getInsuredFee(),2));
        inventory.setInventoryHead(inventoryHead);
        inventory.setInventoryLists(inventoryLists);
        CEB621Message ceb621Message = new CEB621Message();
        ceb621Message.setGuid(cebInvt.getGuid());
        ceb621Message.setVersion("1.0");
        ceb621Message.setInventory(inventory);
        ceb621Message.setBaseTransfer(baseTransfer);
        String xml = XmlUtil.objectToXml(ceb621Message);
        log.info("清单申报组装报文========================================" + xml);
        //推送口岸
        Map<String, String> result = postCustom("","",xml);
        //回执信息
        InventoryReturn inventoryReturn = new InventoryReturn();
        inventoryReturn.setAgentCode(cebInvt.getAgentCode());
        inventoryReturn.setCustomsCode(cebInvt.getCustomsCode());
        inventoryReturn.setEbcCode(cebInvt.getEbcCode());
        inventoryReturn.setEbpCode(cebInvt.getCopCode());
        inventoryReturn.setGuid(cebInvt.getGuid());
        inventoryReturn.setCopNo(cebInvt.getCopNo());
        inventoryReturn.setReturnInfo(result.get("msg"));
        inventoryReturn.setReturnStatus(result.get("status"));
        inventoryReturn.setReturnTime(DateUtil.format(new Date(),"yyyyMMddHHmmss"));
        CEB622Message ceb622Message = new CEB622Message();
        ceb622Message.setInventoryReturn(inventoryReturn);
        this.cebNotifyService.invtNotify(XmlUtil.objectToXml(ceb622Message));
        return new Result(result.get("status").equals("1") ? CodeEnum.SUCCESS : CodeEnum.FAIL_BUSINESS, inventoryReturn.getReturnInfo());
    }

    /**
     * 运单申报
     *
     * @return
     */
    public Result declareWaybillCustom(String orderNo, String ebpCode, String ebcCode){

        CebWaybill cebWaybill = this.cebWaybillService.findByOrderNo(orderNo, ebcCode);
        CebOrder cebOrder = this.cebOrderService.findCebOrder(orderNo, ebcCode);
        List<CebOrderDetail> orderList = this.cebOrderDetailService.findListByOrderId(cebOrder.getId());
        if(cebOrder == null || null == cebWaybill){
            return new Result(CodeEnum.FAIL_BUSINESS, "参数不能为空");
        }
        if(StringUtils.isEmpty(cebWaybill.getLogisticsKey())){
            return new Result(CodeEnum.FAIL_BUSINESS, "物流KEY参数不能为空");
        }
        int waybillStatus = 406;
        String returnInfo = "";
        String logisticsNo = cebWaybill.getLogisticsNo();
        /****************运单快递****************/
        /** 韵达 */
        if(cebWaybill.getLogisticsKey().equals("yunda")){
            CebLogistics cebLogistics = this.cebLogisticsService.findByLogisticsNo(cebWaybill.getLogisticsKey(), cebWaybill.getEbcCode());
            Result result = waybillService.yunda(cebLogistics.getAppKey(), cebLogistics.getAppSecret(), cebLogistics.getPartner(), cebWaybill.getLogisticsNo(), cebOrder, orderList);
            if(!result.isSuccess()){
                waybillStatus = 408;
                returnInfo = result.getMsg();
            }else{
                waybillStatus = 407;
                returnInfo = "运单申报推送成功";
                logisticsNo = result.getMsg();
            }
        }
        /** EMS */
        else if(cebWaybill.getLogisticsKey().equals("ems")){

        }
        if(!StringUtils.isEmpty(logisticsNo) && StringUtils.isEmpty(cebWaybill.getLogisticsNo())){
            cebWaybill.setLogisticsNo(logisticsNo);
            this.cebWaybillService.updateById(cebWaybill);
            //更新清单的运单
            CebInvt cebInvt = this.cebInvtService.findInvt(cebOrder.getOrderNo(), cebOrder.getEbcCode());
            cebInvt.setLogisticsNo(logisticsNo);
            this.cebInvtService.updateById(cebInvt);
            //更新通知运单
            OrderNotify orderNotify = orderNotifyService.findOrderNotify(cebOrder.getOrderNo(), cebOrder.getEbcCode());
            orderNotify.setWaybill(logisticsNo);
            this.orderNotifyService.updateById(orderNotify);
        }
        //调用运单回执
        this.waybillService.waybillReturn(cebOrder.getEbcCode(), cebOrder.getOrderNo(), cebWaybill.getLogisticsCode(), logisticsNo, cebOrder.getGuid(),waybillStatus, returnInfo);
        return new Result(CodeEnum.SUCCESS);
    }



    /**
     * @author 白飞
     * @description: 支付单报关
     * @param: [orderNo]
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @create 2018-09-17 13:51
     **/
    public Result declarePayCustom(String orderNo, String ebpCode, String ebcCode) {
        CebPay cebPay = this.cebPayService.findCebPay(orderNo, ebpCode);
        CebOrder cebOrder = this.cebOrderService.findCebOrder(orderNo, ebcCode);
        Map<String, Object> result = Maps.newHashMap();
        result.put("payStatus", DeclareConstant.DeclareStatus.Status_806.getIndex());
        result.put("returnInfo", "支付单等待申报");
        String payTransactionId = cebOrder.getPayTransactionId();
        try {
            cebOrder.setFreight(null == cebOrder.getFreight() ? new BigDecimal(0) : cebOrder.getFreight());
            CustomDeclareRequstVO customDeclare = new CustomDeclareRequstVO();
            if(cebPay.getPayKey().equals(DeclareConstant.PayCode.alipay.name())){
                customDeclare.setPay_order_id(cebPay.getPayDeclareNo());
                customDeclare.setSub_order_id(cebPay.getOrderNo());
            }
            else if(cebPay.getPayKey().equals(DeclareConstant.PayCode.wxpay.name())){
                customDeclare.setPay_order_id(cebPay.getPayOrderNo());
                customDeclare.setSub_order_id(cebPay.getSplit().equalsIgnoreCase("T") ? cebPay.getOrderNo() : "");
            }
            customDeclare.setOrder_fee(NumberUtil.mul(cebPay.getAmountPaid(), 100).intValue());
            customDeclare.setProduct_fee(NumberUtil.mul(cebPay.getAmountPaid(), 100).intValue());
            customDeclare.setBuyer_cert_id(cebPay.getPayerIdNumber());
            customDeclare.setBuyer_name(cebPay.getPayerName());
            customDeclare.setDeclare_amt(NumberUtil.roundHalfEven(cebPay.getAmountPaid(), 2).toString());
            customDeclare.setPay_trade_num(cebPay.getPayTransactionId());
            customDeclare.setTransport_fee(NumberUtil.roundHalfEven(NumberUtil.mul(cebOrder.getFreight(), 100),2).intValue());
            log.info("customDeclare请求支付申报参数===============" + JSON.toJSONString(customDeclare));
            String paymentConfig = CodeCache.getValueByKey("PaymentConfig", "S" + cebPay.getPayFlag());
            log.info("申报海关 ，支付配置信息：paymentConfig===" + paymentConfig);
            JSONObject payment_config = JSON.parseObject(paymentConfig);

            /*********************************报关开始*********************************/
            //01-微信支付
            if (cebPay.getPayKey().equals(DeclareConstant.PayCode.wxpay.name())) {
                Map<String, Object> resultPay = WxPay.declareCustom(payment_config, customDeclare);
                if ("SUCCESS".equals(resultPay.get("return_code")) && "SUCCESS".equals(resultPay.get("result_code"))) {
                    if (resultPay.get("sub_order_id") != null) {
                        //子流水号不为空时支付平台订单号为微信子订单号
                        cebPay.setPayDeclareNo(resultPay.get("sub_order_id").toString());
                        //对订单子流水号和支付表子流水号更新
                        cebPayService.updateById(cebPay);
                        cebOrder.setPayDeclareNo(cebPay.getPayDeclareNo());
                        this.cebOrderService.updateById(cebOrder);
                    } else {
                        //子流水号为空时支付平台订单号为微信支付订单号
                        cebPay.setPayDeclareNo(resultPay.get("transaction_id").toString());
                    }
                    result.put("payStatus", DeclareConstant.DeclareStatus.Status_807.getIndex());
                    result.put("returnInfo", DeclareConstant.DeclareStatus.Status_807.getName());
                }else {
                    result.put("payStatus", DeclareConstant.DeclareStatus.Status_808.getIndex());
                    result.put("returnInfo", DeclareConstant.DeclareStatus.Status_808.getName());
                    log.info("申报海关 ，支付单申报失败：result===" + cebPay.getOrderNo() + "--------" + JSON.toJSONString(resultPay));
                }
                if(resultPay.get("cert_check_result").equals("DIFFERENT")){
                    result.put("payStatus", DeclareConstant.DeclareStatus.Status_47.getIndex());
                    result.put("returnInfo", DeclareConstant.DeclareStatus.Status_47.getName());
                }
            }
            //02-支付宝支付
            if (cebPay.getPayKey().equals(DeclareConstant.PayCode.alipay.name())) {
                Ali alipay = AliPay.declareCustom(payment_config, customDeclare);
                //02 支付宝
                if (alipay != null && "T".equals(alipay.getIs_success()) && alipay.getResponse() != null && alipay.getResponse().getAlipay() != null && alipay.getResponse().getAlipay().getResult_code().equals("SUCCESS") && alipay.getResponse().getAlipay().getIdentity_check().equalsIgnoreCase("T")) {
                    cebPay.setPayDeclareNo(alipay.getResponse().getAlipay().getTrade_no());
                    result.put("payStatus", DeclareConstant.DeclareStatus.Status_807.getIndex());
                    result.put("returnInfo", DeclareConstant.DeclareStatus.Status_807.getName());
                    cebOrder.setPayDeclareNo(cebPay.getPayDeclareNo());
                    this.cebOrderService.updateById(cebOrder);
                }else{
                    result.put("payStatus", DeclareConstant.DeclareStatus.Status_808.getIndex());
                    result.put("returnInfo", alipay.getResponse().getAlipay() != null ? alipay.getResponse().getAlipay().getDetail_error_des() : "支付单申报失败");
                    log.info("申报海关 ，支付单申报失败：result===" + cebPay.getOrderNo() + "--------" + JSON.toJSONString(alipay));
                }
                //支付与订购人不一致
                if(alipay.getResponse().getAlipay().getIdentity_check().equalsIgnoreCase("F")){
                    result.put("payStatus", DeclareConstant.DeclareStatus.Status_47.getIndex());
                    result.put("returnInfo", DeclareConstant.DeclareStatus.Status_47.getName());
                }
            }
            /*********************************报关结束*********************************/
        }catch (Exception e){
            result.put("payStatus", DeclareConstant.DeclareStatus.Status_805.getIndex());
            result.put("returnInfo", "支付单申报出现异常");
            e.printStackTrace();
            log.info("申报海关 ，支付单申报出现异常：result===" + cebPay.getOrderNo() + "--------" + JSON.toJSONString(result));
        }
        log.info("申报海关 ，自定义结果信息：result===" + JSON.toJSONString(result));
        Integer payStatus =  Integer.valueOf(result.get("payStatus") + "");
        String returnInfo = result.get("returnInfo") + "";
        PaymentReturn paymentReturn = new PaymentReturn();
        paymentReturn.setGuid(cebPay.getGuid());
        paymentReturn.setPayCode(cebPay.getPayCode());
        paymentReturn.setPayTransactionId(payTransactionId);
        paymentReturn.setReturnInfo(returnInfo);
        paymentReturn.setReturnStatus(payStatus + "");
        paymentReturn.setReturnTime(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        paymentReturn.setOrderNo(cebOrder.getOrderNo());
        paymentReturn.setPayDeclareNo(cebOrder.getPayDeclareNo());
        CEB412Message ceb412Message = new CEB412Message();
        ceb412Message.setPaymentReturn(paymentReturn);
        cebNotifyService.paymentNotify(XmlUtil.objectToXml(ceb412Message));

        return new Result(payStatus.equals(DeclareConstant.DeclareStatus.Status_807.getIndex()) ? CodeEnum.SUCCESS.getCode() : CodeEnum.FAIL_PARAMCHECK.getCode(), returnInfo, payStatus);

    }


    /**
     * 根据订单号分别申报 4 单
     *
     * @param orderNo
     *          订单号
     * @param ebpCode
     *          电商平台编码
     * @param ebcCode
     *          电商备案10位编码
     *
     * @return result
     */
    public Result declareCustom(String orderNo, String ebpCode, String ebcCode){
        try {
            //支付单
            Result result = this.declarePayCustom(orderNo, ebpCode, ebcCode);
            if(result.isSuccess()){
                //运单
                result = this.declareWaybillCustom(orderNo, ebpCode, ebcCode);
                if(result.isSuccess()){
                    //订单
                    result = this.declareOrderCustom(orderNo, ebcCode);
                    if(result.isSuccess()){
                        //清单
                        result = this.declareInvtCustom(orderNo, ebcCode);
                    }
                }
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(CodeEnum.FAIL_BUSINESS, "申报出现异常");
        }
    }


    /**
     * 取消申报
     *
     * @param xml
     *          报文
     * @return 响应信息
     */
    public String cancel(String xml){
        try {
            OrderCancelReturn cancelReturn = new OrderCancelReturn();
            EshopDeclareReturn declareReturn = new EshopDeclareReturn();
            if(StringUtils.isEmpty(xml)){
                declareReturn.setDeclareStatus(-1);
                declareReturn.setReturnCode("ERROR");
                declareReturn.setReturnInfo("报文错误");
                cancelReturn.setDeclareReturn(declareReturn);
                return XmlUtil.objectToXml(cancelReturn);
            }
            log.info("取消申报进入参数=====================" + xml);
            OrderCancel orderCancel = XmlUtil.xmlToObject(xml, OrderCancel.class);
            if(orderCancel == null || orderCancel.getOrder() == null || StringUtils.isEmpty(orderCancel.getOrder().getOrderNo()) || StringUtils.isEmpty(orderCancel.getOrder().getEbcCode())){
                declareReturn.setDeclareStatus(-1);
                declareReturn.setReturnCode("ERROR");
                declareReturn.setReturnInfo("报文错误");
                cancelReturn.setDeclareReturn(declareReturn);
                xml = XmlUtil.objectToXml(cancelReturn);
                log.info("取消申报响应=====================" + xml);
                return xml;
            }
            CebInvt cebInvt = this.cebInvtService.findInvt(orderCancel.getOrder().getOrderNo(), orderCancel.getOrder().getEbcCode());
            if(cebInvt == null){
                declareReturn.setDeclareStatus(-1);
                declareReturn.setReturnCode("ERROR");
                declareReturn.setReturnInfo("请检查数据是否在存在");
                cancelReturn.setDeclareReturn(declareReturn);
                xml = XmlUtil.objectToXml(cancelReturn);
                log.info("取消申报响应=====================" + xml);
                return xml;
            }
            if(cebInvt.getInvtStatus() == DeclareConstant.DeclareStatus.Status_44.getIndex()){
                //取消失败
                declareReturn.setDeclareStatus(DeclareConstant.DeclareStatus.Status_44.getIndex());
                declareReturn.setReturnCode("SUCCESS");
                declareReturn.setReturnInfo(DeclareConstant.getDeclareName(declareReturn.getDeclareStatus()));
                cancelReturn.setDeclareReturn(declareReturn);
                EshopOrder order = new EshopOrder();
                order.setEbcCode(cebInvt.getEbcCode());
                order.setEbcName(cebInvt.getEbcName());
                order.setOrderNo(cebInvt.getOrderNo());
                cancelReturn.setOrder(order);
                xml = XmlUtil.objectToXml(cancelReturn);
                log.info("取消申报响应=====================" + xml);
                return xml;
            }
            //待申报，可以直接取消,设置为终止申报
            if(cebInvt.getInvtStatus() == DeclareConstant.DeclareStatus.Status_0.getIndex()){
                cebInvt.setInvtStatus(DeclareConstant.DeclareStatus.Status_44.getIndex());
                this.cebInvtService.updateById(cebInvt);
                declareReturn.setDeclareStatus(DeclareConstant.DeclareStatus.Status_44.getIndex());
                declareReturn.setReturnCode("SUCCESS");
                declareReturn.setReturnInfo(DeclareConstant.getDeclareName(DeclareConstant.DeclareStatus.Status_44.getIndex()));
                cancelReturn.setDeclareReturn(declareReturn);
                EshopOrder order = new EshopOrder();
                order.setEbcCode(cebInvt.getEbcCode());
                order.setEbcName(cebInvt.getEbcName());
                order.setOrderNo(cebInvt.getOrderNo());
                cancelReturn.setOrder(order);
                xml = XmlUtil.objectToXml(cancelReturn);
                log.info("取消申报响应=====================" + xml);
                return xml;
            }
            //取消失败
            declareReturn.setDeclareStatus(DeclareConstant.DeclareStatus.Status_809.getIndex());
            declareReturn.setReturnCode("SUCCESS");
            declareReturn.setReturnInfo(DeclareConstant.getDeclareName(declareReturn.getDeclareStatus()));
            cancelReturn.setDeclareReturn(declareReturn);
            EshopOrder order = new EshopOrder();
            order.setEbcCode(cebInvt.getEbcCode());
            order.setEbcName(cebInvt.getEbcName());
            order.setOrderNo(cebInvt.getOrderNo());
            cancelReturn.setOrder(order);
            xml = XmlUtil.objectToXml(cancelReturn);
            log.info("取消申报响应=====================" + xml);
            return xml;
        }catch (BizException e){
            e.printStackTrace();
            OrderCancelReturn cancelReturn = new OrderCancelReturn();
            EshopDeclareReturn declareReturn = new EshopDeclareReturn();
            declareReturn.setDeclareStatus(-1);
            declareReturn.setReturnCode("ERROR");
            declareReturn.setReturnInfo("报文解析错误");
            cancelReturn.setDeclareReturn(declareReturn);
            xml = XmlUtil.objectToXml(cancelReturn);
            log.info("取消申报响应=====================" + xml);
            return xml;
        }


    }
    /**
     * 根据分拣线和关区推送
     *
     * @param customCode
     *              关区代码
     * @param sortlineId
     *              分拣线
     * @param xml
     *          报文
     * @return Result
     */
    public Map<String, String> postCustom(String customCode, String sortlineId, String xml){
        return HttpUtil.postCustom(xml);
    }
}
