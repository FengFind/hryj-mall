package com.hryj.common;

/**
 * @author 李道云
 * @className: BizCodeEnum
 * @description: 业务代码枚举类
 * @create 2018/7/2 9:06
 **/
public enum BizCodeEnum {

    /**
     * 默认业务代码值
     */
    DEFAULT(10000),

    /**
     * 商品相关的业务 错误 代码 从 11000 - 15000
     */


    /**
     * 商品已下架
     */
    PRODUCT_HAS_DOWN(11000),

    /**
     * 活动已结束
     */
    ACTIVITY_HAS_END(11010),

    /**
     * 商品库存不足
     */
    PRODUCT_INVERTORY_OUT(11020),

    /**
     * 商品不存在
     */
    PRODUCT_NOT_EXISTS(11030),

    /**
     * 商品价格变动
     */
    PRODUCT_PRICE_VARIATION(11040),

    /**
     * 没有覆盖用户的门店和仓库
     */
    NO_COVERED_PARTY(15000),

    /**
     * 组织下没有门店和仓库
     */
    PARTY_HAS_NO_STORE_WAREHOUSE(16000),

    /**
     * 组织已不存在
     */
    PARTY_NOT_EXISTS(16100),

    /**
     * 支付查询无结果
     */
    PAYMENT_FAILED(12000);



    private int code;

    BizCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
