package org.raje.test.common;

import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RequestSender implements Runnable {
	private static final Logger LG = LoggerFactory.getLogger(RequestSender.class);

	@Resource(name = "reqListBlocking")
	private BlockingQueue<RequestContext> reqQueue;

	@Resource
	private AsyncClinetApi api;

	@Override
	public void run() {
		while (true) {
			try {
				RequestContext context = reqQueue.take();
				api.sendRequest(context);
			} catch (Exception e) {
				LG.error("异常终止：", e);
				System.exit(1);
			}
		}

	}
}
