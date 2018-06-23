package org.raje.test.tcp;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.FixedChannelPool.AcquireTimeoutAction;
import io.netty.channel.socket.nio.NioSocketChannel;

@Component
public class NettyPoolManager {
	//private static final Logger logger = LoggerFactory.getLogger(NettyPoolManager.class);

	@Resource
	private NettyConfig config;
	
	private int thread_index=1;

	@Resource
	private ChannelPoolHandler poolHandler;

	protected FixedChannelPool fixedChannelPool;

	@PostConstruct
	public void init() {
		Bootstrap bootstrap = builderBootstrap();
		fixedChannelPool = new FixedChannelPool(bootstrap, poolHandler, ChannelHealthChecker.ACTIVE,
				AcquireTimeoutAction.FAIL, config.getAcquireTimeoutMillis(), config.getMaxConnections(),
				config.getMaxConnections());
	}

	private Bootstrap builderBootstrap() {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup eventLoop = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors() * 2,
				new ThreadFactory() {
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "io_event_loop_"+(thread_index++));
					}

				});
		bootstrap.group(eventLoop);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeoutMills());
		bootstrap.remoteAddress(new InetSocketAddress(config.getHost(), config.getPort()));
		return bootstrap;
	}

	public FixedChannelPool getFixedChannelPool() {
		return fixedChannelPool;
	}

	public void setFixedChannelPool(FixedChannelPool fixedChannelPool) {
		this.fixedChannelPool = fixedChannelPool;
	}

}
