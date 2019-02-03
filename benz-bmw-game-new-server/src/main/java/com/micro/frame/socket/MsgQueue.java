package com.micro.frame.socket;

import java.util.concurrent.LinkedBlockingQueue;

import com.micro.common.util.JsonUtil;
import com.micro.frame.Player;
import com.micro.frame.socket.Request;
import com.micro.frame.socket.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class MsgQueue {
    private LinkedBlockingQueue<Request> receiveQ1 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Request> receiveQ2 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Response> sendQ1 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Response> sendQ2 = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Request> currentReveiveQ = receiveQ1;
    private LinkedBlockingQueue<Response> currentSendQ = sendQ1;

    public MsgQueue() {

    }

    public void receive(Request o) {
        currentReveiveQ.add(o);
    }

    public void send(Player player, BaseRespone o) {
        if (player.getCtx() != null) {
            String t2 = JsonUtil.parseJsonString(o);
            player.getCtx().writeAndFlush(new TextWebSocketFrame(t2));
        }
    }

    public Request get() {
        return currentReveiveQ.poll();
    }

    public void doPrepare() {

    }

    public void doStop() {
        // TODO
    }

    public void doTerminate() {
        // TODO
    }

    public void doDestroy() {
        // TODO
    }

    public Iterable<Request> getAll() {
        if (currentReveiveQ.size() == 0) {
            return null;
        }
        Iterable<Request> ret = currentReveiveQ;
        currentReveiveQ = currentReveiveQ == receiveQ1 ? receiveQ2 : receiveQ1;
        currentReveiveQ.clear();
        return ret;
    }
}