package com.hryj.domaincore;

import com.hryj.utils.UtilMisc;
import lombok.Data;

import java.util.List;

/**
 * @author 王光银
 * @className: ExecuteOperationResult
 * @description:
 * @create 2018/8/28 0028 9:50
 **/
@Data
public class ExecuteOperationResult<T> {

    public ExecuteOperationResult(T t) {
        this.t = t;
    }

    public ExecuteOperationResult(List<String> listenerType) {
        this.listenerType = listenerType;
    }

    public ExecuteOperationResult(String listenerType) {
        this.listenerType = UtilMisc.toList(listenerType);
    }

    public ExecuteOperationResult(T t, String listenerType) {
        this.t = t;
        this.listenerType = UtilMisc.toList(listenerType);
    }

    public ExecuteOperationResult(T t, List<String> listenerType) {
        this.t = t;
        this.listenerType = UtilMisc.toList(listenerType);
    }

    private T t;

    private List<String> listenerType;
}
