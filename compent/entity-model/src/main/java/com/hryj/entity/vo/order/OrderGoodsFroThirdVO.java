package com.hryj.entity.vo.order;

import lombok.Data;

/**
 * @author 罗秋涵
 * @className: OrderGoodsFroThirdVO
 * @description: 第三方平台订单商品信息
 * @create 2018/9/14 0014 14:09
 **/
@Data
public class OrderGoodsFroThirdVO {
    /**
     * 商品货号
     */
    private String serial;
    /**
     * 商品价格
     */
    private String price;
    /**
     * 购买数量
     */
    private Integer num;
}
