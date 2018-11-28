package com.hryj.cache;

/**
 * @author 李道云
 * @className: CacheGroup
 * @description: 缓存分组名枚举
 * @create 2018/6/25 17:19
 **/
public enum CacheGroup {

    CODE("code","字典表",null),
    SMS("sms","短信验证码",null),
    CITY_LEVEL("cityLevel","城市级别",null),
    CITY_CHAILD("cityChild","城市子集",null),
    STAFF_ADMIN_LOGIN("staffAdminLogin","员工后台登录",null),
    STAFF_APP_LOGIN("staffAppLogin","员工APP登录",null),
    USER_LOGIN("userLogin","用户登录",null),
    SELF_PICK_CODE("selfPickCode","自提码",null),
    PRODUCT_STOCK_LOCK("productStockLock","商品库存更新分布式锁标识",null),
    PRODUCT_AUDIT_LOCK("productAuditLock", "商品审核与维护分布式锁标识", null),
    ORDER_TYPE_GROUP("order_type", "订单类别", null),
    ORDER_TYPE_GROUP_KEY("order_type_key", "订单类别key", null),

    ;

    private String value;
    private String name;
    private Integer expireTime;

    CacheGroup(String value, String name, Integer expireTime) {
        this.value = value;
        this.name = name;
        this.expireTime = expireTime;
    }

    public String getValue() {
        return value;
    }

    public Integer getExpireTime() {
        return expireTime;
    }
}
