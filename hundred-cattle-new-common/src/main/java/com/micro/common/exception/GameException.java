package com.micro.common.exception;

import com.micro.common.constant.ErrorConstant;

public class GameException extends RuntimeException {

    private String code;

    public GameException(String msg) {
        super(msg);
    }

    public GameException(ErrorConstant e) {
        super(e.getMsg());
    }

    public GameException(String code, String msg){
        super(msg);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
