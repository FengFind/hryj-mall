package com.hryj.controller;

import cn.hutool.core.codec.Base64;
import com.hryj.common.Result;
import com.hryj.exception.ServerException;
import com.hryj.service.OrderDeclareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 白飞
 * @className: CebNotifyController
 * @description: http://192.168.50.137:10080/order/declare/geteway
 * @create 2018/9/26 13:10
 **/
@RestController
@RequestMapping("/order/declare")
public class OrderDeclareController {

    @Autowired
    private OrderDeclareService orderDeclareService;

    /**
     * 接收订单申报接口
     *
     * @param data
     *          报文
     * @return 结果
     * @throws ServerException
     */
    @PostMapping("/geteway")
    public Result geteway(@RequestParam("data") String data) throws ServerException {
        return orderDeclareService.declare(data);
    }


    /**
     * 支付单申报
     *
     * @param orderNo
     *          订单编号
     * @param ebcCode
     *          企业备案10位编码
     * @return 结果
     * @throws ServerException
     */
    @PostMapping("/pay")
    public Result getewayPay(@RequestParam("orderNo") String orderNo, @RequestParam("ebcCode") String ebcCode) throws ServerException {
        return orderDeclareService.declarePayCustom(orderNo, ebcCode, ebcCode);
    }


    /**
     * 清单申报
     *
     * @param orderNo
     *          订单编号
     * @param ebcCode
     *          企业备案10位编码
     * @return 结果
     * @throws ServerException
     */
    @PostMapping("/invt")
    public Result getewayInvt(@RequestParam("orderNo") String orderNo, @RequestParam("ebcCode") String ebcCode) throws ServerException {
        return orderDeclareService.declareInvtCustom(orderNo, ebcCode);
    }


    /**
     * 订单申报
     *
     * @param orderNo
     *          订单编号
     * @param ebcCode
     *          企业备案10位编码
     * @return 结果
     * @throws ServerException
     */
    @PostMapping("/order")
    public Result getewayOrder(@RequestParam("orderNo") String orderNo, @RequestParam("ebcCode") String ebcCode) throws ServerException {
        return orderDeclareService.declareOrderCustom(orderNo, ebcCode);
    }


    /**
     * 运单申报
     *
     * @param orderNo
     *          订单编号
     * @param ebcCode
     *          企业备案10位编码
     * @return 结果
     * @throws ServerException
     */
    @PostMapping("/waybill")
    public Result getewayWayboll(@RequestParam("orderNo") String orderNo, @RequestParam("ebcCode") String ebcCode) throws ServerException {
        return orderDeclareService.declareWaybillCustom(orderNo, ebcCode,ebcCode);
    }


    /**
     * 取消申报
     *
     * @param data
     *          数据格式Base64的XML
     * @return 结果
     * @throws ServerException
     */
    @PostMapping("/cancel")
    public String cancel(@RequestParam("data") String data) throws ServerException {
        return orderDeclareService.cancel(Base64.decodeStr(data));
    }


}
