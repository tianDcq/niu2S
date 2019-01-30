package com.micro.game.server.handler;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.micro.common.bean.GlobeResponse;
import com.micro.common.util.JsonUtil;
import com.micro.game.server.client.AccountFeignClient;
import com.micro.game.server.common.WebSocketResponse;
import com.micro.game.server.handler.business.AbstractGameHandler;
import com.micro.game.server.handler.business.BullfightGameHandler;
import com.micro.game.server.nettyMap.NettyChannelMap;
import com.micro.game.server.nettyMap.nettyData.WebSocketData;
import com.micro.game.server.service.BullfightGameService;
import com.micro.game.server.util.CodeUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author sam
 * @ClassName: WebSocketFrameHandler
 * @Description: 连接握手断开消息收发处理类
 * @date 2018-07-23
 */
@Component
@Sharable
@Slf4j
@SuppressWarnings("all")
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	/**
	 * 消息处理业务线程池核心线程数
	 */
	@Value("${business.corePoolSize}")
	private Integer corePoolSize;

	/**
	 * 消息处理业务线程池最大线程数
	 */
	@Value("${business.maxQueueSize}")
	private Integer maxQueueSize;

	/**
	 * websocket 是否加密
	 */
	@Value("${webSocket.isEncrypt}")
	private Integer isEncrypt;

	@Resource
	private AccountFeignClient accountFeignClient;

	@Autowired
	private BullfightGameService bullfightGameService;

	@Autowired
	private BullfightGameHandler  bullfightGameHandler;

	/**
	 * 接收客户端发过来的消息
	 *
	 * @param ctx
	 *            管道对象
	 * @param msg
	 *            消息对象
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
		Channel incoming = ctx.channel();
		 execute(msg.text(), ctx);
	}

	private void execute(String request, ChannelHandlerContext ctx) {
		// 解密
		if (isEncrypt == 1) {
			request = CodeUtils.decode(request);
		}
		String msg = JsonUtil.parseJsonString(processRequest(request, ctx));
		// 加密
		if (isEncrypt == 1) {
			msg = CodeUtils.encode(msg);
		}
		ctx.writeAndFlush(new TextWebSocketFrame(msg));
	}
	
	/**
	 * 有客户端来连接第一个执行的方法
	 *
	 * @param ctx
	 *            管道对象
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		WebSocketData webSocketData = new WebSocketData();
		webSocketData.setChannel(channel);
		NettyChannelMap.add(channel.id() + "", webSocketData);
	}

	/**
	 * 客户端失去连接执行的第一个方法
	 *
	 * @param ctx
	 *            管道对象
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		brokeConnection(ctx);
		//NettyChannelMap.removeByChannelId(channel.id() + "");
	}

	/**
	 * 有客户端来连接第2个执行的方法
	 *
	 * @param ctx
	 *            管道对象
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
	}

	/**
	 * 客户端失去连接执行的第二个方法
	 *
	 * @param ctx
	 *            管道对象
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
	}

	/**
	 * 连接出现异常执行的方法
	 *
	 * @param ctx
	 *            管道对象
	 * @param cause
	 *            异常对象
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * 超时机制
	 * 
	 * @param ctx
	 *            管道对象
	 * @param evt
	 *            消息对象
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			case READER_IDLE:
				ctx.close();
				break;
			case WRITER_IDLE:
				break;
			case ALL_IDLE:
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 处理请求
	 *
	 * @param request
	 *            请求消息
	 */
	private WebSocketResponse processRequest(String request, ChannelHandlerContext ctx) {
		AbstractGameHandler handler = null;
		WebSocketResponse webSocketResponse = new WebSocketResponse();
		Map maps = null;
		try {
			maps = (Map) JSON.parse(request);
			String token = String.valueOf(maps.get("token"));
			String msgTypeStr = String.valueOf(maps.get("msgType"));
			if(msgTypeStr.equals("6007")) {
				log.info("==================收到下注消息====================");
			}
			System.err.println();
			GlobeResponse<Object> globeResponse = accountFeignClient.checkToken(token);
			if (!globeResponse.getCode().equals("200")){
				webSocketResponse.setStatus("-99");
				webSocketResponse.setMsgType(msgTypeStr);
				webSocketResponse.setMsg("token校验失败");
				return webSocketResponse;
			}
			String userMsg = String.valueOf(globeResponse.getData());
			int msgType = Integer.valueOf(msgTypeStr);
			if (msgType >= 6001 && msgType < 7000){//牛牛游戏handler
			
				bullfightGameHandler.setUserMsg(userMsg);
				handler = bullfightGameHandler;
			
			} else {
				log.error("没匹配到对应msgType:" + msgType);
				throw new Exception("没匹配到对应msgType");
			}
			handler.execute(request, webSocketResponse, ctx);
		} catch (Exception ex) {
			webSocketResponse.setStatus("0");
			if (maps != null) {
				webSocketResponse.setMsgType(String.valueOf(maps.get("msgType")));
			}
			if (ex.getMessage() == null) {
				webSocketResponse.setMsg("消息处理异常");
			} else {
				webSocketResponse.setMsg(ex.getMessage());
			}
		}
		return webSocketResponse;
	}
 
	/**
	 * websocket 断开操作
	 * @param ctx
	 */
	private void brokeConnection(ChannelHandlerContext ctx){
		try {
			WebSocketData webSocketData = NettyChannelMap.get(ctx.channel().id() + "");
			bullfightGameService.webSocketBroke(ctx);
		}catch (Exception e){
			e.printStackTrace();
			log.info("用户掉线异常：",e);
		}
	}
}