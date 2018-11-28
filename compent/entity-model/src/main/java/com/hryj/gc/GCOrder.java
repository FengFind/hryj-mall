package com.hryj.gc;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

/**
 * @author 叶方宇
 * @className: GCOrder
 * @description:
 * @create 2018/9/11 0011 14:05
 **/
@XmlRootElement(name = "gc")
@XmlAccessorOrder(XmlAccessOrder.ALPHABETICAL)
@Data
public class GCOrder implements Serializable {

    @XmlElement
    private GCOrderHead head;

    @XmlElement
    private GCOrderBody body;

}
