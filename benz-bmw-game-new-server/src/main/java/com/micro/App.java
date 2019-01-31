
package com.micro;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.micro.common.util.ThreadPoolExecutorUtils;
import com.micro.game.server.frame.GameMain;
import com.micro.game.server.game.HCGameMain;
import com.micro.game.server.handler.NettyWebSocketChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author sam
 * @ClassName: GameServerApplication
 * @Description: 游戏服务启动类
 * @date 2018-07-23
 */
@EnableScheduling // 添加定时任务支持
@EnableSwagger2
@PropertySource(value = "classpath:/application.properties")
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class App {

	// 监听端口
	@Value("${tcp.port}")
	private int tcpPort;

	// 处理tcp连接线程
	@Value("${boss.thread.count}")
	private int bossCount;

	// 处理channel通道 io事件线程
	@Value("${worker.thread.count}")
	private int workerCount;

	// 检测连接状态
	@Value("${so.keepalive}")
	private boolean keepAlive;

	// 服务端同一时间可处理连接数的队列大小
	@Value("${so.backlog}")
	private int backlog;

	// 服务启动对象
	@Bean(name = "serverBootstrap")
	public ServerBootstrap bootstrap() {
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup(), workerGroup()).channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(nettyWebSocketChannelInitializer);
		Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
		Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
		for (@SuppressWarnings("rawtypes")
		ChannelOption option : keySet) {
			b.option(option, tcpChannelOptions.get(option));
		}
		return b;
	}

	@Autowired
	@Qualifier("somethingChannelInitializer")
	private NettyWebSocketChannelInitializer nettyWebSocketChannelInitializer;

	// tcp连接管道设置
	@Bean(name = "tcpChannelOptions")
	public Map<ChannelOption<?>, Object> tcpChannelOptions() {
		Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
		options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
		options.put(ChannelOption.SO_BACKLOG, backlog);
		return options;
	}

	// 处理连接线程池
	@Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		return new NioEventLoopGroup(bossCount);
	}

	// 处理业务线程池
	@Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		return new NioEventLoopGroup(workerCount);
	}

	// tcp监听端口
	@Bean(name = "tcpSocketAddress")
	public InetSocketAddress tcpPort() {
		return new InetSocketAddress(tcpPort);
	}

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext context = SpringApplication.run(App.class,
				args);

		ThreadPoolExecutorUtils.getInstance().execute(() -> {
			TCPServer tcpServer = context.getBean(TCPServer.class);
			try {
				tcpServer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		// 游戏主循环
		(new HCGameMain()).run();
	}

}
