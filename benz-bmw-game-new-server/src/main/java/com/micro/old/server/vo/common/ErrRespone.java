package com.micro.old.server.vo.common;

public class ErrRespone extends Response {
    public String msgType;
    public String state;
    public String msg;
    public ErrRespone(String type,String state,String msg){
        super(type,state);
        this.msg=msg;
    }
}