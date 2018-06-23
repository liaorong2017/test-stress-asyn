package org.raje.test.tcp;

import javax.annotation.Resource;

import org.raje.test.common.SemaphoreWithFlag;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

@Component
public class NettyChannelPoolHandler implements ChannelPoolHandler {
	@Resource
	private NettyConfig config;
	
	@Resource
	protected NettyChannelHandler channelHandler;
	
	@Resource
	private NettyMessageAdapter nettyMessageAdapter;

	@Override
	public void channelReleased(Channel ch) throws Exception {
		SemaphoreWithFlag semaphore = ch.attr(NettyConstants.COMMON_SEMAPHORE).get();
		semaphore.release("channelReleased");
	}

	@Override
	public void channelAcquired(Channel ch) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void channelCreated(Channel ch) throws Exception {
		SocketChannel channel = (SocketChannel) ch;
		channel.config().setKeepAlive(true);
		channel.config().setTcpNoDelay(true);
		ch.pipeline().addLast("idleState-handler",
				new IdleStateHandler(config.getReadTimeout(), config.getWriteTimeout(), 10));
		ch.pipeline().addLast(nettyMessageAdapter.encoder());
		ch.pipeline().addLast(nettyMessageAdapter.decoder());
		ch.pipeline().addLast(channelHandler);
	}

}
