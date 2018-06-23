package org.raje.test.tcp;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.raje.test.common.AsyncClinetApi;
import org.raje.test.common.RequestContext;
import org.raje.test.common.Result;
import org.raje.test.common.SemaphoreWithFlag;
import org.raje.test.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ConnectTimeoutException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

@Component
public class NettyAsyncClinetApiImpl implements AsyncClinetApi {
	private static final Logger logger = LoggerFactory.getLogger(NettyChannelHandler.class);

	@Resource
	private NettyConfig config;

	@Resource
	private NettyPoolManager nettyPool;

	@Resource
	private Monitor monitor;

	private Semaphore semaphore;

	@PostConstruct
	public void init() {
		semaphore = new Semaphore(config.maxConnections, true);
	}

	@Override
	public void sendRequest(RequestContext context) {
		try {
			//if (semaphore.tryAcquire(config.acquireTimeoutMillis, TimeUnit.MILLISECONDS)) {
				context.setStartTime(System.currentTimeMillis());
				nettyPool.getFixedChannelPool().acquire().addListener(new GenericFutureListener<Future<Channel>>() {
					@Override
					public void operationComplete(Future<Channel> future) throws Exception {
						if (future.isSuccess()) {
							Channel ch = future.getNow();
							ch.attr(NettyConstants.CHANNEL_POOL_KEY).set(nettyPool.fixedChannelPool);
							ch.attr(NettyConstants.REQUEST_CONTEXT).set(context);
							ch.attr(NettyConstants.COMMON_SEMAPHORE).set(new SemaphoreWithFlag(semaphore));
							ch.writeAndFlush(new String(context.getReqBytes()));
						} else {
							semaphore.release();	
							if (future.cause() instanceof ConnectTimeoutException) {
								monitor.log(context.getStartTime(), Result.connectTimeout);
							} else if (future.cause() instanceof TimeoutException
									|| future.cause() instanceof IllegalStateException) {
                                //TODO Acquire timeout
							} else {
								System.out
										.println("operationComplete exception :" + future.cause().getClass().getName());
								future.cause().printStackTrace();

							}
													
						}
					}
				});
		//	}
		} catch (Exception e) {
			logger.error("未知异常", e);
		}

	}

}