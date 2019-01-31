package com.micro.common.exception;

import com.micro.common.constant.ErrorConstant;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class GlobeException extends RuntimeException {

    private String code;
    private String msg;

    public GlobeException(ErrorConstant e) {
        this.msg = e.getMsg();
        this.code = e.getCode();
    }

    public GlobeException(ErrorConstant e,String str) {
        this.msg = str;
        this.code = e.getCode();
    }

    /**
     * @Description: 不需要这个,只是用来兼容已经用过这个功能的
     * @auth:Anthony
     * @Date:2018/8/11 10:25
     */
    public GlobeException(String code,String message) {

        super(message);
        this.code = code;
        this.msg = message;
        log.error("抛出异常："+message);
    }
}
