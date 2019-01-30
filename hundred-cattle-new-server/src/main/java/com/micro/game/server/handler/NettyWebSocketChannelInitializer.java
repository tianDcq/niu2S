package com.micro.game.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author sam
 * @ClassName: NettyWebSocketChannelInitializer
 * @Description: tcp连接初始化，包装成WebSocket连接，并设置连接通道的各种配置
 * @date 2018-07-23
 */
@Component
@Qualifier("somethingChannelInitializer")
public class NettyWebSocketChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Autowired
    private WebSocketFrameHandler webSocketFrameHandler;

    /**
     * 对超时时间
     */
    @Value("${reader.idle.time}")
    private Integer readerIdleTime;

    /**
     * 初始化管道，添加websocket协议，添加超时机制
     * @param ch channel 对象
     */
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(64*1024));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(new IdleStateHandler(readerIdleTime, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast(webSocketFrameHandler);

    }
}