package com.hryj.exception;

import com.hryj.common.CodeEnum;


/**
 * @author 李道云
 * @className: ParamException
 * @description: 参数异常
 * @create 2018-06-12 16:30
 **/
public class ParamException extends GlobalException {

    private static final long serialVersionUID = 6021390821349937519L;

    public ParamException(String message) {
        super(CodeEnum.FAIL_PARAMCHECK.getCode(), message);
    }

    public ParamException(String message, Throwable e) {
        super(CodeEnum.FAIL_PARAMCHECK.getCode(), message, e);
    }

}
