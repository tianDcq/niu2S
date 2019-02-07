package com.micro.frame.socket;

import org.java_websocket.WebSocketImpl;

public class testWS {
    public static int count=0;
    public static void main(String[] args) {
        WebSocketImpl.DEBUG=false;
        WsServer ws=new WsServer(7788);
        ws.start();
        
    }
}