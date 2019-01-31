package com.micro.common.constant;

public enum ErrorConstant {

    //公共的
    COMMON_001("COMMON_001"),


    //用户模块的
    USER_REG_001("USER_REG_001","此账号已经注册"),
    USER_REG_002("USER_REG_002","密码错误"),
    USER_REG_003("USER_REG_003","添加会员账号失败！"),
    USER_REG_004("USER_REG_004","查询会员信息为空！"),
    USER_REG_005("USER_REG_005","查询会员等级出现错误，请稍后再试！"),
    
    //Redis
    REDIS_001("REDIS_001","用户token存在重复的key"),
    REDIS_002("REDIS_002","用户token失效"),
    REDIS_003("REDIS_003","key失效或者不存在"),
    REDIS_004("REDIS_003","%s key失效或者不存在"),

    
    
 

    //

    ;

    ErrorConstant(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    ErrorConstant(String code) {
        this.code = code;
    }

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
