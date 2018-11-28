package com.hryj.mapper.condition;

import lombok.Data;

/**
 * @author 王光银
 * @className: PartyProdIntersectionCondition
 * @description:
 * @create 2018/7/6 0006 19:03
 **/
@Data
public class PartyProdIntersectionCondition {

    public PartyProdIntersectionCondition() {}
    public PartyProdIntersectionCondition(String value, Integer count) {
        this.value = value;
        this.count = count;
    }

    private String value;

    private Integer count;
}
