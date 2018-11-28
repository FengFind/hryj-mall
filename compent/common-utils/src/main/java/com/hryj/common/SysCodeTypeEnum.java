package com.hryj.common;

/**
 * @author 叶方宇
 * @className: SysCodeTypeEnum
 * @description:
 * @create 2018/7/5 0005 11:50
 **/
public enum SysCodeTypeEnum {

    APPKEY("AppKey","应用唯一标识"),
    EDUCATION("Education","学历"),
    SAFFTYPE("SaffType","员工类型"),
    STORETYPE("StoreType","门店类别"),
    STAFFJOB("StoreJob","岗位名称"),
    ADVERTISINGSCOPE("AdvertisingScope","广告适用范围"),
    ADVERTISINGJUMPTYPE("AdvertisingJumpType","广告跳转类型"),
    ACTIVITYSCOPE("ActivityScope","活动适用范围"),
    ACTIVITYTYPE("ActivityType","活动类型"),
    PRODUCTATTRIBUTETYPE("ProductAttributeType","产品属性类别"),
    PRODUCTAUDITTYPE("ProductAuditType","产品调整类别"),
    PRODUCTMODIFYDATATYPE("ProductModifyDataType","产品修改数据类型"),
    SHOPPINGCARTTYPE("ShoppingCartType","购物车类别"),
    ORDERSTATUS("OrderStatus","订单状态"),
    DELIVERYTYPE("DeliveryType","配送方式"),
    CHANGEUSERTYPE("ChangeUserType","变更人类别"),
    RETURNTYPE("ReturnType","退货单类别"),
    RETURNSTATUS("ReturnStatus","退货单状态"),
    RETURNREASON("ReturnReason","退货原因"),
    DISTRIBUTIONSTATUS("DistributionStatus","配送状态"),
    PAYMENTTYPE("PaymentType","支付类型"),
    PAYMENTMETHOD("PaymentMethod","支付方式"),
    PAYMENTSTATUS("PaymentStatus","支付状态");

    private String type;
    private String note;
    SysCodeTypeEnum(String type,String note){
        this.type = type;
        this.note = note;
    }

    public String getType(){
        return this.type;
    }

}
