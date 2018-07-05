package org.raje.test.tcp;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleIOSessionPool {
	private static final Logger LG = LoggerFactory.getLogger(NioAsyncClinetApi.class);

	private Queue<IOSession> sessions = new ConcurrentLinkedQueue<IOSession>();

	@Resource
	private TcpConfig config;

	@Resource
	private DefaultIOEventDispatch eventDispatch;
	
	
	@Resource
	private DefaultSessionRequestCallback callBack;

	@Resource(name = "totalSendCnt")
	private AtomicInteger totalSendCnt;

	@Resource(name = "hitCacheCnt")
	private AtomicInteger hitCacheCnt;

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

	private IOReactorConfig builderIOReactorConfig() {
		IOReactorConfig.Builder builder = IOReactorConfig.custom();
		builder.setBacklogSize(65535);
		builder.setConnectTimeout(config.getConnectTimeoutMills());
		builder.setSoKeepAlive(false);
		builder.setSoTimeout(config.getReadTimeout());
		builder.setIoThreadCount(Runtime.getRuntime().availableProcessors() * 2);
		builder.setTcpNoDelay(true);
		builder.setSelectInterval(100);
		return builder.build();
	}

	public void release(IOSession session) {
		sessions.add(session);
	}

	public void remove(IOSession session) {
		sessions.remove(session);
	}

	public void execute() {
		IOSession session = sessions.poll();
		if (session == null || session.isClosed()) {
			totalSendCnt.incrementAndGet();
			ioreactor.connect(remoteAddress, null, System.currentTimeMillis(), callBack);
		} else {
			hitCacheCnt.incrementAndGet();
			totalSendCnt.incrementAndGet();
			session.setAttribute(IOSession.ATTACHMENT_KEY, System.currentTimeMillis());
			session.setSocketTimeout(config.getReadTimeout());
			session.setEvent(SelectionKey.OP_WRITE);
		}
	}

}
