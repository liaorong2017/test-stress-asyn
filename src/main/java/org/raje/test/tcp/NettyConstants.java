package org.raje.test.tcp;

import org.raje.test.common.ConnectionResources;
import org.raje.test.request.RequestContext;

import io.netty.channel.pool.SimpleChannelPool;
import io.netty.util.AttributeKey;

public class NettyConstants {

	public static final AttributeKey<SimpleChannelPool> CHANNEL_POOL_KEY = AttributeKey.valueOf("channel_pool");
	public static final AttributeKey<RequestContext> REQUEST_CONTEXT = AttributeKey.valueOf("request_context");
	public static final AttributeKey<ConnectionResources> COMMON_SEMAPHORE = AttributeKey.valueOf("common_semaphore");
}
