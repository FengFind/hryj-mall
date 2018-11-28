package com.hryj.common;

/**
 * @author 李道云
 * @className: CodeEnum
 * @description: 响应代码枚举类
 * @create 2018-06-12 16:30
 **/
public enum CodeEnum {

    SUCCESS(100, "操作成功"),
    FAIL_PARAMCHECK(101,"请求参数校验失败"),
    FAIL_TOKEN_INVALID(102,"登录过期或该账号已在其它地方登录"),
    FAIL_BUSINESS(103,"对不起，业务处理失败"),
    FAIL_UNAUTHORIZED(401,"对不起，您没有权限访问"),
    FAIL_FORBIDDEN(403,"黑名单，禁止访问"),
    FAIL_TIMEOUT(408,"服务器响应超时"),
    FAIL_SERVER(500,"程序内部异常");

    private int code;
    private String msg;

    CodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Boolean isSuccess(int code) {
        return SUCCESS.getCode() == code;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
