package org.raje.test.tcp;

import java.io.IOException;

import javax.annotation.Resource;

import org.raje.test.common.ConnectionResources;
import org.raje.test.common.Result;
import org.raje.test.monitor.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

@Component
@Sharable
public class NettyChannelHandler extends SimpleChannelInboundHandler<String> {
	private static final Logger logger = LoggerFactory.getLogger(NettyChannelHandler.class);

	@Resource
	private Monitor montor;
	
	@Resource
	private NettyAsynCallBack callBack;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		SimpleChannelPool pool = ctx.channel().attr(NettyConstants.CHANNEL_POOL_KEY).get();
		long start =  ctx.channel().attr(NettyConstants.REQUEST_CONTEXT).get();		
		Result result = null;
		try {
			result = callBack.callBack(msg);
			montor.log(start, result);
		} catch (Exception e) {
			logger.error("异常", e);
			montor.log(start, Result.unknow);
		} finally {
			if (pool != null) {
				pool.release(ctx.channel());
			} else {
				ctx.close();
			}

		}

	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		ConnectionResources semaphore = ctx.channel().attr(NettyConstants.COMMON_SEMAPHORE).get();
		semaphore.release();
		ctx.close().sync();
		SimpleChannelPool pool = ctx.channel().attr(NettyConstants.CHANNEL_POOL_KEY).get();
		if (pool != null) {
			pool.release(ctx.channel());
		}

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		long start = ctx.channel().attr(NettyConstants.REQUEST_CONTEXT).get();
		if (cause instanceof IOException) {
			montor.log(start, Result.connectionClosed);
		} else {
			logger.error("未知异常发生", cause);
			montor.log(start, Result.unknow);
		}

	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		long start = ctx.channel().attr(NettyConstants.REQUEST_CONTEXT).get();
		if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
			IdleStateEvent event = (IdleStateEvent) evt;
			if (event.state() == IdleState.READER_IDLE) {
				montor.log(start, Result.readTimeout);
			} else if (event.state() == IdleState.WRITER_IDLE) {
				montor.log(start, Result.writeTimeout);
			}
		} else {
			logger.error("未知Class：" + evt.getClass(), new RuntimeException());
		}
	}

}
