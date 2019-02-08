package com.micro.frame.socket;

import com.micro.frame.Callback;
import com.micro.frame.GameMain;
import com.micro.frame.util.CodeUtils;
import com.micro.frame.util.JsonUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

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

	int index = 0;

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

	/**
	 * 接收客户端发过来的消息
	 *
	 * @param ctx 管道对象
	 * @param msg 消息对象
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
		// String msg = JsonUtil.parseJsonString(request);
		Map o = JsonUtil.parseObject(request, Map.class);

		Request req = new Request();
		req.msg = o;
		req.ctx = ctx;
		req.millisecond = System.currentTimeMillis();
		req.uniqueId = (String) o.get("uniqueId");

		GameMain.getInstance().getMsgQueue().receive(req);
	}

	/**
	 * 有客户端来连接第一个执行的方法
	 *
	 * @param ctx 管道对象
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) {
		log.info("用户连接：" + (++index));
	}

	/**
	 * 客户端失去连接执行的第一个方法
	 *
	 * @param ctx 管道对象
	 */
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) {
		Channel channel = ctx.channel();
		brokeConnection(ctx);
	}

	/**
	 * 有客户端来连接第2个执行的方法
	 *
	 * @param ctx 管道对象
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) {
	}

	/**
	 * 客户端失去连接执行的第二个方法
	 *
	 * @param ctx 管道对象
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
	}

	/**
	 * 连接出现异常执行的方法
	 *
	 * @param ctx   管道对象
	 * @param cause 异常对象
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	/**
	 * 超时机制
	 * 
	 * @param ctx 管道对象
	 * @param evt 消息对象
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
	 * websocket 断开操作
	 * 
	 * @param ctx
	 */
	private void brokeConnection(ChannelHandlerContext ctx) {
		log.info("用户掉线：" + (--index));
		try {
			GameMain.getInstance().getTaskMgr().createTrigger(new Callback() {

				@Override
				public void func() {
					GameMain.getInstance().getRoleMgr().disconnect(ctx);
				}
			}).fire();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("用户掉线异常：", e);
		}
	}
}