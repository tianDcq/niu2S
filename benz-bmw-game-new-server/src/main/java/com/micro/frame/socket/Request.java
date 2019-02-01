package com.micro.frame.socket;

import java.util.Map;

import io.netty.channel.ChannelHandlerContext;

/**
 * Request
 */
public class Request {
    public ChannelHandlerContext ctx;
    public String uniqueId;
    public Map<String, Object> msg;
}