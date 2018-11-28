package com.hryj.entity.vo.declare.yunda;

import com.google.common.collect.Lists;
import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author 白飞
 * @className: YunDaResponses
 * @description:
 * @create 2018/9/28 10:11
 **/
@Data
@XmlRootElement(name = "responses")
@XmlAccessorType(XmlAccessType.FIELD)
public class YunDaResponses {
    private List<YunDaResponse> response = Lists.newArrayList();
}
