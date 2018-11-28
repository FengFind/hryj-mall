package com.hryj.exception;

/**
 * @author 李道云
 * @className: GlobalException
 * @description: 全局异常
 * @create 2018-06-12 16:30
 **/
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 5701182284190108797L;

    private int code;

    public GlobalException(int code, String message) {
        super(message);
        this.code = code;
    }

    public GlobalException(int code, String message, Throwable e) {
        super(message,e);
        this.code = code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
