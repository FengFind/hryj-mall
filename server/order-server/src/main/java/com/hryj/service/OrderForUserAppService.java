package com.hryj.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hryj.cache.CodeCache;
import com.hryj.cache.util.CalculateTaxUtil;
import com.hryj.common.*;
import com.hryj.constant.OrderConstants;
import com.hryj.entity.bo.cart.ShoppingCartRecord;
import com.hryj.entity.bo.order.OrderInfo;
import com.hryj.entity.vo.ListResponseVO;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.RequestVO;
import com.hryj.entity.vo.cart.ShoppingCartPoductVO;
import com.hryj.entity.vo.cart.request.CartOperationRequestVO;
import com.hryj.entity.vo.order.*;
import com.hryj.entity.vo.order.request.OrderForPayRequestVO;
import com.hryj.entity.vo.order.request.OrderIdVO;
import com.hryj.entity.vo.order.request.OrderListRequestVO;
import com.hryj.entity.vo.order.response.*;
import com.hryj.entity.vo.order.response.opentime.OpenTimeResponse;
import com.hryj.entity.vo.product.common.request.ProductValidateItem;
import com.hryj.entity.vo.product.common.request.ProductsValidateRequestVO;
import com.hryj.entity.vo.product.common.response.ProductValidateResponseItem;
import com.hryj.entity.vo.product.common.response.ProductsValidateResponseVO;
import com.hryj.entity.vo.product.crossborder.response.CrossBorderProductValidateResponseItem;
import com.hryj.entity.vo.promotion.activity.response.ActivityInProgressProductItemResponseVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdRequestVO;
import com.hryj.entity.vo.staff.dept.request.DeptIdsRequestVO;
import com.hryj.entity.vo.staff.dept.response.DeptIdByStoreOrWarehouseResponseVO;
import com.hryj.entity.vo.sys.response.CodeInfoVO;
import com.hryj.entity.vo.user.UserIdentityCardVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.exception.BizException;
import com.hryj.feign.ProductFeignClient;
import com.hryj.feign.StoreFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.*;
import com.hryj.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author 罗秋涵
 * @className: OrderForUserAppService
 * @description: 用户订单服务
 * @create 2018/7/4 0004 20:28
 **/
@Slf4j
@Service
public class OrderForUserAppService extends ServiceImpl<OrderForUserAppMapper, OrderInfo> {

    @Autowired
    private OrderForUserAppMapper orderForUserAppMapper;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderProductMapper orderProductMapper;

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StoreFeignClient storeFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private OrderReturnMapper orderReturnMapper;

    @Autowired
    private OrderForStoreMapper orderForStoreMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderSettlementResponseVO>
     * @author 罗秋涵
     * @description: 订单确认
     * @param: [cartoPerationRequestVO]
     * @create 2018-07-04 20:37
     **/
    public Result<OrderSettlementResponseVO> settlementOrder(CartOperationRequestVO cartoPerationRequestVO) {
        //参数判断
        if(cartoPerationRequestVO==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"参数不能为空");
        }
        if(StrUtil.isEmpty(cartoPerationRequestVO.getCartItemIds())){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"购物车编号不能为空");
        }
        if(StrUtil.isEmpty(cartoPerationRequestVO.getPay_price())){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"订单金额不能为空");
        }
        List<String> idsList = CommonUtil.stringToList(cartoPerationRequestVO.getCartItemIds());
        if(idsList==null||idsList.size()==0){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"购物车编号不能为空");
        }
        //获取对应的购物车记录
        List<ShoppingCartRecord> cartRecords = shoppingCartService.findShoppingCartRecordList(idsList);
        if(cartRecords==null||cartRecords.size()==0){
            return new Result(CodeEnum.FAIL_BUSINESS,"无对应购物车记录");
        }
        //设置用户收货地址
        if (cartoPerationRequestVO.getUser_id() == null) {
            Long user_id=orderService.getUserLoginUserId(cartoPerationRequestVO.getLogin_token());
            if(user_id==null||user_id <=0){
                return new Result(CodeEnum.FAIL_BUSINESS,"用户信息异常");
            }
            cartoPerationRequestVO.setUser_id(user_id);
        }
        //获取用户信息
        Result<UserInfoVO> result = userFeignClient.findUserInfoVOByUserId(cartoPerationRequestVO.getUser_id(), null);
        if(result.isSuccess()&&result.getData()!=null){
            //数据处理
            Result<OrderSettlementResponseVO> responseVOResult = this.build(cartRecords, result.getData(), new BigDecimal(cartoPerationRequestVO.getPay_price()));
            if(responseVOResult.isSuccess()&&responseVOResult.getData()!=null){
                return new Result(CodeEnum.SUCCESS,responseVOResult.getData());
            }else{
                return responseVOResult;
            }
        }
        return new Result(CodeEnum.FAIL_BUSINESS);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.ListResponseVO>
     * @author 罗秋涵
     * @description: 查询用户订单
     * @param: [orderListRequestVO]
     * @create 2018-07-06 11:45
     **/
    public Result<ListResponseVO<OrderListInfoVO>> findWaitPayOrderList(OrderListRequestVO orderListRequestVO) {
        //参数校验
        if (orderListRequestVO.getOrder_status() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单状态不能为空");
        }
        ListResponseVO response = new ListResponseVO();
        //如果user_id=null 说明不是代下单
        if (orderListRequestVO.getUser_id() == null) {
            //获取用户信息
            Long userId = orderService.getUserLoginUserId(orderListRequestVO.getLogin_token());
            orderListRequestVO.setUser_id(userId);
        } else {
            //获取员工信息
            Long userId = orderService.getStaffAppLoginUserId(orderListRequestVO.getLogin_token());
            orderListRequestVO.setHelp_staff_id(userId);
            orderListRequestVO.setUser_id(orderListRequestVO.getUser_id());
        }
        //获取订单信息
        List<OrderListInfoVO> orderList = orderForUserAppMapper.findWaitPayOrderList(orderListRequestVO);
        if (orderList == null) {
            orderList = new ArrayList<>();
        } else {
            for (int i = 0; i < orderList.size(); i++) {
                OrderListInfoVO infoVO = orderList.get(i);
                //获取订单商品详细
                List<OrderPorductVO> orderProductList = orderProductMapper.getOrderPorductVOList(infoVO.getOrder_id());
                // start add 商品活动信息调用接口获取 by luoqh  2018-08-21
                //获取活动信息
                orderProductList=orderService.getOrderProductActivityInfo(orderProductList);
                orderList.get(i).setOrderProductList(orderProductList);
                // end add 商品活动信息调用接口获取 by luoqh  2018-08-21
            }
        }
        response.setRecords(orderList);
        return new Result(CodeEnum.SUCCESS, response);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.PageResponseVO                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               <                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               com.hryj.entity.vo.order.OrderListInfoVO>>
     * @author 罗秋涵
     * @description: 分页查询用户订单
     * @param: [orderListRequestVO]
     * @create 2018-07-06 14:01
     **/
    public Result<PageResponseVO<OrderListInfoVO>> findOrderListByOrderStatus(OrderListRequestVO orderListRequestVO) {

        if (CodeCache.getValueByKey("OrderStatus", "S01").equals(orderListRequestVO.getOrder_status())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "请调用查询待支付接口");
        }

        //如果user_id=null 说明不是代下单
        if (orderListRequestVO.getUser_id() == null) {
            //获取用户信息
            Long userId = orderService.getUserLoginUserId(orderListRequestVO.getLogin_token());
            orderListRequestVO.setUser_id(userId);
        } else {
            //门店员工
            Long userId = orderService.getStaffAppLoginUserId(orderListRequestVO.getLogin_token());
            orderListRequestVO.setHelp_staff_id(userId);
        }
        Page page = new Page(orderListRequestVO.getPage_num(), orderListRequestVO.getPage_size());
        //如果状态为已完成，把取消订单同时查出
        if(CodeCache.getValueByKey("OrderStatus","S08").equals(orderListRequestVO.getOrder_status())){
            orderListRequestVO.setOrder_status(orderListRequestVO.getOrder_status()+","+CodeCache.getValueByKey("OrderStatus","S07"));
        }
        //获取订单信息
        List<OrderListInfoVO> orderList = orderForUserAppMapper.findOrderListByOrderStatus(orderListRequestVO, page);
        if (orderList != null) {
            for (int i = 0; i < orderList.size(); i++) {
                OrderListInfoVO infoVO = orderList.get(i);
                //获取订单商品详细
                List<OrderPorductVO> orderProductList = orderProductMapper.getOrderPorductVOList(infoVO.getOrder_id());
                // start add 商品活动信息调用接口获取 by luoqh  2018-08-21
                //获取活动信息
                orderProductList=orderService.getOrderProductActivityInfo(orderProductList);
                // end add 商品活动信息调用接口获取 by luoqh  2018-08-21
                orderList.get(i).setOrderProductList(orderProductList);
            }
        } else {
            orderList = new ArrayList<>();
        }
        PageResponseVO pageResponseVO = new PageResponseVO();
        pageResponseVO.setRecords(orderList);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());

        return new Result(CodeEnum.SUCCESS, pageResponseVO);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderDetailResponseVO>
     * @author 罗秋涵
     * @description: 根据订单编号查询订单详细
     * @param: [orDerIdVO]
     * @create 2018-07-06 14:20
     **/
    public Result<OrderDetailResponseVO> findOrderDetail(OrderIdVO orderIdVO) {
        OrderDetailResponseVO responseVO = new OrderDetailResponseVO();

        //参数校验
        if(orderIdVO==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if(orderIdVO.getOrder_id()==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        //订单基本信息
        OrderInfoVO orderInfoVO = orderInfoMapper.getOrderInfoVO(orderIdVO.getOrder_id());
        //根据门店编号获取名店信息
        DeptIdRequestVO dept = new DeptIdRequestVO();
        dept.setDept_id(orderInfoVO.getParty_id());
        Result<DeptIdByStoreOrWarehouseResponseVO> storeInfo = storeFeignClient.getDeptIdByStoreOrWarehouseDet(dept);
        if(storeInfo==null||storeInfo.getData()==null){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品对应门店无效");
        }
        String party_address="";

        if(StrUtil.isNotEmpty(storeInfo.getData().getCity_name())){
            party_address=party_address+(storeInfo.getData().getCity_name());
        }
        if(StrUtil.isNotEmpty(storeInfo.getData().getArea_name())){
            party_address=party_address+(storeInfo.getData().getArea_name());
        }
        if(StrUtil.isNotEmpty(storeInfo.getData().getDetail_address())){
            party_address=party_address+(storeInfo.getData().getDetail_address());
        }
        orderInfoVO.setParty_contact_name(storeInfo.getData().getContact_name());
        orderInfoVO.setParty_contact_phone(storeInfo.getData().getTelephone());
        orderInfoVO.setParty_address(party_address);
        //如果订单状态待支付计算剩余时间
        if (orderInfoVO.getOrder_status().equals(CodeCache.getValueByKey("OrderStatus", "S01"))) {
            //当前时间减去创建时间
            long datetime = DateUtils.addDays(orderInfoVO.getCreate_time(), 1).getTime() - System.currentTimeMillis();
            orderInfoVO.setLastTime(String.valueOf(datetime));
        }
        //获取订单退货信息
        ReturnOrderDetailsResponseVO orderDetailsResponseVO = orderReturnMapper.getReturnDetails(orderInfoVO.getOrder_id());
        if (orderDetailsResponseVO != null) {
            orderDetailsResponseVO.setReturn_reason(CodeCache.getNameByValue("ReturnReason", orderDetailsResponseVO.getReturn_reason()));
        }else{
            orderDetailsResponseVO=new ReturnOrderDetailsResponseVO();
        }
        //订单物流信息
        OrderLogisticsVO orderLogisticsVO = orderForUserAppMapper.getOrderLogistics(orderIdVO.getOrder_id(), CodeCache.getValueByKey("DistributionType", "S01"));
        if(orderLogisticsVO==null){
            orderLogisticsVO=new OrderLogisticsVO();
            orderLogisticsVO.setSelf_pick_contact(storeInfo.getData().getContact_name());
            orderLogisticsVO.setSelf_pick_phone(storeInfo.getData().getTelephone());
            orderLogisticsVO.setSelf_pick_address(party_address);
        }else{
            orderLogisticsVO.setSelf_pick_contact(storeInfo.getData().getContact_name());
            orderLogisticsVO.setSelf_pick_phone(storeInfo.getData().getTelephone());
            orderLogisticsVO.setSelf_pick_address(party_address);
        }
        //订单商品
        List<OrderPorductVO> orderPorductLis = orderProductMapper.getOrderPorductVOList(orderIdVO.getOrder_id());
        if(orderPorductLis==null){
            orderPorductLis= new ArrayList<>();
        }else{
            // start add 商品活动信息调用接口获取 by luoqh  2018-08-21
            //获取活动信息
            orderPorductLis=orderService.getOrderProductActivityInfo(orderPorductLis);
            // end add 商品活动信息调用接口获取 by luoqh  2018-08-21
        }

        responseVO.setOrderInfoVO(orderInfoVO);
        responseVO.setOderDetailsResponseVO(orderDetailsResponseVO);
        responseVO.setOrderLogisticsVO(orderLogisticsVO);
        responseVO.setOrderPorductList(orderPorductLis);
        return new Result<>(CodeEnum.SUCCESS, responseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderDifferentStateNumResponseVO>
     * @author 罗秋涵
     * @description: 获取用户各状态订单数量
     * @param: [requestVO]
     * @create 2018-07-09 16:19
     **/
    public Result<OrderDifferentStateNumResponseVO> getOrderDifferentStateNum(RequestVO requestVO) {
        //获取用户编号
        Long userId = orderService.getUserLoginUserId(requestVO.getLogin_token());
        OrderDifferentStateNumResponseVO responseVO = orderForUserAppMapper.getOrderDifferentStateNum(CodeCache.getValueByKey("OrderStatus", "S01"),
                CodeCache.getValueByKey("OrderStatus", "S02"),
                CodeCache.getValueByKey("OrderStatus", "S04"),
                CodeCache.getValueByKey("OrderStatus", "S05"), userId);
        return new Result(CodeEnum.SUCCESS, responseVO);
    }

    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderToPayResponseVO>
     * @author 罗秋涵
     * @description: 选择多个代支付订单支付
     * @param: [orderForPayRequestVO]
     * @create 2018-07-12 11:44
     **/
    public Result<OrderToPayResponseVO> selectOrderForPay(OrderForPayRequestVO orderForPayRequestVO) {
        //参数判断
        if (orderForPayRequestVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (StrUtil.isEmpty(orderForPayRequestVO.getOrderNumStr())) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        List<String> numlist = CommonUtil.stringToList(orderForPayRequestVO.getOrderNumStr());
        if(numlist==null||numlist.size()==0){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        OrderToPayResponseVO orderToPayResponseVO = new OrderToPayResponseVO();
        //1、获取订单基本信息
        List<OrderInfo> orderInfoList = orderService.findOrderInfoList(numlist);
        if(orderInfoList==null||orderInfoList.size()==0){
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应订单信息");
        }
        BigDecimal pay_amt = new BigDecimal(0);
        StringBuffer orderNum = new StringBuffer();

        for (int i = 0; i < orderInfoList.size(); i++) {
            OrderInfo orderInfo = orderInfoList.get(i);
            if(!CodeCache.getValueByKey("OrderStatus","S01").equals(orderInfo.getOrder_status())){
                return new Result(CodeEnum.FAIL_BUSINESS, "订单状态变动，请刷新列表");
            }
            pay_amt = NumberUtil.add(pay_amt,orderInfo.getPay_amt());
            if (i == orderInfoList.size() - 1) {
                orderNum.append(orderInfo.getOrder_num());
            } else {
                orderNum.append(orderInfo.getOrder_num() + ",");
            }
        }
        orderToPayResponseVO.setPay_amt(pay_amt.toString());
        orderToPayResponseVO.setOrderNumStr(orderNum.toString());
        List<CodeInfoVO> codeInfoVOS = CodeCache.getCodeList(SysCodeTypeEnum.PAYMENTMETHOD.getType());
        List<String> payMethodList = new ArrayList<>();
        for (CodeInfoVO v : codeInfoVOS) {
            payMethodList.add(v.getCode_value());
        }
        //设置有效的支付类型类别
        orderToPayResponseVO.setPayMethodList(payMethodList);
        return new Result(CodeEnum.SUCCESS, orderToPayResponseVO);
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 确认收货
     * @param:
     * @create 2018-07-20 14:23
     **/
    public Result confirmReceive(OrderIdVO orderIdVO) {
        //参数判断
        if (orderIdVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (orderIdVO.getOrder_id()==null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单Id不能为空");
        }
        OrderInfo orderInfo=orderService.selectById(orderIdVO.getOrder_id());
        if(!SysCodeEnmu.ORDERSTATUS_04.getCodeValue().equals(orderInfo.getOrder_status())){
            return new Result(CodeEnum.FAIL_BUSINESS, "该订单状态不能确认收货");
        }
        OrderStatusVO vo = new OrderStatusVO();
        vo.setOrder_id(orderIdVO.getOrder_id());
        vo.setOrder_status(SysCodeEnmu.ORDERSTATUS_08.getCodeValue());
        vo.setLogin_token(orderIdVO.getLogin_token());
        vo.setStatus_remark("确认收货");
        vo.setChange_reason("确认收货");
        vo.setComplete_time(new Date());
        vo.setUser_id(orderInfo.getUser_id());
        orderService.updateStatus(vo);
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @return com.hryj.common.Result<com.hryj.entity.vo.order.response.OrderConfirmInfoResponseVO>
     * @author 白飞
     * @description:  构建订单
     * @param: [cartItems,userInfo,totalAmount]
     * @create 2018-08-16 17:16
     **/
    public Result<OrderSettlementResponseVO> build(List<ShoppingCartRecord> cartItems, UserInfoVO userInfo, BigDecimal totalAmount){
        if(cartItems == null || cartItems.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"购物车不能为空");
        }
        if(userInfo == null){
            return new Result(CodeEnum.FAIL_BUSINESS,"用户信息不能为空");
        }
        OrderSettlementResponseVO orderConfirmInfoResponseVO = new OrderSettlementResponseVO();
        //数量Map存入购物车记录对应商品数量，用户验证总金额
        Map<Long,Integer> quantityNum = Maps.newHashMap();
        //提交确认订单总金额用于金额校验
        BigDecimal totalPrice=new BigDecimal(0);
        //商品校验请求
        ProductsValidateRequestVO productsValidateRequestVO = new ProductsValidateRequestVO();
        List<ProductValidateItem> productValidateItems = Lists.newArrayList();
        //循环组装校验请求数据
        for (ShoppingCartRecord cartRecord : cartItems) {
            //存入数量用户计算实际总金额
            quantityNum.put(cartRecord.getId(), cartRecord.getQuantity());
            ProductValidateItem productValidate = new ProductValidateItem();
            productValidate.setParty_id(cartRecord.getParty_id());
            productValidate.setProduct_id(cartRecord.getProduct_id());
            productValidate.setActivity_id(cartRecord.getActivity_id());
            productValidate.setRequired_min_inventory_quantity(cartRecord.getQuantity());
            productValidate.setFollow_value(cartRecord.getId() + "");
            productValidateItems.add(productValidate);
        }
        productsValidateRequestVO.setReturn_tax_rate(true);
        productsValidateRequestVO.setProd_summary_list(productValidateItems);
        //发起校验
        Result<ProductsValidateResponseVO> productsValidateResult = productFeignClient.productsValidate(productsValidateRequestVO);
        if(productsValidateResult == null || productsValidateResult.getCode() != CodeEnum.SUCCESS.getCode() || productsValidateResult.getData() == null || productsValidateResult.getData().getProd_validate_result_list() == null){
            return new Result(CodeEnum.FAIL_BUSINESS,"商品校验失败");
        }
        //获得校验结果
        List<ProductValidateResponseItem> productValidateResponseItems = productsValidateResult.getData().getProd_validate_result_list();
        //临时存储验证商品信息
        Map<Long, ProductValidateResponseItem> productValidateResponseItemMap = Maps.newHashMap();
        //根据商品ID分组
        Map<Long, ProductValidateResponseItem> productResponseItemMap = Maps.newHashMap();
        //是否跨境商品
        boolean is_bonded = false;
        //根据商品类型ID分组
        Set<String> productTypeIds = Sets.newHashSet();
        //遍历校验结果
        for (ProductValidateResponseItem productValidateResponseItem : productValidateResponseItems) {
            Long cartId = new Long(productValidateResponseItem.getFollow_value());
            Integer quantity = quantityNum.get(cartId);
            if(StringUtils.isEmpty(productValidateResponseItem.getProduct_type_id())){
                return new Result(CodeEnum.FAIL_PARAMCHECK, "商品类型不能为空");
            }
            if(quantity == null || quantity <= 0 || productValidateResponseItem.getThis_moment_sale_price() == null || productValidateResponseItem.getThis_moment_sale_price().compareTo(new BigDecimal(0)) < 0){
                return new Result(CodeEnum.FAIL_BUSINESS,"商品数据异常");
            }
            if (!productValidateResponseItem.getIs_valid()) {
                log.info("确认订单商品：{}无效，原因：{}", productValidateResponseItem.getProduct_id(), productValidateResponseItem.getOther_comments());
                return new Result(CodeEnum.FAIL_BUSINESS.getCode(), productValidateResponseItem.getOther_comments(), productValidateResponseItem.getValidate_status_code());
            }
            //叠加总价
            totalPrice = NumberUtil.add(totalPrice,NumberUtil.mul(new BigDecimal(quantity), productValidateResponseItem.getThis_moment_sale_price()));
            productValidateResponseItemMap.put(cartId, productValidateResponseItem);
            productResponseItemMap.put(productValidateResponseItem.getProduct_id(), productValidateResponseItem);
            productTypeIds.add(productValidateResponseItem.getProduct_type_id());
            //等到商品校验接口给商品类型才能判断是否需要显示购买人姓名和身份证号
            if(ArrayUtils.contains(OrderConstants.CROSS_BORDER_ORDER_TYPES, productValidateResponseItem.getProduct_type_id())){
                is_bonded = true;
            }
        }
        if(productTypeIds.size() != 1){
            return new Result(CodeEnum.FAIL_PARAMCHECK, "只能购买同一类型商品");
        }
        //比较金额-格式化两位小数
        totalAmount = NumberUtil.roundHalfEven(totalAmount, 2);
        totalPrice = NumberUtil.roundHalfEven(totalPrice, 2);
        if(totalPrice.compareTo(totalAmount) != 0){
            return new Result(CodeEnum.FAIL_BUSINESS.getCode(),"商品价格变动，请重新确认订单", BizCodeEnum.PRODUCT_PRICE_VARIATION.getCode());
        }
        //根据门店分组
        Map<Long, List<ShoppingCartRecord>> partyGroupMap = Maps.newHashMap();
        //门店ID
        Set<Long> storeIds = Sets.newHashSet();
        for (ShoppingCartRecord record : cartItems) {
            if (partyGroupMap.get(record.getParty_id()) == null) {
                List<ShoppingCartRecord> recordList = Lists.newArrayList();
                recordList.add(record);
                partyGroupMap.put(record.getParty_id(), recordList);
            } else {
                partyGroupMap.get(record.getParty_id()).add(record);
            }
            storeIds.add(record.getParty_id());
        }
        //门店查询
        DeptIdsRequestVO deptIdsRequest = new  DeptIdsRequestVO();
        deptIdsRequest.setDept_ids(new ArrayList<>(storeIds));
        Result<ListResponseVO<DeptIdByStoreOrWarehouseResponseVO>> storeResult = this.storeFeignClient.getAppDeptIdsByStoreOrWarehouseDet(deptIdsRequest);
        if(storeResult == null || storeResult.getCode() != CodeEnum.SUCCESS.getCode() || storeResult.getData() == null || storeResult.getData().getRecords() == null){
            return new Result(CodeEnum.FAIL_BUSINESS,"门店信息不存在");
        }
        //门店信息集合
        List<DeptIdByStoreOrWarehouseResponseVO> storeList = storeResult.getData().getRecords();
        if(storeList.size() == 0){
            return new Result(CodeEnum.FAIL_BUSINESS,"门店信息不存在");
        }
        if(storeList.size() != storeIds.size()){
            return new Result(CodeEnum.FAIL_BUSINESS,"门店信息不一致");
        }
        Map<Long, DeptIdByStoreOrWarehouseResponseVO> storeInfoMap = Maps.newHashMap();
        for(DeptIdByStoreOrWarehouseResponseVO store : storeList){
            if(store.getDept_status() == 0){
                return new Result(CodeEnum.FAIL_BUSINESS,store.getDept_name() + "[已停用]");
            }
            //保存门店校验结果
            storeInfoMap.put(store.getDept_id(), store);
        }
        //合计总金额
        BigDecimal orderTotalAmount = new BigDecimal(0);
        //遍历门店商品，组装返回信息
        List<OrderConfirmProductResponseVO> productList = Lists.newArrayList();
        for(Map.Entry<Long, List<ShoppingCartRecord>> entry : partyGroupMap.entrySet()){
            //门店或仓库信息
            Long partyId = entry.getKey();
            DeptIdByStoreOrWarehouseResponseVO storeInfo = storeInfoMap.get(partyId);
            OrderConfirmProductResponseVO productResponseVO = new OrderConfirmProductResponseVO();
            //购买的商品信息
            List<ShoppingCartRecord> cartRecords = entry.getValue();
            if (storeInfo.getDept_type().equals(CodeCache.getValueByKey("DeptType", "S01"))) {
                //根据门店营业时间范围，获取用户配送时间选择列表
                List<OpenTimeResponse> result = orderService.getOpeningHoursList(storeInfo.getBusiness_time_start(), storeInfo.getBusiness_time_end(), 4);
                productResponseVO.setDelivery_time(result);
                productResponseVO.setDelivery_info(CodeCache.getValueByKey("StoreDeliveryMsg", "S01"));
            }else{
                productResponseVO.setDelivery_info(CodeCache.getValueByKey("WarehouseDistributionDesc", "S01"));
            }
            productResponseVO.setParty_id(storeInfo.getDept_id());
            productResponseVO.setDept_type(storeInfo.getDept_type());
            productResponseVO.setParty_name(storeInfo.getDept_name());
            productResponseVO.setParty_contact_name(storeInfo.getContact_name());
            productResponseVO.setParty_contact_phone(storeInfo.getTelephone());
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
            productResponseVO.setParty_address(party_address);
            List<ShoppingCartPoductVO> shoppingCartList = new ArrayList<>();
            //折扣总价
            BigDecimal discountAmount = new BigDecimal(0);
            //订单金额
            BigDecimal orderAmount = new BigDecimal(0);
            //税费金额
            BigDecimal orderTaxAmount = new BigDecimal(0);

            //遍历商品
            for (ShoppingCartRecord cartRecord : cartRecords) {
                //获取商品校验信息
                ProductValidateResponseItem productValidateResponseItem = productValidateResponseItemMap.get(cartRecord.getId());
                //根据商品基本信息、商品校验信息组装ShoppingCartPoductVO
                ShoppingCartPoductVO poduct = new ShoppingCartPoductVO();
                //获取优惠
                BigDecimal discount = productValidateResponseItem.getNormal_price().subtract(productValidateResponseItem.getThis_moment_sale_price());
                poduct.setDiscount_amt(discount + "");
                poduct.setQuantity(cartRecord.getQuantity());
                poduct.setSale_price(productValidateResponseItem.getNormal_price() + "");
                poduct.setActivity_id(cartRecord.getActivity_id());
                poduct.setParty_id(cartRecord.getParty_id());
                poduct.setProduct_id(cartRecord.getProduct_id());
                poduct.setProduct_name(productValidateResponseItem.getProduct_name());
                //获取活动信息
                ActivityInProgressProductItemResponseVO activity = productValidateResponseItem.getPromotion_info();
                poduct.setActivity_mark_image(null != activity ? activity.getActivity_mark_image() : null);
                poduct.setActivity_type(null != activity ?activity.getActivity_type() : null);
                poduct.setCart_record_id(cartRecord.getId());
                poduct.setInto_cart_price(productValidateResponseItem.getThis_moment_sale_price() + "");
                poduct.setInventory_quantity(productValidateResponseItem.getInventory_quantity());
                poduct.setList_image_url(productValidateResponseItem.getList_image_url());
                poduct.setActivity_price(null != cartRecord.getActivity_id() && cartRecord.getActivity_id() > 0L ? productValidateResponseItem.getThis_moment_sale_price() + "" : null);
                poduct.setProduct_type_id(productValidateResponseItem.getProduct_type_id());
                poduct.setProduct_type_name(productValidateResponseItem.getProduct_type_name());
                poduct.setTitle_mark(productValidateResponseItem.getTitle_mark_list() != null ? productValidateResponseItem.getTitle_mark_list().get(0) : null);
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(poduct.getDiscount_amt())){
                    //叠加优惠金额
                    discountAmount = NumberUtil.add(discountAmount, NumberUtil.mul(new BigDecimal(poduct.getDiscount_amt()), new BigDecimal(poduct.getQuantity())));
                }
                //叠加订单金额
                if (poduct.getActivity_id() != null && poduct.getActivity_id() > 0 && org.apache.commons.lang3.StringUtils.isNotEmpty(poduct.getActivity_price())) {
                    poduct.setSale_price(poduct.getActivity_price());
                    orderAmount = NumberUtil.add(orderAmount,NumberUtil.mul(new BigDecimal(poduct.getActivity_price()),new BigDecimal(poduct.getQuantity())));
                    //叠加合计金额
                    orderTotalAmount = NumberUtil.add(orderTotalAmount, (NumberUtil.mul(new BigDecimal(poduct.getActivity_price()), new BigDecimal(poduct.getQuantity()))));
                } else {
                    orderAmount = NumberUtil.add(orderAmount, NumberUtil.mul(new BigDecimal(poduct.getSale_price()), new BigDecimal(poduct.getQuantity())));
                    //叠加合计金额
                    orderTotalAmount = NumberUtil.add(orderTotalAmount, (NumberUtil.mul(new BigDecimal(poduct.getSale_price()), new BigDecimal(poduct.getQuantity()))));
                }

                //判断跨境商品合计税费
                if(is_bonded){
                    ProductValidateResponseItem productItem = productResponseItemMap.get(poduct.getProduct_id());
                    if(null == productItem){
                        throw new BizException("抱歉！系统参数异常，请联系客服");
                    }
                    CrossBorderProductValidateResponseItem crossBorderProduct = productItem.getCrossBorderProductValidateResponseItem();
                    if(crossBorderProduct == null){
                        throw new BizException("抱歉！系统参数异常，请联系客服");
                    }
                    BigDecimal goodsTaxAmount = CalculateTaxUtil.calculateTax(new BigDecimal(poduct.getSale_price()), poduct.getQuantity(), crossBorderProduct.getUnit_1(), crossBorderProduct.getUnit_2(), crossBorderProduct.getIncrement_tax(), crossBorderProduct.getConsume_tax());
                    if(goodsTaxAmount.compareTo(new BigDecimal(0)) <= 0){
                        throw new BizException("抱歉！税金计算异常，请联系客服");
                    }
                    orderTaxAmount = NumberUtil.add(orderTaxAmount, goodsTaxAmount);
                }
                shoppingCartList.add(poduct);
            }
            //跨境商品是否超额 + 提示
            if(is_bonded){
                orderTotalAmount = NumberUtil.add(orderTotalAmount, NumberUtil.roundHalfEven(orderTaxAmount,2));
                String max_amount = CodeCache.getNameByValue("CrossBorderOrderLimitGroup","CrossBorderOrderMaxAmount");
                if(StringUtils.isNotEmpty(max_amount) && new BigDecimal(max_amount).compareTo(new BigDecimal(0)) > 0 && orderAmount.compareTo(new BigDecimal(max_amount)) >= 0){
                    productResponseVO.setMax_amt(max_amount);
                    productResponseVO.setBuy_tips(CodeCache.getNameByValue("CrossBorderOrderLimitGroup", "CrossBorderOrderMaxAmountTips"));
                }
            }
            productResponseVO.setTax_amt(NumberUtil.roundHalfEven(orderTaxAmount, 2).toString());
            productResponseVO.setShoppingCartList(shoppingCartList);
            productResponseVO.setDiscount_amt(discountAmount.toString());
            productResponseVO.setOrder_amt(NumberUtil.roundHalfEven(NumberUtil.add(orderTaxAmount, orderAmount),2).toString());
            productList.add(productResponseVO);
        }
        //跨境商品，获取默认身份证
        orderConfirmInfoResponseVO.setIs_show_buyer(is_bonded);
        if(is_bonded){
            Result<UserIdentityCardVO> userIdentityCardResult = this.userFeignClient.getUserDefaultIdentityCard(userInfo.getUser_id());
            if(userIdentityCardResult != null && userIdentityCardResult.getCode() == CodeEnum.SUCCESS.getCode() && userIdentityCardResult.getData() != null){
                orderConfirmInfoResponseVO.setBuyer_name(userIdentityCardResult.getData().getTrue_name());
                orderConfirmInfoResponseVO.setBuyer_id_card(userIdentityCardResult.getData().getIdentity_card());
            }
        }
        //获取用户地址
        UserReceiveAddressVO userReceiveAddress = new UserReceiveAddressVO();
        userReceiveAddress.setReceive_name(userInfo.getUserAddress().getReceive_name());
        userReceiveAddress.setReceive_phone(userInfo.getUserAddress().getReceive_phone());
        userReceiveAddress.setReceive_address(userInfo.getUserAddress().getLocation_address()+userInfo.getUserAddress().getDetail_address());
        orderConfirmInfoResponseVO.setUserReceiveAddressVO(userReceiveAddress);
        orderConfirmInfoResponseVO.setProductList(productList);
        orderConfirmInfoResponseVO.setOrder_amt(NumberUtil.roundHalfEven(orderTotalAmount, 2).toString());

        return new Result(CodeEnum.SUCCESS, orderConfirmInfoResponseVO);
    }

}

