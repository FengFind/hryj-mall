package com.hryj.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 李道云
 * @className: Result
 * @description: 响应结果对象
 * @create 2018-06-12 16:30
 **/
@ApiModel(value = "响应结果对象")
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "响应码", required = true)
    private int code = CodeEnum.SUCCESS.getCode();

    @ApiModelProperty(value = "响应消息", required = true)
    private String msg = "";

    @ApiModelProperty(value = "结果对象", required = true)
    private T data = null;

    @ApiModelProperty(value = "业务代码值")
    private int biz_code = BizCodeEnum.DEFAULT.getCode();

    public Result() {
        super();
    }

    private static final Result DEFAULT_SUCCESS_RESULT = new Result(CodeEnum.SUCCESS);

    public static Result getDefaultSuccessResult() {
        return DEFAULT_SUCCESS_RESULT;
    }

    public Result(CodeEnum codeEnum) {
        this(codeEnum.getCode(), codeEnum.getMsg());
    }

    public Result(CodeEnum codeEnum, String msg) {
        this(codeEnum.getCode(), msg);
    }

    public Result(CodeEnum codeEnum, T data) {
        this(codeEnum.getCode(), codeEnum.getMsg(), data);
    }

    public Result(CodeEnum codeEnum, BizCodeEnum bizCodeEnum){
        this(codeEnum.getCode(),codeEnum.getMsg(),bizCodeEnum.getCode());
    }

    public Result(CodeEnum codeEnum, T data, BizCodeEnum bizCodeEnum){
        this(codeEnum.getCode(),codeEnum.getMsg(),data,bizCodeEnum.getCode());
    }

    private Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Result(int code, String msg, int biz_code) {
        this.code = code;
        this.msg = msg;
        this.biz_code = biz_code;
    }

    public Result(int code, String msg, T data, int biz_code) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.biz_code = biz_code;
    }

    @ApiModelProperty(value = "是否成功", hidden = true)
    public boolean isSuccess(){
        return CodeEnum.isSuccess(this.code);
    }

    @ApiModelProperty(value = "是否失败", hidden = true)
    public boolean isFailed(){
        return !CodeEnum.isSuccess(this.code);
    }

}
