package com.hryj.exception;

import com.hryj.common.CodeEnum;

/**
 * @author 李道云
 * @className: BizException
 * @description: 业务异常
 * @create 2018-06-12 16:30
 **/
public class BizException extends GlobalException {

    private static final long serialVersionUID = 1355046108056594333L;

    public BizException(String message) {
        super(CodeEnum.FAIL_BUSINESS.getCode(), message);
    }

    public BizException(String message, Throwable e) {
        super(CodeEnum.FAIL_BUSINESS.getCode(), message, e);
    }

}
