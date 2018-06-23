package org.raje.test.tcp;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;

public interface NettyMessageAdapter {
	public ChannelOutboundHandlerAdapter encoder();

	public ChannelInboundHandlerAdapter decoder();
}
