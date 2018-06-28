package org.raje.test.tcp;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.raje.test.request.AsyncClinetApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NioAsyncClinetApi implements AsyncClinetApi {
	private static final Logger LG = LoggerFactory.getLogger(NioAsyncClinetApi.class);

	@Resource(name = "todoList")
	private Queue<NioContext> todoList;

	@Resource(name = "reqList")
	private Queue<NioContext> reqList;

	@Resource
	private NioThread niothread;

	@Resource
	private TimeoutThread timeoutThread;

	@Resource
	private Selector selector;

	@Resource
	private TcpConfig config;

	private RequestProducer producer;

	@PostConstruct
	public void init() {
		niothread.start();
		timeoutThread.start();
	}

	@Override
	public void sendRequest() {
		final NioContext reqCtx = new NioContext(500);
		reqCtx.setTimeoutCallback(new TimeoutCallback(reqCtx));
		reqCtx.setProducer(producer);
		try {
			// 注册NIO处理
			reqCtx.setSocketChannel(SocketChannel.open());
			reqCtx.getSocketChannel().configureBlocking(false);
			reqCtx.getSocketChannel().socket().setTcpNoDelay(true);
			reqCtx.getSocketChannel().socket().setSoTimeout(config.getConnectTimeoutMills());
			reqCtx.getSocketChannel().connect(new InetSocketAddress(config.getHost(), config.getPort()));
			// 添加待处理请求
			todoList.add(reqCtx);
			selector.wakeup();
		} catch (Exception e) {
			LG.error("relayAsyncCall error", e);
		}
	}

}