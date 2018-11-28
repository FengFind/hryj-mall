package com.hryj.entity.vo.declare.yunda;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author 白飞
 * @className: GoodsList
 * @description:
 * @create 2018/9/28 10:02
 **/
@Data
@XmlRootElement(name = "GoodsList")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaGoodsList {

    /** 商品信息 */
    private YunDaGoods goods = new YunDaGoods();
}
