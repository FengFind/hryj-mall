package com.hryj.entity.vo.declare.yunda;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * @author 白飞
 * @className: YunDaGoods
 * @description:
 * @create 2018/9/28 9:59
 **/
@Data
@XmlRootElement(name = "Goods")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaGoods {

    /** 货物名称 */
    private String name = "";
    /** 海关编码 */
    private String hs_code = "";
    /** 货物单价 */
    private BigDecimal unit_price = BigDecimal.ZERO;
    /** 实际重量 */
    private BigDecimal act_weight = BigDecimal.ZERO;
    /** 体积重量 */
    private BigDecimal dim_weight = BigDecimal.ZERO;
    /** 货物数量 */
    private Integer quantity = 0;

    public YunDaGoods(){

    }
    public YunDaGoods(String name, String hs_code, BigDecimal unit_price, BigDecimal act_weight, BigDecimal dim_weight, Integer quantity){
        this.name = name;
        this.hs_code = hs_code;
        this.unit_price = unit_price;
        this.act_weight = act_weight;
        this.dim_weight = dim_weight;
        this.quantity = quantity;
    }


}
