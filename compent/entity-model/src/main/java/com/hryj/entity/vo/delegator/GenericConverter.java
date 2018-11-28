package com.hryj.entity.vo.delegator;

/**
 * @author 王光银
 * @className: GenericConverter
 * @description: 品牌ID
 * @create 2018/9/13 0013 14:37
 **/
public interface GenericConverter<T> {

    T convert(Object source_value);
}
