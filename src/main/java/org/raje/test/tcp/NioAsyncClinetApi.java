package org.raje.test.tcp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOSession;
import org.raje.test.request.AsyncClinetApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NioAsyncClinetApi implements AsyncClinetApi {
	private static final Logger LG = LoggerFactory.getLogger(NioAsyncClinetApi.class);

	@Resource
	private TcpConfig config;

	@Resource
	private DefaultIOEventDispatch eventDispatch;

	@Resource
	private BlockingQueue<IOSession> ioSessions;

	private SocketAddress remoteAddress;

	private DefaultConnectingIOReactor ioreactor;

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
			return new Thread(r, "connection_mng");
		}
	});

	@PostConstruct
	public void init() {
		IOReactorConfig ioReactorConfig = builderIOReactorConfig();

		try {
			ioreactor = new DefaultConnectingIOReactor(ioReactorConfig);
			executor.execute(new Runnable() {

				@Override
				public void run() {
					try {
						ioreactor.execute(eventDispatch);
					} catch (Exception e) {
						LG.error(" DefaultConnectingIOReactor execute error", e);
						System.exit(1);
					}

				}
			});
		} catch (Exception e) {
			LG.error("init DefaultConnectingIOReactor error", e);
			System.exit(1);
		}
		remoteAddress = new InetSocketAddress(config.getHost(), config.getPort());

	}

	@Override
	public void sendRequest() {
		IOSession session = ioSessions.poll();
		if (session == null) {
			ioreactor.connect(remoteAddress, null, System.currentTimeMillis(), null);
		} else {
			session.setAttribute(IOSession.ATTACHMENT_KEY, System.currentTimeMillis());
			session.setEvent(SelectionKey.OP_WRITE | SelectionKey.OP_READ);
		}

	}

	private IOReactorConfig builderIOReactorConfig() {
		IOReactorConfig.Builder builder = IOReactorConfig.custom();
		builder.setBacklogSize(65535);
		builder.setConnectTimeout(config.getConnectTimeoutMills());
		builder.setSoKeepAlive(false);
		builder.setSoTimeout(config.getReadTimeout());
		builder.setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2);
		builder.setTcpNoDelay(true);
		return builder.build();
	}

}
