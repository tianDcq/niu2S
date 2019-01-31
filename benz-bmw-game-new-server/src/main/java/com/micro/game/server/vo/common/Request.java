package com.micro.game.server.vo.common;

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