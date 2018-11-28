package com.hryj.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hryj.cache.CacheGroup;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.cache.RedisLock;
import com.hryj.common.*;
import com.hryj.constant.CommonConstantPool;
import com.hryj.constant.OrderConstants;
import com.hryj.entity.bo.cart.ShoppingCartRecord;
import com.hryj.entity.bo.order.*;
import com.hryj.entity.bo.payment.PaymentGroup;
import com.hryj.entity.bo.payment.PaymentGroupOrder;
import com.hryj.entity.bo.user.UserAddress;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.cart.request.CartOperationRequestVO;
import com.hryj.entity.vo.cart.request.ProductBuyRequestVO;
import com.hryj.entity.vo.cart.request.ProductItemRequestVO;
import com.hryj.entity.vo.cart.request.ShoppingCartRequestVO;
import com.hryj.entity.vo.inventory.request.ProductInventoryLockItem;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRequestVO;
import com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO;
import com.hryj.entity.vo.order.*;
import com.hryj.entity.vo.order.crossOrder.CrossBorderOrderVo;
import com.hryj.entity.vo.order.request.OrderCreateRequestVO;
import com.hryj.entity.vo.order.request.OrderCreateStoreWarehouseVO;
import com.hryj.entity.vo.order.request.OrderIdVO;
import com.hryj.entity.vo.order.request.ReturnOrderRequestVO;
import com.hryj.entity.vo.order.response.*;
import com.hryj.entity.vo.order.response.opentime.OpenTimeResponse;
import com.hryj.entity.vo.order.response.opentime.TimeQuantum;
import com.hryj.entity.vo.payment.PaymentGroupResponseVO;
import com.hryj.entity.vo.product.common.request.PartyProductInventoryAdjustItem;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.response.ProductValidateResponseItem;
import com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO;
import com.hryj.entity.vo.promotion.activity.request.OnlyActivityIdRequestVO;
import com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO;
import com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdsRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.sys.response.CodeInfoVO;
import com.hryj.entity.vo.user.UserAddressVO;
import com.hryj.entity.vo.user.UserIdentityCardVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.exception.BizException;
import com.hryj.feign.ActivityFeignClient;
import com.hryj.feign.ProductFeignClient;
import com.hryj.feign.StoreFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.*;
import com.hryj.scheduled.OrderScheduled;
import com.hryj.sms.AliYunSms;
import com.hryj.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @author 叶方宇
 * @className: OrderService
 * @description:
 * @create 2018/7/5 0005 19:43
 **/
@Slf4j
@Service
public class OrderService extends ServiceImpl<OrderInfoMapper, OrderInfo> {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private OrderReturnMapper orderReturnMapper;

    @Autowired
    private ReturnProductMapper returnProductMapper;

    @Autowired
    private OrderStatusRecordMapper orderStatusRecordMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private PaymentGroupMapper paymentGroupMapper;

    @Autowired
    private PaymentGroupOrderMapper paymentGroupOrderMapper;

    @Autowired
    private OrderSelfPickMapper orderSelfPickMapper;

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;

    @Autowired
    private DistributionProductService distributionProductService;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderPartyProductMapper orderPartyProductMapper;

    @Autowired
    private StoreFeignClient storeFeignClient;

    @Autowired
    private OrderSelfPickService orderSelfPickService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderForUserAppService orderForUserAppService;

    @Autowired
    private RedisLock redisLock;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderScheduled orderScheduled;

    @Autowired
    private  OrderCrossBorderService orderCrossBorderService;

    @Autowired
    private PaymentGroupService paymentGroupService;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.ReturnReasonResponseVO>
     * @author 罗秋涵
     * @description: 获取退货原因列表
     * @param: []
     * @create 2018-07-07 9:24
     **/
    public Result<ReturnReasonResponseVO> getReturnReason() {
        ReturnReasonResponseVO response = new ReturnReasonResponseVO();
        List<CodeInfoVO> return_reason_list = CodeCache.getCodeList("ReturnReason");
        response.setReturn_reason_list(return_reason_list);
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 待支付订单取消、仓库订单，未发货取消。
     * @param: [orderIdVO]
     * @create 2018-07-07 10:49
     **/
    @Transactional
    public Result cancelOrder(OrderIdVO orderIdVO) throws BizException {
        if (orderIdVO.getOrder_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        OrderInfoVO orderInfoVO = orderInfoMapper.getOrderInfoVO(orderIdVO.getOrder_id());
        if(orderInfoVO==null){
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应订单记录");
        }
        //订单状态为已取消
        if(CodeCache.getValueByKey("OrderStatus", "S07").equals(orderInfoVO.getOrder_status())){
            return new Result(CodeEnum.FAIL_BUSINESS, "订单已取消,请刷新列表");
        }

        //订单状态为待支付可以取消
        if (CodeCache.getValueByKey("OrderStatus", "S01").equals(orderInfoVO.getOrder_status())) {
            //更改订单状态
            OrderStatusRecordVO orderStatusRecord = new OrderStatusRecordVO();
            orderStatusRecord.setOrder_id(orderInfoVO.getOrder_id());
            orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S07"));
            orderStatusRecord.setStatus_remark("取消订单");
            orderStatusRecord.setChange_reason("取消订单");
            this.updateOrderStatus(orderStatusRecord, orderIdVO.getLogin_token());
            if(OrderConstants.CROSS_BORDER_ORDER_TYPE_LIST.contains(orderInfoVO.getOrder_type())){
                //回填订单状态及第三方订单号
                OrderCrossBorder orderCrossBorder=new OrderCrossBorder();
                orderCrossBorder.setOrder_id(orderInfoVO.getOrder_id());
                //状态为已取消
                orderCrossBorder.setThird_order_status(OrderConstants.ThirdOrderStatus.cancel.ordinal());
                orderCrossBorder.setThird_order_status_desc(OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.cancel.ordinal()]);
                orderCrossBorder.setCancel_count(orderInfoVO.getCancel_count()+1);
                orderCrossBorderService.updateById(orderCrossBorder);
            }

            //v1.2 luoqh add **********************跨境订单取消*************************start
        } else if (OrderConstants.CROSS_BORDER_ORDER_TYPE_LIST.contains(orderInfoVO.getOrder_type())) {
            //if (orderInfoVO.getCancel_count() > 0) {
            //    return new Result(CodeEnum.FAIL_BUSINESS, "订单取消失败:" +orderInfoVO.getCancel_failed_reason());
            //}
            if(orderInfoVO.getThird_order_status()!=OrderConstants.ThirdOrderStatus.waitDeclare.ordinal()){
                //调用光彩接取消订单接口
                Map<String, String> gcReturnMap = orderScheduled.cancelOrderForGC(orderInfoVO.getThird_order_code(),orderInfoVO.getOrder_num());
                log.info("光彩订单取消返回结果：{}",gcReturnMap);
                if (gcReturnMap != null) {
                    if ("F".equals(gcReturnMap.get("success"))) {
                        //回填第三方订单取消次数
                        OrderCrossBorder orderCrossBorder=new OrderCrossBorder();
                        orderCrossBorder.setOrder_id(orderInfoVO.getOrder_id());
                        orderCrossBorder.setCancel_count(orderInfoVO.getCancel_count()+1);
                        orderCrossBorder.setCancel_failed_reason(gcReturnMap.get("message"));
                        orderCrossBorderService.updateById(orderCrossBorder);
                        //发送短信
                        HashMap messageMap = new HashMap();
                        messageMap.put("fail_reason", gcReturnMap.get("message"));
                        messageMap.put("telephone", CodeCache.getValueByKey("CustomerServicePhone","S01"));
                        AliYunSms.sendSingleSms(orderInfoVO.getUser_phone(), "SMS_146801104", messageMap);
                        return new Result(CodeEnum.FAIL_BUSINESS, "订单取消失败:" + gcReturnMap.get("message"));
                    }
                }else{
                    return new Result(CodeEnum.FAIL_BUSINESS, "订单取消失败");
                }
            }
            //发起退款
            Result result = paymentService.refund(orderInfoVO.getOrder_id());
            log.info("跨境订单，取消订单，退款结果：" + result);
            //退款成功
            if (result.isSuccess()) {
                //更改订单状态
                OrderStatusRecordVO orderStatusRecord = new OrderStatusRecordVO();
                orderStatusRecord.setOrder_id(orderInfoVO.getOrder_id());
                orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S07"));
                orderStatusRecord.setStatus_remark("跨境订单取消成功，退款成功");
                orderStatusRecord.setChange_reason("跨境订单取消成功，退款成功");
                updateOrderStatus(orderStatusRecord, orderIdVO.getLogin_token());
                //回填订单状态及第三方订单号
                OrderCrossBorder orderCrossBorder=new OrderCrossBorder();
                orderCrossBorder.setOrder_id(orderInfoVO.getOrder_id());
                //状态为已取消
                orderCrossBorder.setThird_order_status(OrderConstants.ThirdOrderStatus.cancel.ordinal());
                orderCrossBorder.setThird_order_status_desc(OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.cancel.ordinal()]);
                orderCrossBorder.setCancel_count(orderInfoVO.getCancel_count()+1);
                orderCrossBorderService.updateById(orderCrossBorder);
            } else {
                return result;
            }

            //v1.2 luoqh add **********************跨境订单取消************************end
        } else if (CodeCache.getValueByKey("DeptType", "S02").equals(orderInfoVO.getParty_type())
                && CodeCache.getValueByKey("OrderStatus", "S02").equals(orderInfoVO.getOrder_status())) {
            //部门类型为仓库且订单状态为待发货可以取消，需要退款。
            //发起退款
            Result result = paymentService.refund(orderInfoVO.getOrder_id());
            log.info("仓库订单取消，退款结果：" + result);
            //退款成功
            if (result.isSuccess()) {
                //更改订单状态
                OrderStatusRecordVO orderStatusRecord = new OrderStatusRecordVO();
                orderStatusRecord.setOrder_id(orderInfoVO.getOrder_id());
                orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S07"));
                orderStatusRecord.setStatus_remark("取消订单，退款成功");
                orderStatusRecord.setChange_reason("取消订单，退款成功");
                updateOrderStatus(orderStatusRecord, orderIdVO.getLogin_token());
            } else {
                return result;
            }
        } else if (CodeCache.getValueByKey("OrderStatus", "S07").equals(orderInfoVO.getOrder_status())) {
            return new Result(CodeEnum.FAIL_BUSINESS, "订单已取消,请刷新列表");
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "订单不能取消,请刷新列表");
        }
        //释放库存
        List<OrderPorductVO> orderPorductList = orderProductMapper.getOrderPorductVOList(orderInfoVO.getOrder_id());
        ProductsInventoryLockRequestVO productsInventoryLockRequestVO = new ProductsInventoryLockRequestVO();
        //释放，锁定标识 sub减库存， add加库存
        productsInventoryLockRequestVO.setLock_model("add");
        //释放库存参数
        List<ProductInventoryLockItem> lock_items = new ArrayList<>();
        for (OrderPorductVO orderPorductVO : orderPorductList) {
            ProductInventoryLockItem productInventoryLockItem = new ProductInventoryLockItem();
            productInventoryLockItem.setActivity_id(orderPorductVO.getActivity_id());
            productInventoryLockItem.setLock_quantity(orderPorductVO.getQuantity());
            productInventoryLockItem.setProduct_id(orderPorductVO.getProduct_id());
            productInventoryLockItem.setParty_id(orderPorductVO.getParty_id());
            lock_items.add(productInventoryLockItem);
        }
        productsInventoryLockRequestVO.setLock_items(lock_items);
        //发起请求
        Result<ProductsInventoryLockResponseVO> result = productFeignClient.lockProductInventory(productsInventoryLockRequestVO);
        log.info("取消订单释放库存返回结果：{}",JSON.toJSONString(result));
        if (result.isSuccess()) {
            return new Result(CodeEnum.SUCCESS);
        } else {
            throw new BizException("释放库存失败");
        }
    }


    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 申请退货
     * @param: [returnOrderRequestVO]
     * @create 2018-07-07 15:23
     **/
    @Transactional
    public Result returnOrder(ReturnOrderRequestVO returnOrderRequestVO) {
        Long loginUserId = null;
        String loginUserName = null;
        //获取订单信息
        OrderInfo orderInfo = orderInfoMapper.selectById(returnOrderRequestVO.getOrder_id());
        log.info("申请退货，订单信息："+orderInfo);
        if (orderInfo == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "订单不存在");
        }
        if (orderInfo.getOrder_status().equals(CodeCache.getValueByKey("OrderStatus", "S01"))
                || orderInfo.getOrder_status().equals(CodeCache.getValueByKey("OrderStatus", "S05"))
                || orderInfo.getOrder_status().equals(CodeCache.getValueByKey("OrderStatus", "S06"))
                || orderInfo.getOrder_status().equals(CodeCache.getValueByKey("OrderStatus", "S07"))
                || orderInfo.getOrder_status().equals(CodeCache.getValueByKey("OrderStatus", "S08"))) {

            return new Result(CodeEnum.FAIL_BUSINESS, "该订单不能申请退货");
        }
        //判断订单有没有发起过退货申请
        ReturnOrderDetailsResponseVO returnRecord=orderReturnMapper.getReturnDetails(returnOrderRequestVO.getOrder_id());
        if(returnRecord!=null){
            return new Result(CodeEnum.FAIL_BUSINESS, "无法再次申请退货");
        }
        //获取订单对应商品信息
        List<OrderPorductVO> orderPorductLis = orderProductMapper.getOrderPorductVOList(orderInfo.getId());
        //创建订单退货信息
        OrderReturn orderReturn = new OrderReturn();
        orderReturn.setOrder_id(orderInfo.getId());
        //获取用户信息
        UserLoginVO userInfo = LoginCache.getUserLoginVO(returnOrderRequestVO.getLogin_token());
        //获取用户信息
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(returnOrderRequestVO.getLogin_token());
        if(userInfo!=null){
            loginUserId = userInfo.getUser_id();
            //客户自主退货
            orderReturn.setReturn_type(CodeCache.getValueByKey("ReturnType", "S01"));
        }else if(staffAppLoginVO!=null){
            loginUserId = staffAppLoginVO.getStaff_id();
            loginUserName = staffAppLoginVO.getStaff_name();
            //店员代退货
            orderReturn.setReturn_type(CodeCache.getValueByKey("ReturnType", "S02"));
        }

        //申请中
        orderReturn.setReturn_status(CodeCache.getValueByKey("ReturnStatus", "S01"));
        orderReturn.setReturn_apply_id(loginUserId);
        orderReturn.setReturn_apply_name(loginUserName);
        orderReturn.setReturn_apply_time(new Date());
        orderReturn.setReturn_reason(returnOrderRequestVO.getReturn_reason());
        if(returnOrderRequestVO.getReturn_image()!=null&&returnOrderRequestVO.getReturn_image().endsWith(",")){
            returnOrderRequestVO.setReturn_image(returnOrderRequestVO.getReturn_image().substring(0,returnOrderRequestVO.getReturn_image().length()-1));
        }
        orderReturn.setReturn_image(returnOrderRequestVO.getReturn_image());
        orderReturn.setUser_id(orderInfo.getUser_id());
        orderReturn.setUser_name(orderInfo.getUser_name());
        orderReturn.setUser_phone(orderInfo.getUser_phone());
        orderReturn.setUser_address(orderInfo.getUser_address());
        if(staffAppLoginVO!=null){
            orderReturn.setReturn_remark("店员：" + staffAppLoginVO.getStaff_name() + "发起退货");
        }else{
            orderReturn.setReturn_remark("用户：" + orderInfo.getUser_name() + "发起退货");
        }

        //目前为全额退
        orderReturn.setReturn_total(orderInfo.getPay_amt());
        //目前调整退货金额标识都为0，1-是,0-否
        orderReturn.setAdjust_return_amt_flag(0);
        orderReturn.setCreate_time(new Date());
        orderReturn.setUpdate_time(new Date());
        orderReturnMapper.insert(orderReturn);
        //循环创建退货单商品信息
        for (OrderPorductVO orderInfoVO : orderPorductLis) {
            ReturnProduct returnProduct = new ReturnProduct();
            returnProduct.setReturn_id(orderReturn.getId());
            returnProduct.setOrder_id(orderInfo.getId());
            returnProduct.setOrder_product_id(orderInfoVO.getOrder_product_id());
            returnProduct.setReturn_quantity(orderInfoVO.getQuantity());
            if (orderInfoVO.getActivity_price() == null || "".equals(orderInfoVO.getActivity_price())) {
                returnProduct.setReturn_price(new BigDecimal(orderInfoVO.getActual_price()));
            } else {
                returnProduct.setReturn_price(new BigDecimal(orderInfoVO.getActivity_price()));
            }
            returnProduct.setReturn_amt(NumberUtil.mul(new BigDecimal(orderInfoVO.getActual_price()),new BigDecimal(orderInfoVO.getQuantity())));
            returnProduct.setCreate_time(new Date());
            returnProduct.setUpdate_time(new Date());
            //保持退货单商品明细
            returnProductMapper.insert(returnProduct);
        }
        //更改订单状态
        OrderStatusRecordVO orderStatusRecord = new OrderStatusRecordVO();
        orderStatusRecord.setOrder_id(orderInfo.getId());
        orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S05"));
        orderStatusRecord.setStatus_remark("申请退货成功");
        orderStatusRecord.setChange_reason("申请退货成功");
        this.updateOrderStatus(orderStatusRecord,returnOrderRequestVO.getLogin_token());
        //更改配货单状态
        DistributionInfoVO distributionInfoVO = orderDistributionMapper.selectDistributionInfoByOrderId(orderInfo.getId());
        if (distributionInfoVO != null) {
            //配送单状态为待分配待取货时
            if (CodeCache.getValueByKey("DistributionStatus", "S01").equals(distributionInfoVO.getDistribution_status())
                ||CodeCache.getValueByKey("DistributionStatus", "S02").equals(distributionInfoVO.getDistribution_status())) {
                OrderDistribution orderDistribution=new OrderDistribution();
                orderDistribution.setId(distributionInfoVO.getDistribution_id());
                orderDistribution.setDistribution_status(CodeCache.getValueByKey("DistributionStatus", "S05"));
                orderDistributionMapper.updateById(orderDistribution);
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 立即购买，生成订单
     * @param:
     * @create 2018-07-07 10:22
     **/
    /*public Result createOrderInfoNow(OrderSaveNowRequestVO orderSaveNowRequestVO) {

        OrderCreateFromCartVO orderCreateFromCartVO = orderInfoMapper.orderCreateNow(orderSaveNowRequestVO);
        //商品验证
        String follow_value = String.valueOf(System.nanoTime());
        Result<ProductsValidateResponseVO> validateResponseResult = ProductValidateUtil.productsValidate(orderSaveNowRequestVO.getParty_id(),
                orderSaveNowRequestVO.getProduct_id(),
                orderSaveNowRequestVO.getActivity_id(),
                orderSaveNowRequestVO.getQuantity(),
                follow_value,
                productFeignClient);
        if (validateResponseResult.isFailed()) {
            return new Result(CodeEnum.FAIL_BUSINESS, "商品验证失败");
        }
        ProductsValidateResponseVO responseVO = validateResponseResult.getData();
        if (!responseVO.isValidatePassed()) {
            return new Result(CodeEnum.FAIL_BUSINESS, responseVO.getItemByFlowValue(follow_value).getOther_comments());
        }

        OrderInfo orderInfo = new OrderInfo();
        //库存下架禁售等验证
        UserLoginVO userLoginVO = null;
        StaffAppLoginVO staffAppLoginVO = null;
        //代购判断
        if (orderSaveNowRequestVO.getUser_id() == null) {
            //表示非代购，通过缓存取得用户信息
            userLoginVO = LoginCache.getUserLoginVO(orderSaveNowRequestVO.getLogin_token());
        } else {//否则为代购，需要调用接口查询用户信息
            staffAppLoginVO = LoginCache.getStaffAppLoginVO(orderSaveNowRequestVO.getLogin_token());
            userLoginVO = new UserLoginVO();
            UserInfoVO infoVO = userFeignClient.findUserInfoVOByUserId(orderSaveNowRequestVO.getUser_id()
                    , null).getData();
            userLoginVO.setReceive_address(infoVO.getReceive_address());
            userLoginVO.setReceive_phone(infoVO.getPhone_num());
            userLoginVO.setReceive_name(infoVO.getUser_name());
        }

        //活动信息
        OrderActivityInfoVO activityInfoVO = null;
        if (orderSaveNowRequestVO.getActivity_id() != null&&orderSaveNowRequestVO.getActivity_id()!=0) {
            activityInfoVO = getActivityMessage(orderSaveNowRequestVO.getActivity_id()
                    , orderSaveNowRequestVO.getParty_id(), orderSaveNowRequestVO.getProduct_id());
        }
        //订单信息
        orderInfo.setDelivery_type(orderSaveNowRequestVO.getDelivery_type());
        orderInfo.setApp_key(orderSaveNowRequestVO.getApp_key());
        orderInfo.setParty_name(orderCreateFromCartVO.getParty_name());
        orderInfo.setHope_delivery_start_time(orderSaveNowRequestVO.getHope_delivery_start_time());
        orderInfo.setHope_delivery_end_time(orderSaveNowRequestVO.getHope_delivery_end_time());
        orderInfo.setOrder_date(new Date());
        orderInfo.setOrder_num(CommonUtil.getDateRandom());
        orderInfo.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
        orderInfo.setParty_id(orderSaveNowRequestVO.getParty_id());
        orderInfo.setPay_amt(activityInfoVO == null
                ? orderCreateFromCartVO.getSale_price() : activityInfoVO.getActivity_price());
        orderInfo.setTotal_cost(orderCreateFromCartVO.getCost_price());
        orderInfo.setUser_address(userLoginVO.getReceive_address());
        orderInfo.setUser_id(userLoginVO.getUser_id());
        orderInfo.setUser_name(userLoginVO.getReceive_name());
        orderInfo.setUser_phone(userLoginVO.getReceive_phone());
        orderInfo.setHelp_staff_id(orderSaveNowRequestVO.getUser_id() == null
                ? staffAppLoginVO.getStaff_id() : orderSaveNowRequestVO.getUser_id());
        //总成本价格
        BigDecimal cost_price = new BigDecimal(0.0);
        //总优惠金额
        BigDecimal discount_amt = new BigDecimal(0.0);
        //订单总金额
        BigDecimal order_amt = new BigDecimal(0.0);
        //实付金额
        BigDecimal pay_amt = new BigDecimal(0.0);
        //毛利
        BigDecimal order_profit = new BigDecimal(0.0);

        //成本价格
        cost_price = orderCreateFromCartVO.getCost_price().multiply(new BigDecimal(orderSaveNowRequestVO.getQuantity()));
        orderInfo.setTotal_cost(cost_price);

        //售价
        order_amt = orderCreateFromCartVO.getSale_price().multiply(new BigDecimal(orderSaveNowRequestVO.getQuantity()));
        orderInfo.setOrder_amt(order_amt);

        //优惠价格,当有活动的时候才计算
        if (orderSaveNowRequestVO.getActivity_id() != null) {
            discount_amt = activityInfoVO.getActivity_price().multiply(new BigDecimal(orderSaveNowRequestVO.getQuantity()));
            discount_amt = order_amt.subtract(discount_amt);
            orderInfo.setDiscount_amt(discount_amt);
        }

        //计算实付金额，当有活动的时候去掉优惠金额，否则为原价
        if (orderInfo.getDiscount_amt() == null) {
            pay_amt = order_amt;
        } else {
            pay_amt = order_amt.subtract(discount_amt);
        }
        orderInfo.setPay_amt(pay_amt);

        //实付金额减去成本，计算毛利
        order_profit = pay_amt.subtract(cost_price);
        orderInfo.setOrder_profit(order_profit);
        //保存
        orderInfoMapper.insert(orderInfo);

        //订单状态信息
        OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
        orderStatusRecord.setOperator_id(userLoginVO.getUser_id());
        orderStatusRecord.setOperator_name(userLoginVO.getReceive_name());
        //等字典表
        orderStatusRecord.setChange_reason(null);
        orderStatusRecord.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
        orderStatusRecord.setOperator_user_type(orderSaveNowRequestVO.getUser_id() == null
                ? SysCodeEnmu.CHANGEUSERTYPE_01.getCodeValue() : SysCodeEnmu.CHANGEUSERTYPE_02.getCodeValue());
        orderStatusRecord.setOrder_id(orderInfo.getId());
        //保存商品信息
        orderProductMapper.insert(createOrderProduct(orderCreateFromCartVO, orderInfo, orderSaveNowRequestVO.getActivity_id()));
        //锁定库存
        partyProductInventoryAdjust(orderSaveNowRequestVO.getActivity_id(), orderSaveNowRequestVO.getProduct_id(),
                -orderSaveNowRequestVO.getQuantity());
        return new Result(CodeEnum.SUCCESS, encapsulationOrderToPayResponseVO(orderInfo.getOrder_num()
                , orderInfo.getPay_amt().toString()));
    }
*/
    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 生成订单后返回值封装
     * @param:
     * @create 2018-07-07 16:01
     **/
    public OrderToPayResponseVO encapsulationOrderToPayResponseVO(String order_num, String pay_amt) {
        OrderToPayResponseVO orderToPayResponseVO = new OrderToPayResponseVO();
        orderToPayResponseVO.setOrderNumStr(order_num);
        orderToPayResponseVO.setPay_amt(pay_amt);
        List<String> payMethodList = new ArrayList<>();
        List<CodeInfoVO> codeInfoVOS = CodeCache.getCodeList(SysCodeTypeEnum.PAYMENTMETHOD.getType());
        for (CodeInfoVO v : codeInfoVOS) {
            payMethodList.add(v.getCode_value());
        }
        orderToPayResponseVO.setPayMethodList(payMethodList);
        return orderToPayResponseVO;
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 生成订单商品信息，保存
     * @param:
     * @create 2018-07-07 16:05
     **/
    public OrderProduct createOrderProduct(OrderCreateFromCartVO orderCreateFromCartVO, OrderInfo orderInfo, Long activityId) {
        //订单商品信息
        OrderProduct product = new OrderProduct();
        product.setActivity_id(activityId);
        product.setActual_price(orderInfo.getPay_amt());
        product.setCost_price(orderCreateFromCartVO.getCost_price());
        product.setOrg_price(orderCreateFromCartVO.getSale_price());
        product.setParty_id(orderCreateFromCartVO.getParty_id());
        product.setPrice_modify_flag(0);
        product.setProduct_category_id(orderCreateFromCartVO.getProd_cate_id());
        product.setList_image_url(orderCreateFromCartVO.getList_image_url());
        product.setProduct_id(orderCreateFromCartVO.getProduct_id());
        product.setProduct_name(orderCreateFromCartVO.getProduct_name());
        product.setQuantity(orderCreateFromCartVO.getQuantity());
        product.setOrder_id(orderInfo.getId());
        return product;
    }

    /**
     * @return java.lang.Long
     * @author 罗秋涵
     * @description: 获取登录员工Id
     * @param: [token]
     * @create 2018-07-07 16:25
     **/
    public Long getStaffAppLoginUserId(String token) {
        //获取员工信息
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(token);
        Long loginUserId = staffAppLoginVO.getStaff_id();

        return loginUserId;
    }

    /**
     * @return com.hryj.entity.vo.staff.user.StaffAppLoginVO
     * @author 罗秋涵
     * @description: 根据token 获取登录员工信息
     * @param: [token]
     * @create 2018-07-09 14:45
     **/
    public StaffAppLoginVO getStaffAppLoginUser(String token) {

        //获取员工信息
        return LoginCache.getStaffAppLoginVO(token);
    }

    /**
     * @return java.lang.Long
     * @author 罗秋涵
     * @description: 获取登录员工Id
     * @param: [token]
     * @create 2018-07-07 16:25
     **/
    public Long getUserLoginUserId(String token) {

        //获取用户信息
        UserLoginVO userLogin = LoginCache.getUserLoginVO(token);
        Long loginUserId = userLogin.getUser_id();
        return loginUserId;
    }

    /**
     * @return com.hryj.entity.vo.user.UserLoginVO
     * @author 罗秋涵
     * @description: 根据token 获取用户信息
     * @param: [token]
     * @create 2018-07-09 14:44
     **/
    public UserLoginVO getUserLoginUser(String token) {
        //获取用户信息
        return LoginCache.getUserLoginVO(token);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderSettlementResponseVO>
     * @author 罗秋涵
     * @description: 立即购买——确认订单
     * @param: [shoppingCartRequestVO]
     * @create 2018-07-05 19:42
     **/
    public Result<OrderSettlementResponseVO> immediateBuy(ShoppingCartRequestVO shoppingCartRequestVO) {
        if(shoppingCartRequestVO==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"请求参数不能为空");
        }
        if(shoppingCartRequestVO.getQuantity()==null||shoppingCartRequestVO.getQuantity()<=0){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品数量必须大于0");
        }
        //先添加到购物车
        Result result = shoppingCartService.addShoppingCart(shoppingCartRequestVO, CodeCache.getValueByKey("ShoppingCartType", "S04"));
        //判断返回结果
        if (result.getCode() == CodeEnum.SUCCESS.getCode()) {
            //查询记录返回订单确认页面
            CartOperationRequestVO cartoPerationRequestVO = new CartOperationRequestVO();
            //参数赋值
            cartoPerationRequestVO.setUser_id(shoppingCartRequestVO.getUser_id());
            cartoPerationRequestVO.setCartItemIds(result.getData().toString());
            cartoPerationRequestVO.setLogin_token(shoppingCartRequestVO.getLogin_token());
            cartoPerationRequestVO.setPay_price(shoppingCartRequestVO.getPay_price());
            Result<OrderSettlementResponseVO> settlementOrder = orderForUserAppService.settlementOrder(cartoPerationRequestVO);
            return settlementOrder;
        } else {

            return result;
        }
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderConfirmInfoResponseVO>
     * @author 白飞
     * @description: 确认订单
     * @param: [cartRequestVO]
     * @create 2018-08-16 17:16
     **/
    public Result<OrderConfirmInfoResponseVO> confirmInfo(ProductBuyRequestVO productBuyRequestVO) {

        if(productBuyRequestVO == null || productBuyRequestVO.getProduct_items() == null || productBuyRequestVO.getProduct_items().size() == 0){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"请求参数不能为空");
        }
        if(productBuyRequestVO.getType() == null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"请求类型来源参数不能为空");
        }
        if(productBuyRequestVO.getType() < 0 || productBuyRequestVO.getType() >= ProductBuyRequestVO.BuyType.values().length){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"请求类型来源参数异常");
        }
        if(productBuyRequestVO.getTotal_amount() == null || NumberUtil.isLessOrEqual(productBuyRequestVO.getTotal_amount(),BigDecimal.ZERO)){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品总金额不能为空");
        }
        List<ProductItemRequestVO> product_items = productBuyRequestVO.getProduct_items();
        List<String> cartIds  = Lists.newArrayList();
        //当前门店或仓库ID
        List<Long> currentStoreIds = Lists.newArrayList();
        //购物车记录存储
        Map<Long, ProductItemRequestVO> productItemRequestMap = Maps.newHashMap();
        for(ProductItemRequestVO productItemRequestVO : product_items){
            if(productItemRequestVO == null){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"参数不能为空");
            }
            if(productItemRequestVO.getParty_id() == null || productItemRequestVO.getParty_id() <= 0L){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"门店或仓库不存在");
            }
            if(productItemRequestVO.getQuantity() == null || productItemRequestVO.getQuantity() <= 0){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"商品数量必须大于0");
            }
            if(productItemRequestVO.getProduct_id() == null || productItemRequestVO.getProduct_id() <= 0L){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"该商品不存在");
            }
            //如果从购物车请求的，必须带上购物车ID，否则提示错误
            if(productBuyRequestVO.getType() == ProductBuyRequestVO.BuyType.cart.ordinal()){
                if(productItemRequestVO.getCart_id() == null || productItemRequestVO.getCart_id() <= 0L){
                    return new Result(CodeEnum.FAIL_PARAMCHECK,"购物车ID不存在");
                }
                cartIds.add(productItemRequestVO.getCart_id() + "");
                productItemRequestMap.put(productItemRequestVO.getCart_id(), productItemRequestVO);
            }
            currentStoreIds.add(productItemRequestVO.getParty_id());
        }
        //用户ID
        Long user_id = null;
        //当前用户包含的门店范围
        List<Long> storeRange = null;
        //判断是否代下单用户
        if(productBuyRequestVO.getUser_id() != null && productBuyRequestVO.getUser_id() > 0L){
            StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(productBuyRequestVO.getLogin_token());
            if(null == staffAppLoginVO){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"请先登录，再操作");
            }
            user_id = productBuyRequestVO.getUser_id();
            storeRange = this.shoppingCartService.getUserServiceStoreList(null, user_id);
        }else{
            UserLoginVO userLoginVO= LoginCache.getUserLoginVO(productBuyRequestVO.getLogin_token());
            if(null == userLoginVO){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"请先登录，再购买");
            }
            user_id = userLoginVO.getUser_id();
            storeRange = this.shoppingCartService.getUserServiceStoreList(productBuyRequestVO.getLogin_token(), null);
        }
        if(null == storeRange || storeRange.size() == 0){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"对不起，不在服务区内");
        }
        //当订单来源不是分享商品的订单进行服务区域 判断
        if(productBuyRequestVO.getShare_user_id() == null || productBuyRequestVO.getShare_user_id() <= 0L){
            for(Long partyId : currentStoreIds){
                if(!ArrayUtils.contains(storeRange.toArray(), partyId)){
                    return new Result(CodeEnum.FAIL_PARAMCHECK,"对不起，不在服务区内");
                }
            }
        }
        Result<UserInfoVO> result = userFeignClient.findUserInfoVOByUserId(user_id, null);
        if(null == result || result.isFailed() || result.getData() == null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"用户信息不存在");
        }
        //获取用户信息
        UserInfoVO userInfo = result.getData();
        //获取用户地址列表
        List<UserAddressVO> userAddresses = Lists.newArrayList();
        Result<ListResponseVO<UserAddressVO>> resultUserAddress = this.userFeignClient.findUserAddressListByUserId(user_id);
        if(null != resultUserAddress && resultUserAddress.isSuccess() && resultUserAddress.getData() != null && resultUserAddress.getData().getRecords() != null){
            userAddresses = resultUserAddress.getData().getRecords();
        }
        //购物商品集合
        List<OrderProductItemVO> orderProductItems = Lists.newArrayList();
        //立即购买与拼团进入-非购物车查询
        if(ProductBuyRequestVO.BuyType.cart.ordinal() != productBuyRequestVO.getType()){
            ProductItemRequestVO productItemRequestVO = product_items.get(0);
            if(productItemRequestVO.getShop_price() == null || NumberUtil.isLessOrEqual(productItemRequestVO.getShop_price(),BigDecimal.ZERO)){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"商品价格不能为空");
            }
            OrderProductItemVO orderProductItem = new OrderProductItemVO();
            //立即购买时，购物车ID设置为0
            orderProductItem.setCart_id(0L);
            orderProductItem.setParty_id(productItemRequestVO.getParty_id());
            orderProductItem.setProduct_id(productItemRequestVO.getProduct_id());
            orderProductItem.setActivity_id(productItemRequestVO.getActivity_id());
            orderProductItem.setQuantity(productItemRequestVO.getQuantity());
            orderProductItem.setShop_price(productItemRequestVO.getShop_price());
            orderProductItems.add(orderProductItem);
            return build(orderProductItems, productBuyRequestVO.getUser_id(), productBuyRequestVO.getShare_user_id(), productBuyRequestVO.getShare_source(), userInfo, userAddresses, productBuyRequestVO.getTotal_amount());
        }
        //根据当前用户查询购物车
        List<ShoppingCartRecord> cartItems = this.shoppingCartService.findShoppingCartRecordList(cartIds);
        if(cartItems == null || cartItems.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"购物车不能为空");
        }
        //验证购物车条数
        if(cartItems.size() != productBuyRequestVO.getProduct_items().size()){
            return new Result(CodeEnum.FAIL_BUSINESS,"购物车商品条数不一致");
        }
        for (ShoppingCartRecord cartRecord : cartItems){
            ProductItemRequestVO productItemRequestVO = productItemRequestMap.get(cartRecord.getId());
            if(!productItemRequestVO.getQuantity().equals(cartRecord.getQuantity())){
                return new Result(CodeEnum.FAIL_BUSINESS,"购物车商品数量不一致");
            }
            if(!productItemRequestVO.getParty_id().equals(cartRecord.getParty_id())){
                return new Result(CodeEnum.FAIL_BUSINESS,"购物车门店或仓库不一致");
            }
            if(!productItemRequestVO.getProduct_id().equals(cartRecord.getProduct_id())){
                return new Result(CodeEnum.FAIL_BUSINESS,"购物车商品不一致");
            }
            OrderProductItemVO orderProductItem = new OrderProductItemVO();
            orderProductItem.setCart_id(cartRecord.getId());
            orderProductItem.setParty_id(productItemRequestVO.getParty_id());
            orderProductItem.setProduct_id(productItemRequestVO.getProduct_id());
            orderProductItem.setActivity_id(productItemRequestVO.getActivity_id());
            orderProductItem.setQuantity(productItemRequestVO.getQuantity());
            orderProductItem.setShop_price(productItemRequestVO.getShop_price());
            orderProductItems.add(orderProductItem);
        }
        return build(orderProductItems, productBuyRequestVO.getUser_id(), productBuyRequestVO.getShare_user_id(), productBuyRequestVO.getShare_source(), userInfo, userAddresses, productBuyRequestVO.getTotal_amount());
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderConfirmInfoResponseVO>
     * @author 白飞
     * @description:  构建订单
     * @param: [cartItems,user_id,share_user_id,share_source,userInfo,userAddresses,totalAmount]
     * @create 2018-08-16 17:16
     **/
     private Result build(List<OrderProductItemVO> orderProductItems, Long user_id, Long share_user_id, String share_source, UserInfoVO userInfo, List<UserAddressVO> userAddresses, BigDecimal totalAmount){
        OrderConfirmInfoResponseVO orderConfirmInfoResponseVO = new OrderConfirmInfoResponseVO();
         //根据门店分组商品信息
         Map<Long, List<OrderProductItemVO>> partyGroupMap = Maps.newHashMap();
         //数量Map存入购物车记录对应商品数量，用户验证总金额
         Map<Long,Integer> quantityMap = Maps.newHashMap();
         Set<Long> storeIds = Sets.newHashSet();
         for(OrderProductItemVO orderProductItem : orderProductItems){
             if (partyGroupMap.get(orderProductItem.getParty_id()) == null) {
                 List<OrderProductItemVO> recordList = Lists.newArrayList();
                 recordList.add(orderProductItem);
                 partyGroupMap.put(orderProductItem.getParty_id(), recordList);
             } else {
                 partyGroupMap.get(orderProductItem.getParty_id()).add(orderProductItem);
             }
             storeIds.add(orderProductItem.getParty_id());
             quantityMap.put(orderProductItem.getCart_id(), orderProductItem.getQuantity());
         }

         //门店或仓库校验
         Callable<List<DeptIdByStoreOrWarehouseResponseVO>> callableStoreWarehouse = () -> {
             DeptIdsRequestVO deptIdsRequest = new  DeptIdsRequestVO();
             deptIdsRequest.setDept_ids(new ArrayList<>(storeIds));
             Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> storeResult = storeFeignClient.getAppDeptIdsByStoreOrWarehouseDet(deptIdsRequest);
             if(storeResult == null || storeResult.getCode() != CodeEnum.SUCCESS.getCode() || storeResult.getData() == null || storeResult.getData().getRecords() == null){
                 return null;
             }
             return storeResult.getData().getRecords();
         };

         //获取默认订购人
         Callable<UserIdentityCardVO> callableUserIdentityCard = () -> {
           Result<UserIdentityCardVO> result = userFeignClient.getUserDefaultIdentityCard(user_id);
           if(result != null && result.getCode() == CodeEnum.SUCCESS.getCode() && result.getData() != null){
               return result.getData();
           }
           return null;
         };

         //商品校验
         Callable<List<ProductValidateResponseItem>> callableProducts = () -> {
             ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
             List<ProductValidateItem> productValidateItems = Lists.newArrayList();
             //循环组装校验请求数据
             for (OrderProductItemVO orderProductItem : orderProductItems) {
                 ProductValidateItem productValidate = new ProductValidateItem();
                 productValidate.setParty_id(orderProductItem.getParty_id());
                 productValidate.setProduct_id(orderProductItem.getProduct_id());
                 productValidate.setActivity_id(orderProductItem.getActivity_id());
                 productValidate.setRequired_min_inventory_quantity(orderProductItem.getQuantity());
                 productValidate.setFollow_value(orderProductItem.getCart_id() + "");
                 productValidateItems.add(productValidate);
             }
             productsValidateRequestVO.setProd_summary_list(productValidateItems);
             //发起校验
             Result<ProductsValidateResponseVO> productsValidateResult = productFeignClient.productsValidate(productsValidateRequestVO);
             if(productsValidateResult == null || productsValidateResult.getCode() != CodeEnum.SUCCESS.getCode() || productsValidateResult.getData() == null || productsValidateResult.getData().getProd_validate_result_list() == null){
                 return null;
             }
             //获得校验结果
             return productsValidateResult.getData().getProd_validate_result_list();
         };

         UserIdentityCardVO userIdentityCard = null;
         //门店或仓库信息
         Map<Long, DeptIdByStoreOrWarehouseResponseVO> storeInfoMap = Maps.newHashMap();
         //临时存储验证商品信息
         Map<Long, ProductValidateResponseItem> productValidateResponseItemMap = Maps.newHashMap();
         FutureTask<List<DeptIdByStoreOrWarehouseResponseVO>> futureTaskStore = new FutureTask<>(callableStoreWarehouse);
         FutureTask<List<ProductValidateResponseItem>> futureTaskProducts = new FutureTask<>(callableProducts);
         FutureTask<UserIdentityCardVO> futureTaskUserIdentityCard = new FutureTask<>(callableUserIdentityCard);
         ThreadUtil.execute(new Thread(futureTaskStore));
         ThreadUtil.execute(new Thread(futureTaskProducts));
         ThreadUtil.execute(new Thread(futureTaskUserIdentityCard));
         try {
             //门店信息集合
             List<DeptIdByStoreOrWarehouseResponseVO> storeList = futureTaskStore.get();
             //商品信息
             List<ProductValidateResponseItem> productValidateResponseItems = futureTaskProducts.get();
             //仓库校验
             Result result = this.storeBathValidate(storeIds, storeInfoMap, storeList);
             if(result.getCode() != CodeEnum.SUCCESS.getCode()){
                 return result;
             }
             //商品校验结果
             Result resultProduct = this.productBathValidate(quantityMap, productValidateResponseItemMap, productValidateResponseItems);
             if(resultProduct.getCode() != CodeEnum.SUCCESS.getCode()){
                 return resultProduct;
             }
             userIdentityCard = futureTaskUserIdentityCard.get();
         } catch (InterruptedException e) {
             throw new BizException("系统异常，请稍后再试");
         } catch (ExecutionException e) {
             throw new BizException("系统异常，请稍后再试");
         }
        //合计总金额
        BigDecimal totalOrderAmount = new BigDecimal(0);
        //合计总金额
        BigDecimal totalDiscountAmount = new BigDecimal(0);
        //遍历门店或仓库，组装返回信息
        List<OrderConfirmStoreWarehouseResponseVO> storeWarehouses = Lists.newArrayList();
        for(Map.Entry<Long, List<OrderProductItemVO>> entry : partyGroupMap.entrySet()){
            //门店或仓库信息
            Long partyId = entry.getKey();
            DeptIdByStoreOrWarehouseResponseVO storeInfo = storeInfoMap.get(partyId);
            OrderConfirmStoreWarehouseResponseVO storeWarehouse = new OrderConfirmStoreWarehouseResponseVO();
            //购买的商品信息
            List<OrderProductItemVO> orderProductItemList = entry.getValue();
            if (storeInfo.getDept_type().equals(CodeCache.getValueByKey("DeptType", "S01"))) {
                //根据门店营业时间范围，获取用户配送时间选择列表
                List<OpenTimeResponse> result = this.getOpeningHoursList(storeInfo.getBusiness_time_start(), storeInfo.getBusiness_time_end(), 4);
                storeWarehouse.setDelivery_time(result);
                storeWarehouse.setDelivery_info(CodeCache.getValueByKey("StoreDeliveryMsg", "S01"));
            }else{
                storeWarehouse.setDelivery_info(CodeCache.getValueByKey("WarehouseDistributionDesc", "S01"));
            }
            storeWarehouse.setParty_id(storeInfo.getDept_id());
            storeWarehouse.setDept_type(storeInfo.getDept_type());
            storeWarehouse.setParty_name(storeInfo.getDept_name());
            storeWarehouse.setParty_contact_name(storeInfo.getContact_name());
            storeWarehouse.setParty_contact_phone(storeInfo.getTelephone());
            String party_address="";
            if(StrUtil.isNotEmpty(storeInfo.getCity_name())){
                party_address = party_address + storeInfo.getCity_name();
            }
            if(StrUtil.isNotEmpty(storeInfo.getArea_name())){
                party_address = party_address + storeInfo.getArea_name();
            }
            if(StrUtil.isNotEmpty(storeInfo.getDetail_address())){
                party_address = party_address + storeInfo.getDetail_address();
            }
            storeWarehouse.setParty_address(party_address);
            //折扣总价
            BigDecimal discountAmount = new BigDecimal(0);
            //订单金额
            BigDecimal orderAmount = new BigDecimal(0);
            //遍历商品
            for (OrderProductItemVO orderProductItem : orderProductItemList) {
                //获取商品校验信息
                ProductValidateResponseItem productValidateResponseItem = productValidateResponseItemMap.get(orderProductItem.getCart_id());
                //获取优惠
                BigDecimal discount = productValidateResponseItem.getNormal_price().subtract(productValidateResponseItem.getThis_moment_sale_price());
                discount = discount.compareTo(new BigDecimal(0)) < 0 ? new BigDecimal(0) :discount;
                orderProductItem.setDiscount(discount);
                orderProductItem.setSale_price(productValidateResponseItem.getNormal_price());
                orderProductItem.setProduct_name(productValidateResponseItem.getProduct_name());
                //获取活动信息
                ActivityInProgressProductItemResponseVO activity = productValidateResponseItem.getPromotion_info();
                orderProductItem.setActivity_mark_image(null != activity ? activity.getActivity_mark_image() : "");
                orderProductItem.setActivity_type(null != activity ? activity.getActivity_type() : "");
                orderProductItem.setShop_price(productValidateResponseItem.getThis_moment_sale_price());
                orderProductItem.setInventory_quantity(productValidateResponseItem.getInventory_quantity());
                orderProductItem.setList_image_url(productValidateResponseItem.getList_image_url());
                orderProductItem.setActivity_price(null != orderProductItem.getActivity_id() && orderProductItem.getActivity_id() > 0L ? productValidateResponseItem.getThis_moment_sale_price() : null);

                if(null != orderProductItem.getDiscount()){
                    //叠加优惠金额
                    discountAmount = NumberUtil.add(discountAmount, NumberUtil.mul(orderProductItem.getDiscount(), new BigDecimal(orderProductItem.getQuantity())));
                }
                //叠加订单金额
                if (orderProductItem.getActivity_id() != null && orderProductItem.getActivity_id() > 0L && null != orderProductItem.getActivity_price()) {
                    orderAmount = NumberUtil.add(orderAmount,NumberUtil.mul(orderProductItem.getActivity_price(),new BigDecimal(orderProductItem.getQuantity())));
                    //叠加合计金额
                    totalOrderAmount = NumberUtil.add(totalOrderAmount, NumberUtil.mul(orderProductItem.getActivity_price(), new BigDecimal(orderProductItem.getQuantity())));
                } else {
                    orderAmount = NumberUtil.add(orderAmount, NumberUtil.mul(orderProductItem.getSale_price(), new BigDecimal(orderProductItem.getQuantity())));
                    //叠加合计金额
                    totalOrderAmount = NumberUtil.add(totalOrderAmount, NumberUtil.mul(orderProductItem.getSale_price(), new BigDecimal(orderProductItem.getQuantity())));
                }
            }
            totalDiscountAmount = NumberUtil.add(totalDiscountAmount, discountAmount);
            storeWarehouses.add(storeWarehouse);
            storeWarehouse.setProduct_list(orderProductItemList);
            storeWarehouse.setDiscount_amount(discountAmount.toString());
            storeWarehouse.setOrder_amount(orderAmount.toString());
        }
        orderConfirmInfoResponseVO.setUser_id(user_id);
        orderConfirmInfoResponseVO.setUser_address_list(userAddresses);
        orderConfirmInfoResponseVO.setStore_warehouse_list(storeWarehouses);
        orderConfirmInfoResponseVO.setTotal_order_amount(totalOrderAmount);
        orderConfirmInfoResponseVO.setTotal_discount_amount(totalDiscountAmount);
        orderConfirmInfoResponseVO.setShare_user_id(share_user_id);
        orderConfirmInfoResponseVO.setShare_source(share_source);
        orderConfirmInfoResponseVO.setBuyer_name(userIdentityCard.getTrue_name());
        orderConfirmInfoResponseVO.setBuyer_id_card(userIdentityCard.getIdentity_card());
        return new Result(CodeEnum.SUCCESS, orderConfirmInfoResponseVO);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderToPayResponseVO>
     * @author 白飞
     * @description: 订单创建去支付
     * @param: [orderCreateRequest]
     * @create 2018-08-22 17:29
     **/
    @Transactional
    public Result<OrderPaymentResponseVO> create(OrderCreateRequestVO orderCreateRequest){

        if (StringUtils.isEmpty(orderCreateRequest.getPay_amount())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单支付总金额不能为空");
        }
        if (orderCreateRequest.getAddress_id() == null || orderCreateRequest.getAddress_id() <= 0L) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "收货地址不能为空");
        }
        if (orderCreateRequest.getStore_warehouse_list() == null || orderCreateRequest.getStore_warehouse_list().size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "门店或仓库不能为空");
        }
        StaffAppLoginVO staffAppLogin = null;
        UserLoginVO userLoginVO = null;
        //判断是否代下单用户
        if(orderCreateRequest.getUser_id() != null && orderCreateRequest.getUser_id() > 0L){
            staffAppLogin = LoginCache.getStaffAppLoginVO(orderCreateRequest.getLogin_token());
            if(null == staffAppLogin){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"请先登录，再操作");
            }
        }else{
            userLoginVO = LoginCache.getUserLoginVO(orderCreateRequest.getLogin_token());
            if(null == userLoginVO){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"请先登录，再购买");
            }
        }
        List<Long> cartIdsList = Lists.newArrayList();
        List<Long> storeIds = Lists.newArrayList();
        Map<Long, OrderProductItemVO> orderProductItemMap = Maps.newHashMap();
        Map<Long, OrderCreateStoreWarehouseVO> orderCreateStoreWarehouseMap = Maps.newHashMap();
        boolean isCartBuy = true;
        for(OrderCreateStoreWarehouseVO orderCreateStoreWarehouseVO : orderCreateRequest.getStore_warehouse_list()){
            if(orderCreateStoreWarehouseVO == null || orderCreateStoreWarehouseVO.getParty_id() == null || orderCreateStoreWarehouseVO.getParty_id() <= 0L){
                return new Result(CodeEnum.FAIL_PARAMCHECK, "门店或仓库不能为空");
            }
            if(orderCreateStoreWarehouseVO.getProduct_list() == null || orderCreateStoreWarehouseVO.getProduct_list().size() == 0){
                return new Result(CodeEnum.FAIL_PARAMCHECK, "商品信息不能为空");
            }
            storeIds.add(orderCreateStoreWarehouseVO.getParty_id());
            for(OrderProductItemVO orderProductItemVO : orderCreateStoreWarehouseVO.getProduct_list()){
                if(orderProductItemVO == null || orderProductItemVO.getProduct_id() == null || orderProductItemVO.getProduct_id() <= 0L){
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "商品信息不能为空");
                }
                if(orderProductItemVO.getActivity_id() != null && orderProductItemVO.getActivity_id() >= 0L){
                    if(orderProductItemVO.getActivity_price() == null || orderProductItemVO.getActivity_price().compareTo(new BigDecimal(0)) <= 0){
                        return new Result(CodeEnum.FAIL_PARAMCHECK, "商品活动价格不能为空");
                    }
                }
                if(orderProductItemVO.getQuantity() == null || orderProductItemVO.getQuantity() <= 0){
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "商品数量必须大于0");
                }
                if(orderProductItemVO.getShop_price() == null || orderProductItemVO.getShop_price().compareTo(new BigDecimal(0)) <= 0){
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "商品价格不能为空");
                }
                if(orderProductItemVO.getCart_id() != null && orderProductItemVO.getCart_id() > 0L){
                    cartIdsList.add(orderProductItemVO.getCart_id());
                    orderProductItemMap.put(orderProductItemVO.getCart_id(), orderProductItemVO);
                }else{
                    orderCreateStoreWarehouseMap.put(orderCreateStoreWarehouseVO.getParty_id(), orderCreateStoreWarehouseVO);
                    orderProductItemVO.setCart_id(0L);
                    orderProductItemMap.put(0L, orderProductItemVO);
                    isCartBuy = false;
                    break;
                }
            }
        }
        //购物商品集合
        List<OrderProductItemVO> orderProductItems = Lists.newArrayList();
        //商品校验
        Map<Long,Integer> quantityMap = Maps.newHashMap();
        //非购物车提交
        if(!isCartBuy){
            OrderProductItemVO orderProductItem = orderProductItemMap.get(0L);
            if(orderProductItem.getShop_price() == null || orderProductItem.getShop_price().compareTo(new BigDecimal(0)) <= 0){
                return new Result(CodeEnum.FAIL_PARAMCHECK,"商品价格不能为空");
            }
            quantityMap.put(0L, orderProductItem.getQuantity());
            orderProductItems.add(orderProductItem);
        } else {
            //根据当前用户查询购物车
            List<ShoppingCartRecord> cartItems = this.shoppingCartService.findByIds(cartIdsList);
            if(cartItems == null || cartItems.size() == 0){
                return new Result(CodeEnum.FAIL_BUSINESS,"购物车不能为空");
            }
            for (ShoppingCartRecord cartRecord : cartItems){
                OrderProductItemVO orderProductItemVO = orderProductItemMap.get(cartRecord.getId());
                if(!orderProductItemVO.getQuantity().equals(cartRecord.getQuantity())){
                    return new Result(CodeEnum.FAIL_BUSINESS,"购物车商品数量不一致");
                }
                if(!orderProductItemVO.getParty_id().equals(cartRecord.getParty_id())){
                    return new Result(CodeEnum.FAIL_BUSINESS,"购物车门店或仓库不一致");
                }
                if(!orderProductItemVO.getProduct_id().equals(cartRecord.getProduct_id())){
                    return new Result(CodeEnum.FAIL_BUSINESS,"购物车商品不一致");
                }
                quantityMap.put(cartRecord.getId(), cartRecord.getQuantity());
                orderProductItems.add(orderProductItemVO);
            }
        }
        //地址查询
        Callable<UserAddress> callableUserAddress = () -> {
            Result<UserAddress> result = userFeignClient.findByAddressId(orderCreateRequest.getAddress_id());
            return (result == null || result.getCode() != CodeEnum.SUCCESS.getCode() || result.getData() == null) ? null : result.getData();
        };
        //门店查询
        Callable<List<DeptIdByStoreOrWarehouseResponseVO>> callableStoreWarehouse = () -> {
            DeptIdsRequestVO deptIdsRequest = new  DeptIdsRequestVO();
            deptIdsRequest.setDept_ids(new ArrayList<>(storeIds));
            Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> storeResult = storeFeignClient.getAppDeptIdsByStoreOrWarehouseDet(deptIdsRequest);
            if(storeResult == null || storeResult.getCode() != CodeEnum.SUCCESS.getCode() || storeResult.getData() == null || storeResult.getData().getRecords() == null){
                return null;
            }
            return storeResult.getData().getRecords();
        };
        //商品校验
        Callable<List<ProductValidateResponseItem>> callableProducts = () -> {
            ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
            List<ProductValidateItem> productValidateItems = Lists.newArrayList();
            //循环组装校验请求数据
            for (OrderProductItemVO orderProductItem : orderProductItems) {
                ProductValidateItem productValidate = new ProductValidateItem();
                productValidate.setParty_id(orderProductItem.getParty_id());
                productValidate.setProduct_id(orderProductItem.getProduct_id());
                productValidate.setActivity_id(orderProductItem.getActivity_id());
                productValidate.setRequired_min_inventory_quantity(orderProductItem.getQuantity());
                productValidate.setFollow_value(orderProductItem.getCart_id() + "");
                productValidateItems.add(productValidate);
            }
            productsValidateRequestVO.setProd_summary_list(productValidateItems);
            //发起校验
            Result<ProductsValidateResponseVO> productsValidateResult = productFeignClient.productsValidate(productsValidateRequestVO);
            if(productsValidateResult == null || productsValidateResult.getCode() != CodeEnum.SUCCESS.getCode() || productsValidateResult.getData() == null || productsValidateResult.getData().getProd_validate_result_list() == null){
                return null;
            }
            //获得校验结果
            return productsValidateResult.getData().getProd_validate_result_list();
        };
        FutureTask<UserAddress> futureTaskUserAddress = new FutureTask<>(callableUserAddress);
        FutureTask<List<DeptIdByStoreOrWarehouseResponseVO>> futureTaskStore = new FutureTask<>(callableStoreWarehouse);
        FutureTask<List<ProductValidateResponseItem>> futureTaskProduct = new FutureTask<>(callableProducts);
        ThreadUtil.execute(new Thread(futureTaskUserAddress));
        ThreadUtil.execute(new Thread(futureTaskStore));
        ThreadUtil.execute(new Thread(futureTaskProduct));
        //异步保存订购人
        if(!StringUtils.isEmpty(orderCreateRequest.getBuyer_name()) && !StringUtils.isEmpty(orderCreateRequest.getBuyer_id_card())){
            ThreadUtil.execAsync(new Runnable() {
                @Override
                public void run() {
                    UserIdentityCardVO userIdentityCard = new UserIdentityCardVO();
                    userIdentityCard.setUser_id(orderCreateRequest.getUser_id());
                    userIdentityCard.setTrue_name(orderCreateRequest.getBuyer_name());
                    userIdentityCard.setIdentity_card(orderCreateRequest.getBuyer_id_card());
                    userFeignClient.saveUserIdentityCard(userIdentityCard);
                }
            });
        }
        //收货地址
        UserAddress userAddress = null;
        //门店信息集合
        Map<Long, DeptIdByStoreOrWarehouseResponseVO> storeMap = Maps.newHashMap();
        Map<Long, ProductValidateResponseItem> productValidateResponseItemMap = Maps.newHashMap();
        try {
            userAddress = futureTaskUserAddress.get();
            if(userAddress == null){
                return new Result(CodeEnum.FAIL_PARAMCHECK, "收货地址不存在");
            }
            List<DeptIdByStoreOrWarehouseResponseVO>  storeList = futureTaskStore.get();
            Result result = this.storeBathValidate(new HashSet<>(storeIds), storeMap, storeList);
            if(result.getCode() != CodeEnum.SUCCESS.getCode()){
                return result;
            }
            List<ProductValidateResponseItem> productValidateResponseItems = futureTaskProduct.get();
            result = this.productBathValidate(quantityMap, productValidateResponseItemMap, productValidateResponseItems);
            if(result.getCode() != CodeEnum.SUCCESS.getCode()){
                return result;
            }
        }catch (Exception e){
            throw new BizException("系统业务系统异常，请稍后再试");
        }
        //处理校验商品价格数据
        for(OrderCreateStoreWarehouseVO orderCreateStoreWarehouseVO : orderCreateRequest.getStore_warehouse_list()){
            BigDecimal orderAmount = new BigDecimal(0);
            for(OrderProductItemVO orderProductItemVO : orderCreateStoreWarehouseVO.getProduct_list()){
                ProductValidateResponseItem productValidateResponseItem = productValidateResponseItemMap.get(orderProductItemVO.getCart_id());
                if(orderProductItemVO.getActivity_id() != null && orderProductItemVO.getActivity_id() > 0L){
                    if(productValidateResponseItem.getThis_moment_sale_price().compareTo(orderProductItemVO.getActivity_price()) != 0){
                        return new Result(CodeEnum.FAIL_PARAMCHECK,"商品价格已更新，请重新购买");
                    }
                    orderProductItemVO.setShop_price(orderProductItemVO.getActivity_price());
                }
                BigDecimal shop_price = NumberUtil.roundHalfEven(orderProductItemVO.getShop_price(), 2);
                if(productValidateResponseItem.getThis_moment_sale_price().compareTo(shop_price) != 0){
                    return new Result(CodeEnum.FAIL_PARAMCHECK,"商品价格已更新，请重新购买");
                }
                orderAmount = NumberUtil.add(orderAmount, NumberUtil.mul(orderProductItemVO.getShop_price(), orderProductItemVO.getQuantity()));
            }
            orderAmount = NumberUtil.roundHalfEven(orderAmount, 2);
            if(NumberUtil.roundHalfEven(orderCreateStoreWarehouseVO.getOrder_amount(), 2).compareTo(orderAmount) != 0){
                return new Result(CodeEnum.FAIL_PARAMCHECK.getCode(), "商品价格发生变动", 11040);
            }
        }
        //多个订单编号，用逗号分隔
        String order_sns = "";
        //合计支付金额
        BigDecimal totalPayAmount = new BigDecimal(0);
        //订单保存-数据准备
        for(OrderCreateStoreWarehouseVO orderCreateStoreWarehouseVO : orderCreateRequest.getStore_warehouse_list()){
            DeptIdByStoreOrWarehouseResponseVO storeInfo = storeMap.get(orderCreateStoreWarehouseVO.getParty_id());
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setApp_key(orderCreateRequest.getApp_key());
            orderInfo.setParty_id(storeInfo.getDept_id());
            orderInfo.setParty_name(storeInfo.getDept_name());
            orderInfo.setParty_path(storeInfo.getDept_path());
            orderInfo.setParty_type(storeInfo.getDept_type());
            orderInfo.setOrder_date(new Date());
            orderInfo.setOrder_num(CommonUtil.getDateRandom());
            orderInfo.setOrder_type(SysCodeEnmu.ORDERTYPE_01.getCodeValue());
            orderInfo.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
            orderInfo.setDelivery_type(orderCreateStoreWarehouseVO.getDelivery_type());
            orderInfo.setSales_channel(orderCreateRequest.getCall_source());
            orderInfo.setUser_id(userLoginVO != null ? userLoginVO.getUser_id() : orderCreateRequest.getUser_id());
            orderInfo.setUser_name(userAddress.getReceive_name());
            orderInfo.setUser_address(userAddress.getDetail_address());
            orderInfo.setUser_phone(userAddress.getReceive_phone());
            orderInfo.setShare_user_id(orderCreateRequest.getShare_user_id());
            orderInfo.setShare_source(orderCreateRequest.getShare_source());
            orderInfo.setOrder_remark(orderCreateStoreWarehouseVO.getMemo());
            orderInfo.setHelp_staff_id(staffAppLogin != null ? staffAppLogin.getStaff_id() : null);
            orderInfo.setHelp_store_id(staffAppLogin != null && staffAppLogin.getDeptGroup() != null ? staffAppLogin.getDeptGroup().getId() : null);
            if (orderCreateStoreWarehouseVO.getDelivery_type().equals(SysCodeEnmu.DELIVERYTYPE_02.getCodeValue())) {
                orderInfo.setHope_delivery_start_time(DateUtil.date(orderCreateStoreWarehouseVO.getHope_delivery_start_time()));
                orderInfo.setHope_delivery_end_time((DateUtil.date(orderCreateStoreWarehouseVO.getHope_delivery_end_time())));
            }
            BigDecimal orderAmount = new BigDecimal(0);
            BigDecimal discountAmount = new BigDecimal(0);
            BigDecimal totalCost = new BigDecimal(0);
            List<OrderProduct> orderProducts = Lists.newArrayList();
            for(OrderProductItemVO orderProductItemVO : orderCreateStoreWarehouseVO.getProduct_list()){
                ProductValidateResponseItem productValidateItem = productValidateResponseItemMap.get(orderProductItemVO.getCart_id());
                if(orderProductItemVO.getActivity_id() != null && orderProductItemVO.getActivity_id() > 0L && orderProductItemVO.getActivity_price() != null){
                    orderProductItemVO.setShop_price(orderProductItemVO.getActivity_price());
                    //取得优惠
                    BigDecimal discount = NumberUtil.sub(productValidateItem.getNormal_price(), orderProductItemVO.getActivity_price());
                    discount = discount.compareTo(new BigDecimal(0)) < 0 ? new BigDecimal(0) : discount;
                    discountAmount = NumberUtil.add(discountAmount, NumberUtil.mul(discount, orderProductItemVO.getQuantity()));
                }
                //订单商品信息
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setOrder_id(orderInfo.getId());
                orderProduct.setActivity_id(orderProductItemVO.getActivity_id());
                orderProduct.setCost_price(productValidateItem.getCost_price());
                orderProduct.setOrg_price(productValidateItem.getNormal_price());
                orderProduct.setParty_id(orderInfo.getParty_id());
                orderProduct.setPrice_modify_flag(0);
                orderProduct.setProduct_category_id(productValidateItem.getProd_cate_id());
                orderProduct.setList_image_url(productValidateItem.getList_image_url());
                orderProduct.setProduct_id(productValidateItem.getProduct_id());
                orderProduct.setProduct_name(productValidateItem.getProduct_name());
                orderProduct.setQuantity(orderProductItemVO.getQuantity());
                orderProduct.setActual_price(orderProductItemVO.getShop_price());
                totalCost = NumberUtil.add(totalCost, NumberUtil.mul(orderProduct.getCost_price(), orderProduct.getQuantity()));
                orderAmount = NumberUtil.add(orderAmount, NumberUtil.mul(orderProduct.getActual_price(), orderProduct.getQuantity()));
                orderProducts.add(orderProduct);
            }
            totalCost = NumberUtil.roundHalfEven(totalCost, 2);
            orderAmount = NumberUtil.roundHalfEven(orderAmount, 2);
            discountAmount = NumberUtil.roundHalfEven(discountAmount, 2);
            BigDecimal order_profit = NumberUtil.roundHalfEven(NumberUtil.sub(orderAmount, totalCost), 2);
            orderInfo.setTotal_cost(totalCost);
            orderInfo.setOrder_amt(orderAmount);
            orderInfo.setDiscount_amt(discountAmount);
            orderInfo.setOrder_profit(order_profit);
            totalPayAmount = NumberUtil.add(totalPayAmount, orderAmount);
            //保存订单
            super.insert(orderInfo);
            //保存订单明细
            for (OrderProduct orderProduct : orderProducts){
                orderProduct.setOrder_id(orderInfo.getId());
                this.orderProductMapper.insert(orderProduct);
            }
            //订单状态信息
            OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
            orderStatusRecord.setOrder_id(orderInfo.getId());
            orderStatusRecord.setOperator_id(null != userLoginVO ? userLoginVO.getUser_id() : (staffAppLogin != null ? staffAppLogin.getStaff_id() : null));
            orderStatusRecord.setOperator_name(null != userLoginVO ? userLoginVO.getReceive_name() : (staffAppLogin != null ? staffAppLogin.getStaff_name() : null));
            //等字典表，订单当前处于哪个操作流程
            orderStatusRecord.setChange_reason(null != userLoginVO ? "用户下单" : "门店代下单");
            orderStatusRecord.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
            orderStatusRecord.setOperator_user_type(userLoginVO != null ? SysCodeEnmu.CHANGEUSERTYPE_01.getCodeValue() : SysCodeEnmu.CHANGEUSERTYPE_02.getCodeValue());
            //保存订单记录
            this.orderStatusRecordMapper.insert(orderStatusRecord);
            order_sns += "".equals(order_sns) ? orderInfo.getOrder_num() : "," + orderInfo.getOrder_num();
        }
        if(isCartBuy){
            //清空购物车
            this.shoppingCartMapper.deleteShoppingCartByIds(cartIdsList);
        }
        //减库存
        ProductsInventoryLockRequestVO productsInventoryLockRequestVO = new ProductsInventoryLockRequestVO();
        //释放，锁定标识 sub减库存， add加库存
        productsInventoryLockRequestVO.setLock_model(CommonConstantPool.SUB);
        //释放库存参数
        List<ProductInventoryLockItem> lock_items = Lists.newArrayList();
        for(OrderProductItemVO orderProductItem : orderProductItems){
            ProductInventoryLockItem productInventoryLockItem = new ProductInventoryLockItem();
            productInventoryLockItem.setParty_id(orderProductItem.getParty_id());
            productInventoryLockItem.setProduct_id(orderProductItem.getProduct_id());
            productInventoryLockItem.setActivity_id(orderProductItem.getActivity_id());
            productInventoryLockItem.setLock_quantity(orderProductItem.getQuantity());
            lock_items.add(productInventoryLockItem);
        }
        productsInventoryLockRequestVO.setLock_items(lock_items);
        //发起请求
        Result<ProductsInventoryLockResponseVO> lockResult = productFeignClient.lockProductInventory(productsInventoryLockRequestVO);
        if(!lockResult.isSuccess()){
            throw new BizException("库存扣减失败");
        }
        OrderPaymentResponseVO orderToPayResponseVO = this.paymentResponse(order_sns, totalPayAmount);
        return new Result(CodeEnum.SUCCESS, orderToPayResponseVO);
    }

    /**
     * 去支付响应VO
     *
     * @param order_sns
     *          订单一个或多个（用，分隔）编号
     * @param pay_amount
     *          支付金额
     * @return 对象
     */
    public OrderPaymentResponseVO paymentResponse(String order_sns, BigDecimal pay_amount) {
        OrderPaymentResponseVO orderToPayResponseVO = new OrderPaymentResponseVO();
        orderToPayResponseVO.setOrder_num_str(order_sns);
        orderToPayResponseVO.setPay_amount(pay_amount);
        List<String> payMethodList = Lists.newArrayList();
        List<CodeInfoVO> codeInfoVOS = CodeCache.getCodeList(SysCodeTypeEnum.PAYMENTMETHOD.getType());
        for (CodeInfoVO codeInfo : codeInfoVOS) {
            payMethodList.add(codeInfo.getCode_value());
        }
        orderToPayResponseVO.setPay_method_list(payMethodList);
        return orderToPayResponseVO;
    }

    /**
     * 订单确认-门店或仓库校验
     *
     * @param storeIds
     *          门店或仓库ID
     * @param storeInfoMap
     *          门店或仓库Map存储
     * @param storeList
     *           仓库信息集合
     * @return 成功或失败
     */
    private Result storeBathValidate(Set<Long> storeIds, Map<Long, DeptIdByStoreOrWarehouseResponseVO> storeInfoMap, List<DeptIdByStoreOrWarehouseResponseVO> storeList){
         if(storeList == null || storeList.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"门店信息不存在");
        }
        if(storeList.size() != storeIds.size()){
            return new Result(CodeEnum.FAIL_BUSINESS,"门店信息不一致");
        }
        for(DeptIdByStoreOrWarehouseResponseVO store : storeList){
            if(store.getDept_status() == 0){
                return new Result(CodeEnum.FAIL_BUSINESS,store.getDept_name() + "[已停用]");
            }
            //保存门店校验结果
            storeInfoMap.put(store.getDept_id(), store);
        }
        return new Result(CodeEnum.SUCCESS, "门店或仓库校验成功");
    }


    /**
     * 订单确认-批量商品验证
     *
     * @param quantityMap
     *             数量Map存储
     * @param productValidateResponseItemMap
     *              商品验证Map存储
     * @param productValidateResponseItems
     *              订单商品校验明细
     * @return 返回失败，成功
     */
    private Result productBathValidate(Map<Long,Integer> quantityMap, Map<Long, ProductValidateResponseItem> productValidateResponseItemMap, List<ProductValidateResponseItem> productValidateResponseItems){

        if(productValidateResponseItems == null || productValidateResponseItems.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品校验失败");
        }
        //遍历校验结果
        for (ProductValidateResponseItem productValidateResponseItem : productValidateResponseItems) {
            Long cartId = new Long(productValidateResponseItem.getFollow_value());
            Integer quantity = quantityMap.get(cartId);
            if(quantity == null || quantity <= 0 || productValidateResponseItem.getThis_moment_sale_price() == null || productValidateResponseItem.getThis_moment_sale_price().compareTo(new BigDecimal(0)) < 0){
                return new Result(CodeEnum.FAIL_BUSINESS,"商品数量和价格数据异常");
            }
            if (!productValidateResponseItem.getIs_valid()) {
                log.info("确认订单商品：{}无效，原因：{}", productValidateResponseItem.getProduct_id(), productValidateResponseItem.getOther_comments());
                return new Result(CodeEnum.FAIL_BUSINESS.getCode(), productValidateResponseItem.getOther_comments(), productValidateResponseItem.getValidate_status_code());
            }
            productValidateResponseItemMap.put(cartId, productValidateResponseItem);
        }
        return new Result(CodeEnum.SUCCESS, "商品校验成功");
    }


    /**
     * @return java.util.List<com.hryj.entity.bo.order.OrderInfo>
     * @author 罗秋涵
     * @description: 获取订单列表
     * @param: [numlist]
     * @create 2018-07-11 9:49
     **/
    public List<OrderInfo> findOrderInfoList(List<String> numlist) {

        return orderInfoMapper.findOrderInfoList(numlist);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 更新订单状态
     * @param: [order_id, order_status]
     * @create 2018-07-09 17:30
     **/
    @Transactional
    public Result updateOrderStatus(OrderStatusRecordVO orderStatusRecord, String login_token) {
        //参数判断
        if (orderStatusRecord == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK);
        }
        if (orderStatusRecord.getOrder_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        if (orderStatusRecord.getOrder_status() == null || "".equals(orderStatusRecord.getOrder_status().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单状态不能为空");
        }

        OrderStatusRecord record=new OrderStatusRecord();
        record.setOrder_id(orderStatusRecord.getOrder_id());
        record.setOrder_status(orderStatusRecord.getOrder_status());
        record.setStatus_remark(orderStatusRecord.getStatus_remark());
        record.setOperator_user_type(orderStatusRecord.getOperator_user_type());
        record.setOperator_id(orderStatusRecord.getOperator_id());
        record.setOperator_name(orderStatusRecord.getOperator_name());
        record.setChange_reason(orderStatusRecord.getChange_reason());
        //如果操作人为空根据token查询操作人
        if (record.getOperator_id() == null || record.getOperator_name() == null) {
            if (StrUtil.isNotEmpty(login_token)) {
                UserLoginVO userInfo = getUserLoginUser(login_token);
                StaffAppLoginVO staffInfo = getStaffAppLoginUser(login_token);
                if (userInfo != null) {
                    record.setOperator_id(userInfo.getUser_id());
                    record.setOperator_name(userInfo.getPhone_num());
                    record.setOperator_user_type(CodeCache.getValueByKey("OperatorUserType", "S01"));
                } else if (staffInfo != null) {
                    record.setOperator_id(staffInfo.getStaff_id());
                    record.setOperator_name(staffInfo.getStaff_name());
                    record.setOperator_user_type(CodeCache.getValueByKey("OperatorUserType", "S02"));
                }
            }

        }
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(record.getOrder_id());
        orderInfo.setOrder_status(record.getOrder_status());
        //订单状态为已完成时设置完成时间
        if(orderInfo.getOrder_status()!=null&&CodeCache.getValueByKey("OrderStatus", "S08").equals(orderInfo.getOrder_status())){
            orderInfo.setComplete_time(new Date());
            if(orderStatusRecord.getUser_id()!=null){
                //如果是已完成状态，判断是否为首单,新交易标识:1-是,0-否
                Integer num = orderInfoMapper.countNewTradeFlag(orderStatusRecord.getUser_id());
                if(num<1){
                    orderInfo.setNew_trade_flag(1);
                }else {
                    orderInfo.setNew_trade_flag(0);
                }
            }
        }
        //修改编号
        orderInfoMapper.updateById(orderInfo);
        //获取订单状态历史记录
        List<OrderStatusRecord> recordList = orderInfoMapper.selectOrderStatusList(orderStatusRecord.getOrder_id());
        //首次变更记录
        if (recordList == null) {
            record.setConsume_time(0);
        } else {
            //计算耗时
            Date record_time = recordList.get(0).getRecord_time();
            Long consume_time = DateUtil.betweenMs(record_time, new Date());
            record.setConsume_time(consume_time.intValue());
        }

        record.setRecord_time(new Date());
        orderStatusRecordMapper.insert(record);

        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 支付成功后订单处理
     * * @param: [outTradeNo, tradeNo]
     * @create 2018-07-11 18:49
     **/
    @Transactional
    public Result callbackAfterPay(Map<String, Object> param_map, String pay_method, String outTradeNo, String tradeNo) {
        log.info("支付回调：平台返回结果：{}，pay_method：{}，outTradeNo：{}，tradeNo：{}",param_map,pay_method,outTradeNo,tradeNo);
        //获取支付组
        EntityWrapper<PaymentGroup> wrapper = new EntityWrapper<PaymentGroup>();
        wrapper.eq("pay_group_sn", outTradeNo);
        PaymentGroup group = paymentGroupService.selectOne(wrapper);
        if(group==null||group.getPay_status()==null){
            return new Result(CodeEnum.FAIL_BUSINESS, "回调执行失败，无对应支付组记录");
        }
        if (CodeCache.getValueByKey("PaymentStatus", "S01").equals(group.getPay_status())) {
            //更改支付状态
            PaymentGroup paymentGroup = new PaymentGroup();
            paymentGroup.setId(group.getId());
            paymentGroup.setPay_trade_num(tradeNo);
            paymentGroup.setNotify_data(JSON.toJSONString(param_map));
            paymentGroup.setPay_status(CodeCache.getValueByKey("PaymentStatus", "S02"));
            String payment_account = "";
            if(CodeCache.getValueByKey("PaymentMethod", "S01").equals(pay_method)){
                payment_account = (String) param_map.get("openid");
            }
            if(CodeCache.getValueByKey("PaymentMethod", "S02").equals(pay_method)){
                payment_account = (String) param_map.get("buyer_id");
            }
            paymentGroup.setPayment_account(payment_account);
            paymentGroupMapper.updateById(paymentGroup);
            //获取支付组订单列表
            List<PaymentGroupOrder> groupOrderList = paymentGroupMapper.getRecordByGroupId(outTradeNo);
            if(groupOrderList==null){
                return new Result(CodeEnum.FAIL_BUSINESS, "订单支付组记录为空");
            }
            for (int i=0; i<groupOrderList.size(); i++) {
                PaymentGroupOrder payOrder = groupOrderList.get(i);
                //创建订单状态记录
                OrderStatusRecordVO orderStatusRecord = new OrderStatusRecordVO();
                //订单状态
                OrderInfo orderInfo = new OrderInfo();
                //创建订单相关信息表（自提，配送）
                OrderInfo info = orderInfoMapper.selectById(payOrder.getOrder_id());
                //如果配送方式为自提生自提码
                if (info.getDelivery_type().equals(CodeCache.getValueByKey("DeliveryType", "S01"))) {
                    //自提方式
                    //订单状态为待自提
                    orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S03"));
                    orderInfo.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S03"));
                    OrderSelfPick orderSelfPick = new OrderSelfPick();
                    orderSelfPick.setOrder_id(info.getId());
                    //获取自提码
                    String selfPickCode = orderSelfPickService.createSelfPickCode();
                    orderSelfPick.setSelf_pick_code(selfPickCode);
                    orderSelfPick.setSelf_pick_status(CodeCache.getValueByKey("SelfPickStatus", "S01"));
                    orderSelfPick.setUser_id(info.getUser_id());
                    orderSelfPick.setUser_name(info.getUser_name());
                    orderSelfPick.setUser_phone(info.getUser_phone());
                    orderSelfPick.setUser_address(info.getUser_address());
                    //根据门店编号获取名店信息
                    DeptIdRequestVO dept = new DeptIdRequestVO();
                    dept.setDept_id(info.getParty_id());
                    Result<DeptIdByStoreOrWarehouseResponseVO> storeInfo = storeFeignClient.getDeptIdByStoreOrWarehouseDet(dept);
                    orderSelfPick.setSelf_pick_address(storeInfo.getData().getDetail_address());
                    orderSelfPick.setSelf_pick_contact(storeInfo.getData().getContact_name());
                    orderSelfPick.setSelf_pick_phone(storeInfo.getData().getTelephone());
                    orderSelfPickMapper.insert(orderSelfPick);
                    //发送短信
                    HashMap messageMap = new HashMap();
                    messageMap.put("self_pick_code", selfPickCode);
                    messageMap.put("store_name", storeInfo.getData().getDept_name());
                    messageMap.put("telephone", storeInfo.getData().getTelephone());
                    AliYunSms.sendSingleSms(orderSelfPick.getUser_phone(), "SMS_139238760", messageMap);
                } else if (info.getDelivery_type().equals(CodeCache.getValueByKey("DeliveryType", "S02"))) {
                    //送货上门
                    //获取用户信息
                    Result<UserInfoVO> result = userFeignClient.findUserInfoVOByUserId(info.getUser_id(), info.getUser_phone());
                    UserInfoVO userInfoVO=null;
                    if(result!=null&&result.getData()!=null){
                        userInfoVO=result.getData();
                    }else{
                        userInfoVO=new UserInfoVO();
                    }
                    //生成配送单
                    OrderDistribution orderDistribution = new OrderDistribution();
                    orderDistribution.setOrder_id(info.getId());
                    orderDistribution.setDistribution_type(CodeCache.getValueByKey("DistributionType", "S01"));
                    orderDistribution.setDistribution_status(CodeCache.getValueByKey("DistributionStatus", "S01"));
                    orderDistribution.setUser_id(info.getUser_id());
                    orderDistribution.setUser_name(info.getUser_name());
                    orderDistribution.setUser_phone(info.getUser_phone());
                    orderDistribution.setUser_address(info.getUser_address());
                    orderDistribution.setAddress_locations(userInfoVO.getLocations());
                    orderDistribution.setHope_delivery_start_time(info.getHope_delivery_start_time());
                    orderDistribution.setHope_delivery_end_time(info.getHope_delivery_end_time());
                    orderDistribution.setActual_delivery_end_time(info.getHope_delivery_end_time());
                    //订单状态为待发货
                    orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S02"));
                    orderInfo.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S02"));
                    orderDistributionMapper.insert(orderDistribution);
                    List<DistributionProduct> productList = new ArrayList<>();
                    //获取订单对应商品信息
                    List<OrderPorductVO> orderPorductList = orderProductMapper.getOrderPorductVOList(info.getId());
                    for (OrderPorductVO porductVO : orderPorductList) {
                        DistributionProduct distributionProduct = new DistributionProduct();
                        distributionProduct.setDistribution_id(orderDistribution.getId());
                        distributionProduct.setOrder_id(info.getId());
                        distributionProduct.setDistribution_id(porductVO.getProduct_id());
                        distributionProduct.setDistribution_quantity(porductVO.getQuantity());
                        productList.add(distributionProduct);
                    }
                    distributionProductService.insertBatch(productList);
                }else if(info.getDelivery_type().equals(CodeCache.getValueByKey("DeliveryType", "S03"))){
                    //快递方式
                    orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S02"));
                    orderInfo.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S02"));
                }
                orderStatusRecord.setOrder_id(payOrder.getOrder_id());
                orderStatusRecord.setStatus_remark("支付成功回调，更新状态");
                orderStatusRecord.setChange_reason("支付成功回调，更新状态");
                //如果是跨境订单则，回填订单状态及第三方订单号
                if(OrderConstants.CROSS_BORDER_ORDER_TYPE_LIST.contains(info.getOrder_type())){
                    OrderCrossBorder orderCrossBorder=new OrderCrossBorder();
                    orderCrossBorder.setOrder_id(payOrder.getOrder_id());
                    //状态为描述设置为申报中（订单状态依旧为待申报）
                    orderCrossBorder.setThird_order_status_desc(OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareing.ordinal()]);
                    orderCrossBorderService.updateById(orderCrossBorder);
                }

                //订单状态记录
                this.updateOrderStatus(orderStatusRecord,null);
                orderInfo.setId(payOrder.getOrder_id());
                orderInfo.setPay_method(pay_method);
                orderInfo.setPay_time(new Date());
                //单笔支付时订单编号设置为支付组编号,并修改支付住订单编号
                if(groupOrderList.size()==1){
                    orderInfo.setOrder_num(group.getPay_group_sn());
                    PaymentGroupOrder paymentGroupOrder=new PaymentGroupOrder();
                    paymentGroupOrder.setId(payOrder.getId());
                    paymentGroupOrder.setOrder_num(group.getPay_group_sn());
                    paymentGroupOrderMapper.updateById(paymentGroupOrder);
                }
                //修改订单状态
                orderInfoMapper.updateById(orderInfo);

            }
        } else {
            return new Result(CodeEnum.SUCCESS, "该订单已执行过回调程序，无需重复执行");
        }
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据Id更新订单状态, 记录订单状态
     * @param:
     * @create 2018-07-11 12:07
     **/
    public void updateStatus(OrderStatusVO vo) {
        log.info("根据Id更新订单状态, 记录订单状态开始,updateStatus" + JSON.toJSONString(vo));
        //更新订单状态
        if(StringUtils.isEmpty(vo.getOrder_status())){
            return;
        }
        if(SysCodeEnmu.ORDERSTATUS_08.getCodeValue().equals(vo.getOrder_status())
                &&vo.getOrder_id()!=null){
            //如果是已完成状态，判断是否为首单,新交易标识:1-是,0-否
            Integer k = orderInfoMapper.countNewTradeFlag(vo.getUser_id());
            if(k<1){
                vo.setNew_trade_flag(1);
            }else {
                vo.setNew_trade_flag(0);
            }
        }
        //OrderInfo orderInfo = new OrderInfo();
        orderInfoMapper.updateOrderInfoStatus(vo);
        //对订单状态记录表添加新数据（通过获取的员工，用户信息来判断操作用户类型）
        OrderStatusRecordInsertVo statusVO = new OrderStatusRecordInsertVo();
        statusVO.setUserLoginVO(LoginCache.getUserLoginVO(vo.getLogin_token()));
        statusVO.setStaffAppLoginVO(LoginCache.getStaffAppLoginVO(vo.getLogin_token()));
        statusVO.setStaffAdminLoginVO(LoginCache.getStaffAdminLoginVO(vo.getLogin_token()));
        statusVO.setOrder_id(vo.getOrder_id());
        statusVO.setOrderStatus(vo.getOrder_status());
        statusVO.setStatus_remark(vo.getStatus_remark());
        statusVO.setChange_reason(vo.getChange_reason());
        insertOrderStatusRecord(statusVO);
        log.info("根据Id更新订单状态, 记录订单状态完成,updateStatus" + JSON.toJSONString(vo));
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 添加订单状态记录表
     * @param:
     * @create 2018-07-11 14:09
     **/
    public void insertOrderStatusRecord(OrderStatusRecordInsertVo vo) {
        OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
        if (vo.getUserLoginVO() != null&&vo.getUserLoginVO().getUser_id()!=null) {
            //用户
            UserLoginVO userLoginVO = vo.getUserLoginVO();
            orderStatusRecord.setOperator_user_type(SysCodeEnmu.OPERATORUSERTYPE_01.getCodeValue());
            orderStatusRecord.setOperator_name(userLoginVO.getReceive_name());
            orderStatusRecord.setOperator_id(userLoginVO.getUser_id());
        } else if (vo.getStaffAppLoginVO() != null&&vo.getStaffAppLoginVO().getStaff_id()!=null) {
            //员工
            orderStatusRecord.setOperator_user_type(SysCodeEnmu.OPERATORUSERTYPE_02.getCodeValue());
            StaffAppLoginVO staffAppLoginVO = vo.getStaffAppLoginVO();
            orderStatusRecord.setOperator_name(staffAppLoginVO.getStaff_name());
            orderStatusRecord.setOperator_id(staffAppLoginVO.getStaff_id());
        }
        else if (vo.getStaffAdminLoginVO() != null&&vo.getStaffAdminLoginVO().getStaff_id()!=null) {
            //后台
            orderStatusRecord.setOperator_user_type(SysCodeEnmu.OPERATORUSERTYPE_02.getCodeValue());
            StaffAdminLoginVO staffAdminLoginVO = vo.getStaffAdminLoginVO();
            orderStatusRecord.setOperator_name(staffAdminLoginVO.getStaff_name());
            orderStatusRecord.setOperator_id(staffAdminLoginVO.getStaff_id());
        }
        orderStatusRecord.setOrder_id(vo.getOrder_id());
        orderStatusRecord.setOrder_status(vo.getOrderStatus());
        orderStatusRecord.setRecord_time(new Date());
        orderStatusRecord.setStatus_remark(vo.getStatus_remark());
        orderStatusRecord.setChange_reason(vo.getChange_reason());
        orderStatusRecordMapper.insert(orderStatusRecord);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 支付后查询结果
     * @param: [confirmPayOrderRequestVO]
     * @create 2018-07-12 17:17
     **/
    public Result<PaymentGroupResponseVO> getOrderPayResult(SingleParamVO singleParamVO) {
        log.info("支付后执行手动查询支付结果：{}",singleParamVO);
        if(singleParamVO==null||StrUtil.isEmpty(singleParamVO.getStringParam())){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        //获取支付结果
        PaymentGroupResponseVO paymentGroup = paymentGroupMapper.getPaymentGroupInfo(singleParamVO.getStringParam());
        if (paymentGroup == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应支付记录");
        }
        //等于02说明支付成功
        if (CodeCache.getValueByKey("PaymentStatus", "S02").equals(paymentGroup.getPay_status())) {
            return new Result(CodeEnum.SUCCESS, paymentGroup);
        } else {
            //手动查询支付结果
            Map<String, Object> result= paymentService.queryDealStatus(paymentGroup.getApp_key(),paymentGroup.getPayment_method(),paymentGroup.getPay_trade_num(),paymentGroup.getPay_group_sn(),paymentGroup.getPayment_channel());
            log.info("订单支付后查询支付结果："+result);
            //微信
            if (result != null && paymentGroup.getPayment_method().equals(CodeCache.getValueByKey("PaymentMethod", "S01"))) {
                //判断是否有返回支付业务状态
                if (result.get("trade_state") == null) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "支付结果查询异常");
                }
                //支付成功
                if ("SUCCESS".equals(result.get("trade_state"))) {
                    log.info("微信查询结果为支付成功，手动执行订单回调接口"+result);
                    this.callbackAfterPay(result, paymentGroup.getPayment_method(), result.get("out_trade_no").toString(), result.get("transaction_id").toString());
                    return new Result(CodeEnum.SUCCESS, paymentGroup);
                }else{
                    return new Result(CodeEnum.FAIL_BUSINESS, null, BizCodeEnum.PAYMENT_FAILED);
                }
            }//支付宝
            else if (result != null && paymentGroup.getPayment_method().equals(CodeCache.getValueByKey("PaymentMethod", "S02"))) {
                //获得查询结果
                Map<String, Object> aliPayresult = (Map<String, Object>) result.get("alipay_trade_query_response");
                //检测业务状态是否有值
                if (aliPayresult.get("trade_status") == null) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "支付结果查询异常");
                }
                //结果为支付成功处理
                if ("10000".equals(aliPayresult.get("code").toString()) && "TRADE_SUCCESS".equals(aliPayresult.get("trade_status").toString())) {
                    log.info("支付宝查询结果为支付成功，手动执行订单回调接口"+result);
                    this.callbackAfterPay(aliPayresult, paymentGroup.getPayment_method(), aliPayresult.get("out_trade_no").toString(), aliPayresult.get("trade_no").toString());
                    return new Result(CodeEnum.SUCCESS, paymentGroup);
                }else{
                    return new Result(CodeEnum.FAIL_BUSINESS, null, BizCodeEnum.PAYMENT_FAILED);
                }
            }
            return new Result(CodeEnum.FAIL_BUSINESS, null, BizCodeEnum.PAYMENT_FAILED);
        }
    }


    /**
     * @return com.hryj.entity.vo.ListResponseVO<com.hryj.entity.vo.order.response.opentime.OpenTimeResponse>
     * @author 罗秋涵
     * @description: 获取营业时间
     * @param: [startTime, endTime, dateScope]
     * @create 2018-07-13 18:03
     **/
    public List<OpenTimeResponse> getOpeningHoursList(String startTime, String endTime, int dateScope) {
        List<OpenTimeResponse> response = new ArrayList<>();
        String str[] = startTime.split(":");
        //开始时
        int start = Integer.valueOf(str[0]);
        //开始分
        int startmin = Integer.valueOf(str[1]);
        String str1[] = endTime.split(":");
        //结束时
        int end = Integer.valueOf(str1[0]);
        //结束分
        int endmin = Integer.valueOf(str1[1]);
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startDate = df.format(new Date());
        SimpleDateFormat dff = new SimpleDateFormat("HH");
        SimpleDateFormat dffmin = new SimpleDateFormat("mm");
        int nowTime = Integer.valueOf(dff.format(new Date()));
        int nowMin = Integer.valueOf(dffmin.format(new Date()));
        for (int i = 0; i < dateScope; i++) {
            OpenTimeResponse timeResponse = new OpenTimeResponse();
            List<TimeQuantum> quantums = new ArrayList<>();
            List<String> list = new ArrayList<>();
            int st = 0;
            int en = end;
            String key = "";
            if (i == 0) {
                if(startmin==0&&nowMin>0){
                    st = nowTime+1;
                }else if(startmin==0&&nowMin==0){
                    st = nowTime;
                }else if(startmin==30&&nowMin>30){
                    st = nowTime+1;
                }else if(startmin==30&&nowMin<=30){
                    st = nowTime;
                }
                key = startDate;
            } else {
                st = start;
                key = df.format(System.currentTimeMillis() + i * 24 * 60 * 60 * 1000);
            }
            for (int a = st; a < end; a++) {
                TimeQuantum quantum = new TimeQuantum();
                String overTime = "";
                String beginTime = key + " " + a + ":" + startmin + ":00";
                a++;
                overTime = key + " " + a + ":" + startmin + ":00";
                a--;
                quantum.setBeginTime(DateUtil.parse(beginTime, DatePattern.NORM_DATETIME_PATTERN).getTime());
                quantum.setEndTime(DateUtil.parse(overTime, DatePattern.NORM_DATETIME_PATTERN).getTime());
                if (startmin == 30 && endmin == 0) {
                    if (a == end - 1) {
                        break;
                    } else {
                        quantums.add(quantum);
                    }
                } else {
                    quantums.add(quantum);
                }

            }
            //如果小时列表为空则不计着天
            if (quantums != null && quantums.size() > 0) {
                timeResponse.setBaseDate(DateUtil.parseDate(key).getTime());
                timeResponse.setQuantumList(quantums);
                response.add(timeResponse);
            }
        }
        return response;
    }


    public OrderActivityInfoVO getActivityMessage(@Param("activity_id") Long activity_id,
                                                                           @Param("party_id") Long party_id, @Param("product_id") Long product_id) {
        return orderInfoMapper.getActivityMessage(activity_id, party_id, product_id);
    }

    /**
     * @return
     * @author 叶方宇
     * @methodDesc:
     * @methodName:
     * @description: 锁定库存
     * @param: quantity:锁定库存用负数
     * @create 2018-07-12 20:17
     **/
    public void partyProductInventoryAdjust(Long party_id,Long product_id, Integer quantity) throws BizException {
        PartyProductInventoryAdjustItem item = new PartyProductInventoryAdjustItem();
        item.setProduct_id(product_id);
        item.setParty_id(party_id);
        item.setAdjust_num(quantity);
        orderPartyProductMapper.updateProductQuantity(item);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodDesc:
     * @methodName:
     * @description: 锁定库存
     * @param: isRelease:true-释放，false：锁定
     * @create 2018-07-12 20:17
     **/
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void partyProductInventoryAdjust(List<ProductInventoryAdjustVO> adjustVOList,boolean isRelease) {
        //根据party_id和product_id进行分组累加后，再去给库存加锁，否则会一直获取不到锁，因为party_id和product_id可能都相同
        Map<String,Integer> group_map = new HashMap<>();
        for(ProductInventoryAdjustVO piaVO : adjustVOList){
            String key = piaVO.getParty_id()+"_"+piaVO.getProduct_id();
            Integer value = piaVO.getQuantity();
            if(group_map.get(key) !=null){
                group_map.put(key,group_map.get(key)+value);
            }else{
                group_map.put(key,value);
            }
        }
        log.info("锁定库存：group_map=" + JSON.toJSONString(group_map));
        try {
            //批量加锁
            for (String key : group_map.keySet()) {
                //加锁，获取不到锁就阻塞等待100毫秒，连续20次获取不到就提示系统繁忙
                int lockRetryTime = 0;
                while (!redisLock.lock(CacheGroup.PRODUCT_STOCK_LOCK.getValue(),key,10)) {
                    if (lockRetryTime++ > 20) {
                        throw new BizException("系统太繁忙，请稍后再试");
                    }
                    ThreadUtil.safeSleep(100);//线程等待
                }
            }
            for(String key : group_map.keySet()){
                String[] keyArr = key.split("_");
                Long party_id = Long.parseLong(keyArr[0]);
                Long product_id = Long.parseLong(keyArr[1]);
                //查询当前库存
                Integer allQuantity = orderPartyProductMapper.findProductQuantityById(party_id, product_id);
                Integer quantity = 0;
                if(!isRelease){
                    //如果是锁定
                    quantity = allQuantity - group_map.get(key);
                    if(quantity<0){
                        throw new BizException("库存不足");
                    }
                }else{
                    quantity = allQuantity + group_map.get(key);
                }
                log.info("库存锁定打印:减去后数量："+quantity+"---原数量："+allQuantity+"---更新数量："+group_map.get(key));
                this.partyProductInventoryAdjust(party_id,product_id,quantity);
            }
        }catch (Exception e){
            throw e;
        }finally {
            //批量解锁
            for (String key : group_map.keySet()) {
                redisLock.unLock(CacheGroup.PRODUCT_STOCK_LOCK.getValue(),key);
            }
        }
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 新版商品验证
     * @param:
     * @create 2018-07-20 16:12
     **/
    public Result<ProductsValidateResponseVO> productsValidate(Long activityId, Long partyId, Long productId, Integer quantity) {
        log.info("新版商品验证,productsValidate:activityId,partyId,productId,quantity"
                + JSON.toJSONString(activityId+","+partyId+","+productId+","+quantity));
        //商品验证
        ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
        List<ProductValidateItem> prod_summary_list = new ArrayList<>();
        ProductValidateItem item = new ProductValidateItem();
        item.setActivity_id(activityId);
        item.setParty_id(partyId);
        item.setProduct_id(productId);
        item.setRequired_min_inventory_quantity(quantity);
        prod_summary_list.add(item);
        productsValidateRequestVO.setProd_summary_list(prod_summary_list);
        Result<ProductsValidateResponseVO> result = productFeignClient.productsValidate(productsValidateRequestVO);

        if (result != null && result.getData() != null && result.getData().getProd_validate_result_list() != null) {
            List<ProductValidateResponseItem> responseItems = result.getData().getProd_validate_result_list();
            if (responseItems.size() < 1) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "该商品数据异常！");
            }
            for (ProductValidateResponseItem it : responseItems) {
                if (!it.getIs_valid()) {
                    return new Result(CodeEnum.FAIL_BUSINESS.getCode(), "", result.getData().getProd_validate_result_list().get(0).getValidate_status_code());
                }
            }
        }

        return new Result<>(CodeEnum.SUCCESS);
    }

    /**
     * @author 罗秋涵
     * @description: 根据门店编号获取门店营业时间列表
     * @param: [dept_id]
     * @return com.hryj.common.Result
     * @create 2018-08-03 15:12
     **/
    public List<OpenTimeResponse> getOpeningHoursListByDeptId(Long dept_id) {
        //根据门店编号获取名店信息
        DeptIdRequestVO dept = new DeptIdRequestVO();
        dept.setDept_id(dept_id);
        //获取门店信息
        Result<DeptIdByStoreOrWarehouseResponseVO> storeInfo = storeFeignClient.getDeptIdByStoreOrWarehouseDet(dept);
        List<OpenTimeResponse> responseList=new ArrayList<>();
        if (storeInfo.getData().getDept_type().equals(CodeCache.getValueByKey("DeptType", "S01"))) {
            //根据门店营业时间范围，获取用户配送时间选择列表
            responseList = getOpeningHoursList(storeInfo.getData().getBusiness_time_start(), storeInfo.getData().getBusiness_time_end(), 4);
        }

        return responseList;
    }

    /**
     * @author 罗秋涵
     * @description: 订单商品信息中组装活动信息
     * @param: [orderPorductList]
     * @return java.util.List<com.hryj.entity.vo.order.OrderPorductVO>
     * @create 2018-08-21 11:40
     **/
    public List<OrderPorductVO> getOrderProductActivityInfo(List<OrderPorductVO> orderPorductList){
        //返回数据集合
        List<OrderPorductVO> responseList=new ArrayList<>();
        //获取活动信息请求参数
        List<OnlyActivityIdRequestVO> activityIds=new ArrayList<>();
        //根据活动编号分组
        Map<Long,OrderActivityInfoResponseVO> orderActivityInfoMap=new HashMap<>();
        //循环组装请求数据
        if(orderPorductList!=null&&orderPorductList.size()>0){
            for(int i=0;i<orderPorductList.size();i++){
                OnlyActivityIdRequestVO activityRequestVO=new OnlyActivityIdRequestVO();
                activityRequestVO.setActivity_id(orderPorductList.get(i).getActivity_id());
            }
            Result<List<OrderActivityInfoResponseVO>> result=activityFeignClient.getActivityInfoById(activityIds);
            if(result.isSuccess()&&result.getData()!=null){
                List<OrderActivityInfoResponseVO> voList=result.getData();
                for(OrderActivityInfoResponseVO vo:voList){
                    if(orderActivityInfoMap.get(vo.getActivity_id())==null){
                        orderActivityInfoMap.put(vo.getActivity_id(),vo);
                    }
                }
            }
            //循环编号订单商品信息设置活动信息
            for(int i=0;i<orderPorductList.size();i++){
                //根据活动编号获取map中的活动信息
                if(orderPorductList.get(i).getActivity_id()!=null&&orderPorductList.get(i).getActivity_id()>0
                        &&orderActivityInfoMap.get(orderPorductList.get(i).getActivity_id())!=null){
                    OrderActivityInfoResponseVO vo=orderActivityInfoMap.get(orderPorductList.get(i).getActivity_id());
                    orderPorductList.get(i).setActivity_name(vo.getActivity_name());
                    orderPorductList.get(i).setActivity_type(vo.getActivity_type());
                    orderPorductList.get(i).setActivity_mark_image(vo.getActivity_mark_image());
                }
            }

        }

        return orderPorductList;
    }


    /**
     * @author 罗秋涵
     * @description: 根据订单类型获取对应支付渠道 v1.2 新增
     * @param: [order_type]
     * @return java.lang.Long
     * @create 2018-09-13 11:10
     **/
    public Long getOrderPayChannel(String order_type){
        Long party_id=100000L;
        if(order_type==null||"".equals(order_type)){
            return null;
        }
        if(OrderConstants.RESALE_ORDER_TYPE_LIST.contains(order_type)){
            party_id=100000L;
        }else if(OrderConstants.CROSS_BORDER_ORDER_TYPE_LIST.contains(order_type)){
            party_id=100001L;
        }else{
            return null;
        }
        return party_id;
    }




    /**
     * @author 罗秋涵
     * @description: 查询待同步订单
     * @param: [pay_status, order_type, third_order_status, adjustment_type_id]
     * @return java.util.List<com.hryj.entity.vo.order.crossOrder.CrossBorderOrderVo>
     * @create 2018-09-17 11:20
     **/
    public List<CrossBorderOrderVo> findAwaitSyncOrderList(String pay_status, String order_type, int third_order_status, String adjustment_type_id) {
        List<CrossBorderOrderVo> borderOrderList=orderInfoMapper.findAwaitSyncOrderList(pay_status,order_type,third_order_status,adjustment_type_id);
        return borderOrderList;
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单编号查询订单信息
     * @param:
     * @return
     * @create 2018-09-19 17:06
     **/

    public OrderInfo getOrderInfoByNum(String orderNum){
        return orderInfoMapper.getOrderInfoByNum(orderNum);
    }
}
