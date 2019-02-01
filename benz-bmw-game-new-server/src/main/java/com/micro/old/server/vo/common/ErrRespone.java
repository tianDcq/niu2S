package com.micro.old.server.vo.common;

public class ErrRespone extends Response {
    public String msg;
    public ErrRespone(int type,int state,String msg){
        super(type,state);
        this.msg=msg;
    }
}