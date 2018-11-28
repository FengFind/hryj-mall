package com.hryj.exception;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.hryj.common.CodeEnum;
import com.hryj.common.Result;
import com.hryj.utils.SendEmail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 李道云
 * @className: ServerExceptionHandler
 * @description: 全局异常处理
 * @create 2018/7/17 9:08
 **/
@Slf4j
@ControllerAdvice
public class ServerExceptionHandler {

    /**
     * 处理参数校验的异常
     * @param e
     * @return
     */
    @ExceptionHandler(ParamException.class)
    @ResponseBody
    public Result handleParamException(ParamException e){
        log.error("处理参数校验的异常:handleParamException:" + ExceptionUtil.stacktraceToString(e));
        log.error("处理参数校验的异常:e===" + ExceptionUtil.stacktraceToString(e));
        return new Result(CodeEnum.FAIL_PARAMCHECK, ExceptionUtil.getSimpleMessage(e));
    }

    /**
     * 处理业务处理的异常
     * @param e
     * @return
     */
    @ExceptionHandler(BizException.class)
    @ResponseBody
    public Result handleBizException(BizException e){
        log.error("处理业务处理的异常:handleBizException:" + ExceptionUtil.stacktraceToString(e));
        log.error("处理业务处理的异常:e===" + ExceptionUtil.stacktraceToString(e));
        SendEmail.sendEmail(ExceptionUtil.getSimpleMessage(e),ExceptionUtil.stacktraceToString(e));
        return new Result(CodeEnum.FAIL_BUSINESS, ExceptionUtil.getSimpleMessage(e));
    }

    /**
     * 处理程序内部的异常
     * @param e
     * @return
     */
    @ExceptionHandler(ServerException.class)
    @ResponseBody
    public Result handleServerException(ServerException e){
        log.error("处理程序内部的异常:handleServerException:" + ExceptionUtil.stacktraceToString(e));
        log.error("处理程序内部的异常:e===" + ExceptionUtil.stacktraceToString(e));
        SendEmail.sendEmail(ExceptionUtil.getSimpleMessage(e),ExceptionUtil.stacktraceToString(e));
        return new Result(CodeEnum.FAIL_SERVER, ExceptionUtil.getSimpleMessage(e));
    }

    /**
     * 处理所有不可知的异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException(Exception e){
        log.error("处理所有不可知的异常:handleException:" + ExceptionUtil.stacktraceToString(e));
        log.error("处理所有不可知的异常:e===" + ExceptionUtil.stacktraceToString(e));
        SendEmail.sendEmail(ExceptionUtil.getSimpleMessage(e),ExceptionUtil.stacktraceToString(e));
        return new Result(CodeEnum.FAIL_SERVER, "系统太繁忙，请稍后再试");
    }


}