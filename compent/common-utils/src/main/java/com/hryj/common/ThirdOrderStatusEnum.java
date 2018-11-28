package com.hryj.common;

import com.hryj.constant.OrderConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 叶方宇
 * @className: ThirdOrderStatusEnum
 * @description:
 * @create 2018/9/14 0014 16:18
 **/
public enum ThirdOrderStatusEnum {

    UNDECLARED("30","订单还未申报海关", OrderConstants.ThirdOrderStatus.waitDeclare.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.waitDeclare.ordinal()]),
    ALREADY_DECLARED("40","订单已申报海关", OrderConstants.ThirdOrderStatus.declareing.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareing.ordinal()]),
    UNDECLARED_SUCCESS("50","订单申报海关成功", OrderConstants.ThirdOrderStatus.declareSuccess.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareSuccess.ordinal()]),
    BONDED_WAREHOUSE("60","订单已推送到保税仓库", OrderConstants.ThirdOrderStatus.declareSuccess.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareSuccess.ordinal()]),
    OUT_OF_THE_TREASURY("70","保税仓库已出货", OrderConstants.ThirdOrderStatus.declareSuccess.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareSuccess.ordinal()]),
    ALREADY_SHIPPED("80","订单已设置发货", OrderConstants.ThirdOrderStatus.declareSuccess.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareSuccess.ordinal()]),
    HAVE_SIGNED_IN("90","订单已收货", OrderConstants.ThirdOrderStatus.declareSuccess.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareSuccess.ordinal()]),
    CUSTOMS_REFUND("100","海关退单", OrderConstants.ThirdOrderStatus.declareFail.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.declareFail.ordinal()]),
    CONFIRM_FAIL("T","订单需取消退款",OrderConstants.ThirdOrderStatus.cancel.ordinal() + "", OrderConstants.THIRD_ORDER_STATUS[OrderConstants.ThirdOrderStatus.cancel.ordinal()]);


    private String gcStatus;
    private String gcStatus_desc;
    private String status;
    private String status_desc;
    ThirdOrderStatusEnum(String gcStatus,String gcStatus_desc,String status,String status_desc){
        this.gcStatus = gcStatus;
        this.gcStatus_desc = gcStatus_desc;
        this.status = status;
        this.status_desc = status_desc;
    }

    public String getGcStatus() {
        return gcStatus;
    }

    public String getGcStatus_desc() {
        return gcStatus_desc;
    }

    public String getStatus() {
        return status;
    }

    public String getStatus_desc() {
        return status_desc;
    }

    public static final String APPKEY = "3de4c6f9db20ce981231eeba403ae1f2";

    public static final String CANCEL_ORDER="cancel_order";

    public static final String SELECT_ORDER = "select_order";

    public static final String SELECT_GOODS = "select_goods";

    public static final String SUCCESS_T="T";

    public static final String SUCCESS_F="F";

    public static final String URL = "https://www.qqbsmall.com/gcshop/index.php?gct=partner";

    public static final String PARTNER_ID = "89";

    public static final String CROSS_BORDER_BONDED_ORDER = "cross_border_bonded_order";

    public static final String NEW_RETAIL_ORDER = "new_retail_order";


    public static Map<String,String> orderType = new HashMap<>();

    //状态对应值
    public static Map<String,String> GCmap_status = new HashMap<>();
    public static Map<String,String> HRmap_status = new HashMap<>();

    //状态对应描述
    public static Map<String,String> GCmap_status_desc = new HashMap<>();
    public static Map<String,String> HRmap_status_desc = new HashMap<>();

    /**
     * 初始化对应表
     */
    static {
        //红瑞对应光彩状态
        //HRmap_status.put(UNDECLARED.status,UNDECLARED.gcStatus);
        HRmap_status.put(ALREADY_DECLARED.status,ALREADY_DECLARED.gcStatus);
        HRmap_status.put(UNDECLARED_SUCCESS.status,UNDECLARED_SUCCESS.gcStatus);
        HRmap_status.put(BONDED_WAREHOUSE.status,BONDED_WAREHOUSE.gcStatus);
        HRmap_status.put(OUT_OF_THE_TREASURY.status,OUT_OF_THE_TREASURY.gcStatus);
        HRmap_status.put(ALREADY_SHIPPED.status,ALREADY_SHIPPED.gcStatus);
        HRmap_status.put(HAVE_SIGNED_IN.status,HAVE_SIGNED_IN.gcStatus);
        HRmap_status.put(CUSTOMS_REFUND.status,CUSTOMS_REFUND.gcStatus);
        HRmap_status.put(CONFIRM_FAIL.status,CONFIRM_FAIL.gcStatus);

        //光彩对应红瑞状态
        //GCmap_status.put(UNDECLARED.gcStatus,UNDECLARED.status);
        GCmap_status.put(ALREADY_DECLARED.gcStatus,ALREADY_DECLARED.status);
        GCmap_status.put(UNDECLARED_SUCCESS.gcStatus,UNDECLARED_SUCCESS.status);
        GCmap_status.put(BONDED_WAREHOUSE.gcStatus,BONDED_WAREHOUSE.status);
        GCmap_status.put(OUT_OF_THE_TREASURY.gcStatus,OUT_OF_THE_TREASURY.status);
        GCmap_status.put(ALREADY_SHIPPED.gcStatus,ALREADY_SHIPPED.status);
        GCmap_status.put(HAVE_SIGNED_IN.gcStatus,HAVE_SIGNED_IN.status);
        GCmap_status.put(CUSTOMS_REFUND.gcStatus,CUSTOMS_REFUND.status);
        GCmap_status.put(CONFIRM_FAIL.gcStatus,CONFIRM_FAIL.status);

        //红瑞状态对应描述
        //HRmap_status_desc.put(UNDECLARED.status,UNDECLARED.status_desc);
        HRmap_status_desc.put(ALREADY_DECLARED.status,ALREADY_DECLARED.status_desc);
        HRmap_status_desc.put(UNDECLARED_SUCCESS.status,UNDECLARED_SUCCESS.status_desc);
        HRmap_status_desc.put(BONDED_WAREHOUSE.status,BONDED_WAREHOUSE.status_desc);
        HRmap_status_desc.put(OUT_OF_THE_TREASURY.status,OUT_OF_THE_TREASURY.status_desc);
        HRmap_status_desc.put(ALREADY_SHIPPED.status,ALREADY_SHIPPED.status_desc);
        HRmap_status_desc.put(HAVE_SIGNED_IN.status,HAVE_SIGNED_IN.status_desc);
        HRmap_status_desc.put(CUSTOMS_REFUND.status,CUSTOMS_REFUND.status_desc);
        HRmap_status_desc.put(CONFIRM_FAIL.status,CONFIRM_FAIL.status_desc);

        //光彩状态对应描述
        //GCmap_status_desc.put(UNDECLARED.gcStatus,UNDECLARED.gcStatus_desc);
        GCmap_status_desc.put(ALREADY_DECLARED.gcStatus,ALREADY_DECLARED.gcStatus_desc);
        GCmap_status_desc.put(UNDECLARED_SUCCESS.gcStatus,UNDECLARED_SUCCESS.gcStatus_desc);
        GCmap_status_desc.put(BONDED_WAREHOUSE.gcStatus,BONDED_WAREHOUSE.gcStatus_desc);
        GCmap_status_desc.put(OUT_OF_THE_TREASURY.gcStatus,OUT_OF_THE_TREASURY.gcStatus_desc);
        GCmap_status_desc.put(ALREADY_SHIPPED.gcStatus,ALREADY_SHIPPED.gcStatus_desc);
        GCmap_status_desc.put(HAVE_SIGNED_IN.gcStatus,HAVE_SIGNED_IN.gcStatus_desc);
        GCmap_status_desc.put(CUSTOMS_REFUND.gcStatus,CUSTOMS_REFUND.gcStatus_desc);
        GCmap_status_desc.put(CONFIRM_FAIL.gcStatus,CONFIRM_FAIL.gcStatus_desc);

        //订单类型
        orderType.put(CROSS_BORDER_BONDED_ORDER,"跨境订单");
        orderType.put(NEW_RETAIL_ORDER,"新零售订单");
    }
}
