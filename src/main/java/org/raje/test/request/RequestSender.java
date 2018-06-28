package org.raje.test.request;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;

import org.raje.test.common.ConnectionResources;
import org.raje.test.monitor.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RequestSender implements Runnable {
	private static final Logger LG = LoggerFactory.getLogger(RequestSender.class);

	@Resource
	private AsyncClinetApi api;

	@Resource
	private ConnectionResources connections;

	@Resource
	private Semaphore staySenderRequest;

	@Resource
	private Counter counter;

	@Resource
	private AtomicInteger currTpsPlain;

	private AtomicLong discardCnt = new AtomicLong(0);

	@Value("${acquire.timeout:10}")
	private long acquireTimeout;

	@Override
	public void run() {
		while (true) {
			try {
				staySenderRequest.acquire();
				if (discardCnt.get() > 0)
					discardCnt.decrementAndGet();
				if (discardCnt.get() <= 0) {
					trySendRequest();
				} else {
					trySendRequestNoWait();
				}
			} catch (Exception e) {
				LG.error("异常终止：", e);
				System.exit(1);
			}
		}

	}

	private void trySendRequest() throws InterruptedException {
		// long start = System.currentTimeMillis();
		if (connections.tryAcquire(acquireTimeout, TimeUnit.MILLISECONDS)) {
			api.sendRequest();
		} else {
			// TODO 表示连接不够，开始出现等待，如果出现一次等待就开始计算要丢弃的请求数
			// long cost = System.currentTimeMillis() - start;
			discardCnt.set(currTpsPlain.get() * acquireTimeout / 1000);
		}
	}

	private boolean trySendRequestNoWait() {
		if (connections.tryAcquire()) {
			api.sendRequest();
			return true;
		}
		return false;
	}
}
