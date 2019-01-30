package com.micro.game.server.handler.business;

import com.micro.game.server.common.WebSocketResponse;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author sam
 * @ClassName: AbstractGameHandler
 * @Description: 业务处理类基类
 * @date 2018-07-24
 */
public abstract class AbstractGameHandler {

	protected String userMsg;
	 
	/**
	 * 业务处理方法
	 * 
	 * @param request
	 *        客户端请求
	 * @param response
	 *        服务器响应
	 */


	abstract public void  execute(String request, WebSocketResponse response, ChannelHandlerContext ctx)
			throws Exception;
}
