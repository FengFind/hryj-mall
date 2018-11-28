package com.hryj.exception;

import com.hryj.common.CodeEnum;

/**
 * @author 李道云
 * @className: ServerException
 * @description: 程序异常
 * @create 2018-06-12 16:30
 **/
public class ServerException extends GlobalException {

    private static final long serialVersionUID = 6021390821349937519L;

    public ServerException(String message) {
        super(CodeEnum.FAIL_SERVER.getCode(), message);
    }

    public ServerException(String message, Throwable e) {
        super(CodeEnum.FAIL_SERVER.getCode(), message, e);
    }
}
