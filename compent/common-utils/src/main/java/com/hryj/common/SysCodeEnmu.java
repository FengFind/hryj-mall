package com.hryj.common;

/**
 * @author 叶方宇
 * @className: SysCodeEnmu
 * @description:
 * @create 2018/7/5 0005 13:56
 **/
public enum SysCodeEnmu {

    //应用唯一标识
    APPKEY_01("AppKey","S01","HRYJ-USER-OLD","红瑞颐家用户端APP-老年版"),
    APPKEY_02("AppKey","S02","HRYJ-USER-YOUNG","红瑞颐家用户端APP-年轻版"),
    APPKEY_03("AppKey","S03","HRYJ-STORE","红瑞颐家门店端APP"),

    //学历
    EDUCATION_01("Education","S01","01","小学"),
    EDUCATION_02("Education","S02","02","初中"),
    EDUCATION_03("Education","S03","03","中专"),
    EDUCATION_04("Education","S04","04","高中"),
    EDUCATION_05("Education","S05","05","大专"),
    EDUCATION_06("Education","S06","06","本科"),
    EDUCATION_07("Education","S07","07","硕士"),
    EDUCATION_08("Education","S08","08","博士"),

    //门店类别
    STORETYPE_01("StoreType","S01","01","普通门店"),
    STORETYPE_02("StoreType","S02","02","旗舰门店"),

    //部门类别
    DEPTETYPE_01("DeptType","S01","01","门店"),
    DEPTETYPE_02("DeptType","S02","02","仓库"),
    DEPTETYPE_03("DeptType","S03","03","普通部门"),


    //员工类型
    SAFFTYPE_01("StaffType","S01","01","普通员工"),
    SAFFTYPE_02("StaffType","S02","02","内置员工"),

    //岗位名称
    STAFFJOB_01("StaffJob","S01","01","店长"),
    STAFFJOB_02("StaffJob","S02","02","店员"),
    STAFFJOB_03("StaffJob","S03","03","兼职"),


    //广告适用范围
    ADVERTISINGSCOPE_01("AdvertisingScope","S01","01","仓库"),
    ADVERTISINGSCOPE_02("AdvertisingScope","S02","02","门店"),

    //广告跳转类型
    ADVERTISINGJUMPTYPE_01("AdvertisingJumpType","S01","01","跳转URL"),
    ADVERTISINGJUMPTYPE_02("AdvertisingJumpType","S02","02","跳转商品"),
    ADVERTISINGJUMPTYPE_03("AdvertisingJumpType","S03","03","跳转活动"),

    //活动适用范围
    ACTIVITYSCOPE_01("ActivityScope","S01","01","仓库"),
    ACTIVITYSCOPE_02("ActivityScope","S02","02","门店"),

    //活动类型
    ACTIVITYTYPE_01("ActivityType","S01","01","爆款"),
    ACTIVITYTYPE_02("ActivityType","S02","02","团购"),

    //产品属性类别
    PRODUCTATTRIBUTETYPE_01("ProductAttributeType","S01","01","分类属性"),
    PRODUCTATTRIBUTETYPE_02("ProductAttributeType","S02","02","自定义属性"),

    //产品调整类别
    PRODUCTAUDITTYPE_01("ActivityType","S01","01","新增"),
    PRODUCTAUDITTYPE_02("ActivityType","S02","02","修改"),

    //产品修改数据类型
    PRODUCTMODIFYDATATYPE_01("ProductModifyDataType","S01","01","产品中心库"),
    PRODUCTMODIFYDATATYPE_02("ProductModifyDataType","S02","02","仓库或者门店"),

    //购物车类别
    SHOPPINGCARTTYPE_01("ShoppingCartType","S01","01","默认购物车"),
    SHOPPINGCARTTYPE_02("ShoppingCartType","S02","02","促销购物车"),

    //订单状态
    ORDERSTATUS_01("OrderStatus","S01","01","待支付"),
    ORDERSTATUS_02("OrderStatus","S02","02","待发货"),
    ORDERSTATUS_03("OrderStatus","S03","03","待自提"),
    ORDERSTATUS_04("OrderStatus","S04","04","已发货"),
    ORDERSTATUS_05("OrderStatus","S05","05","退货申请中"),
    ORDERSTATUS_06("OrderStatus","S06","06","退货成功"),
    ORDERSTATUS_07("OrderStatus","S07","07","已取消"),
    ORDERSTATUS_08("OrderStatus","S08","08","已完成"),

    //配送方式
    DELIVERYTYPE_01("DeliveryType","S01","01","自提"),
    DELIVERYTYPE_02("DeliveryType","S02","02","送货上门"),
    DELIVERYTYPE_03("DeliveryType","S03","03","快递"),

    //变更人类别
    CHANGEUSERTYPE_01("ChangeUserType","S01","01","用户"),
    CHANGEUSERTYPE_02("ChangeUserType","S02","02","公司员工"),

    //自提状态
    SELFPICKSTATUS_01("SelfPickStatus","S01","01","待自提"),
    SELFPICKSTATUS_02("SelfPickStatus","S02","02","已自提"),

    //退货单类别
    RETURNTYPE_01("ReturnType","S01","01","客户自主退货"),
    RETURNTYPE_02("ReturnType","S02","02","店员代退货"),

    //退货单状态
    RETURNSTATUS_01("ReturnStatus","S01","01","申请中"),
    RETURNSTATUS_02("ReturnStatus","S02","02","已分配"),
    RETURNSTATUS_03("ReturnStatus","S03","03","同意退货"),
    RETURNSTATUS_04("ReturnStatus","S04","04","取消退货"),
    RETURNSTATUS_05("ReturnStatus","S05","05","拒绝退货"),

    //退货原因
    RETURNREASON_01("ReturnReason","S01","01","无理由退货"),
    RETURNREASON_02("ReturnReason","S02","02","质量问题退货"),
    RETURNREASON_03("ReturnReason","S03","03","其他"),

    //配送单状态
    DISTRIBUTIONSTATUS_01("DistributionStatus","S01","01","待分配"),
    DISTRIBUTIONSTATUS_02("DistributionStatus","S02","02","待配送"),
    DISTRIBUTIONSTATUS_03("DistributionStatus","S03","03","配送完成"),
    DISTRIBUTIONSTATUS_04("DistributionStatus","S04","04","配送超时"),
    DISTRIBUTIONSTATUS_05("DistributionStatus","S05","05","取消配送"),

    //配送类别
    DISTRIBUTIONTYPE_01("DistributionType","S01","01","送货"),
    DISTRIBUTIONTYPE_02("DistributionType","S02","02","取货"),


    //支付类型
    PAYMENTTYPE_01("PaymentType","S01","01","收款"),
    PAYMENTTYPE_02("PaymentType","S02","02","支出"),

    //操作人类别
    OPERATORUSERTYPE_01("OperatorUserType","S01","01","用户"),
    OPERATORUSERTYPE_02("OperatorUserType","S02","02","公司员工"),


    //支付方式
    PAYMENTMETHOD_01("PaymentMethod","S01","01","微信"),
    PAYMENTMETHOD_02("PaymentMethod","S02","02","支付宝"),
    PAYMENTMETHOD_03("PaymentMethod","S03","03","银联"),
    PAYMENTMETHOD_04("PaymentMethod","S04","04","积分抵扣"),

    //支付状态
    PAYMENTSTATUS_01("PaymentStatus","S01","01","支付中"),
    PAYMENTSTATUS_02("PaymentStatus","S02","02","支付成功"),
    PAYMENTSTATUS_03("PaymentStatus","S03","03","支付失败"),
    PAYMENTSTATUS_04("PaymentStatus","S04","04","支付超时"),

    //订单类型
    ORDERTYPE_01("OrderType","S01","01","普通订单"),
    ORDERTYPE_02("OrderType","S02","02","预付订单"),

    //服务提成规则:-,-配
    SERVICE_RULE_01("OrderType","S01","01","自定义"),
    SERVICE_RULE_02("OrderType","S02","02","平均分配");

    private String codeType;
    private String codeKey;
    private String codeValue;
    private String codeName;

    SysCodeEnmu(String codeKey,String codeType,String codeValue,String codeName){
        this.codeKey = codeKey;
        this.codeType = codeType;
        this.codeValue = codeValue;
        this.codeName = codeName;
    }

    public  String getCodeValue(){
        return this.codeValue;
    }

    public String getCodeName(){
        return this.codeName;
    }


}

