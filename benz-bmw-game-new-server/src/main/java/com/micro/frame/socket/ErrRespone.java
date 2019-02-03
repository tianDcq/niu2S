package com.micro.frame.socket;

public class ErrRespone extends BaseRespone {
    public String msg;

    public ErrRespone(int type, int state, String msg) {
        super(type, state);
        this.msg = msg;
    }
}