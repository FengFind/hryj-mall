package com.hryj.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.hryj.cache.CodeCache;
import com.hryj.cache.LoginCache;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.common.SysCodeEnmu;
import com.hryj.common.ThirdOrderStatusEnum;
import com.hryj.entity.bo.order.*;
import com.hryj.entity.vo.PageResponseVO;
import com.hryj.entity.vo.order.*;
import com.hryj.entity.vo.order.request.*;
import com.hryj.entity.vo.order.response.AdminOrderDetailResponseVO;
import com.hryj.entity.vo.order.response.AdminOrderReturnResponseVO;
import com.hryj.entity.vo.order.response.opentime.OpenTimeResponse;
import com.hryj.entity.vo.order.response.opentime.TimeQuantum;
import com.hryj.entity.vo.staff.user.StaffAdminLoginVO;
import com.hryj.entity.vo.staff.user.StaffAppLoginVO;
import com.hryj.entity.vo.staff.user.StaffDeptVO;
import com.hryj.entity.vo.user.UserInfoVO;
import com.hryj.exception.BizException;
import com.hryj.feign.StaffFeignClient;
import com.hryj.feign.UserFeignClient;
import com.hryj.mapper.*;
import com.hryj.permission.PermissionManageHandler;
import com.hryj.scheduled.OrderScheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author 叶方宇
 * @className: OrderAdminService
 * @description:
 * @create 2018/7/4 0004 17:15
 **/
@Slf4j
@Service
public class OrderAdminService extends ServiceImpl<OrderInfoMapper, OrderInfo> {

    @Autowired
    private OrderInfoMapper orderInfoMapper;//订单信息

    @Autowired
    private OrderReturnMapper orderReturnMapper;//退货信息

    @Autowired
    private OrderExpressMapper orderExpressMapper;//物流信息

    @Autowired
    private OrderProductMapper orderProductMapper;//订单商品,优惠金额,实付金额

    @Autowired
    private OrderDistributionMapper orderDistributionMapper;//订单分配信息

    @Autowired
    private OrderDistributionProductMapper orderDistributionProductMapper;//配送单商品信息

    @Autowired
    private OrderStatusRecordMapper orderStatusRecordMapper;//订单进度

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderReturnProductMapper orderReturnProductMapper;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderForStoreService orderForStoreService;

    @Autowired
    private StaffFeignClient staffFeignClien;

    @Autowired
    private OrderScheduled orderScheduled;
    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据订单Id查询后台所需订单详情信息
     * @param: order_id
     * @create 2018-07-04 18:22
     **/

    public Result<AdminOrderDetailResponseVO> getAdminOrderDetailResponse(OrderIdVO orderIdVO) {
        if(orderIdVO.getOrder_id()==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK);
        }
        AdminOrderDetailResponseVO responseVO = new AdminOrderDetailResponseVO();
        //获取订单详情
        OrderInfoVO u = orderInfoMapper.getOrderInfoVO(orderIdVO.getOrder_id());
        responseVO.setOrderInfoVO(u);
        //获取订单状态信息
        List<OrderStatusRecord> orderStatusList = orderStatusRecordMapper.getOrderStatusRecordVOList(orderIdVO.getOrder_id());
        List<OrderStatusRecordVO> orderStatusVOList = new ArrayList<>();
        OrderStatusRecordVO vo = null;
        for (int i = 0; i < orderStatusList.size(); i++) {//循环计算耗时
            vo = new OrderStatusRecordVO();
            vo.setChange_reason(orderStatusList.get(i).getChange_reason());
            vo.setOperator_id(orderStatusList.get(i).getOperator_id());
            vo.setOperator_name(orderStatusList.get(i).getOperator_name());
            vo.setOrder_status(orderStatusList.get(i).getOrder_status());
            vo.setRecord_time(DateUtil.format(orderStatusList.get(i).getRecord_time(), DatePattern.NORM_DATETIME_PATTERN));
            if (i == 0) {
                vo.setTake_time("0s");
            } else {//耗时判断，天/小时/分钟/秒
                vo.setTake_time(getTakeTimeString(orderStatusList.get(i - 1).getRecord_time().getTime(), orderStatusList.get(i).getRecord_time().getTime()));
            }
            orderStatusVOList.add(vo);
        }
        responseVO.setOrderStatusList(orderStatusVOList);
        //获取订单物流信息
        responseVO.setOrderLogisticsVO(orderExpressMapper.getOrderExpressMessage(orderIdVO.getOrder_id()));
        //订单退货信息
        List<AdminOrderReturnResponseVO> orderReturnList = orderReturnMapper.getAdminOrderDetailResponseVO(orderIdVO.getOrder_id());
        if (orderReturnList == null) orderReturnList = new ArrayList<>();
        responseVO.setOrderReturnList(orderReturnList);
        //获取订单商品信息列表
        List<OrderPorductVO> orderPorductList = orderProductMapper.getOrderPorductVOList(orderIdVO.getOrder_id());
        if (orderPorductList == null){
            orderPorductList = new ArrayList<>();
        }
        else{
            //总数量
            Integer countQuantity = 0;
            //原售价
            for(OrderPorductVO v : orderPorductList){
                countQuantity+= v.getQuantity();
            }
            responseVO.setCountQuantity(countQuantity.toString());
        }
        responseVO.setOrderPorductList(orderPorductList);
        //获取订单客户信息
        UserInfoVO user = userFeignClient.findUserInfoVOByUserId(u.getUser_id(),null).getData();
        OrderUserVO orderUserVO = new OrderUserVO();
        orderUserVO.setReferral_code(user.getReferral_code());
        orderUserVO.setStaff_name(user.getStaff_name());
        orderUserVO.setUser_address(user.getUserAddress().getLocation_address()+user.getUserAddress().getDetail_address());
        orderUserVO.setUser_name(user.getUserAddress().getReceive_name());
        orderUserVO.setUser_phone(user.getUserAddress().getReceive_phone());
        responseVO.setOrderUserVO(orderUserVO);
        return new Result(CodeEnum.SUCCESS, responseVO);
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 根据规则返回耗时字符串
     * @param:
     * @return
     * @create 2018-07-09 15:05
     **/
    private static int S = 60;//小于一分钟
    private static int H = 60 * 60;//小于一小时
    private static int D = 60 * 60 * 24;//小于一天

    public String getTakeTimeString(Long btime, Long etime) {
        long time = (etime - btime) / 1000;//总共多少秒
        if (time < S) {
            //小于一分钟
            return time + "s";
        } else if (time < H) { //小于一小时
            return time / S + "mins" + time % S + "s";
        } else if (time < D) {//小于24小时
            long m = time / S;//总共多少分钟
            //一天内,小时:分钟
            return m / S + "h" + m % S + "mins";
        } else {
            //大于一天  天:小:时
            long m = time / S;//总共多少分钟
            long day = time / D; //多少天
            return day + "day" + (m / 60) % 24 + "h" + m % 60 + "mins";
        }
    }


    public static void main(String[] args) {
        OrderAdminService o = new OrderAdminService();
        System.err.println(o.getTakeTimeString(1111500024123412l,1111501024123412l));
    }

    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 后台分页查询订单信息
     * @param:
     * @create 2018-07-05 11:03
     **/
    public Result<PageResponseVO<AdminOrderInfoVO>> getAdminOrderInfoVOList(AdminOrderListRequestVO adminOrderListRequestVO) {
        PageResponseVO pageResponseVO = new PageResponseVO();
        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(adminOrderListRequestVO.getLogin_token());
        if (null == staffAdminLoginVO) {
            return new Result(CodeEnum.FAIL_TOKEN_INVALID);
        }
        String deptPath = staffAdminLoginVO.getDeptGroup().getDept_path();
        adminOrderListRequestVO.setParty_path(deptPath);
        /*if(StrUtil.isNotEmpty(adminOrderListRequestVO.getOrder_status_list())){
            //如果包含已完成状态，需要将已取消订单一起查出
            if(adminOrderListRequestVO.getOrder_status_list().indexOf("08")>=0){
                adminOrderListRequestVO.setOrder_status_list(adminOrderListRequestVO.getOrder_status_list()+",07");
            }
        }*/
        if (!CodeCache.getValueByKey("StaffType", "S03").equals(staffAdminLoginVO.getStaff_type())) {//03-超级管理员
            adminOrderListRequestVO.setStaff_id(staffAdminLoginVO.getStaff_id());
        }
        Page page = new Page(adminOrderListRequestVO.getPage_num(), adminOrderListRequestVO.getPage_size());
        List<AdminOrderInfoVO> orderInfoVOList = orderInfoMapper.getOrderInfoVOListByPage(adminOrderListRequestVO, page);
        pageResponseVO.setRecords(orderInfoVOList);
        pageResponseVO.setTotal_count(page.getTotal());
        pageResponseVO.setTotal_page(page.getPages());
        return new Result<>(CodeEnum.SUCCESS, pageResponseVO);
    }


    /**
     * @return
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 添加物流信息（仓库发货时候，需要填写）
     * @param:
     * @create 2018-07-05 11:03
     **/
    @Transactional
    public Result insertDeliveryForExpress(OrderExpressVO orderExpressVO) {
        if(StringUtils.isEmpty(orderExpressVO.getOrder_id())){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"订单ID无效");
        }
        /*if(StringUtils.isEmpty(orderExpressVO.getExpress_code())){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"快递单号无效");
        }
        if(StringUtils.isEmpty(orderExpressVO.getExpress_id())){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"快递公司编号无效");
        }
        if(StringUtils.isEmpty(orderExpressVO.getExpress_name())){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"快递公司无效");
        }*/
        OrderExpress orderExpress = new OrderExpress();
        OrderInfoVO orderInfoVO = orderInfoMapper.getOrderInfoVO(orderExpressVO.getOrder_id());
        if(!SysCodeEnmu.ORDERSTATUS_02.getCodeValue().equals(orderInfoVO.getOrder_status())){
            return new Result(CodeEnum.FAIL_BUSINESS,"该订单不是待发货状态，不能录入快递物流信息");
        }
        OrderLogisticsVO exvo = orderExpressMapper.getOrderExpressMessage(orderExpressVO.getOrder_id());
        if(exvo !=null && StrUtil.isNotEmpty(exvo.getExpress_code())){
            return new Result(CodeEnum.FAIL_BUSINESS,"快递物流信息已录入");
        }

        //获取当前登录人信息
        /*StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(orderExpressVO.getLogin_token());
        if(staffAdminLoginVO==null||staffAdminLoginVO.getDeptGroup()==null){
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }*/

        //判断部门
        /*if(orderInfoVO.getParty_id()!=null&& !orderInfoVO.getParty_id().equals(staffAdminLoginVO.getDeptGroup().getId())){
            return new Result(CodeEnum.FAIL_BUSINESS,"无权操作订单");
        }*/
        //物流信息非必填，其中任何一样没有就不存数据
        if(!StringUtils.isEmpty(orderExpressVO.getExpress_code())
                ||!StringUtils.isEmpty(orderExpressVO.getExpress_id())
                ||!StringUtils.isEmpty(orderExpressVO.getExpress_name())) {
            orderExpress.setExpress_name(orderExpressVO.getExpress_name());
            orderExpress.setExpress_id(orderExpressVO.getExpress_id());
            orderExpress.setExpress_code(orderExpressVO.getExpress_code());
            orderExpress.setOrder_id(orderExpressVO.getOrder_id());
            orderExpress.setUser_address(orderInfoVO.getUser_address());
            orderExpress.setUser_name(orderInfoVO.getUser_name());
            orderExpress.setUser_id(orderInfoVO.getUser_id());
            orderExpress.setUser_phone(orderInfoVO.getUser_phone());
            orderExpressMapper.insert(orderExpress);
        }
        //更新订单状态
        OrderStatusVO vo = new OrderStatusVO();
        vo.setOrder_id(orderExpressVO.getOrder_id());
        vo.setOrder_status(SysCodeEnmu.ORDERSTATUS_04.getCodeValue());
        vo.setLogin_token(orderExpressVO.getLogin_token());
        vo.setChange_reason("仓库发货");
        vo.setStatus_remark("仓库发货");
        orderService.updateStatus(vo);
        return new Result<>(CodeEnum.SUCCESS);
    }



    /**
     * @author 罗秋涵
     * @description: 后台配分配，调度
     * @param: [deliveryForDistributionVO]
     * @return com.hryj.common.Result
     * @create 2018-07-10 16:22
     **/
    public Result deliveryForDistribution(DeliveryForDistributionVO deliveryForDistributionVO) {
        //参数判断
        if (deliveryForDistributionVO == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "参数不能为空");
        }
        if (deliveryForDistributionVO.getOrder_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "订单编号不能为空");
        }
        if (deliveryForDistributionVO.getStaff_id() == null) {
            return new Result(CodeEnum.FAIL_PARAMCHECK, "分配员工编号不能为空");
        }
        StaffAdminLoginVO staffAdminLoginVO = null;
        //根据编号修改记录
        OrderDistribution newdistribution = new OrderDistribution();
        staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(deliveryForDistributionVO.getLogin_token());
        OrderInfo orderInfo = orderInfoMapper.selectById(deliveryForDistributionVO.getOrder_id());
        if (orderInfo == null) {
            return new Result(CodeEnum.FAIL_BUSINESS, "无对应订单记录");
        }
        if (staffAdminLoginVO != null) {
            //判断部门
            if (orderInfo.getParty_id() != null && !orderInfo.getParty_id().equals(staffAdminLoginVO.getDeptGroup().getId())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "不能分配其他门店配送单");
            }
            newdistribution.setAssign_staff_id(staffAdminLoginVO.getStaff_id());
            newdistribution.setAssign_staff_name(staffAdminLoginVO.getStaff_name());
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "无效员工");
        }
        //获取退货处理人信息
        Result<StaffDeptVO> result = staffFeignClien.findStaffDeptVO(null, null, deliveryForDistributionVO.getStaff_id());
        //判断退货处理人是否在职，是否更改部门
        if (result.isSuccess() && result.getData() != null && result.getData().getDept_id() != null && result.getData().getStaff_status() != null) {
            if (!result.getData().getStaff_status() || !result.getData().getDept_id().equals(staffAdminLoginVO.getDeptGroup().getId())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "配送人信息异常");
            }
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "配送人信息异常");
        }
        //订单状态为待发货||已发货时才能调度
        if (CodeCache.getValueByKey("OrderStatus", "S02").equals(orderInfo.getOrder_status())
                || CodeCache.getValueByKey("OrderStatus", "S04").equals(orderInfo.getOrder_status())) {
            //查询配送单详情
            OrderDistribution distribution = orderDistributionMapper.getOrderDistributionInfo(deliveryForDistributionVO.getOrder_id(),
                    CodeCache.getValueByKey("DistributionType", "S01"));
            if (distribution == null) {
                return new Result(CodeEnum.FAIL_BUSINESS, "无对应记录");
            }
            //配送完成，取消配送，配送超时，状态不能再次分配
            if (CodeCache.getValueByKey("DistributionStatus", "S03").equals(distribution.getDistribution_status())
                    || CodeCache.getValueByKey("DistributionStatus", "S04").equals(distribution.getDistribution_status())
                    || CodeCache.getValueByKey("DistributionStatus", "S05").equals(distribution.getDistribution_status())) {
                return new Result(CodeEnum.FAIL_BUSINESS, "该配送单状态已结束，不能再次分配");
            }
            newdistribution.setId(distribution.getId());
            newdistribution.setDistribution_staff_id(deliveryForDistributionVO.getStaff_id());
            newdistribution.setDistribution_staff_name(deliveryForDistributionVO.getStaff_name());
            newdistribution.setDistribution_status(CodeCache.getValueByKey("DistributionStatus", "S02"));
            //获取配送费用
            Double distribution_amt = orderReturnMapper.getOrderInfoByOrderId(deliveryForDistributionVO.getStaff_id());
            if (distribution_amt == null) {
                distribution_amt = 0.0;
            }
            newdistribution.setDistribution_amt(new BigDecimal(distribution_amt));
            //修改
            orderDistributionMapper.updateById(newdistribution);
            OrderStatusVO vo = new OrderStatusVO();
            vo.setOrder_id(deliveryForDistributionVO.getOrder_id());
            vo.setOrder_status(SysCodeEnmu.ORDERSTATUS_04.getCodeValue());
            vo.setLogin_token(deliveryForDistributionVO.getLogin_token());
            vo.setChange_reason("分配/调度送货人");
            vo.setStatus_remark("分配/调度送货人");
            orderService.updateStatus(vo);
        } else {
            return new Result(CodeEnum.FAIL_BUSINESS, "订单状态变动，请刷新列表");
        }

        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 罗秋涵
     * @description: 后台管理退货分配处理人
     * @param: [deliveryForDistributionVO]
     * @return com.hryj.common.Result
     * @create 2018-07-10 16:56
     **/
    public Result returnForDistribution(DeliveryForDistributionVO deliveryForDistributionVO) {
        //参数判断
        if(deliveryForDistributionVO==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"参数不能为空");
        }
        if(deliveryForDistributionVO.getOrder_id()==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"订单编号不能为空");
        }
        if(deliveryForDistributionVO.getStaff_id()==null){
            return new Result(CodeEnum.FAIL_PARAMCHECK,"分配员工编号不能为空");
        }

        StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(deliveryForDistributionVO.getLogin_token());
        if(staffAdminLoginVO==null||staffAdminLoginVO.getDeptGroup()==null){
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }
        //获取退货处理人信息
        Result<StaffDeptVO> result= staffFeignClien.findStaffDeptVO(null,null,deliveryForDistributionVO.getStaff_id());
        //判断退货处理人是否在职，是否更改部门
        if(result.isSuccess()&&result.getData()!=null&&result.getData().getDept_id()!=null&&result.getData().getStaff_status()!=null){
            if(!result.getData().getStaff_status()||!result.getData().getDept_id().equals(staffAdminLoginVO.getDeptGroup().getId())){
                return new Result(CodeEnum.FAIL_BUSINESS, "配送人信息异常");
            }
        }else{
            return new Result(CodeEnum.FAIL_BUSINESS, "配送人信息异常");
        }

        OrderInfo orderInfo =orderInfoMapper.selectById(deliveryForDistributionVO.getOrder_id());
        if(orderInfo==null){
            return new Result(CodeEnum.FAIL_BUSINESS,"无对应订单记录");
        }
        //判断部门
        if(orderInfo.getParty_id()!=null&& !orderInfo.getParty_id().equals(staffAdminLoginVO.getDeptGroup().getId())){
            return new Result(CodeEnum.FAIL_BUSINESS,"不能分配其他门店退货单");
        }
        OrderReturn orderReturn=orderReturnMapper.getOrderReturnInfo(deliveryForDistributionVO.getOrder_id());
        if(orderReturn==null){
            return new Result(CodeEnum.FAIL_BUSINESS,"无对应记录");
        }
        //如果退货单状态并非申请中的时候，不可再次分配
        else if(!StringUtils.isEmpty(orderReturn.getReturn_handel_id())){
            return new Result(CodeEnum.FAIL_BUSINESS,"该订单已经分配人员");
        }
        //修改退货单信息
        OrderReturn newOrderReturn=new OrderReturn();
        newOrderReturn.setId(orderReturn.getId());
        newOrderReturn.setReturn_handel_id(deliveryForDistributionVO.getStaff_id());
        newOrderReturn.setReturn_handel_name(deliveryForDistributionVO.getStaff_name());
        newOrderReturn.setReturn_status(CodeCache.getValueByKey("ReturnStatus","S02"));
        orderReturnMapper.updateById(newOrderReturn);
        return new Result(CodeEnum.SUCCESS);
    }

    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 处理退货单
     * @param:
     * @return
     * @create 2018-07-11 11:19
     **/
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Result setReturnStatus(DistributionOrderIdVO vo){
        if(StringUtils.isEmpty(vo.getOrder_id())||StringUtils.isEmpty(vo.getReturn_status())){
            return new Result(CodeEnum.FAIL_PARAMCHECK);
        }

        OrderStatusVO statusVO = new OrderStatusVO();
        boolean isReturn = false;//需要退款的标识

        //获取订单详情
        OrderInfoVO orderInfoVO = orderInfoMapper.getOrderInfoVO(vo.getOrder_id());
        if(!SysCodeEnmu.ORDERSTATUS_05.getCodeValue().equals(orderInfoVO.getOrder_status())){
            return new Result(CodeEnum.FAIL_BUSINESS, "该订单已处理");
        }

        //获取当前登录人信息
        /*StaffAdminLoginVO staffAdminLoginVO = LoginCache.getStaffAdminLoginVO(vo.getLogin_token());
        if(staffAdminLoginVO==null||staffAdminLoginVO.getDeptGroup()==null){
            return new Result(CodeEnum.FAIL_BUSINESS, "员工信息异常");
        }

        //判断部门
        if(u.getParty_id()!=null&& !u.getParty_id().equals(staffAdminLoginVO.getDeptGroup().getId())){
            return new Result(CodeEnum.FAIL_BUSINESS,"无权操作订单");
        }*/
        //判断是否跨境订单
        if(PermissionManageHandler.PermissionSupport.ORDER_TYPE_CROSS_BORDER_BONDED_ORDER.getMapping_value()
                .equals(orderInfoVO.getOrder_type_name())){
            //是否可取消true-可以；false-不可以
            boolean isSuccess = crossOrderReturn(orderInfoVO,statusVO);
            if(isSuccess){
                //设置为同意退货
                vo.setReturn_status(SysCodeEnmu.RETURNSTATUS_03.getCodeValue());
            }else{
                //设置为拒绝退货
                vo.setReturn_status(SysCodeEnmu.RETURNSTATUS_05.getCodeValue());
            }
        }
        //仓库判断
        if(orderInfoVO.getParty_type().equals(SysCodeEnmu.DEPTETYPE_02.getCodeValue())){
            //同意退货
            if(vo.getReturn_status().equals(SysCodeEnmu.RETURNSTATUS_03.getCodeValue())){
                //退货成功
                statusVO.setChange_reason("仓库退货单处理");
                statusVO.setOrder_status(SysCodeEnmu.ORDERSTATUS_06.getCodeValue());
                isReturn = true;
            }else if(vo.getReturn_status().equals(SysCodeEnmu.RETURNSTATUS_05.getCodeValue())
                    ||vo.getReturn_status().equals(SysCodeEnmu.RETURNSTATUS_04.getCodeValue())){//拒绝退货,取消
                //订单完成
                statusVO.setOrder_status(SysCodeEnmu.ORDERSTATUS_08.getCodeValue());
                statusVO.setUser_id(orderInfoVO.getUser_id());
                statusVO.setChange_reason("仓库取消或拒绝退货");
                statusVO.setComplete_time(new Date());
            }else{
                return new Result(CodeEnum.FAIL_BUSINESS, "退货状态有误");
            }

        }else{
            //*****门店*****/
            statusVO.setOrder_id(vo.getOrder_id());
            DistributionStatusVO distributionStatusVO = new DistributionStatusVO();
            distributionStatusVO.setOrder_id(vo.getOrder_id());
            distributionStatusVO.setDistribution_type(SysCodeEnmu.DISTRIBUTIONTYPE_01.getCodeValue());

            //查询配送状态 判断是否发货，对应改变不同的订单状态
            DistributionInfoVO distributionInfoVO = orderDistributionMapper.selectDistributionInfoByOrderId(vo.getOrder_id());
            if(SysCodeEnmu.RETURNSTATUS_03.getCodeValue().equals(vo.getReturn_status())){//同意退货
                if(orderInfoVO.getDelivery_type().equals(SysCodeEnmu.DELIVERYTYPE_01.getCodeValue())){
                    //订单状态-退货成功
                    statusVO.setOrder_status(SysCodeEnmu.ORDERSTATUS_06.getCodeValue());
                    isReturn = true;
                }else{
                    //****配送*****/
                    //未送
                    if(distributionInfoVO.getDistribution_status().equals(SysCodeEnmu.DISTRIBUTIONSTATUS_01.getCodeValue())||
                            distributionInfoVO.getDistribution_status().equals(SysCodeEnmu.DISTRIBUTIONSTATUS_02.getCodeValue())
                            ||distributionInfoVO.getDistribution_status().equals(SysCodeEnmu.DISTRIBUTIONSTATUS_05.getCodeValue())){
                        isReturn = true;
                        //订单状态-退货成功
                        statusVO.setOrder_status(SysCodeEnmu.ORDERSTATUS_06.getCodeValue());
                        distributionStatusVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_05.getCodeValue());
                    }else{
                        //已送，订单状态改为退货申请中
                        statusVO.setOrder_status(SysCodeEnmu.ORDERSTATUS_05.getCodeValue());
                        //配送单状态
                        distributionStatusVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_02.getCodeValue());
                        //获取用户登录信息
                        StaffAppLoginVO staffAppLoginVO =LoginCache.getStaffAppLoginVO(vo.getLogin_token());
                        Double ofu = orderReturnMapper.getOrderInfoByOrderId(orderInfoVO.getParty_id());
                        if(ofu==null){
                            ofu = 0.0;
                        }
                        UserInfoVO userInfoVO =  userFeignClient.findUserInfoVOByUserId(orderInfoVO.getUser_id(),null).getData();
                        //生成配送单信息
                        OrderDistribution ob = new OrderDistribution();
                        ob.setDistribution_type(SysCodeEnmu.DISTRIBUTIONTYPE_02.getCodeValue());
                        ob.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_02.getCodeValue());
                        ob.setDistribution_staff_name(staffAppLoginVO.getStaff_name());
                        ob.setDistribution_staff_phone(staffAppLoginVO.getPhone_num());
                        ob.setDistribution_staff_id(staffAppLoginVO.getStaff_id());
                        ob.setDistribution_amt(new BigDecimal(ofu));
                        ob.setUser_id(orderInfoVO.getUser_id());
                        ob.setUser_name(orderInfoVO.getUser_name());
                        ob.setUser_phone(orderInfoVO.getUser_phone());
                        ob.setUser_address(orderInfoVO.getUser_address());
                        ob.setAddress_locations(userInfoVO.getLocations());
                        ob.setOrder_id(orderInfoVO.getOrder_id());
                        orderDistributionMapper.insert(ob);
                        distributionStatusVO.setDistribution_type(null);
                        //生成配送单产品信息
                        List<ReturnProduct> list = orderReturnProductMapper.getReturnProductList(vo.getOrder_id());
                        for(ReturnProduct p : list){
                            DistributionProduct product = new DistributionProduct();
                            product.setOrder_product_id(p.getOrder_product_id());
                            product.setDistribution_quantity(p.getReturn_quantity());
                            product.setOrder_id(p.getOrder_id());
                            product.setOrder_product_id(ob.getId());
                            orderDistributionProductMapper.insert(product);
                        }
                        statusVO.setChange_reason("门店端同意退货");
                    }

                }
            }else{//********取消退货*************/
                //查询订单状态表
                List<OrderStatusRecord> list = orderStatusRecordMapper.getOrderStatusRecordVOList(orderInfoVO.getOrder_id());
                //如果是取消，变为该订单上一个状态
                if(list.size()<2){
                    return new Result(CodeEnum.FAIL_BUSINESS, "订单状态表数据有误");
                }
                statusVO.setOrder_status(list.get(list.size()-2).getOrder_status());
                //********自提********/
                if(orderInfoVO.getDelivery_type().equals(SysCodeEnmu.DELIVERYTYPE_01.getCodeValue())){
                    statusVO.setChange_reason("自提退货取消");
                }else{//配送
                    List<OpenTimeResponse> listTime =  orderService.getOpeningHoursListByDeptId(orderInfoVO.getParty_id());
                    if(listTime==null || listTime.size()<2){
                        return new Result(CodeEnum.FAIL_BUSINESS, "门店营业时间有误");
                    }
                    //获得最后一条数据,计算最终的确认送达时间
                    OpenTimeResponse time = listTime.get(1);
                    TimeQuantum t = time.getQuantumList().get(0);//营业开始时间
                    TimeQuantum t1 = time.getQuantumList().get(time.getQuantumList().size()-1);//营业结束时间
                    Date date = new Date();
                    Long endTime = distributionInfoVO.getHope_delivery_end_time().getTime()+3600*1000;
                    if(endTime>=t1.getEndTime()){
                        date.setTime(t.getBeginTime()+3600*1000);
                    }else{
                        date.setTime(endTime);
                    }
                    distributionStatusVO.setActual_delivery_end_time(date);
                    if(StringUtils.isEmpty(distributionInfoVO.getDistribution_staff_name())){
                        //未分配
                        distributionStatusVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_01.getCodeValue());
                    }else{
                        //有分配
                        if(distributionInfoVO.getDistribution_status().equals(SysCodeEnmu.DISTRIBUTIONSTATUS_03.getCodeValue())||
                                distributionInfoVO.getDistribution_status().equals(SysCodeEnmu.DISTRIBUTIONSTATUS_04.getCodeValue())){
                            //已送达
                            statusVO.setOrder_status(SysCodeEnmu.ORDERSTATUS_04.getCodeValue());
                            distributionStatusVO.setDistribution_status(distributionInfoVO.getDistribution_status());
                        }else{
                            //未送达
                            distributionStatusVO.setDistribution_status(SysCodeEnmu.DISTRIBUTIONSTATUS_02.getCodeValue());
                        }

                    }
                    statusVO.setChange_reason("配送退货取消");
                }
            }
            //更新配送单状态
            if(distributionStatusVO.getDistribution_type()!=null){
                orderDistributionMapper.updateDistributionStatusByOrderId(distributionStatusVO);
            }
        }
        //更新退货单状态
        orderReturnMapper.updateReturnStatus(vo);
        statusVO.setLogin_token(vo.getLogin_token());
        statusVO.setOrder_id(vo.getOrder_id());
        statusVO.setStatus_remark("退货处理");
        //更新订单状态
        orderService.updateStatus(statusVO);
        Result result=null;
        if(isReturn){
            try {
                // 退款-释放库存
                result = confirmSuccess(vo.getOrder_id());
            }catch (Exception e){
                throw new BizException("库存锁定失败");
            }
            if (result==null||result.getCode() != 100) {
                throw new BizException("退货失败，请联系"+CodeCache.getValueByKey("CustomerServicePhone","S01"));
            }
        }
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * 同意退货后的操作
     * @param orderId
     * @return
     */
    public Result confirmSuccess(Long orderId) throws BizException {
        //退款
        Result result = paymentService.refund(orderId);
        if (result.getCode() != 100) {
            return result;
        }
        //释放库存
        try {
            orderForStoreService.resetStockNew(orderId,null);
        }catch (Exception e){
            throw e;
        }
        return new Result(CodeEnum.SUCCESS);
    }


    /**
     * @author 叶方宇
     * @methodName:
     * @methodDesc:
     * @description: 光彩订单取消处理
     * @param:
     * @return
     * @create 2018-09-18 10:01
     **/
    public boolean crossOrderReturn(OrderInfoVO orderInfoVO,OrderStatusVO statusVO){
        Map<String,String> map = orderScheduled.cancelOrderForGC(orderInfoVO.getThird_order_code(),orderInfoVO.getOrder_num());
        //可以取消
        if(ThirdOrderStatusEnum.SUCCESS_F.equals(map.get("success"))){
            statusVO.setChange_reason(map.get("message"));
            return true;
        }
        return false;
    }
}
