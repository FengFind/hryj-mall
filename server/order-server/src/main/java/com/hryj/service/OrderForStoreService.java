package com.hryj.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hryj.cache.CacheGroup;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.cache.RedisService;
import com.hryj.cache.util.CalculateTaxUtil;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.constant.OrderConstants;
import com.hryj.entity.bo.cart.ShoppingCartRecord;
import com.hryj.entity.bo.order.*;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.inventory.request.ProductInventoryLockItem;
import com.hryj.entity.vo.inventory.request.ProductsInventoryLockRequestVO;
import com.hryj.entity.vo.inventory.response.ProductsInventoryLockResponseVO;
import com.hryj.entity.vo.order.*;
import com.hryj.entity.vo.order.request.*;
import com.hryj.entity.vo.order.response.*;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.response.ProductValidateResponseItem;
import com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO;
import com.hryj.entity.vo.product.crossborder.response.CrossBorderProductValidateResponseItem;
import com.hryj.entity.vo.promotion.activity.request.PartyProductActivityRequestVO;
import com.hryj.entity.vo.promotion.activity.response.OrderActivityInfoResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdsRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import com.hryj.entity.vo.user.UserIdentityCardVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.entity.vo.user.UserLoginVO;
import com.hryj.exception.BizException;
import com.hryj.feign.*;
import com.hryj.mapper.*;
import com.hryj.sms.AliYunSms;
import com.hryj.utils.CommonUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author 叶方宇
 * @className: OrderForStoreService
 * @description: 门店端订单服务
 * @create 2018/7/5 0005 21:18
 **/
@Slf4j
@Service
public class OrderForStoreService extends ServiceImpl<ShoppingCartMapper, ShoppingCartRecord> {


    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private OrderStatusRecordMapper orderStatusRecordMapper;

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private OrderDistributionProductMapper orderDistributionProductMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderForStoreMapper orderForStoreMapper;

    @Autowired
    private OrderReturnMapper orderReturnMapper;

    @Autowired
    private OrderSelfPickMapper orderSelfPickMapper;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderSelfPickService orderSelfPickService;

    @Autowired
    private  OrderCrossBorderService orderCrossBorderService;

    @Autowired
    private  OrderAdjustmentService orderAdjustmentService;

    @Autowired
    private OrderStatusRecordService orderStatusRecordService;

    @Autowired
    private  OrderProductService orderProductService;

    @Autowired
    private StoreFeignClient storeFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private RedisService redisService;

    @Autowired
    private StaffFeignClient staffFeignClien;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 取订单价格，优惠，成本
     * @param:
     * @create 2018-07-06 20:08
     **/
    public List<OrderCreateFromCartVO> findShoppingCartRecordListByIds(List<Long> ids) {
        return shoppingCartMapper.findShoppingCartRecordListByIdsCreateOrder(ids);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据购物车下单生成订单
     * @param:
     * @create 2018-07-05 20:22
     **/
    @Transactional
    public Result createOrderInfoDetails(OrderSaveRequestVO orderSaveRequestVO) {

        if (StringUtils.isEmpty(orderSaveRequestVO.getPay_price())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "总金额不能为空");
        }
        if (StringUtils.isEmpty(orderSaveRequestVO.getAddress())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "收货地址不能为空");
        }
        if (orderSaveRequestVO.getOrderItemList() == null || orderSaveRequestVO.getOrderItemList().size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品信息不能为空");
        }
        List<OrderSaveItemVO> orderItemList = orderSaveRequestVO.getOrderItemList();


        List<Long> idsList = new ArrayList<>();
        //每一个店铺的配送类型分割,通过购物车来分割
        Map<Long, String> delivery_typeMap = new HashMap<>();
        for (OrderSaveItemVO vo : orderItemList) {
            idsList.add(vo.getCart_record_id());
        }
        List<OrderCreateFromCartVO> cartVOlist = shoppingCartMapper.findShoppingCartRecordListByIdsCreateOrderNew(idsList);
        for (OrderSaveItemVO vo : orderItemList) {
            for (OrderCreateFromCartVO occ : cartVOlist) {
                if (vo.getCart_record_id().equals(occ.getCart_record_id())) {
                    vo.setActivity_id(occ.getActivity_id());
                    vo.setParty_id(occ.getParty_id());
                    vo.setProduct_id(occ.getProduct_id());
                    vo.setQuantity(occ.getQuantity());
                    vo.setShare_source(occ.getShare_source());
                    vo.setShare_user_id(occ.getShare_user_id());
                }
            }
        }
        orderSaveRequestVO.setOrderItemList(orderItemList);
        //代购判断
        if (orderSaveRequestVO.getUser_id() == null) {
            //表示非代下单，通过缓存取得用户信息
            UserLoginVO userLoginVO = LoginCache.getUserLoginVO(orderSaveRequestVO.getLogin_token());
            return createOrder(orderSaveRequestVO, userLoginVO, null);
        } else {
            //否则为代下单，需要调用接口查询用户信息
            UserInfoVO userInfoVO = userFeignClient.findUserInfoVOByUserId(orderSaveRequestVO.getUser_id(), null).getData();
            if (userInfoVO == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "无效用户");
            }

            UserLoginVO userLoginVO = new UserLoginVO();
            userLoginVO.setUser_id(userInfoVO.getUser_id());
            userLoginVO.setPhone_num(userInfoVO.getPhone_num());
            userLoginVO.setReceive_name(userInfoVO.getUser_name());
            userLoginVO.setReceive_address(userInfoVO.getReceive_address());
            userLoginVO.setUserAddress(userInfoVO.getUserAddress());
            StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(orderSaveRequestVO.getLogin_token());
            return createOrder(orderSaveRequestVO, userLoginVO, staffAppLoginVO);
        }
    }


    /**
     * @return
     * @author 白飞
     * @methodName:
     * @methodDesc:
     * @description: 通过商品、活动生成订单
     * @param:
     * @create 2018-09-18 12:00
     **/
    @Transactional
    public Result createOrder(final OrderSaveRequestVO orderSaveRequestVO, final UserLoginVO userLogin, StaffAppLoginVO staffAppLogin){

        if (StringUtils.isEmpty(orderSaveRequestVO.getPay_price())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "总金额不能为空");
        }
        if (StringUtils.isEmpty(orderSaveRequestVO.getAddress())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "收货地址不能为空");
        }
        if (orderSaveRequestVO.getOrderItemList() == null || orderSaveRequestVO.getOrderItemList().size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品信息不能为空");
        }

        List<Long> cartIds = Lists.newArrayList();
        //商品列表
        List<OrderSaveItemVO> orderItemList = orderSaveRequestVO.getOrderItemList();
        //配送时间
        Map<Long, Map<String, Long>> hopeDeliveryMap= Maps.newHashMap();
        //根据仓库或门店分组，配送类型
        Map<Long, String> deliveryTypeMap = Maps.newHashMap();
        //根据仓库或门店分组，订单类型
        Map<Long, String> orderTypeMap = Maps.newHashMap();
        //仓库或门店ID
        Set<Long> deptIds = Sets.newHashSet();
        //根据门店或仓库分组-分享用户
        Map<Long, Long> shareUserIdMap = Maps.newHashMap();
        Map<Long, String> shareUserSourceMap = Maps.newHashMap();
        Map<Long,Integer> quantityMap = Maps.newHashMap();
        for (OrderSaveItemVO orderSaveItem : orderItemList) {
            if(null != orderSaveItem.getHope_delivery_start_time() && orderSaveItem.getHope_delivery_start_time() > 0L && null != orderSaveItem.getHope_delivery_end_time() && orderSaveItem.getHope_delivery_end_time() > 0L){
                Map<String, Long> dateMap = Maps.newHashMap();
                dateMap.put("hope_delivery_start_time", orderSaveItem.getHope_delivery_start_time());
                dateMap.put("hope_delivery_end_time", orderSaveItem.getHope_delivery_end_time());
                hopeDeliveryMap.put(orderSaveItem.getParty_id(), dateMap);
            }
            if(orderSaveItem.getShare_user_id() != null && !StringUtils.isEmpty(orderSaveItem.getShare_source())){
                shareUserIdMap.put(orderSaveItem.getParty_id(), orderSaveItem.getShare_user_id());
                shareUserSourceMap.put(orderSaveItem.getParty_id(), orderSaveItem.getShare_source());
            }
            quantityMap.put(orderSaveItem.getCart_record_id(), orderSaveItem.getQuantity());
            cartIds.add(orderSaveItem.getCart_record_id());
            deptIds.add(orderSaveItem.getParty_id());
            deliveryTypeMap.put(orderSaveItem.getParty_id(), orderSaveItem.getDelivery_type());
        }
        //门店查询
        Callable<List<DeptIdByStoreOrWarehouseResponseVO>> callableStoreWarehouse = () -> {
            DeptIdsRequestVO deptIdsRequest = new  DeptIdsRequestVO();
            deptIdsRequest.setDept_ids(new ArrayList<>(deptIds));
            Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> storeResult = storeFeignClient.getAppDeptIdsByStoreOrWarehouseDet(deptIdsRequest);
            if(storeResult == null || storeResult.getCode() != CodeEnum.SUCCESS.getCode() || storeResult.getData() == null || storeResult.getData().getRecords() == null){
                return null;
            }
            return storeResult.getData().getRecords();
        };
        //商品校验
        Callable<List<ProductValidateResponseItem>> callableProducts = () -> {
            //验证商品请求VO
            ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
            List<ProductValidateItem> productValidateItems = Lists.newArrayList();
            //循环组装校验请求数据
            for (OrderSaveItemVO orderSaveItem : orderItemList) {
                ProductValidateItem productValidate = new ProductValidateItem();
                productValidate.setParty_id(orderSaveItem.getParty_id());
                productValidate.setProduct_id(orderSaveItem.getProduct_id());
                productValidate.setActivity_id(orderSaveItem.getActivity_id());
                productValidate.setRequired_min_inventory_quantity(orderSaveItem.getQuantity());
                productValidate.setFollow_value(orderSaveItem.getCart_record_id() + "");
                productValidateItems.add(productValidate);
            }
            productsValidateRequestVO.setReturn_tax_rate(true);
            productsValidateRequestVO.setProd_summary_list(productValidateItems);
            //发起校验
            Result<ProductsValidateResponseVO> productsValidateResult = productFeignClient.productsValidate(productsValidateRequestVO);
            if(productsValidateResult == null || productsValidateResult.getCode() != CodeEnum.SUCCESS.getCode() || productsValidateResult.getData() == null || productsValidateResult.getData().getProd_validate_result_list() == null){
                return null;
            }
            //获得校验结果
            return productsValidateResult.getData().getProd_validate_result_list();
        };
        FutureTask<List<DeptIdByStoreOrWarehouseResponseVO>> futureTaskStore = new FutureTask<>(callableStoreWarehouse);
        FutureTask<List<ProductValidateResponseItem>> futureTaskProduct = new FutureTask<>(callableProducts);
        ThreadUtil.execute(new Thread(futureTaskStore));
        ThreadUtil.execute(new Thread(futureTaskProduct));
        //门店信息集合
        Map<Long, DeptIdByStoreOrWarehouseResponseVO> storeMap = Maps.newHashMap();
        Map<Long, ProductValidateResponseItem> productValidateResponseItemMap = Maps.newHashMap();
        try {
            List<DeptIdByStoreOrWarehouseResponseVO>  storeList = futureTaskStore.get();
            Result result = this.storeBathValidate(deptIds, storeMap, storeList);
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
        //商品总金额
        //BigDecimal total_product_amount = new BigDecimal(0);
        //根据仓库门店分组发货仓
        Map<Long, String> deliveryWarehouseMap = Maps.newHashMap();
        Set<String> productTypeIds = Sets.newHashSet();
        //根据门店ID分组商品
        Map<Long, List<ProductValidateResponseItem>> productItemMapByPartyId = Maps.newHashMap();
        for (OrderSaveItemVO orderSaveItem : orderItemList){
            ProductValidateResponseItem productValidateResponseItem = productValidateResponseItemMap.get(orderSaveItem.getCart_record_id());
            if(orderSaveItem.getActivity_id() != null && orderSaveItem.getActivity_id() > 0L){
                if(productValidateResponseItem.getThis_moment_sale_price().compareTo(new BigDecimal(0)) <= 0){
                    return new Result(CodeEnum.FAIL_PARAMCHECK,"商品价格已更新，请重新购买");
                }
            }
            String productType = OrderConstants.ORDER_TYPE_ALL_MAP.get(productValidateResponseItem.getProduct_type_id());
            if(StringUtils.isEmpty(productType)){
                return new Result(CodeEnum.FAIL_PARAMCHECK, "订单创建失败！商品类型不能为空");
            }
            productTypeIds.add(productValidateResponseItem.getProduct_type_id());
            orderTypeMap.put(productValidateResponseItem.getParty_id(), productValidateResponseItem.getProduct_type_id());
            //根据仓库ID分组商品
            if(productItemMapByPartyId.get(orderSaveItem.getParty_id()) == null){
                List<ProductValidateResponseItem> productValidateResponseItems = Lists.newArrayList();
                productValidateResponseItems.add(productValidateResponseItem);
                productItemMapByPartyId.put(orderSaveItem.getParty_id(), productValidateResponseItems);
            }else{
                productItemMapByPartyId.get(orderSaveItem.getParty_id()).add(productValidateResponseItem);
            }
            if(!StringUtils.isEmpty(productValidateResponseItem.getChannel()) && !StringUtils.isEmpty(productValidateResponseItem.getChannel_name())){
                deliveryWarehouseMap.put(productValidateResponseItem.getParty_id(), productValidateResponseItem.getChannel() + "," + productValidateResponseItem.getChannel_name());
            }
        }

        //不允许多类型商品提交
        if(productTypeIds.size() > 1){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单创建失败！只能购买同一类型商品");
        }

        //多个订单编号，用逗号分隔
        String order_sns = "";
        BigDecimal totalPayAmount = new BigDecimal(0);
        //订单集合
        List<OrderInfo> orderInfos = Lists.newArrayList();
        //保存订单状态
        List<OrderStatusRecord> orderStatusRecords = Lists.newArrayList();
        //跨境订单
        List<OrderCrossBorder> orderCrossBorders = Lists.newArrayList();
        //订单明细
        List<OrderProduct> orderProducts = Lists.newArrayList();
        //调整金额
        List<OrderAdjustment> orderAdjustments = Lists.newArrayList();
        //循环店铺组装订单报文
        for(Long partyId : deptIds){
            DeptIdByStoreOrWarehouseResponseVO storeInfo = storeMap.get(partyId);
            //配送类型
            String deliveryType = deliveryTypeMap.get(partyId);
            List<ProductValidateResponseItem> productValidateResponseItems = productItemMapByPartyId.get(partyId);
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setId(IdWorker.getId());
            orderInfo.setApp_key(orderSaveRequestVO.getApp_key());
            orderInfo.setParty_id(storeInfo.getDept_id());
            orderInfo.setParty_name(storeInfo.getDept_name());
            orderInfo.setParty_path(storeInfo.getDept_path());
            orderInfo.setParty_type(storeInfo.getDept_type());
            orderInfo.setOrder_date(new Date());
            orderInfo.setOrder_num(CommonUtil.getDateRandom());
            orderInfo.setOrder_type(OrderConstants.ORDER_TYPE_ALL_MAP.get(orderTypeMap.get(partyId)));
            orderInfo.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
            orderInfo.setDelivery_type(deliveryType);
            String deliveryWarehouse = deliveryWarehouseMap.get(partyId);
            orderInfo.setDelivery_warehouse(!StringUtils.isEmpty(deliveryWarehouse) ? deliveryWarehouse.split(",")[0] : null);
            orderInfo.setDelivery_warehouse_name(!StringUtils.isEmpty(deliveryWarehouse) ? deliveryWarehouse.split(",")[1] : null);
            orderInfo.setSales_channel(orderSaveRequestVO.getCall_source());
            orderInfo.setUser_id(userLogin != null ? userLogin.getUser_id() : orderSaveRequestVO.getUser_id());
            //设置用户收货地址******20180930 luoqh add start***************
            orderInfo.setUser_name(userLogin.getReceive_name());
            orderInfo.setUser_address(userLogin.getReceive_address());
            orderInfo.setUser_phone(userLogin.getPhone_num());
            orderInfo.setReceive_phone(userLogin.getReceive_phone());
            //设置用户收货地址******20180930 luoqh add end***************
            if(userLogin.getUserAddress() != null){
                orderInfo.setProvince_id(userLogin.getUserAddress().getProvince_code());
                orderInfo.setCity_id(userLogin.getUserAddress().getCity_code());
                orderInfo.setArea_id(userLogin.getUserAddress().getArea_code());
                String areaInfo = userLogin.getUserAddress().getProvince_name() + " " + userLogin.getUserAddress().getCity_name() + " " + userLogin.getUserAddress().getArea_name();
                orderInfo.setArea_info(areaInfo);
            }
            orderInfo.setShare_user_id(shareUserIdMap.get(partyId));
            orderInfo.setShare_source(shareUserSourceMap.get(partyId));
            orderInfo.setOrder_remark("");
            orderInfo.setHelp_staff_id(staffAppLogin != null ? staffAppLogin.getStaff_id() : null);
            orderInfo.setHelp_store_id(staffAppLogin != null && staffAppLogin.getDeptGroup() != null ? staffAppLogin.getDeptGroup().getId() : null);
            if (deliveryType.equals(SysCodeEnmu.DELIVERYTYPE_02.getCodeValue())) {
                Map<String,Long> hopeDeliveryDateMap = hopeDeliveryMap.get(partyId);
                if(null != hopeDeliveryDateMap){
                    orderInfo.setHope_delivery_start_time(DateUtil.date(hopeDeliveryDateMap.get("hope_delivery_start_time")));
                    orderInfo.setHope_delivery_end_time((DateUtil.date(hopeDeliveryDateMap.get("hope_delivery_end_time"))));
                }
            }

            BigDecimal orderAmount = new BigDecimal(0);
            BigDecimal discountAmount = new BigDecimal(0);
            BigDecimal totalCost = new BigDecimal(0);
            BigDecimal partyTax = new BigDecimal(0);
            //商品明细
            for(ProductValidateResponseItem productItem : productValidateResponseItems){
                if(productItem.getActivity_id() != null && productItem.getActivity_id() > 0L && productItem.getThis_moment_sale_price() != null){
                    //取得优惠
                    BigDecimal discount = NumberUtil.sub(productItem.getNormal_price(), productItem.getThis_moment_sale_price());
                    discount = discount.compareTo(new BigDecimal(0)) < 0 ? new BigDecimal(0) : discount;
                    discountAmount = NumberUtil.add(discountAmount, NumberUtil.mul(discount, productItem.getRequired_min_inventory_quantity()));
                }
                //订单商品信息
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setId(IdWorker.getId());
                orderProduct.setOrder_id(orderInfo.getId());
                orderProduct.setActivity_id(productItem.getActivity_id());
                orderProduct.setCost_price(productItem.getCost_price());
                orderProduct.setOrg_price(productItem.getNormal_price());
                orderProduct.setParty_id(orderInfo.getParty_id());
                orderProduct.setPrice_modify_flag(0);
                orderProduct.setProduct_category_id(productItem.getProd_cate_id());
                orderProduct.setList_image_url(productItem.getList_image_url());
                orderProduct.setProduct_type_id(productItem.getProduct_type_id());
                orderProduct.setProduct_id(productItem.getProduct_id());
                orderProduct.setProduct_name(productItem.getProduct_name());
                orderProduct.setQuantity(productItem.getRequired_min_inventory_quantity());
                orderProduct.setActual_price(productItem.getThis_moment_sale_price());
                orderProduct.setThird_sku_id(null != productItem && productItem.getCrossBorderProductValidateResponseItem() != null ? productItem.getCrossBorderProductValidateResponseItem().getThird_sku_id() : null);
                orderProduct.setHs_code(null != productItem && productItem.getCrossBorderProductValidateResponseItem() != null ? productItem.getCrossBorderProductValidateResponseItem().getHs_code() : null);
                totalCost = NumberUtil.add(totalCost, NumberUtil.mul(orderProduct.getCost_price(), orderProduct.getQuantity()));
                orderAmount = NumberUtil.add(orderAmount, NumberUtil.mul(orderProduct.getActual_price(), orderProduct.getQuantity()));
                //跨境订单,计算税
                if(ArrayUtils.contains(OrderConstants.CROSS_BORDER_ORDER_TYPES, orderInfo.getOrder_type())){
                    CrossBorderProductValidateResponseItem crossBorderProduct = productItem.getCrossBorderProductValidateResponseItem();
                    if(crossBorderProduct == null){
                        throw new BizException("抱歉！系统参数异常，无法继续");
                    }
                    BigDecimal tax_amount = CalculateTaxUtil.calculateTax(orderProduct.getActual_price(), orderProduct.getQuantity(), crossBorderProduct.getUnit_1(), crossBorderProduct.getUnit_2(), crossBorderProduct.getIncrement_tax(), crossBorderProduct.getConsume_tax());
                    if(tax_amount.compareTo(new BigDecimal(0)) <= 0){
                        throw new BizException("抱歉！税金计算异常，无法继续");
                    }
                    partyTax = NumberUtil.add(partyTax, tax_amount);
                    //订单调整金额数据
                    tax_amount = NumberUtil.roundHalfEven(tax_amount, 2);
                    OrderAdjustment orderAdjustment = new OrderAdjustment();
                    orderAdjustment.setId(IdWorker.getId());
                    orderAdjustment.setOrder_id(orderInfo.getId());
                    orderAdjustment.setOrder_item_id(orderProduct.getId());
                    orderAdjustment.setAdjustment_type_id(OrderConstants.CROSS_BORDER_PROD_TAX);
                    orderAdjustment.setAmount(tax_amount);
                    orderAdjustment.setUser_id(userLogin != null ? userLogin.getUser_id() : null);
                    orderAdjustment.setUser_name(userLogin != null ? userLogin.getReceive_name() : null);
                    orderAdjustment.setSource_refrence_id(crossBorderProduct.getTax_id());
                    orderAdjustment.setDescription("单个商品的综合税费");
                    orderAdjustments.add(orderAdjustment);
                }
                orderProducts.add(orderProduct);
            }
            partyTax = NumberUtil.roundHalfEven(partyTax,2);
            totalCost = NumberUtil.roundHalfEven(totalCost, 2);
            orderAmount = NumberUtil.roundHalfEven(orderAmount, 2);
            discountAmount = NumberUtil.roundHalfEven(discountAmount, 2);
            BigDecimal order_profit = NumberUtil.roundHalfEven(NumberUtil.sub(orderAmount, totalCost), 2);
            orderInfo.setTotal_cost(totalCost);
            orderInfo.setOrder_amt(orderAmount);
            orderInfo.setDiscount_amt(discountAmount);
            orderInfo.setOrder_profit(order_profit);
            orderAmount = NumberUtil.roundHalfEven(NumberUtil.add(partyTax, orderAmount),  2);
            orderInfo.setPay_amt(orderAmount);
            orderInfos.add(orderInfo);
            totalPayAmount = NumberUtil.add(totalPayAmount, orderInfo.getPay_amt());
            order_sns += "".equals(order_sns) ? orderInfo.getOrder_num() : "," + orderInfo.getOrder_num();
            //订单状态信息
            OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
            orderStatusRecord.setId(IdWorker.getId());
            orderStatusRecord.setOrder_id(orderInfo.getId());
            orderStatusRecord.setOperator_id(userLogin.getUser_id());
            orderStatusRecord.setOperator_name(userLogin.getReceive_name());
            //等字典表，订单当前处于哪个操作流程
            orderStatusRecord.setChange_reason("用户下单");
            orderStatusRecord.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
            orderStatusRecord.setOperator_user_type(staffAppLogin == null ? SysCodeEnmu.CHANGEUSERTYPE_01.getCodeValue() : SysCodeEnmu.CHANGEUSERTYPE_02.getCodeValue());
            orderStatusRecords.add(orderStatusRecord);

            //保存跨境订单信息
            if(ArrayUtils.contains(OrderConstants.CROSS_BORDER_ORDER_TYPES, orderInfo.getOrder_type())){
                //跨境大于2000，返回
                String max_amount = CodeCache.getNameByValue("CrossBorderOrderLimitGroup","CrossBorderOrderMaxAmount");
                if(StringUtils.isEmpty(max_amount)){
                    return new Result(CodeEnum.FAIL_PARAMCHECK.getCode(), "跨境商品订单限额参数异常", 11040);
                }
                if(orderInfo.getOrder_amt().compareTo(new BigDecimal(max_amount)) >= 0){
                    return new Result(CodeEnum.FAIL_PARAMCHECK.getCode(), "跨境订单金额不能大于￥" + max_amount, 11144);
                }
                if(StringUtil.isEmpty(orderSaveRequestVO.getBuyer_name()) || StringUtil.isEmpty(orderSaveRequestVO.getBuyer_id_card())){
                    return new Result(CodeEnum.FAIL_PARAMCHECK.getCode(), "请填写订购人和身份证号码", 11040);
                }
                OrderCrossBorder orderCrossBorder = new OrderCrossBorder();
                orderCrossBorder.setOrder_id(orderInfo.getId());
                orderCrossBorder.setThird_order_status(OrderConstants.ThirdOrderStatus.waitDeclare.ordinal());
                orderCrossBorder.setSubscriber_name(orderSaveRequestVO.getBuyer_name());
                orderCrossBorder.setSubscriber_id_card(orderSaveRequestVO.getBuyer_id_card());
                orderCrossBorder.setCancel_count(0);
                orderCrossBorders.add(orderCrossBorder);
            }
        }
        totalPayAmount = NumberUtil.roundHalfEven(totalPayAmount, 2);
        BigDecimal payAmount = NumberUtil.roundHalfEven(orderSaveRequestVO.getPay_price(),2);
        if(totalPayAmount.compareTo(payAmount) != 0){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"商品价格已更新，请重新购买");
        }
        //批量保存订单
        this.orderService.insertBatch(orderInfos);
        //批量保存订单商品明细
        this.orderProductService.insertBatch(orderProducts);
        //批量保存跨境订单扩展信息
        if(orderCrossBorders.size() > 0){
            this.orderCrossBorderService.insertBatch(orderCrossBorders);
        }
        //批量保存订单调整信息
        if(orderAdjustments.size() > 0){
            this.orderAdjustmentService.insertBatch(orderAdjustments);
        }
        //批量保存订单状态信息
        orderStatusRecordService.insertBatch(orderStatusRecords);

        //异步保存订购人
        if(!StringUtils.isEmpty(orderSaveRequestVO.getBuyer_name()) && !StringUtils.isEmpty(orderSaveRequestVO.getBuyer_id_card())){
            ThreadUtil.execAsync(new Runnable() {
                @Override
                public void run() {
                    UserIdentityCardVO userIdentityCard = new UserIdentityCardVO();
                    userIdentityCard.setId(IdWorker.getId());
                    userIdentityCard.setUser_id(userLogin.getUser_id());
                    userIdentityCard.setIdentity_card(orderSaveRequestVO.getBuyer_id_card());
                    userIdentityCard.setTrue_name(orderSaveRequestVO.getBuyer_name());
                    userFeignClient.saveUserIdentityCard(userIdentityCard);
                }
            });
        }
        this.shoppingCartMapper.deleteShoppingCartByIds(cartIds);
        try {
            //库存锁定
            ProductsInventoryLockRequestVO productsInventoryLockRequestVO = new ProductsInventoryLockRequestVO();
            productsInventoryLockRequestVO.setLock_model("sub");
            List<ProductInventoryLockItem> productInventoryLockItems = new ArrayList<>();
            for (OrderSaveItemVO orderSaveItem : orderItemList) {
                ProductInventoryLockItem productInventoryLockItem = new ProductInventoryLockItem();
                productInventoryLockItem.setParty_id(orderSaveItem.getParty_id());
                productInventoryLockItem.setProduct_id(orderSaveItem.getProduct_id());
                productInventoryLockItem.setLock_quantity(orderSaveItem.getQuantity());
                productInventoryLockItems.add(productInventoryLockItem);
            }
            productsInventoryLockRequestVO.setLock_items(productInventoryLockItems);
            Result<ProductsInventoryLockResponseVO> result = productFeignClient.lockProductInventory(productsInventoryLockRequestVO);
            if (result == null || !result.isSuccess() || result.getData() == null || !result.isSuccess()) {
                throw new BizException("库存不足");
            }
        } catch (Exception e) {
            throw new BizException("库存不足");
        }
        return new Result(CodeEnum.SUCCESS, orderService.encapsulationOrderToPayResponseVO(order_sns, totalPayAmount.toString()));
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
     * 该方法已废弃
     *
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 通过商品、活动生成订单
     * @param:
     * @create 2018-07-05 20:22
     **/
    @Transactional
    public Result createOrderInfoDetailsForProduct(final OrderSaveRequestVO orderSaveRequestVO, final UserLoginVO userLoginVO, StaffAppLoginVO staffAppLoginVO) {

        if (StringUtils.isEmpty(orderSaveRequestVO.getPay_price())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "总金额不能为空");
        }
        if (StringUtils.isEmpty(orderSaveRequestVO.getAddress())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "收货地址不能为空");
        }
        if (orderSaveRequestVO.getOrderItemList() == null || orderSaveRequestVO.getOrderItemList().size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "商品信息不能为空");
        }
        List<OrderSaveItemVO> orderItemList = orderSaveRequestVO.getOrderItemList();
        Map<Long, Long[]> dateMap = new HashMap<>();
        List<PartyProductActivityRequestVO> partyProductActivityRequestVOList = new ArrayList<>();
        Long date[] = null;
        //商品参数
        ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
        List<ProductValidateItem> prod_summary_list = new ArrayList<>();
        //门店参数
        DeptIdsRequestVO deptIdsRequestVO = new DeptIdsRequestVO();
        List<Long> dept_ids = new ArrayList<>();
        deptIdsRequestVO.setDept_ids(dept_ids);
        //配送类型
        Map<Long, String> deliveryTypeMap = Maps.newHashMap();
        //每一个店铺的配送类型分割
        for (OrderSaveItemVO vo : orderItemList) {
            PartyProductActivityRequestVO ppa = new PartyProductActivityRequestVO();
            ppa.setActivity_id(vo.getActivity_id());
            ppa.setParty_id(vo.getParty_id());
            ppa.setProduct_id(vo.getProduct_id());
            if (vo.getActivity_id() != null) {
                partyProductActivityRequestVOList.add(ppa);
            }
            if (!dateMap.containsKey(vo.getParty_id())) {
                date = new Long[2];
                date[0] = vo.getHope_delivery_start_time();
                date[1] = vo.getHope_delivery_end_time();
                dateMap.put(vo.getParty_id(), date);
                dept_ids.add(vo.getParty_id());
                deliveryTypeMap.put(vo.getParty_id(), vo.getDelivery_type());
            }
            ProductValidateItem item = new ProductValidateItem();
            item.setActivity_id(vo.getActivity_id());
            item.setParty_id(vo.getParty_id());
            item.setProduct_id(vo.getProduct_id());
            prod_summary_list.add(item);
            productsValidateRequestVO.setReturn_tax_rate(true);
            productsValidateRequestVO.setProd_summary_list(prod_summary_list);
        }
        //获取验证后的数据(商品信息)
        Result<ProductsValidateResponseVO> proresult = productFeignClient.productsValidate(productsValidateRequestVO);
        //商品验证
        Result result = productsValidate(proresult);
        if (result.getCode() != CodeEnum.SUCCESS.getCode()) {
            return result;
        }
        //根据商品ID分组
        Map<Long, ProductValidateResponseItem> productValidateResponseItemMap = Maps.newHashMap();
        //根据商品类型ID分组
        Set<String> productTypeIds = Sets.newHashSet();
        String productType = null;
        //是否跨境商品
        boolean is_bonded = false;
        List<ProductValidateResponseItem> productValidateResponseItems = proresult.getData().getProd_validate_result_list();
        for(ProductValidateResponseItem productValidateResponseItem : productValidateResponseItems){

            productValidateResponseItemMap.put(productValidateResponseItem.getProduct_id(), productValidateResponseItem);
            productType = OrderConstants.CROSS_BORDER_ORDER_TYPE_MAP.get(productValidateResponseItem.getProduct_type_id());
            if(StringUtils.isEmpty(productType)){
                return new Result(CodeEnum.FAIL_PARAMCHECK, "订单创建失败！商品类型不能为空");
            }
            productTypeIds.add(productType);
            if(ArrayUtils.contains(OrderConstants.CROSS_BORDER_ORDER_TYPES, productValidateResponseItem.getProduct_type_id())){
                is_bonded = true;
            }
        }
        if(productTypeIds.size() > 1){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单创建失败！只能购买同一类型商品");
        }
        //从购物车处获取订单价格，成本
        List<OrderCreateFromCartVO> cartVOlist = combinationProductList(orderItemList, proresult.getData().getProd_validate_result_list());

        if (partyProductActivityRequestVOList.size() > 0) {
            //获取活动信息
            List<OrderActivityInfoResponseVO> activityMap = activityFeignClient.orderProductJoinActivityInfo(partyProductActivityRequestVOList).getData();
            if (activityMap == null || activityMap.size() == 0) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "订单创建失败！活动数据有误");
            }
            combinationActivityList(cartVOlist, activityMap);
        }

        //获取门店信息
        List<DeptIdByStoreOrWarehouseResponseVO> depList = storeFeignClient.getAppDeptIdsByStoreOrWarehouseDet(deptIdsRequestVO).getData().getRecords();
        if (depList == null || depList.size() < 1) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单创建失败！门店信息异常");
        }
        combinationPartyList(cartVOlist, depList);

        if (cartVOlist == null || cartVOlist.size() < 1) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单创建失败！数据查询异常");
        }

        //根据门店ID分组分割订单，计算订单优惠，成本，价格
        Map<Long, OrderInfo> orderInfoMap = Maps.newHashMap();
        Map<Long, OrderStatusRecord> orderStatusRecordMap = Maps.newHashMap();
        Map<Long, List<OrderProduct>> productListMap = Maps.newHashMap();

        for (OrderCreateFromCartVO orderCreateFromCart : cartVOlist) {

            //多个商品为一个订单，有多个订单商品信息，生成对应的订单状态信息
            if (!orderInfoMap.containsKey(orderCreateFromCart.getParty_id())) {
                //不存在则新增订单，生成对应商品信息列表，并生成对应的订单状态
                //订单信息
                OrderInfo orderInfo = new OrderInfo();
                String delivery_type = deliveryTypeMap.get(orderCreateFromCart.getParty_id());
                orderInfo.setDelivery_type(delivery_type);
                //判断属于一般订单、跨境订单类型
                orderInfo.setOrder_type(productType);

                orderInfo.setApp_key(orderSaveRequestVO.getApp_key());
                orderInfo.setParty_name(orderCreateFromCart.getParty_name());
                orderInfo.setParty_path(orderCreateFromCart.getParty_path());
                orderInfo.setParty_type(orderCreateFromCart.getParty_type());
                if (delivery_type.equals(SysCodeEnmu.DELIVERYTYPE_02.getCodeValue())) {
                    orderInfo.setHope_delivery_start_time(DateUtil.date(dateMap.get(orderCreateFromCart.getParty_id())[0]));
                    orderInfo.setHope_delivery_end_time((DateUtil.date(dateMap.get(orderCreateFromCart.getParty_id())[1])));
                }
                orderInfo.setShare_source(orderCreateFromCart.getShare_source());
                orderInfo.setShare_user_id(orderCreateFromCart.getShare_user_id());
                orderInfo.setOrder_date(new Date());
                orderInfo.setOrder_num(CommonUtil.getDateRandom());
                orderInfo.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
                orderInfo.setParty_id(orderCreateFromCart.getParty_id());
                orderInfo.setSales_channel(orderSaveRequestVO.getCall_source());
                orderInfo.setUser_id(userLoginVO.getUser_id());
                orderInfo.setUser_name(userLoginVO.getReceive_name());
                orderInfo.setUser_address(userLoginVO.getReceive_address());
                orderInfo.setUser_phone(userLoginVO.getPhone_num());
                if(userLoginVO.getUserAddress() != null){
                    orderInfo.setProvince_id(userLoginVO.getUserAddress().getProvince_code());
                    orderInfo.setCity_id(userLoginVO.getUserAddress().getCity_code());
                    orderInfo.setArea_id(userLoginVO.getUserAddress().getArea_code());
                    String areaInfo = userLoginVO.getUserAddress().getProvince_name() + " " + userLoginVO.getUserAddress().getCity_name() + " " + userLoginVO.getUserAddress().getArea_name();
                    orderInfo.setArea_info(areaInfo);
                }
                orderInfo.setHelp_staff_id(staffAppLoginVO != null ? staffAppLoginVO.getStaff_id() : null);
                orderInfo.setHelp_store_id(staffAppLoginVO != null && staffAppLoginVO.getDeptGroup() != null ? staffAppLoginVO.getDeptGroup().getId() : null);
                orderInfoMap.put(orderCreateFromCart.getParty_id(), orderInfo);
                //订单状态信息
                OrderStatusRecord orderStatusRecord = new OrderStatusRecord();
                orderStatusRecord.setOperator_id(userLoginVO.getUser_id());
                orderStatusRecord.setOperator_name(userLoginVO.getReceive_name());
                //等字典表，订单当前处于哪个操作流程
                orderStatusRecord.setChange_reason("用户下单");
                orderStatusRecord.setOrder_status(SysCodeEnmu.ORDERSTATUS_01.getCodeValue());
                orderStatusRecord.setOperator_user_type(staffAppLoginVO == null ? SysCodeEnmu.CHANGEUSERTYPE_01.getCodeValue() : SysCodeEnmu.CHANGEUSERTYPE_02.getCodeValue());
                orderStatusRecordMap.put(orderCreateFromCart.getParty_id(), orderStatusRecord);
            }
            //订单商品信息
            OrderProduct product = new OrderProduct();
            product.setActivity_id(orderCreateFromCart.getActivity_id());
            product.setCost_price(orderCreateFromCart.getCost_price());
            product.setOrg_price(orderCreateFromCart.getSale_price());
            product.setParty_id(orderCreateFromCart.getParty_id());
            product.setPrice_modify_flag(0);
            ProductValidateResponseItem productValidateResponseItem = productValidateResponseItemMap.get(orderCreateFromCart.getProduct_id());
            product.setThird_sku_id(null != productValidateResponseItem && productValidateResponseItem.getCrossBorderProductValidateResponseItem() != null ? productValidateResponseItem.getCrossBorderProductValidateResponseItem().getThird_sku_id() : null);
            product.setProduct_category_id(orderCreateFromCart.getProd_cate_id());
            product.setList_image_url(orderCreateFromCart.getList_image_url());
            product.setProduct_id(orderCreateFromCart.getProduct_id());
            product.setProduct_name(orderCreateFromCart.getProduct_name());
            product.setQuantity(orderCreateFromCart.getQuantity());
            product.setActual_price(orderCreateFromCart.getActivity_price() == null ? orderCreateFromCart.getSale_price() : orderCreateFromCart.getActivity_price());
            if (productListMap.containsKey(orderCreateFromCart.getParty_id())) {
                //如果商品对应的店铺已经存在
                productListMap.get(orderCreateFromCart.getParty_id()).add(product);
            } else {//否则
                List<OrderProduct> orderProducts = new ArrayList<>();
                orderProducts.add(product);
                productListMap.put(orderCreateFromCart.getParty_id(), orderProducts);
            }
        }

        //总支付金额
        BigDecimal pay_price = BigDecimal.ZERO;
        //总成本价格
        BigDecimal cost_price = BigDecimal.ZERO;
        //总优惠金额
        BigDecimal discount_amt = BigDecimal.ZERO;
        //订单总金额
        BigDecimal order_amt = BigDecimal.ZERO;
        StringBuffer stringBuffer = new StringBuffer();
        Map<Long, BigDecimal> partyTaxMap = Maps.newHashMap();
        //根据店铺分组后循环，计算订单价格信息
        for (Long partyId : productListMap.keySet()) {
            OrderInfo orderInfo = orderInfoMap.get(partyId);
            //循环每一件商品
            List<OrderProduct> orderProducts = productListMap.get(partyId);
            //循环所有商品，计算商品总价，总成本，总毛利，总优惠，总实付
            BigDecimal partyTax = new BigDecimal(0);
            for (OrderProduct orderProduct : orderProducts) {
                //商品成本乘以数量
                //成本价格
                BigDecimal cost_price_one = NumberUtil.mul(orderProduct.getCost_price(), new BigDecimal(orderProduct.getQuantity()));
                //总成本累加
                cost_price = NumberUtil.add(cost_price, cost_price_one);
                //有活动,计算优惠价格
                if (orderProduct.getActivity_id() != null) {
                    //单件商品的优惠金额，优惠金额等于原售价减去活动金额(实际价格)，计算出单件商品优惠
                    //优惠金额
                    BigDecimal discount_amt_one = NumberUtil.sub(orderProduct.getOrg_price(), orderProduct.getActual_price());
                    //单件商品优惠乘以数量等于该商品优惠
                    discount_amt_one = NumberUtil.mul(discount_amt_one, new BigDecimal(orderProduct.getQuantity()));
                    //累加
                    discount_amt = NumberUtil.add(discount_amt, discount_amt_one);
                }
                //商品原价乘以数量
                BigDecimal order_amt_one = NumberUtil.mul(orderProduct.getOrg_price(), new BigDecimal(orderProduct.getQuantity()));
                //总原价累加
                order_amt = NumberUtil.add(order_amt, order_amt_one);
                //跨境订单,计算税
                if(is_bonded){
                    ProductValidateResponseItem productValidateResponseItem = productValidateResponseItemMap.get(orderProduct.getProduct_id());
                    if(null == productValidateResponseItem){
                        throw new BizException("抱歉！系统参数异常，无法继续");
                    }
                    CrossBorderProductValidateResponseItem crossBorderProduct = productValidateResponseItem.getCrossBorderProductValidateResponseItem();
                    if(crossBorderProduct == null){
                        throw new BizException("抱歉！系统参数异常，无法继续");
                    }
                    BigDecimal tax_amount = CalculateTaxUtil.calculateTax(orderProduct.getActual_price(), orderProduct.getQuantity(), crossBorderProduct.getUnit_1(), crossBorderProduct.getUnit_2(), crossBorderProduct.getIncrement_tax(), crossBorderProduct.getConsume_tax());
                    if(tax_amount.compareTo(new BigDecimal(0)) <= 0){
                        throw new BizException("抱歉！税金计算异常，无法继续");
                    }
                    partyTax = NumberUtil.add(partyTax, tax_amount);
                }
            }
            partyTax = NumberUtil.roundHalfEven(partyTax, 2);
            partyTaxMap.put(partyId, partyTax);
            //所有价格赋值
            orderInfo.setTotal_cost(cost_price);//订单成本
            orderInfo.setDiscount_amt(discount_amt);//订单优惠金额
            orderInfo.setOrder_amt(order_amt.subtract(orderInfo.getDiscount_amt()));//订单金额
            orderInfo.setOrder_profit(orderInfo.getOrder_amt().subtract(orderInfo.getTotal_cost()));//订单毛利
            //加上税费
            BigDecimal pay_amount = NumberUtil.add(partyTax, orderInfo.getOrder_amt());
            orderInfo.setPay_amt(NumberUtil.roundHalfEven(pay_amount, 2));//订单实付金额
            //总支付金额
            pay_price = NumberUtil.add(pay_price, orderInfo.getPay_amt());
            //加上税费
            //pay_price = NumberUtil.add(pay_price, partyTax);
            orderInfoMap.put(partyId, orderInfo);
            stringBuffer.append(orderInfo.getOrder_num());
            stringBuffer.append(",");
            //归零
            cost_price = BigDecimal.ZERO;//总成本价格
            discount_amt = BigDecimal.ZERO;//总优惠金额
            order_amt = BigDecimal.ZERO;//订单总金额
        }
        if (NumberUtil.roundHalfEven(pay_price, 2).compareTo(NumberUtil.roundHalfEven(orderSaveRequestVO.getPay_price(), 2)) != 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK.getCode(), "商品价格发生变动", 11040);
        }

        //生成对应订单信息
        for (Long partyId : orderInfoMap.keySet()) {
            OrderInfo orderInfo = orderInfoMap.get(partyId);
            orderInfoMapper.insert(orderInfo);//保存

            //给订单状态赋值订单id
            OrderStatusRecord orderStatusRecord = orderStatusRecordMap.get(partyId);
            orderStatusRecord.setOrder_id(orderInfo.getId());
            //订单状态保存
            orderStatusRecordMapper.insert(orderStatusRecord);

            //保存跨境订单信息
            if(is_bonded){
                //跨境大于2000，抛出异常
                String max_amount = CodeCache.getNameByValue("CrossBorderOrderLimitGroup","CrossBorderOrderMaxAmount");
                if(StringUtils.isEmpty(max_amount)){
                    return new Result(CodeEnum.FAIL_PARAMCHECK.getCode(), "跨境商品订单限额参数异常", 11040);
                }
                if(orderInfo.getOrder_amt().compareTo(new BigDecimal(max_amount)) >= 0){
                    return new Result(CodeEnum.FAIL_PARAMCHECK.getCode(), "跨境订单不能大于￥" + max_amount, 11144);
                }
                OrderCrossBorder orderCrossBorder = new OrderCrossBorder();
                orderCrossBorder.setOrder_id(orderInfo.getId());
                orderCrossBorder.setThird_order_status(OrderConstants.ThirdOrderStatus.waitDeclare.ordinal());
                orderCrossBorder.setSubscriber_name(orderSaveRequestVO.getBuyer_name());
                orderCrossBorder.setSubscriber_id_card(orderSaveRequestVO.getBuyer_id_card());
                orderCrossBorderService.insert(orderCrossBorder);
            }
            //保存订单商品信息
            List<OrderProduct> products = new ArrayList<>();
            List<OrderAdjustment> orderAdjustments = Lists.newArrayList();
            List<OrderProduct> orderProducts = productListMap.get(partyId);
            for (OrderProduct orderProduct : orderProducts) {
                orderProduct.setOrder_id(orderInfo.getId());
                orderProduct.setId(IdWorker.getId());
                products.add(orderProduct);
                if(is_bonded){
                    OrderAdjustment orderAdjustment = new OrderAdjustment();
                    orderAdjustment.setId(IdWorker.getId());
                    orderAdjustment.setOrder_id(orderInfo.getId());
                    orderAdjustment.setOrder_item_id(orderProduct.getId());
                    orderAdjustment.setAdjustment_type_id(OrderConstants.CROSS_BORDER_PROD_TAX);
                    //订单调整金额数据
                    ProductValidateResponseItem productValidateResponseItem = productValidateResponseItemMap.get(orderProduct.getProduct_id());
                    CrossBorderProductValidateResponseItem crossBorderProduct = productValidateResponseItem.getCrossBorderProductValidateResponseItem();
                    BigDecimal tax_amount = CalculateTaxUtil.calculateTax(orderProduct.getActual_price(), orderProduct.getQuantity(), crossBorderProduct.getUnit_1(), crossBorderProduct.getUnit_2(), crossBorderProduct.getIncrement_tax(), crossBorderProduct.getConsume_tax());
                    tax_amount = NumberUtil.roundHalfEven(tax_amount, 2);
                    orderAdjustment.setAmount(tax_amount);
                    orderAdjustment.setUser_id(userLoginVO != null ? userLoginVO.getUser_id() : null);
                    orderAdjustment.setUser_name(userLoginVO != null ? userLoginVO.getReceive_name() : null);
                    orderAdjustment.setSource_refrence_id(productValidateResponseItem.getCrossBorderProductValidateResponseItem().getTax_id());
                    orderAdjustment.setDescription("单个商品的综合税费");
                    orderAdjustments.add(orderAdjustment);
                }
            }
            this.orderAdjustmentService.insertBatch(orderAdjustments);
            //批量插入
            orderProductMapper.insertBatch(products);
        }
        //如果订购人不为空，保存
        if(!StringUtils.isEmpty(orderSaveRequestVO.getBuyer_name()) && !StringUtils.isEmpty(orderSaveRequestVO.getBuyer_id_card())){
            UserIdentityCardVO userIdentityCard = new UserIdentityCardVO();
            userIdentityCard.setId(IdWorker.getId());
            userIdentityCard.setUser_id(userLoginVO.getUser_id());
            userIdentityCard.setIdentity_card(orderSaveRequestVO.getBuyer_id_card());
            userIdentityCard.setTrue_name(orderSaveRequestVO.getBuyer_name());
            userFeignClient.saveUserIdentityCard(userIdentityCard);
        }
        //删除对应购物车
        List<Long> cartIdList = new ArrayList<>();
        for (OrderSaveItemVO v : orderItemList) {
            if (v.getCart_record_id() != null && v.getCart_record_id() != 0) {
                cartIdList.add(v.getCart_record_id());
            }
        }
        String orderNum = stringBuffer.toString();
        orderNum = orderNum.substring(0, orderNum.lastIndexOf(","));
        shoppingCartMapper.deleteShoppingCartByIds(cartIdList);

        //库存锁定
        ProductsInventoryLockRequestVO requestVO = new ProductsInventoryLockRequestVO();
        requestVO.setLock_model("sub");
        List<ProductInventoryLockItem> lock_items = new ArrayList<>();
        requestVO.setLock_items(lock_items);
        for (OrderCreateFromCartVO orderCreateFromCartVO : cartVOlist) {
            ProductInventoryLockItem piaVO = new ProductInventoryLockItem();
            piaVO.setParty_id(orderCreateFromCartVO.getParty_id());
            piaVO.setProduct_id(orderCreateFromCartVO.getProduct_id());
            piaVO.setLock_quantity(orderCreateFromCartVO.getQuantity());
            lock_items.add(piaVO);
        }
        try {
            ProductsInventoryLockResponseVO responseVO = productFeignClient.lockProductInventory(requestVO).getData();
            if (responseVO == null || !responseVO.isSuccess()) {
                throw new BizException("库存不足");
            }
        } catch (Exception e) {
            throw new BizException("库存不足");
        }
        return new Result(CodeEnum.SUCCESS, orderService.encapsulationOrderToPayResponseVO(orderNum, pay_price.toString()));
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO       <       com.hryj.entity.vo.order.response.UserDistributionInfoVO>>
     * @author 罗秋涵
     * @description: 查询待分配配送单列表
     * @param: [distributionForStoreRequestVO]
     * @create 2018-07-09 19:30
     **/
    public Result<DistributionListReponseVO> findDistributionListForStore(DistributionForStoreRequestVO distributionForStoreRequestVO) {
        //参数判断
        if (distributionForStoreRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK);
        }
        if (distributionForStoreRequestVO.getDistribution_status() == null || "".equals(distributionForStoreRequestVO.getDistribution_status().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送状态不能为空");
        }

        if (distributionForStoreRequestVO.getDistribution_type() == null || "".equals(distributionForStoreRequestVO.getDistribution_type().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送类型不能为空");
        }
        DistributionListReponseVO response = new DistributionListReponseVO();
        List<UserDistributionInfoVO> distributionInfoVOList = new ArrayList<>();
        Map<Long, List<DistributionInfoVO>> listMap = new LinkedHashMap<>();
        List<DistributionInfoVO> distribution = new ArrayList<>();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(distributionForStoreRequestVO.getLogin_token());
        //判断岗位
        if (CodeCache.getValueByKey("StaffJob", "S01").equals(staffAppLoginVO.getStaff_job())) {
            //获取记录(店长)
            distribution = orderForStoreMapper.findDistributionListForBuinour(distributionForStoreRequestVO, staffAppLoginVO.getDeptGroup().getId());
        }
        if (distribution == null) {
            distribution = new ArrayList<>();
            response.setUserDistributionList(distributionInfoVOList);
            response.setCount(String.valueOf(distribution.size()));
            return new Result(CodeEnum.SUCCESS, response);
        }

        //根据用户分组
        for (int i = 0; i < distribution.size(); i++) {
            DistributionInfoVO tmp = distribution.get(i);
            Long endTime = tmp.getActual_delivery_end_time().getTime();
            tmp.setComplete_time(tmp.getComplete_time());
            Long startTime = System.currentTimeMillis();
            tmp.setStart_timestamp(String.valueOf(tmp.getHope_delivery_start_time().getTime()));
            tmp.setEnd_timestamp(String.valueOf(tmp.getHope_delivery_end_time().getTime()));
            //计算时间
            Long lastTime = endTime - startTime;
            distribution.get(i).setLastTime(String.valueOf(lastTime));
            if (listMap.get(tmp.getUser_id()) != null) {
                listMap.get(tmp.getUser_id()).add(tmp);
            } else {
                List<DistributionInfoVO> tmpList = new ArrayList<>();
                tmpList.add(tmp);
                listMap.put(tmp.getUser_id(), tmpList);
            }
        }
        //遍历分组
        for (Long key : listMap.keySet()) {
            UserDistributionInfoVO userDistributionInfoVO = new UserDistributionInfoVO();
            userDistributionInfoVO.setDistributionList(listMap.get(key));
            distributionInfoVOList.add(userDistributionInfoVO);
        }
        response.setUserDistributionList(distributionInfoVOList);
        response.setCount(String.valueOf(distribution.size()));
        return new Result(CodeEnum.SUCCESS, response);
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 订单处理数量统计
     * @param:
     * @create 2018-07-09 16:49
     **/
    public Result distributionProcessingCount(RequestVO requestVO) {
        WaitHandelOrderCountResponseVO vo = new WaitHandelOrderCountResponseVO();
        OrderStatusCountVO orderStatusCountVO = new OrderStatusCountVO();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(requestVO.getLogin_token());
        orderStatusCountVO.setDistribution_staff_id(staffAppLoginVO.getStaff_id());
        orderStatusCountVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_02.getCodeValue());
        orderStatusCountVO.setDistribution_type(SysCodeEnmu.DISTRIBUTIONTYPE_01.getCodeValue());
        //待配送数量
        vo.setWait_delivery_num(orderDistributionMapper.countTheStatus(orderStatusCountVO));
        orderStatusCountVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_02.getCodeValue());
        orderStatusCountVO.setDistribution_type(SysCodeEnmu.DISTRIBUTIONTYPE_02.getCodeValue());
        //待退货处理数量
        if (CodeCache.getValueByKey("DeptType", "S01").equals(staffAppLoginVO.getDeptGroup().getDept_type())) {
            //门店员工
            vo.setWait_return_num(orderReturnMapper.countReturnToDo(staffAppLoginVO.getStaff_id()));
        } else if (CodeCache.getValueByKey("DeptType", "S02").equals(staffAppLoginVO.getDeptGroup().getDept_type())) {
            //仓库员工
            vo.setWait_return_num(orderReturnMapper.countReturnFroWarehouse(CodeCache.getValueByKey("ReturnStatus", "S01"), staffAppLoginVO.getDeptGroup().getId()));
        }
        //待退取货数量
        vo.setWait_take_num(orderDistributionMapper.countTheStatus(orderStatusCountVO));
        //待分配配送/退货
        if (CodeCache.getValueByKey("StaffJob", "S01").equals(staffAppLoginVO.getStaff_job())) {
            //根据门店编号查询待分配的配送和退货
            vo.setWait_distribution(orderDistributionMapper.counttWaitDistributionNum(staffAppLoginVO.getDeptGroup().getId()));
        } else {
            vo.setWait_distribution(0);
        }
        return new Result(CodeEnum.SUCCESS, vo);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.SelfPickResponseVO>
     * @author 罗秋涵
     * @description: 根据自提码获取自提信息
     * @param: [selfPickRequestVO]
     * @create 2018-07-10 9:45
     **/
    public Result<SelfPickResponseVO> findOrderSelfPick(SelfPickRequestVO selfPickRequestVO) {
        //参数判断
        if (selfPickRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (selfPickRequestVO.getSelf_pick_code() == null || "".equals(selfPickRequestVO.getSelf_pick_code().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请填写正确的自提码");
        }
        //Redis里校验验证码是否存在
        if (!orderSelfPickService.existsSelfPickKey(selfPickRequestVO.getSelf_pick_code())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "无效自提码");
        }

        if (!Validator.isNumber(selfPickRequestVO.getSelf_pick_code()) || selfPickRequestVO.getSelf_pick_code().length() != 6) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请输入正确的自提码");
        }

        SelfPickResponseVO response = orderForStoreMapper.getSelfPickRecord(selfPickRequestVO.getSelf_pick_code());
        if (response == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应自提信息，请核对自提码");
        } else {
            //获取订单信息
            OrderInfo orderInfo = orderService.selectById(response.getOrder_id());
            //获取员工信息
            StaffAppLoginVO staffAppLoginVO = orderService.getStaffAppLoginUser(selfPickRequestVO.getLogin_token());
            Long dept_id = staffAppLoginVO.getDeptGroup().getId();
            if (!orderInfo.getParty_id().equals(dept_id)) {
                return new Result(CodeEnum.FAIL_BUSINESS, "该订单不属于操作人所属门店");
            }
            List<OrderPorductVO> orderPorductLis = orderProductMapper.getOrderPorductVOList(response.getOrder_id());
            // start add 商品活动信息调用接口获取 by luoqh  2018-08-21
            //获取活动信息
            orderPorductLis = orderService.getOrderProductActivityInfo(orderPorductLis);
            // end add 商品活动信息调用接口获取 by luoqh  2018-08-21
            response.setOrderProductList(orderPorductLis);
        }

        return new Result(CodeEnum.SUCCESS, response);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderSelfPickListResponseVO>
     * @author 罗秋涵
     * @description: 根据电话号码查询自提列表
     * @param: [selfPickRequestVO]
     * @create 2018-07-10 11:08
     **/
    public Result<ListResponseVO<SelfPickResponseVO>> findOrderSelfPickListByPhoneNum(SelfPickRequestVO selfPickRequestVO) {
        //参数判断
        if (selfPickRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (selfPickRequestVO.getPhone_num() == null || "".equals(selfPickRequestVO.getPhone_num().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请填写正确的手机号码");
        }
        if (!Validator.isNumber(selfPickRequestVO.getPhone_num()) || selfPickRequestVO.getPhone_num().length() != 11) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请输入正确的手机号码");
        }
        StaffAppLoginVO staffAppLoginVO = orderService.getStaffAppLoginUser(selfPickRequestVO.getLogin_token());
        if (selfPickRequestVO == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "登录账号无效");
        }
        ListResponseVO response = new ListResponseVO();
        //获取自提单列表
        List<SelfPickResponseVO> userSelfPickList = orderForStoreMapper.findUserSelfPickList(staffAppLoginVO.getDeptGroup().getId(), selfPickRequestVO.getPhone_num(), CodeCache.getValueByKey("OrderStatus", "S03"));
        if (userSelfPickList != null && userSelfPickList.size() > 0) {
            for (int i = 0; i < userSelfPickList.size(); i++) {
                //查询对应商品列表
                List<OrderPorductVO> orderPorductLis = orderProductMapper.getOrderPorductVOList(userSelfPickList.get(i).getOrder_id());
                // start add 商品活动信息调用接口获取 by luoqh  2018-08-21
                //获取活动信息
                orderPorductLis = orderService.getOrderProductActivityInfo(orderPorductLis);
                // end add 商品活动信息调用接口获取 by luoqh  2018-08-21
                userSelfPickList.get(i).setOrderProductList(orderPorductLis);
                userSelfPickList.get(i).setCreate_timestamp(userSelfPickList.get(i).getCreate_time().getTime());
            }
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应自提信息，请核对电话号码");
        }

        response.setRecords(userSelfPickList);
        return new Result(CodeEnum.SUCCESS, response);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 确认自提
     * @param: [confirmSelfPickRequestVO]
     * @create 2018-07-10 11:26
     **/
    @Transactional
    public Result confirmSelfPick(ConfirmSelfPickRequestVO confirmSelfPickRequestVO) {
        //参数判断
        if (confirmSelfPickRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (confirmSelfPickRequestVO.getSelf_pick_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "自提码编号不能为空");
        }
        // 查询自提信息
        OrderSelfPick orderSelfPick = orderSelfPickMapper.selectById(confirmSelfPickRequestVO.getSelf_pick_id());
        if (orderSelfPick == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "自提订单不存在");
        }
        if (CodeCache.getValueByKey("SelfPickStatus", "S02").equals(orderSelfPick.getSelf_pick_status())) {
            return new Result(CodeEnum.FAIL_BUSINESS, "该订单已经提货");
        }
        //获取订单信息
        OrderInfo orderInfo = orderService.selectById(orderSelfPick.getOrder_id());
        if (orderInfo == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "订单不存在");
        }
        //获取员工信息
        StaffAppLoginVO staffAppLoginVO = orderService.getStaffAppLoginUser(confirmSelfPickRequestVO.getLogin_token());
        if (staffAppLoginVO == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }
        if (staffAppLoginVO.getDeptGroup() == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }
        Long dept_id = staffAppLoginVO.getDeptGroup().getId();
        if (!orderInfo.getParty_id().equals(dept_id)) {
            return new Result(CodeEnum.FAIL_BUSINESS, "该订单不属于操作人所属门店");
        }
        if (!CodeCache.getValueByKey("OrderStatus", "S03").equals(orderInfo.getOrder_status())) {
            return new Result(CodeEnum.FAIL_BUSINESS, "订单状态变动，不能提货");
        }
        //修改自提单状态
        OrderSelfPick orderSelf = new OrderSelfPick();
        orderSelf.setId(orderSelfPick.getId());
        orderSelf.setSelf_pick_status(CodeCache.getValueByKey("SelfPickStatus", "S02"));
        orderSelf.setSelf_pick_handel_id(staffAppLoginVO.getStaff_id());
        orderSelf.setSelf_pick_handel_name(staffAppLoginVO.getStaff_name());
        orderSelf.setComplete_time(new Date());
        //修改自提单状态
        orderSelfPickMapper.updateById(orderSelf);
        //更新订单状态
        OrderStatusRecordVO orderStatusRecord = new OrderStatusRecordVO();
        orderStatusRecord.setOrder_id(orderSelfPick.getOrder_id());
        orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S08"));
        orderStatusRecord.setStatus_remark("用户自提成功");
        orderStatusRecord.setOperator_user_type(CodeCache.getValueByKey("OperatorUserType", "S02"));
        orderStatusRecord.setOperator_id(staffAppLoginVO.getStaff_id());
        orderStatusRecord.setOperator_name(staffAppLoginVO.getStaff_name());
        orderStatusRecord.setChange_reason("用户自提成功");
        orderStatusRecord.setUser_id(orderInfo.getUser_id());
        orderService.updateOrderStatus(orderStatusRecord, confirmSelfPickRequestVO.getLogin_token());
        //根据门店编号获取名店信息
        DeptIdRequestVO dept = new DeptIdRequestVO();
        dept.setDept_id(orderInfo.getParty_id());
        Result<DeptIdByStoreOrWarehouseResponseVO> storeInfo = storeFeignClient.getDeptIdByStoreOrWarehouseDet(dept);
        //组装短信参数
        HashMap messageMap = new HashMap();
        messageMap.put("self_pick_date", DateUtil.formatDateTime(new Date()));
        messageMap.put("store_name", storeInfo.getData().getDept_name());
        messageMap.put("order_num", orderInfo.getOrder_num());
        messageMap.put("telephone", storeInfo.getData().getTelephone());
        AliYunSms.sendSingleSms(orderSelfPick.getUser_phone(), "SMS_139233706", messageMap);
        //删除redis中的自提码
        redisService.delete1(CacheGroup.SELF_PICK_CODE.getValue(), orderSelfPick.getSelf_pick_code());
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 配送单分配人
     * @param: [sendOrdersRequestVO]
     * @create 2018-07-10 17:38
     **/
    @Transactional
    public Result assignDistribution(SendOrdersRequestVO sendOrdersRequestVO) {
        //参数判断
        if (sendOrdersRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (sendOrdersRequestVO.getDistribution_ids() == null || "".equals(sendOrdersRequestVO.getDistribution_ids().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送单编号不能为空");
        }
        if (sendOrdersRequestVO.getStaff_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送人不能为空");
        }
        List<String> ids = CommonUtil.stringToList(sendOrdersRequestVO.getDistribution_ids());
        if (ids == null || ids.size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送单编号不能为空");
        }
        //获取员工信息
        StaffAppLoginVO staffAppLoginVO = orderService.getStaffAppLoginUser(sendOrdersRequestVO.getLogin_token());
        if (staffAppLoginVO == null || staffAppLoginVO.getDeptGroup() == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }
        //获取配送人信息
        Result<StaffDeptVO> result = staffFeignClien.findStaffDeptVO(null, null, sendOrdersRequestVO.getStaff_id());
        //判断配送人是否在职，是否更改部门
        if (result.isSuccess() && result.getData() != null && result.getData().getDept_id() != null && result.getData().getStaff_status() != null) {
            if (!result.getData().getStaff_status() || !result.getData().getDept_id().equals(staffAppLoginVO.getDeptGroup().getId())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "配送人信息异常");
            }
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "配送人信息异常");
        }

        List<OrderDistribution> orderDistributions = new ArrayList<>();
        for (String id : ids) {

            OrderDistribution distribution = orderDistributionMapper.selectById(new Long(id));
            if (distribution == null) {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "无对应配送单信息");
            }
            //判断配送单状态如果状态为，配送完成，配送超时，取消配送，则不能分配处理人
            if (CodeCache.getValueByKey("DistributionStatus", "S03").equals(distribution.getDistribution_status())
                    || CodeCache.getValueByKey("DistributionStatus", "S04").equals(distribution.getDistribution_status())
                    || CodeCache.getValueByKey("DistributionStatus", "S05").equals(distribution.getDistribution_status())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "订单状态变动，无法分配");
            }
            orderDistributions.add(distribution);
        }
        if (orderDistributions == null || orderDistributions.size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "无对应配送单信息");
        }
        //修改记录
        for (OrderDistribution orderDistribution : orderDistributions) {
            //根据编号修改记录
            OrderDistribution newdistribution = new OrderDistribution();
            newdistribution.setId(orderDistribution.getId());
            newdistribution.setDistribution_staff_id(sendOrdersRequestVO.getStaff_id());
            newdistribution.setDistribution_staff_name(sendOrdersRequestVO.getStaff_name());
            newdistribution.setDistribution_staff_phone(sendOrdersRequestVO.getStaff_phone());
            newdistribution.setAssign_staff_id(staffAppLoginVO.getStaff_id());
            newdistribution.setAssign_staff_name(staffAppLoginVO.getStaff_name());
            newdistribution.setDistribution_status(CodeCache.getValueByKey("DistributionStatus", "S02"));
            //获取配送费用
            Double distribution_amt = orderReturnMapper.getOrderInfoByOrderId(sendOrdersRequestVO.getStaff_id());
            if (distribution_amt == null) {
                distribution_amt = 0.0;
            }
            newdistribution.setDistribution_amt(new BigDecimal(distribution_amt));
            //修改
            orderDistributionMapper.updateById(newdistribution);
            //更新订单状态
            OrderStatusRecordVO orderStatusRecord = new OrderStatusRecordVO();
            orderStatusRecord.setOrder_id(orderDistribution.getOrder_id());
            orderStatusRecord.setOrder_status(CodeCache.getValueByKey("OrderStatus", "S04"));
            orderStatusRecord.setStatus_remark("分配配送人员");
            orderStatusRecord.setOperator_user_type(CodeCache.getValueByKey("OperatorUserType", "S02"));
            orderStatusRecord.setOperator_id(staffAppLoginVO.getStaff_id());
            orderStatusRecord.setOperator_name(staffAppLoginVO.getStaff_name());
            orderStatusRecord.setChange_reason("分配配送人员");
            orderService.updateOrderStatus(orderStatusRecord, sendOrdersRequestVO.getLogin_token());
        }
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @return com.hryj.common.Result
     * @author 罗秋涵
     * @description: 分配退货处理人
     * @param: [sendOrdersRequestVO]
     * @create 2018-07-10 18:03
     **/
    @Transactional
    public Result returnDistribution(SendOrdersRequestVO sendOrdersRequestVO) {
        //参数判断
        if (sendOrdersRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (sendOrdersRequestVO.getDistribution_ids() == null || "".equals(sendOrdersRequestVO.getDistribution_ids().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "退货单编号不能为空");
        }
        if (sendOrdersRequestVO.getStaff_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送人不能为空");
        }
        //获取员工信息
        StaffAppLoginVO staffAppLoginVO = orderService.getStaffAppLoginUser(sendOrdersRequestVO.getLogin_token());
        if (staffAppLoginVO == null || staffAppLoginVO.getDeptGroup() == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }
        //获取退货处理人信息
        Result<StaffDeptVO> result = staffFeignClien.findStaffDeptVO(null, null, sendOrdersRequestVO.getStaff_id());
        //判断退货处理人是否在职，是否更改部门
        if (result.isSuccess() && result.getData() != null && result.getData().getDept_id() != null && result.getData().getStaff_status() != null) {
            if (!result.getData().getStaff_status() || !result.getData().getDept_id().equals(staffAppLoginVO.getDeptGroup().getId())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "取货人信息异常");
            }
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "取货人信息异常");
        }
        List<String> ids = CommonUtil.stringToList(sendOrdersRequestVO.getDistribution_ids());
        if (ids == null || ids.size() == 0) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "退货单编号不能为空");
        }
        List<OrderReturn> orderReturnList = new ArrayList<>();
        for (String id : ids) {
            OrderReturn orderReturn = orderReturnMapper.selectById(new Long(id));
            if (orderReturn == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "无对应记录");
            }
            //如果退货单状态并非申请中的时候，不可再次分配
            if (!StringUtils.isEmpty(orderReturn.getReturn_handel_id())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "该订单已分配人员");
            }
            orderReturnList.add(orderReturn);
        }
        if (orderReturnList == null || orderReturnList.size() == 0) {
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应记录");
        }
        //修改记录
        for (OrderReturn orderReturn : orderReturnList) {
            //修改退货单信息
            OrderReturn newOrderReturn = new OrderReturn();
            newOrderReturn.setId(orderReturn.getId());
            newOrderReturn.setReturn_handel_id(sendOrdersRequestVO.getStaff_id());
            newOrderReturn.setReturn_handel_name(sendOrdersRequestVO.getStaff_name());
            newOrderReturn.setReturn_status(CodeCache.getValueByKey("ReturnStatus", "S02"));
            orderReturnMapper.updateById(newOrderReturn);
        }
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 确认 配送/取货 完成 ,修改配送单状态和订单状态
     * @param:
     * @create 2018-07-10 16:11
     **/
    @Transactional
    public Result confirmDistributionDetail(DistributionConfirmRequestVo requestVO) {
        List<DistributionConfirmVo> confirmRequestVoList = requestVO.getConfirmRequestVoList();

        if (confirmRequestVoList == null || confirmRequestVoList.size() < 1) {
            return new Result(CodeEnum.FAIL_BUSINESS, "参数异常");
        }
        Map<Long, DistributionInfoVO> distributionInfoVOMap = new HashMap<>();
        for (DistributionConfirmVo v : confirmRequestVoList) {
            if (StringUtils.isEmpty(v.getOrder_id())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "订单ID有误");
            }
            //获取订单详情
            OrderInfoVO u = orderInfoMapper.getOrderInfoVO(v.getOrder_id());
            if (u == null || u.getParty_type() == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "订单数据异常");
            }
            //仓库判断
            if (u.getParty_type().equals("02")) {
                return new Result(CodeEnum.FAIL_BUSINESS, "仓库人员没有该操作权限");
            } else {
                DistributionInfoVO od = orderDistributionMapper.getOrderDistributionById(v.getDistribution_id());
                distributionInfoVOMap.put(v.getDistribution_id(), od);
                StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(requestVO.getLogin_token());
                if (!od.getDistribution_staff_id().equals(staffAppLoginVO.getStaff_id())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "该订单已分配其他人");
                }
                if (StringUtils.isEmpty(v.getDistribution_id())) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "配送单ID有误");
                }
                String type = v.getDistribution_type();
                if (StringUtils.isEmpty(type)) {
                    return new Result(CodeEnum.FAIL_PARAMCHECK, "请传入正确的配送类别");
                }
                //送货
                if (type.equals(SysCodeEnmu.DISTRIBUTIONTYPE_01.getCodeValue())) {
                    if (SysCodeEnmu.ORDERSTATUS_05.getCodeValue().equals(u.getOrder_status())) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "该订单处于退货申请中");
                    }
                } else {
                    //取货
                    if (!SysCodeEnmu.ORDERSTATUS_05.getCodeValue().equals(u.getOrder_status())) {
                        return new Result(CodeEnum.FAIL_BUSINESS, "该订单已处理");
                    }
                }
            }
        }

        for (DistributionConfirmVo v : confirmRequestVoList) {
            OrderStatusVO vo = new OrderStatusVO();
            ConfirmDistributionVO confirmDistributionVO = new ConfirmDistributionVO();
            String type = v.getDistribution_type();
            confirmDistributionVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_03.getCodeValue());
            if (type.equals(SysCodeEnmu.DISTRIBUTIONTYPE_01.getCodeValue())) {
                //送货
                vo.setStatus_remark("配送完成");
                vo.setChange_reason("配送完成");
                //获取配送单信息
                DistributionInfoVO od = distributionInfoVOMap.get(v.getDistribution_id());
                if (od.getActual_delivery_end_time().getTime() - System.currentTimeMillis() < 0) {
                    confirmDistributionVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_04.getCodeValue());
                }

            } else if (type.equals(SysCodeEnmu.DISTRIBUTIONTYPE_02.getCodeValue())) {
                //取货成功
                vo.setStatus_remark("取货成功");
                vo.setChange_reason("取货成功");
                vo.setOrder_status(SysCodeEnmu.ORDERSTATUS_06.getCodeValue());
                //释放库存
                resetStockNew(v.getOrder_id(), null);
                //退款--获取订单编号：
                Result result = paymentService.refund(v.getOrder_id());
                if (result.getCode() != 100) {
                    return new Result(CodeEnum.FAIL_BUSINESS, "退款失败");
                }
            } else {
                return new Result(CodeEnum.FAIL_PARAMCHECK, "请传入正确的配送类别");
            }
            confirmDistributionVO.setId(v.getDistribution_id());
            confirmDistributionVO.setComplete_time(new Date());
            //更新配送单状态
            orderDistributionMapper.confirmDistributionDetail(confirmDistributionVO);
            //更新订单状态
            vo.setLogin_token(requestVO.getLogin_token());
            vo.setOrder_id(v.getOrder_id());
            orderService.updateStatus(vo);
        }

        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO       <       com.hryj.entity.vo.order.response.ReturnResponseVO>>
     * @author 罗秋涵
     * @description: 获取待分配退货列表
     * @param: [returnOrderListRequestVO]
     * @create 2018-07-10 20:12
     **/
    public Result<PageResponseVO<ReturnVO>> findReturnListForStore(ReturnRequestVO returnRequestVO) {
        if (returnRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        Page page = new Page(returnRequestVO.getPage_num(), returnRequestVO.getPage_size());
        //获取部门
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(returnRequestVO.getLogin_token());
        //查询类型：01：查询待分配退货单，02：查询已取货退货单
        List<ReturnVO> returnVOList = new ArrayList<>();
        //判断岗位，店长才能查询
        if (CodeCache.getValueByKey("StaffJob", "S01").equals(staffAppLoginVO.getStaff_job())) {
            if ("01".equals(returnRequestVO.getFind_type())) {
                //根据状态查询
                returnVOList = orderForStoreMapper.findReturnListForUnallocated(CodeCache.getValueByKey("ReturnStatus", "S01"), staffAppLoginVO.getDeptGroup().getId(), page);
            } else if ("02".equals(returnRequestVO.getFind_type())) {
                returnVOList = orderForStoreMapper.findReturnListForAllot(staffAppLoginVO.getDeptGroup().getId(), page);
            }
        }
        if (returnVOList == null) {
            returnVOList = new ArrayList<>();
        }
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(returnVOList);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());

        return new Result(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO   <   com.hryj.entity.vo.order.response.ReturnResponseVO>>
     * @author 罗秋涵
     * @description: 查询退货单列表）
     * @param: [returnOrderListRequestVO]
     * @create 2018-07-10 21:36
     **/
    public Result<PageResponseVO<ReturnVO>> findReturnListForStaff(ReturnOrderListRequestVO returnOrderListRequestVO) {
        if (returnOrderListRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        List<ReturnVO> returnVOList = null;
        Page page = new Page(returnOrderListRequestVO.getPage_num(), returnOrderListRequestVO.getPage_size());
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(returnOrderListRequestVO.getLogin_token());
        if (staffAppLoginVO == null || staffAppLoginVO.getDeptGroup() == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }
        //门店
        if (CodeCache.getValueByKey("DeptType", "S01").equals(staffAppLoginVO.getDeptGroup().getDept_type())) {
            returnVOList = orderForStoreMapper.findReturnListForStaff(returnOrderListRequestVO, staffAppLoginVO.getStaff_id(), page);
        } else if (CodeCache.getValueByKey("DeptType", "S02").equals(staffAppLoginVO.getDeptGroup().getDept_type())) {
            //仓库
            returnOrderListRequestVO.setReturn_status(CodeCache.getValueByKey("ReturnStatus", "S01"));
            returnVOList = orderForStoreMapper.findReturnListForWarehouse(returnOrderListRequestVO, staffAppLoginVO.getDeptGroup().getId(), page);
        }
        if (returnVOList == null) {
            returnVOList = new ArrayList<>();
        }
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(returnVOList);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        return new Result(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.ReturnOrderDetailsResponseVO>
     * @author 罗秋涵
     * @description: 获取退货单详情
     * @param: [returnOrderDetailsRequestVO]
     * @create 2018-07-10 21:49
     **/
    public Result<ReturnOrderDetailsResponseVO> getReturnOrderDetails(ReturnOrderDetailsRequestVO returnOrderDetailsRequestVO) {
        //参数判断
        if (returnOrderDetailsRequestVO == null) {
            new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (returnOrderDetailsRequestVO.getReturn_id() == null) {
            new Result(CodeEnum.FAIL_PARAMCHECK, "退货单编号不能为空");
        }
        ReturnOrderDetailsResponseVO responseVO = orderForStoreMapper.getReturnDetail(returnOrderDetailsRequestVO);
        if (responseVO == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "退货单不存在");
        }
        List<ReturnProductVO> returnProductList = orderForStoreMapper.findReturnProductList(returnOrderDetailsRequestVO);
        if (returnOrderDetailsRequestVO == null) {
            returnProductList = new ArrayList<>();
        }
        responseVO.setReturnProductList(returnProductList);

        return new Result(CodeEnum.SUCCESS, responseVO);
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 查询配送单列表（门店端，配送订单，已超时）
     * @param:
     * @create 2018-07-09 20:16
     **/
    public Result findDistributionList(DistributionRequestVO distributionRequestVO) {
        //封装入返回对象中 -----感觉包裹的层次太多了
        DistributionListReponseVO distributionListReponseVO = new DistributionListReponseVO();
        List<UserDistributionInfoVO> userDistributionList = new ArrayList<>();
        //获取登录人账号
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(distributionRequestVO.getLogin_token());
        distributionRequestVO.setDistribution_staff_id(staffAppLoginVO.getStaff_id());
        //定义返回值列表
        List<OrderDistribution> list = null;
        String status = distributionRequestVO.getDistribution_status();
        if (SysCodeEnmu.DISTRIBUTIONSTATUS_02.getCodeValue().equals(status) ||
                SysCodeEnmu.DISTRIBUTIONSTATUS_03.getCodeValue().equals(status) ||
                SysCodeEnmu.DISTRIBUTIONSTATUS_04.getCodeValue().equals(status)) {
            list = orderDistributionMapper.selectDistributionList(distributionRequestVO);
        } else {
            distributionListReponseVO.setUserDistributionList(userDistributionList);
            distributionListReponseVO.setCount("0");
            return new Result(CodeEnum.SUCCESS, distributionListReponseVO);
        }

        //列表需要针对用户分割
        DistributionInfoVO vo = null;
        Map<Long, List<DistributionInfoVO>> userMap = new LinkedHashMap<>();
        List<DistributionInfoVO> distributionInfoVOS = null;
        for (OrderDistribution o : list) {
            vo = new DistributionInfoVO();
            vo.setAddress_locations(o.getAddress_locations());
            vo.setDistribution_id(o.getId());
            vo.setOrder_id(o.getOrder_id());
            vo.setDistribution_staff_name(o.getDistribution_staff_name());
            vo.setDistribution_staff_phone(o.getDistribution_staff_phone());
            vo.setDistribution_status(o.getDistribution_status());
            vo.setDistribution_type(o.getDistribution_type());
            vo.setHope_delivery_end_time(o.getHope_delivery_end_time());
            vo.setHope_delivery_start_time(o.getHope_delivery_start_time());
            vo.setStart_timestamp(String.valueOf(vo.getHope_delivery_start_time().getTime()));
            vo.setEnd_timestamp(String.valueOf(vo.getHope_delivery_end_time().getTime()));
            vo.setUser_address(o.getUser_address());
            vo.setUser_name(o.getUser_name());
            vo.setUser_phone(o.getUser_phone());
            vo.setLastTime((o.getActual_delivery_end_time().getTime() - System.currentTimeMillis()) + "");
            //如果存在
            if (userMap.containsKey(o.getUser_id())) {
                userMap.get(o.getUser_id()).add(vo);
            } else {
                distributionInfoVOS = new ArrayList<>();
                distributionInfoVOS.add(vo);
                userMap.put(o.getUser_id(), distributionInfoVOS);
            }
        }

        UserDistributionInfoVO userDistributionInfoVO = null;
        //前端需要list不能为null，这里进行判断封装
        if (list == null || list.size() == 0) {
            distributionInfoVOS = new ArrayList<>();
            userDistributionInfoVO = new UserDistributionInfoVO();
            userDistributionInfoVO.setDistributionList(distributionInfoVOS);
        } else {
            List<DistributionInfoVO> list1 = null;
            for (Long l : userMap.keySet()) {
                userDistributionInfoVO = new UserDistributionInfoVO();
                list1 = userMap.get(l);
                //根据时间排序
                //orderListByHopeEndTime(list1, 1);
                userDistributionInfoVO.setDistributionList(list1);
                userDistributionList.add(userDistributionInfoVO);
            }
        }
        distributionListReponseVO.setUserDistributionList(userDistributionList);
        //总共多少条数据
        distributionListReponseVO.setCount(list.size() + "");
        return new Result(CodeEnum.SUCCESS, distributionListReponseVO);
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据规则返回剩余时间字符串，有超时
     * (OrderAdminService也有一个，但是返回字符串不一样，这边只需要天时分)
     * @param:
     * @return
     * @create 2018-07-09 15:05
     **/
    private static int S = 60;//一分钟
    private static int H = 60 * 60;//一小时
    private static int D = 60 * 60 * 24;//一天

    public String getTakeTimeString(Long btime, Long etime) {
        long t = etime - btime;
        long time = t / 1000;//总共多少秒
        time = Math.abs(time);//有超时情况取绝对值计算
        String timeString = null;
        if (time < S) {
            //小于一分钟
            timeString = "1分钟";
        } else if (time < H) { //小于一小时
            timeString = time / H + "分钟";
        } else if (time < D) {//小于24小时
            long m = time / S;//总共多少分钟
            //一天内,小时:分钟
            timeString = m / S + "小时" + m % S + "分钟";
        } else {
            //大于一天  天:小:时
            long m = time / S;//总共多少分钟
            long day = time / D; //多少天
            timeString = day + "天" + (m / 60) % 24 + "小时" + m % 60 + "分钟";
        }
        if (t < 0) {
            timeString += "超时" + timeString;
        }
        return timeString;
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 已配送返回时间字符串（按天返回，不足一天返回：今天）
     * @param:
     * @create 2018-07-10 15:30
     **/
    public String getTakeTimeStringForOutOfTime(Long btime, Long etime) {
        long time = (etime - btime) / 1000;//总共多少秒
        if (time / D < 1) {
            return "今天";
        } else {
            return time / D + "";
        }
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据配送单期望到达时间排序
     * @param: order: 2升序 1倒序
     * @create 2018-07-10 12:07
     **/
    public static void orderListByHopeEndTime(List<DistributionInfoVO> list, Integer order) {
        //倒序
        if (order == 1 || order == null) {
            list.sort((DistributionInfoVO d1, DistributionInfoVO d2) -> d2.getHope_delivery_end_time()
                    .compareTo(d1.getHope_delivery_end_time()));
        } else {//升序
            list.sort((DistributionInfoVO d1, DistributionInfoVO d2) -> d1.getHope_delivery_end_time()
                    .compareTo(d2.getHope_delivery_end_time()));
        }
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 查询配送单详情
     * @param:
     * @create 2018-07-09 20:21
     **/
    public Result<DistributionDetailResponseVO> findDistributionDetail(DistributionDetailRequestVO distributionDetailRequestVO) {
        //参数校验
        if (distributionDetailRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数为空");
        }
        if (distributionDetailRequestVO.getDistribution_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送单编号不能为空");
        }

        DistributionDetailResponseVO distributionDetailResponseVO = new DistributionDetailResponseVO();
        //查询到配送单信息
        OrderDistribution orderDistribution = orderDistributionMapper.selectById(distributionDetailRequestVO.getDistribution_id());
        if (orderDistribution == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应配送单信息");
        }
        DistributionInfoVO vo = new DistributionInfoVO();
        vo.setAddress_locations(orderDistribution.getAddress_locations());
        vo.setDistribution_id(orderDistribution.getId());
        vo.setDistribution_staff_name(orderDistribution.getDistribution_staff_name());
        vo.setDistribution_staff_phone(orderDistribution.getDistribution_staff_phone());
        vo.setDistribution_status(orderDistribution.getDistribution_status());
        vo.setDistribution_type(orderDistribution.getDistribution_type());
        vo.setHope_delivery_start_time(orderDistribution.getHope_delivery_start_time());
        vo.setHope_delivery_end_time(orderDistribution.getHope_delivery_end_time());
        if (vo.getHope_delivery_start_time() != null) {
            vo.setStart_timestamp(String.valueOf(vo.getHope_delivery_start_time().getTime()));
        }
        Long last = null;
        if (vo.getHope_delivery_end_time() != null) {
            vo.setEnd_timestamp(String.valueOf(vo.getHope_delivery_end_time().getTime()));
            last = orderDistribution.getActual_delivery_end_time().getTime() - System.currentTimeMillis();
        }
        vo.setUser_address(orderDistribution.getUser_address());
        vo.setUser_name(orderDistribution.getUser_name());
        vo.setUser_phone(orderDistribution.getUser_phone());
        vo.setComplete_time(orderDistribution.getUpdate_time());
        //设置订单编号
        vo.setOrder_id(orderDistribution.getOrder_id());

        vo.setLastTime(last + "");
        distributionDetailResponseVO.setDistributionInfoVO(vo);
        //查询订单商品信息
        distributionDetailResponseVO.setDistributionProductList(orderInfoMapper
                .selectOrderInfoAndOrderProductMessage(orderDistribution.getOrder_id()));
        //计算剩余时间 格式：00:00:00
       /* StringBuilder time = new StringBuilder();
        long k = (last) / (1000);//总相隔秒数
        time.append(k / 3600);//小时
        time.append(":");
        time.append(k / 60);//分钟
        time.append(":");
        time.append(k % 60);//秒*/
        //distributionDetailResponseVO.setLastTime(last+"");
        return new Result(CodeEnum.SUCCESS, distributionDetailResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionResponseVO>
     * @author 罗秋涵
     * @description: 查询配送单列表（已配送，已超时，待取货，已取货）
     * @param: [distributionRequestVO]
     * @create 2018-07-17 21:54
     **/
    public Result<PageResponseVO<DistributionInfoVO>> findDistributionForStaff(DistributionRequestVO distributionRequestVO) {
        //参数判断
        if (distributionRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (distributionRequestVO.getDistribution_status() == null || "".equals(distributionRequestVO.getDistribution_status().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送单状态不能为空");
        }
        if (distributionRequestVO.getDistribution_type() == null || "".equals(distributionRequestVO.getDistribution_type().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送单类型不能为空");
        }
        Page page = new Page(distributionRequestVO.getPage_num(), distributionRequestVO.getPage_size());
        Long userId = orderService.getStaffAppLoginUserId(distributionRequestVO.getLogin_token());
        distributionRequestVO.setDistribution_staff_id(userId);
        List<DistributionInfoVO> distribution = distribution = orderForStoreMapper.findDistributionStaff(distributionRequestVO, page);
        if (distribution == null) {
            distribution = new ArrayList<>();
        }
        //循环好像结果期望送达时间转时间戳
        for (int i = 0; i < distribution.size(); i++) {
            if (distribution.get(i) != null
                    && distribution.get(i).getHope_delivery_start_time() != null
                    && distribution.get(i).getHope_delivery_end_time() != null) {
                distribution.get(i).setStart_timestamp(String.valueOf(distribution.get(i).getHope_delivery_start_time().getTime()));
                distribution.get(i).setEnd_timestamp(String.valueOf(distribution.get(i).getHope_delivery_end_time().getTime()));
                //配送完成距当前时间
                distribution.get(i).setLastTime(String.valueOf(System.currentTimeMillis() - distribution.get(i).getComplete_time().getTime()));
            }
        }
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(distribution);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        return new Result(CodeEnum.SUCCESS, pageResponseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.DistributionResponseVO>
     * @author 罗秋涵
     * @description: 查询已分配配送单列表
     * @param: [distributionForStoreRequestVO]
     * @create 2018-07-18 16:42
     **/
    public Result<PageResponseVO<DistributionInfoVO>> findAssignedDistributionList(DistributionForStoreRequestVO distributionForStoreRequestVO) {
        //参数判断
        if (distributionForStoreRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK);
        }
        if (distributionForStoreRequestVO.getDistribution_status() == null || "".equals(distributionForStoreRequestVO.getDistribution_status().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送状态不能为空");
        }

        if (distributionForStoreRequestVO.getDistribution_type() == null || "".equals(distributionForStoreRequestVO.getDistribution_type().trim())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "配送类型不能为空");
        }
        PageResponseVO response = new PageResponseVO();
        Page page = new Page(distributionForStoreRequestVO.getPage_num(), distributionForStoreRequestVO.getPage_size());
        List<DistributionInfoVO> distribution = new ArrayList<>();
        StaffAppLoginVO staffAppLoginVO = LoginCache.getStaffAppLoginVO(distributionForStoreRequestVO.getLogin_token());
        if (CodeCache.getValueByKey("StaffJob", "S01").equals(staffAppLoginVO.getStaff_job())) {
            //获取记录(店长)
            distribution = orderForStoreMapper.findDistributionListForBuinourByStatus(distributionForStoreRequestVO, staffAppLoginVO.getDeptGroup().getId(), page);
        }
        if (distribution == null) {
            distribution = new ArrayList<>();
        }
        response.setRecords(distribution);
        response.setTotal_count(page.getTotal());
        response.setTotal_page(page.getPages());
        return new Result(CodeEnum.SUCCESS, response);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据orderid库存锁定
     * @param:
     * @create 2018-07-19 16:16
     **/
    /*public void updateStock(Long orderId) {
        List<OrderInfoToDistributionVO> list1 = orderDistributionProductMapper.getOrderProductMessage(orderId);
        //更新库存
        for (OrderInfoToDistributionVO v : list1) {
            orderService.partyProductInventoryAdjust(v.getParty_id(), v.getProduct_id(), v.getQuantity());
        }
    }*/


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 释放库存，单个或者多个商品
     * @param:
     * @create 2018-08-02 9:37
     **/
    public void resetStock(Long orderId, Long order_product_id) {
        List<OrderReturnProductVO> returnProductList = orderReturnMapper.getReturnOrderProductMessage(orderId, order_product_id);
        List<ProductInventoryAdjustVO> adjustVOList = new ArrayList<>();
        for (OrderReturnProductVO orderReturnProductVO : returnProductList) {
            ProductInventoryAdjustVO productInventoryAdjustVO = new ProductInventoryAdjustVO();
            productInventoryAdjustVO.setParty_id(orderReturnProductVO.getParty_id());
            productInventoryAdjustVO.setProduct_id(orderReturnProductVO.getProduct_id());
            productInventoryAdjustVO.setQuantity(orderReturnProductVO.getReturn_quantity());
            adjustVOList.add(productInventoryAdjustVO);
            resetStockReleaseFlag(orderReturnProductVO);
        }
        orderService.partyProductInventoryAdjust(adjustVOList, true);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 释放库存，单个或者多个商品
     * @param:
     * @create 2018-08-02 9:37
     **/
    public void resetStockNew(Long orderId, Long order_product_id) {
        ProductsInventoryLockRequestVO requestVO = new ProductsInventoryLockRequestVO();
        requestVO.setLock_model("add");
        List<ProductInventoryLockItem> lock_items = new ArrayList<>();
        requestVO.setLock_items(lock_items);
        List<OrderReturnProductVO> returnProductList = orderReturnMapper.getReturnOrderProductMessage(orderId, order_product_id);
        for (OrderReturnProductVO orderReturnProductVO : returnProductList) {
            ProductInventoryLockItem productInventoryLockItem = new ProductInventoryLockItem();
            productInventoryLockItem.setParty_id(orderReturnProductVO.getParty_id());
            productInventoryLockItem.setProduct_id(orderReturnProductVO.getProduct_id());
            productInventoryLockItem.setLock_quantity(orderReturnProductVO.getReturn_quantity());
            lock_items.add(productInventoryLockItem);
            resetStockReleaseFlag(orderReturnProductVO);
        }
        ProductsInventoryLockResponseVO responseVO = null;
        try {
            responseVO = productFeignClient.lockProductInventory(requestVO).getData();
            if (responseVO == null || !responseVO.isSuccess()) {
                throw new BizException("释放库存失败");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 释放库存标识修改
     * @param:
     * @create 2018-08-08 16:46
     **/
    public void resetStockReleaseFlag(OrderReturnProductVO orderReturnProductVO) {
        OrderProduct product = new OrderProduct();
        product.setId(orderReturnProductVO.getOrder_product_id());
        product.setPrice_modify_flag(1);
        orderProductMapper.updateById(product);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO       <       com.hryj.entity.vo.order.response.HistoricalOrderListResponseVO>>
     * @author 罗秋涵
     * @description: 门店端获取待支付历史订单
     * @param: [orderListRequestVO]
     * @create 2018-07-20 16:20
     **/
    public Result<ListResponseVO<HistoricalOrderListResponseVO>> findWaitPayOrderList(OrderListRequestVO orderListRequestVO) {

        if (orderListRequestVO.getOrder_status() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单状态不能为空");
        }
        ListResponseVO response = new ListResponseVO();
        //获取员工信息
        Long staff_id = orderService.getStaffAppLoginUserId(orderListRequestVO.getLogin_token());
        orderListRequestVO.setHelp_staff_id(staff_id);
        //获取订单信息
        List<HistoricalOrderListResponseVO> orderList = orderForStoreMapper.findWaitPayOrderList(orderListRequestVO);
        response.setRecords(orderList);

        return new Result(CodeEnum.SUCCESS, response);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO       <       com.hryj.entity.vo.order.response.HistoricalOrderListResponseVO>>
     * @author 罗秋涵
     * @description: 分页查询订单列表
     * @param: [orderListRequestVO]
     * @create 2018-07-20 16:25
     **/
    public Result<PageResponseVO<HistoricalOrderListResponseVO>> findOrderListByOrderStatus(OrderListRequestVO orderListRequestVO) {
        if (orderListRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数为空");
        }
        if (orderListRequestVO.getOrder_status() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单状态不能为空");
        }
        //如果订单状态为已完成查询结果加入已取消订单
        if (CodeCache.getValueByKey("OrderStatus", "S08").equals(orderListRequestVO.getOrder_status())) {
            orderListRequestVO.setOrder_status(orderListRequestVO.getOrder_status() + "," + CodeCache.getValueByKey("OrderStatus", "S07"));
        }
        //获取员工信息
        Long staff_id = orderService.getStaffAppLoginUserId(orderListRequestVO.getLogin_token());
        orderListRequestVO.setHelp_staff_id(staff_id);
        Page page = new Page(orderListRequestVO.getPage_num(), orderListRequestVO.getPage_size());
        //分页获取订单列表
        List<HistoricalOrderListResponseVO> orderList = orderForStoreMapper.findOrderListByStatus(orderListRequestVO, page);
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(orderList);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        return new Result(CodeEnum.SUCCESS, pageResponseVO);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 获取对应活动信息
     * @param:
     * @create 2018-08-17 10:35
     **/
    public OrderActivityInfoResponseVO getActivityInfo(Long productId, Long activityId, List<OrderActivityInfoResponseVO> activityList) {
        if (activityId != null && activityId != 0) {
            for (OrderActivityInfoResponseVO orderActivity : activityList) {
                if (orderActivity.getActivity_id().equals(activityId) && orderActivity.getProduct_id().equals(productId)) {
                    return orderActivity;
                }
            }
        }
        return null;
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 商品验证
     * @param:
     * @create 2018-08-20 15:09
     **/
    public Result productsValidate(Result<ProductsValidateResponseVO> result) {
        if (result != null && result.getData() != null && result.getData().getProd_validate_result_list() != null) {
            List<ProductValidateResponseItem> responseItems = result.getData().getProd_validate_result_list();
            if (responseItems.size() < 1) {
                return new Result<>(CodeEnum.FAIL_BUSINESS, "该商品数据异常！");
            }
            //存放不同的商品类别普通/跨境
            Map<String,String> differenceType = new HashMap<>();
            for (ProductValidateResponseItem it : responseItems) {
                if (!it.getIs_valid()) {
                    return new Result(CodeEnum.FAIL_BUSINESS.getCode(), "", result.getData().getProd_validate_result_list().get(0).getValidate_status_code());
                }
            }
        }
        return new Result<>(CodeEnum.SUCCESS);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: list组合, 将商品数据合并到list中
     * @param:
     * @create 2018-08-17 14:24
     **/
    public List<OrderCreateFromCartVO> combinationProductList(List<OrderSaveItemVO> orderSaveItemVOList, List<ProductValidateResponseItem> prod_validate_result_list) {
        List<OrderCreateFromCartVO> cartVOlist = new ArrayList<>();
        OrderCreateFromCartVO orderCreateFromCartVO = null;
        for (ProductValidateResponseItem pvitem : prod_validate_result_list) {
            for (OrderSaveItemVO osItem : orderSaveItemVOList) {
                //当活动id都为空的时候 或者活动ID都不为空并且活动id相等的情况下，才合并list
                if ((osItem.getActivity_id() == null && pvitem.getActivity_id() == null)
                        || ((osItem.getActivity_id() != null && pvitem.getActivity_id() != null)
                        && (osItem.getActivity_id().equals(pvitem.getActivity_id())))) {
                    if (osItem.getParty_id().equals(pvitem.getParty_id())
                            && osItem.getProduct_id().equals(pvitem.getProduct_id())) {
                        orderCreateFromCartVO = new OrderCreateFromCartVO();
                        orderCreateFromCartVO.setQuantity(osItem.getQuantity());
                        orderCreateFromCartVO.setActivity_id(osItem.getActivity_id());
                        orderCreateFromCartVO.setParty_id(osItem.getParty_id());
                        orderCreateFromCartVO.setProduct_id(osItem.getProduct_id());
                        orderCreateFromCartVO.setCost_price(pvitem.getCost_price());
                        orderCreateFromCartVO.setSale_price(pvitem.getNormal_price());
                        orderCreateFromCartVO.setList_image_url(pvitem.getList_image_url());
                        orderCreateFromCartVO.setProduct_name(pvitem.getProduct_name());
                        orderCreateFromCartVO.setProd_cate_id(pvitem.getProd_cate_id());
                        orderCreateFromCartVO.setShare_source(osItem.getShare_source());
                        orderCreateFromCartVO.setShare_user_id(osItem.getShare_user_id());
                        cartVOlist.add(orderCreateFromCartVO);
                    }
                }
            }
        }
        return cartVOlist;
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 将门店信息合并进list
     * @param:
     * @create 2018-08-20 14:07
     **/
    public void combinationPartyList(List<OrderCreateFromCartVO> cartVOlist, List<DeptIdByStoreOrWarehouseResponseVO> depList) {
        for (DeptIdByStoreOrWarehouseResponseVO depitem : depList) {
            for (OrderCreateFromCartVO ocItem : cartVOlist) {
                if ((ocItem.getParty_id().equals(depitem.getDept_id()))) {
                    ocItem.setParty_name(depitem.getDept_name());
                    ocItem.setParty_type(depitem.getDept_type());
                    ocItem.setParty_path(depitem.getDept_path());
                }
            }
        }
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description:将活动信息合并进list
     * @param:
     * @create 2018-08-20 14:56
     **/
    public void combinationActivityList(List<OrderCreateFromCartVO> cartVOlist, List<OrderActivityInfoResponseVO> actList) {
        for (OrderActivityInfoResponseVO actitem : actList) {
            for (OrderCreateFromCartVO ocItem : cartVOlist) {
                //当活动id都为空的时候 或者活动ID都不为空并且活动id相等的情况下，才合并list
                if ((actitem.getActivity_id() == null && ocItem.getActivity_id() == null)
                        || ((actitem.getActivity_id() != null && ocItem.getActivity_id() != null)
                        && (actitem.getActivity_id().equals(ocItem.getActivity_id())))) {
                    if (actitem.getParty_id().equals(ocItem.getParty_id())
                            && actitem.getParty_id().equals(ocItem.getParty_id())
                            && actitem.getProduct_id().equals(ocItem.getProduct_id())) {
                        ocItem.setActivity_price(actitem.getActivity_price());
                    }
                }
            }
        }

    }


    /*public void combinationList(List list1,Class list1Class,List list2,Class list2Class){
        for(int i=0;i< list1.size();i++){
            for(int j=0;j<list2.size();j++){
                try {
                    Object obj1= callMethod(getMethod(list2Class,"getParty_id",Long.TYPE,true),list2Class,null);
                    if(obj1==null){

                    }
                }catch (Exception e){

                }
                getFieldForClass(list1Class,list2Class);
            }
        }
    }*/

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 获取该class下字段
     * @param:
     * @create 2018-08-17 16:50
     **/
    public void getFieldForClass(Class clazz1, Class clazz2) {
        Field[] fields = clazz1.getDeclaredFields();
        for (Field f : fields) {
            Object obj1 = callMethod(getMethod(clazz1, "get" + synthesisMethodName(f.getName()), f.getType(), true)
                    , clazz1, null);
            callMethod(getMethod(clazz2, "set" + synthesisMethodName(f.getName()), f.getType(), false)
                    , clazz2, obj1);
        }
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 获取get/set方法
     * @param:
     * @create 2018-08-17 17:19
     **/
    public static String synthesisMethodName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1, fieldName.length());
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 放回对应方法方法
     * @param:clazz:方法类，methodName:方法名, paramCLass:参数类型,isGet：true=get,false=set 方法
     * @create 2018-08-17 17:24
     **/
    public Method getMethod(Class clazz, String methodName, Class paramCLass, boolean isGet) {
        Method method = null;
        try {
            if (isGet) {
                method = clazz.getMethod(methodName, null);
            } else {
                method = clazz.getMethod(methodName, paramCLass);
            }
        } catch (Exception e) {
            return null;
        }
        return method;
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 调用方法
     * @param:
     * @create 2018-08-20 11:18
     **/
    public Object callMethod(Method method, Class clazz, Object params) {
        Object object = null;
        try {
            if (method != null) {
                object = method.invoke(clazz.newInstance(), params);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return object;
    }


}